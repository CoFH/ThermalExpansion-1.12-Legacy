package thermalexpansion.block.ender;

import cofh.api.energy.IEnergyHandler;
import cofh.api.transport.IEnderEnergyHandler;
import cofh.api.transport.IEnderFluidHandler;
import cofh.api.transport.IEnderItemHandler;
import cofh.api.transport.RegistryEnderAttuned;
import cofh.network.ITileInfoPacketHandler;
import cofh.network.PacketCoFHBase;
import cofh.network.PacketHandler;
import cofh.network.PacketTileInfo;
import cofh.util.BlockHelper;
import cofh.util.CoreUtils;
import cofh.util.EnergyHelper;
import cofh.util.FluidHelper;
import cofh.util.RedstoneControlHelper;
import cofh.util.ServerHelper;
import cofh.util.StringHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TileRSControl;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.GuiHandler;
import thermalexpansion.gui.client.ender.GuiTesseract;
import thermalexpansion.gui.container.ender.ContainerTesseract;
import thermalexpansion.util.Utils;

public class TileTesseract extends TileRSControl implements ITileInfoPacketHandler, IEnderEnergyHandler, IEnderFluidHandler, IEnderItemHandler, IFluidHandler,
		ISidedInventory {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileTesseract.class, "thermalexpansion.Tesseract");
	}

	protected static final int[] SLOTS = { 0 };

	public enum PacketInfoID {
		NAME_LIST, ALTER_NAME_LIST, TILE_INFO
	}

	public enum TransferMode {
		SEND, RECV, SENDRECV, BLOCKED
	}

	private boolean isSendingEnergy = false;
	private boolean isSendingFluid = false;
	private boolean isSendingItems = false;

	int itemTrackerAdjacent;
	int itemTrackerRemote;
	int fluidTrackerAdjacent;
	int fluidTrackerRemote;
	int energyTrackerAdjacent;
	int energyTrackerRemote;

	boolean cached = false;
	IEnergyHandler[] adjacentEnergyHandlers = new IEnergyHandler[6];
	IFluidHandler[] adjacentFluidHandlers = new IFluidHandler[6];

	public int frequency = -1;
	public byte modeItem = (byte) TransferMode.RECV.ordinal();
	public byte modeFluid = (byte) TransferMode.RECV.ordinal();
	public byte modeEnergy = (byte) TransferMode.RECV.ordinal();

	/* Augment Variables */

	public TileTesseract() {

		inventory = new ItemStack[1];
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.ender.tesseract.name";
	}

	@Override
	public int getType() {

		return 0;
	}

	@Override
	public boolean sendRedstoneUpdates() {

		return true;
	}

	@Override
	public void blockBroken() {

		removeFromRegistry();
	}

	@Override
	public void onChunkUnload() {

		removeFromRegistry();
	}

	@Override
	public void onNeighborBlockChange() {

		super.onNeighborBlockChange();
		updateAdjacentHandlers();
	}

	@Override
	public void onNeighborTileChange(int tileX, int tileY, int tileZ) {

		super.onNeighborTileChange(tileX, tileY, tileZ);
		updateAdjacentHandler(tileX, tileY, tileZ);
	}

	@Override
	public void invalidate() {

		super.invalidate();
		blockBroken();
	}

	@Override
	public void validate() {

		super.validate();
		if (frequency != -1) {
			addToRegistry();
		}
	}

	@Override
	public void updateEntity() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		if (!cached) {
			updateAdjacentHandlers();
		}
		if (timeCheck() && inventory[0] != null) {
			sendItem(inventory[0]);

			if (inventory[0].stackSize <= 0) {
				inventory[0] = null;
				callNeighborTileChange();
			}
		}
	}

	protected void updateAdjacentHandlers() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		for (int i = 0; i < 6; i++) {
			TileEntity tile = BlockHelper.getAdjacentTileEntity(this, i);

			if (tile instanceof TileTesseract) {
				continue;
			}
			if (FluidHelper.isFluidHandler(tile)) {
				adjacentFluidHandlers[i] = (IFluidHandler) tile;
			} else {
				adjacentFluidHandlers[i] = null;
			}
			if (EnergyHelper.isEnergyHandlerFromSide(tile, ForgeDirection.VALID_DIRECTIONS[i ^ 1])) {
				adjacentEnergyHandlers[i] = (IEnergyHandler) tile;
			} else {
				adjacentEnergyHandlers[i] = null;
			}
		}
		cached = true;
	}

	protected void updateAdjacentHandler(int x, int y, int z) {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		int side = BlockHelper.determineAdjacentSide(this, x, y, z);

		TileEntity tile = worldObj.getTileEntity(x, y, z);
		if (tile instanceof TileTesseract) {
			return;
		}
		if (FluidHelper.isFluidHandler(tile)) {
			adjacentFluidHandlers[side] = (IFluidHandler) tile;
		} else {
			adjacentFluidHandlers[side] = null;
		}
		if (EnergyHelper.isEnergyHandlerFromSide(tile, ForgeDirection.VALID_DIRECTIONS[side ^ 1])) {
			adjacentEnergyHandlers[side] = (IEnergyHandler) tile;
		} else {
			adjacentEnergyHandlers[side] = null;
		}
	}

	public boolean isOwner(String username) {

		return username == null ? false : username.equals(owner);
	}

	public void addEntry(int theFreq, String freqName) {

		if (ServerHelper.isClientWorld(worldObj)) {
			PacketHandler.sendToServer(PacketTileInfo.newPacket(this).addByte(PacketInfoID.ALTER_NAME_LIST.ordinal()).addBool(false)
					.addString(access.isPublic() ? "_public_" : owner.toLowerCase()).addString(String.valueOf(theFreq)).addString(freqName));
		}
	}

	public void removeEntry(int theFreq, String freqName) {

		if (ServerHelper.isClientWorld(worldObj)) {
			PacketHandler.sendToServer(PacketTileInfo.newPacket(this).addByte(PacketInfoID.ALTER_NAME_LIST.ordinal()).addBool(true)
					.addString(access.isPublic() ? "_public_" : owner.toLowerCase()).addString(String.valueOf(theFreq)).addString(freqName));
		}
	}

	public void addToRegistry() {

		RegistryEnderAttuned.add(this);
	}

	public void removeFromRegistry() {

		RegistryEnderAttuned.remove(this);
	}

	public void setTileInfo(int theFreq) {

		if (ServerHelper.isClientWorld(worldObj)) {
			frequency = theFreq;
			PacketHandler.sendToServer(PacketTileInfo.newPacket(this).addByte(PacketInfoID.TILE_INFO.ordinal()).addByte(modeItem).addByte(modeFluid)
					.addByte(modeEnergy).addByte(access.ordinal()).addInt(theFreq));
		}
	}

	/* SEND METHODS */
	int sendEnergy(int energy, boolean simulate) {

		List<IEnderEnergyHandler> validOutputs = RegistryEnderAttuned.getLinkedEnergyOutputs(this);
		int startAmount = energy;

		if (startAmount <= 0) {
			return 0;
		}
		if (validOutputs != null) {
			isSendingEnergy = true;
			IEnderEnergyHandler handler;

			for (int i = energyTrackerRemote; i < validOutputs.size() && energy > 0; i++) {
				handler = validOutputs.get(i);
				if (handler.canReceiveEnergy()) {
					energy = handler.receiveEnergy(energy, simulate);
				}
			}
			for (int i = 0; i < validOutputs.size() && i < energyTrackerRemote && energy > 0; i++) {
				handler = validOutputs.get(i);
				if (handler.canReceiveEnergy()) {
					energy = handler.receiveEnergy(energy, simulate);
				}
			}
			energyTrackerRemote = incrRemoteTracker(energyTrackerRemote, validOutputs.size());
		}
		isSendingEnergy = false;
		return startAmount - energy;
	}

	int sendFluid(FluidStack fluid, boolean doFill) {

		List<IEnderFluidHandler> validOutputs = RegistryEnderAttuned.getLinkedFluidOutputs(this);
		int startAmount = fluid.amount;

		if (startAmount <= 0) {
			return 0;
		}
		if (validOutputs != null) {
			isSendingFluid = true;
			IEnderFluidHandler handler;

			for (int i = fluidTrackerRemote; i < validOutputs.size() && fluid != null && fluid.amount > 0; i++) {
				handler = validOutputs.get(i);
				if (handler.canReceiveFluid()) {
					fluid = handler.receiveFluid(fluid, doFill);
				}
			}
			for (int i = 0; i < validOutputs.size() && i < fluidTrackerRemote && fluid != null && fluid.amount > 0; i++) {
				handler = validOutputs.get(i);
				if (handler.canReceiveFluid()) {
					fluid = handler.receiveFluid(fluid, doFill);
				}
			}
			fluidTrackerRemote = incrRemoteTracker(fluidTrackerRemote, validOutputs.size());
		}
		isSendingFluid = false;
		return startAmount - fluid.amount;
	}

	void sendItem(ItemStack item) {

		List<IEnderItemHandler> validOutputs = RegistryEnderAttuned.getLinkedItemOutputs(this);

		if (validOutputs != null) {
			isSendingItems = true;
			IEnderItemHandler handler;

			for (int i = itemTrackerRemote; i < validOutputs.size() && item != null && item.stackSize > 0; i++) {
				handler = validOutputs.get(i);
				if (handler.canReceiveItems()) {
					item = handler.receiveItem(item);
				}
			}
			for (int i = 0; i < validOutputs.size() && i < itemTrackerRemote && item != null && item.stackSize > 0; i++) {
				handler = validOutputs.get(i);
				if (handler.canReceiveItems()) {
					item = handler.receiveItem(item);
				}
			}
			itemTrackerRemote = incrRemoteTracker(itemTrackerRemote, validOutputs.size());
		}
		if (item != null && item.stackSize > 0) {
			inventory[0] = item;
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		}
		isSendingItems = false;
	}

	/* TRACKER METHODS */
	public void incrEnergyTrackerAdjacent() {

		energyTrackerAdjacent++;
		for (int side = energyTrackerAdjacent; side < 6; side++) {
			if (adjacentEnergyHandlers[side] != null) {
				energyTrackerAdjacent = side;
				return;
			}
		}
		energyTrackerAdjacent %= 6;
		for (int side = 0; side < energyTrackerAdjacent; side++) {
			if (adjacentEnergyHandlers[side] != null) {
				energyTrackerAdjacent = side;
				return;
			}
		}
		energyTrackerAdjacent = 0;
	}

	public void incrFluidTrackerAdjacent() {

		fluidTrackerAdjacent++;
		for (int side = fluidTrackerAdjacent; side < 6; side++) {
			if (adjacentFluidHandlers[side] != null) {
				fluidTrackerAdjacent = side;
				return;
			}
		}
		fluidTrackerAdjacent %= 6;
		for (int side = 0; side < fluidTrackerAdjacent; side++) {
			if (adjacentFluidHandlers[side] != null) {
				fluidTrackerAdjacent = side;
				return;
			}
		}
		fluidTrackerAdjacent = 0;
	}

	public void incrItemTrackerAdjacent() {

		itemTrackerAdjacent++;
		for (int side = itemTrackerAdjacent; side < 6; side++) {
			if (Utils.isAdjacentInventory(this, side)) {
				itemTrackerAdjacent = side;
				return;
			}
		}
		itemTrackerAdjacent %= 6;
		for (int side = 0; side < itemTrackerAdjacent; side++) {
			if (Utils.isAdjacentInventory(this, side)) {
				itemTrackerAdjacent = side;
				return;
			}
		}
		itemTrackerAdjacent = 0;
	}

	public int incrRemoteTracker(int tracker, int max) {

		tracker++;
		if (tracker >= max) {
			tracker = 0;
		}
		return tracker;
	}

	/* HELPER METHODS */
	public int addToAdjInventory(TileEntity tile, int from, ItemStack stack) {

		TileEntity tileInventory = BlockHelper.getAdjacentTileEntity(tile, from);

		if (tileInventory instanceof TileTesseract) {
			return stack.stackSize;
		}
		return Utils.addToAdjacentInventory(this, from, stack);
	}

	public boolean isAdjacentInventory(int side) {

		TileEntity tile = BlockHelper.getAdjacentTileEntity(worldObj, xCoord, yCoord, zCoord, side);
		return tile instanceof TileTesseract ? false : Utils.isInventory(tile, side);
	}

	public boolean modeSendEnergy() {

		return modeEnergy == TransferMode.SEND.ordinal() || modeEnergy == TransferMode.SENDRECV.ordinal();
	}

	public boolean modeReceiveEnergy() {

		return modeEnergy == TransferMode.RECV.ordinal() || modeEnergy == TransferMode.SENDRECV.ordinal();
	}

	public boolean modeSendFluid() {

		return modeFluid == TransferMode.SEND.ordinal() || modeFluid == TransferMode.SENDRECV.ordinal();
	}

	public boolean modeReceiveFluid() {

		return modeFluid == TransferMode.RECV.ordinal() || modeFluid == TransferMode.SENDRECV.ordinal();
	}

	public boolean modeSendItems() {

		return modeItem == TransferMode.SEND.ordinal() || modeItem == TransferMode.SENDRECV.ordinal();
	}

	public boolean modeReceiveItems() {

		return modeItem == TransferMode.RECV.ordinal() || modeItem == TransferMode.SENDRECV.ordinal();
	}

	public void incEnergyMode() {

		modeEnergy++;
		if (modeEnergy > 3) {
			modeEnergy = 0;
		}
	}

	public void decEnergyMode() {

		modeEnergy--;
		if (modeEnergy < 0) {
			modeEnergy = 3;
		}
	}

	public void incFluidMode() {

		modeFluid++;
		if (modeFluid > 3) {
			modeFluid = 0;
		}
	}

	public void decFluidMode() {

		modeFluid--;
		if (modeFluid < 0) {
			modeFluid = 3;
		}
	}

	public void incItemMode() {

		modeItem++;
		if (modeItem > 3) {
			modeItem = 0;
		}
	}

	public void decItemMode() {

		modeItem--;
		if (modeItem < 0) {
			modeItem = 3;
		}
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiTesseract(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTesseract(inventory, this);
	}

	@Override
	public boolean openGui(EntityPlayer player) {

		if (CoreUtils.isFakePlayer(player)) {
			return true;
		}
		if (canPlayerAccess(player.getCommandSenderName())) {
			if (ServerHelper.isServerWorld(worldObj)) {
				sendNamesList((EntityPlayerMP) player);
			}
			player.openGui(ThermalExpansion.instance, GuiHandler.TILE_ID, worldObj, xCoord, yCoord, zCoord);
			return true;
		}
		if (ServerHelper.isServerWorld(worldObj)) {
			player.addChatMessage(new ChatComponentText(StringHelper.localize("chat.cofh.secure1") + " " + owner + "! "
					+ StringHelper.localize("chat.cofh.secure2")));
		}
		return true;
	}

	@Override
	public void sendGuiNetworkData(Container container, ICrafting player) {

		player.sendProgressBarUpdate(container, 0, canPlayerAccess(((EntityPlayer) player).getDisplayName()) ? 1 : 0);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		modeItem = nbt.getByte("Item.Mode");
		modeFluid = nbt.getByte("Fluid.Mode");
		modeEnergy = nbt.getByte("Energy.Mode");

		itemTrackerAdjacent = nbt.getInteger("Item.Adj");
		itemTrackerRemote = nbt.getInteger("Item.Rem");
		fluidTrackerAdjacent = nbt.getInteger("Fluid.Adj");
		fluidTrackerRemote = nbt.getInteger("Fluid.Rem");
		energyTrackerAdjacent = nbt.getInteger("Energy.Adj");
		energyTrackerRemote = nbt.getInteger("Energy.Rem");

		frequency = nbt.getInteger("Frequency");

		addToRegistry();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Item.Mode", modeItem);
		nbt.setByte("Fluid.Mode", modeFluid);
		nbt.setByte("Energy.Mode", modeEnergy);

		nbt.setInteger("Item.Adj", itemTrackerAdjacent);
		nbt.setInteger("Item.Rem", itemTrackerRemote);
		nbt.setInteger("Fluid.Adj", fluidTrackerAdjacent);
		nbt.setInteger("Fluid.Rem", fluidTrackerRemote);
		nbt.setInteger("Energy.Adj", energyTrackerAdjacent);
		nbt.setInteger("Energy.Rem", energyTrackerRemote);

		nbt.setInteger("Frequency", frequency);
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addByte(modeEnergy);
		payload.addByte(modeFluid);
		payload.addByte(modeItem);
		payload.addInt(frequency);

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		return null;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		modeEnergy = payload.getByte();
		modeFluid = payload.getByte();
		modeItem = payload.getByte();
		frequency = payload.getInt();

		isActive = frequency == -1 ? false : true;
	}

	/* ITileInfoPacketHandler */
	@Override
	public void handleTileInfoPacket(PacketCoFHBase payload, boolean isServer, EntityPlayer thePlayer) {

		switch (PacketInfoID.values()[payload.getByte()]) {
		case NAME_LIST:
			RegistryEnderAttuned.clearClientNames();
			int nameCount = payload.getInt();
			for (int i = 0; i < nameCount; i++) {
				RegistryEnderAttuned.addClientNames(payload.getString(), payload.getString());
			}
			ThermalExpansion.proxy.updateTesseractGui();
			return;
		case ALTER_NAME_LIST:
			if (payload.getBool()) { // If Remove
				RegistryEnderAttuned.linkConf.getCategory(payload.getString()).remove(payload.getString());
			} else {
				RegistryEnderAttuned.linkConf.get(payload.getString(), payload.getString(), "").set(payload.getString());
			}
			sendNamesList((EntityPlayerMP) thePlayer);
			RegistryEnderAttuned.linkConf.save();
			return;
		case TILE_INFO:
			removeFromRegistry();
			modeItem = payload.getByte();
			modeFluid = payload.getByte();
			modeEnergy = payload.getByte();
			access = AccessMode.values()[payload.getByte()];
			frequency = payload.getInt();
			addToRegistry();

			isActive = frequency == -1 ? false : true;

			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			callNeighborTileChange();

			sendNamesList((EntityPlayerMP) thePlayer);
			return;
		}
	}

	/*
	 * WARNING - Only sends to player
	 */
	public void sendNamesList(EntityPlayerMP thePlayer) {

		String lookupName = access.isPublic() ? "_Public_" : owner;
		Map<String, Property> curList = RegistryEnderAttuned.linkConf.getCategory(lookupName.toLowerCase());

		PacketCoFHBase payload = PacketTileInfo.newPacket(this);
		if (curList != null) {
			payload.addByte((byte) PacketInfoID.NAME_LIST.ordinal());
			payload.addInt(curList.size());

			for (Property curProp : curList.values()) {
				payload.addString(curProp.getName());
				payload.addString(curProp.getString());
			}
		} else {
			payload.addByte((byte) PacketInfoID.NAME_LIST.ordinal());
			payload.addInt(0);
		}
		PacketHandler.sendTo(payload, thePlayer);
	}

	/* IInventory */
	@Override
	public int getSizeInventory() {

		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int i) {

		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {

		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {

		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack stack) {

		sendItem(stack);
	}

	/* IEnderAttuned */
	@Override
	public String getChannelString() {

		return access.isPublic() ? "_public_" : owner;
	}

	@Override
	public int getFrequency() {

		return frequency;
	}

	@Override
	public boolean setFrequency(int frequency) {

		if (!access.isPublic() || frequency > 999 || frequency < 0) {
			return false;
		}
		removeFromRegistry();
		this.frequency = frequency;
		addToRegistry();
		isActive = true;

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		markDirty();
		return true;
	}

	@Override
	public boolean clearFrequency() {

		if (!access.isPublic()) {
			return false;
		}
		removeFromRegistry();
		frequency = -1;
		addToRegistry();
		isActive = false;

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		markDirty();
		return true;
	}

	/* IEnderEnergyReceptor */
	@Override
	public boolean canSendEnergy() {

		return modeSendEnergy();
	}

	@Override
	public boolean canReceiveEnergy() {

		return !isSendingEnergy && modeReceiveEnergy();
	}

	@Override
	public int receiveEnergy(int energy, boolean simulate) {

		if (!redstoneControlOrDisable()) {
			return energy;
		}
		for (int side = energyTrackerAdjacent; side < 6 && energy > 0; side++) {
			if (adjacentEnergyHandlers[side] != null) {
				energy -= adjacentEnergyHandlers[side].receiveEnergy(ForgeDirection.VALID_DIRECTIONS[side ^ 1], energy, simulate);
			}
		}
		for (int side = 0; side < energyTrackerAdjacent && side < 6 && energy > 0; side++) {
			if (adjacentEnergyHandlers[side] != null) {
				energy -= adjacentEnergyHandlers[side].receiveEnergy(ForgeDirection.VALID_DIRECTIONS[side ^ 1], energy, simulate);
			}
		}
		incrEnergyTrackerAdjacent();
		return energy;
	}

	/* IEnderFluidReceptor */
	@Override
	public boolean canSendFluid() {

		return modeSendFluid();
	}

	@Override
	public boolean canReceiveFluid() {

		return !isSendingFluid && modeReceiveFluid();
	}

	@Override
	public FluidStack receiveFluid(FluidStack fluid, boolean doFill) {

		if (!redstoneControlOrDisable()) {
			return fluid;
		}
		for (int side = fluidTrackerAdjacent; side < 6 && fluid.amount > 0; side++) {
			if (adjacentFluidHandlers[side] != null) {
				fluid.amount -= adjacentFluidHandlers[side].fill(ForgeDirection.VALID_DIRECTIONS[side ^ 1], fluid, doFill);
			}
		}
		for (int side = 0; side < fluidTrackerAdjacent && side < 6 && fluid.amount > 0; side++) {
			if (adjacentFluidHandlers[side] != null) {
				fluid.amount -= adjacentFluidHandlers[side].fill(ForgeDirection.VALID_DIRECTIONS[side ^ 1], fluid, doFill);
			}
		}
		incrFluidTrackerAdjacent();
		return fluid;
	}

	/* IEnderItemReceptor */
	@Override
	public boolean canSendItems() {

		return modeSendItems();
	}

	@Override
	public boolean canReceiveItems() {

		return !isSendingItems && modeReceiveItems();
	}

	@Override
	public ItemStack receiveItem(ItemStack stack) {

		if (!redstoneControlOrDisable()) {
			return stack;
		}
		for (int side = itemTrackerAdjacent; side < 6 && stack != null && stack.stackSize > 0; side++) {
			if (isAdjacentInventory(side)) {
				stack.stackSize = addToAdjInventory(this, side, stack.copy());
			}
		}
		for (int side = 0; side < itemTrackerAdjacent && stack != null && stack.stackSize > 0; side++) {
			if (isAdjacentInventory(side)) {
				stack.stackSize = addToAdjInventory(this, side, stack.copy());
			}
		}
		incrItemTrackerAdjacent();
		return stack;
	}

	/* IEnergyHandler */
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {

		if (frequency == -1 || !redstoneControlOrDisable() || !canSendEnergy() || ServerHelper.isClientWorld(worldObj)) {
			return 0;
		}
		return sendEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {

		return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {

		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {

		return 0;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {

		return true;
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (frequency == -1 || !redstoneControlOrDisable() || !canSendFluid() || ServerHelper.isClientWorld(worldObj) || resource == null) {
			return 0;
		}
		return sendFluid(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		return TEProps.EMPTY_TANK_INFO;
	}

	/* IPortableData */
	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		if (!canPlayerAccess(player.getCommandSenderName())) {
			return;
		}
		RedstoneControlHelper.getControlFromNBT(tag);

		frequency = tag.getInteger("Frequency");
		modeItem = tag.getByte("ModeItems");
		modeFluid = tag.getByte("ModeFluid");
		modeEnergy = tag.getByte("ModeEnergy");

		markDirty();
		sendUpdatePacket(Side.CLIENT);
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		if (!canPlayerAccess(player.getCommandSenderName())) {
			return;
		}
		RedstoneControlHelper.setItemStackTagRS(tag, this);

		tag.setInteger("Frequency", frequency);
		tag.setByte("ModeItems", modeItem);
		tag.setByte("ModeFluid", modeFluid);
		tag.setByte("ModeEnergy", modeEnergy);
	}

	/* ISecurable */
	@Override
	public boolean setAccess(AccessMode access) {

		this.access = access;
		sendUpdatePacket(Side.SERVER);

		if (ServerHelper.isClientWorld(worldObj)) {
			setTileInfo(-1);
		}
		return true;
	}

	/* ISidedInventory */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		return SLOTS;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {

		if (frequency == -1 || !redstoneControlOrDisable() || !canSendItems() || inventory[0] != null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {

		return false;
	}

}
