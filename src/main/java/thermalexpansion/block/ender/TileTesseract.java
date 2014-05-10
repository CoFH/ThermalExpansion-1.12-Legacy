package thermalexpansion.block.ender;

import cofh.api.energy.IEnergyHandler;
import cofh.api.tileentity.ISecureTile;
import cofh.api.transport.IEnderAttuned;
import cofh.core.CoFHProps;
import cofh.network.CoFHPacket;
import cofh.network.CoFHTileInfoPacket;
import cofh.network.ITileInfoPacketHandler;
import cofh.network.PacketHandler;
import cofh.util.BlockHelper;
import cofh.util.CoreUtils;
import cofh.util.EnergyHelper;
import cofh.util.FluidHelper;
import cofh.util.RegistryEnderAttuned;
import cofh.util.ServerHelper;
import cofh.util.StringHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
import thermalexpansion.block.TileRSInventory;
import thermalexpansion.core.TEProps;
import thermalexpansion.util.Utils;

public class TileTesseract extends TileRSInventory implements ISecureTile, ISidedInventory, IFluidHandler, IEnergyHandler, ITileInfoPacketHandler, IEnderAttuned {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileTesseract.class, "cofh.thermalexpansion.Tesseract");
		guiId = ThermalExpansion.proxy.registerGui("Tesseract", "ender", true);
	}

	protected static int guiId;
	protected static final int[] SLOTS = { 0 };

	public enum PacketInfoID {
		NAME_LIST, ALTER_NAME_LIST, TILE_INFO
	}

	String owner = CoFHProps.DEFAULT_OWNER;
	public int frequency = -1;

	public byte modeItem = (byte) TransferMode.RECV.ordinal();
	public byte modeFluid = (byte) TransferMode.RECV.ordinal();
	public byte modeEnergy = (byte) TransferMode.RECV.ordinal();
	public AccessMode access = AccessMode.PUBLIC;

	public boolean isActive = false;

	int itemTrackerAdjacent;
	int itemTrackerRemote;
	int fluidTrackerAdjacent;
	int fluidTrackerRemote;
	int energyTrackerAdjacent;
	int energyTrackerRemote;

	boolean cached = false;
	IEnergyHandler[] adjacentEnergyHandlers = new IEnergyHandler[6];
	IFluidHandler[] adjacentFluidHandlers = new IFluidHandler[6];

	/* Client-Side Only */
	public boolean canAccess = true;

	public TileTesseract() {

		inventory = new ItemStack[1];
	}

	@Override
	public void blockBroken() {

		removeFromRegistry();
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
	public boolean sendRedstoneUpdates() {

		return true;
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.tesseract.name";
	}

	@Override
	public int getType() {

		return 0;
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
				worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
			}
		}
	}

	public void setTileInfo(int theFreq) {

		if (ServerHelper.isClientWorld(worldObj)) {
			frequency = theFreq;
			PacketHandler.sendToServer(CoFHTileInfoPacket.newPacket(this).addByte(PacketInfoID.TILE_INFO.ordinal()).addByte(modeItem).addByte(modeFluid).addByte(modeEnergy).addByte(access.ordinal()).addInt(theFreq));
		}
	}

	public void addEntry(int theFreq, String freqName) {

		if (ServerHelper.isClientWorld(worldObj)) {
			PacketHandler.sendToServer(CoFHTileInfoPacket.newPacket(this).addByte(PacketInfoID.ALTER_NAME_LIST.ordinal()).addBool(false).addString(access.isPublic() ? "_public_" : owner.toLowerCase()).addString(String.valueOf(theFreq)).addString(freqName));
		}
	}

	public void removeEntry(int theFreq, String freqName) {

		if (ServerHelper.isClientWorld(worldObj)) {
			PacketHandler.sendToServer(CoFHTileInfoPacket.newPacket(this).addByte(PacketInfoID.ALTER_NAME_LIST.ordinal()).addBool(true).addString(access.isPublic() ? "_public_" : owner.toLowerCase()).addString(String.valueOf(theFreq)).addString(freqName));
		}
	}

	public List<IEnderAttuned> getValidItemOutputs() {

		List<IEnderAttuned> theTeleports = RegistryEnderAttuned.getLinkedItemOutputs(this);
		List<IEnderAttuned> validOutputs = new LinkedList<IEnderAttuned>();

		if (theTeleports == null) {
			return validOutputs;
		}
		IEnderAttuned curThing;

		for (int i = 0; i < theTeleports.size(); ++i) {
			curThing = theTeleports.get(i);

			if (curThing.currentlyValidToReceiveItems(this)) {
				validOutputs.add(curThing);
			}
		}
		return validOutputs;
	}

	public List<IEnderAttuned> getValidFluidOutputs() {

		List<IEnderAttuned> theTeleports = RegistryEnderAttuned.getLinkedFluidOutputs(this);
		List<IEnderAttuned> validOutputs = new LinkedList<IEnderAttuned>();

		if (theTeleports == null) {
			return validOutputs;
		}
		IEnderAttuned curThing;

		for (int i = 0; i < theTeleports.size(); ++i) {
			curThing = theTeleports.get(i);

			if (curThing.currentlyValidToReceiveFluid(this)) {
				validOutputs.add(curThing);
			}
		}
		return validOutputs;
	}

	public List<IEnderAttuned> getValidEnergyOutputs() {

		List<IEnderAttuned> theTeleports = RegistryEnderAttuned.getLinkedEnergyOutputs(this);
		List<IEnderAttuned> validOutputs = new LinkedList<IEnderAttuned>();

		if (theTeleports == null) {
			return validOutputs;
		}
		IEnderAttuned curThing;

		for (int i = 0; i < theTeleports.size(); ++i) {
			curThing = theTeleports.get(i);

			if (curThing.currentlyValidToReceiveEnergy(this)) {
				validOutputs.add(curThing);
			}
		}
		return validOutputs;
	}

	public void addToRegistry() {

		RegistryEnderAttuned.add(this);
	}

	public void removeFromRegistry() {

		RegistryEnderAttuned.remove(this);
	}

	@Override
	public void onChunkUnload() {

		removeFromRegistry();
	}

	@Override
	public boolean openGui(EntityPlayer player) {

		if (CoreUtils.isFakePlayer(player)) {
			return true;
		}
		if (canPlayerAccess(player.getCommandSenderName())) {
			sendNamesList((EntityPlayerMP) player);
			player.openGui(ThermalExpansion.instance, guiId, worldObj, xCoord, yCoord, zCoord);
			return true;
		}
		if (ServerHelper.isServerWorld(worldObj)) {
			player.addChatMessage(new ChatComponentText(StringHelper.localize("message.cofh.secure1") + " " + owner + "! " + StringHelper.localize("message.cofh.secure2")));
		}
		return true;
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

	@Override
	public String getOwnerString() {

		return access.isPublic() ? "_public_" : owner;
	}

	/* SEND METHODS */
	void sendItem(ItemStack item) {

		List<IEnderAttuned> validOutputs = getValidItemOutputs();

		for (int i = itemTrackerRemote; i < validOutputs.size() && item != null && item.stackSize > 0; i++) {
			item = validOutputs.get(i).receiveItem(item);
		}
		for (int i = 0; i < validOutputs.size() && i < itemTrackerRemote && item != null && item.stackSize > 0; i++) {
			item = validOutputs.get(i).receiveItem(item);
		}
		itemTrackerRemote = incrRemoteTracker(itemTrackerRemote, validOutputs.size());

		if (item != null && item.stackSize > 0) {
			inventory[0] = item;
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		}
	}

	int sendFluid(FluidStack fluid, boolean doFill) {

		List<IEnderAttuned> validOutputs = getValidFluidOutputs();
		int startAmount = fluid.amount;

		if (validOutputs.size() <= 0 || startAmount <= 0) {
			return 0;
		}
		for (int i = fluidTrackerRemote; i < validOutputs.size() && fluid != null && fluid.amount > 0; i++) {
			fluid = validOutputs.get(i).receiveFluid(fluid, doFill);
		}
		for (int i = 0; i < validOutputs.size() && i < fluidTrackerRemote && fluid != null && fluid.amount > 0; i++) {
			fluid = validOutputs.get(i).receiveFluid(fluid, doFill);
		}
		fluidTrackerRemote = incrRemoteTracker(fluidTrackerRemote, validOutputs.size());
		return startAmount - fluid.amount;
	}

	int sendEnergy(int energy, boolean simulate) {

		List<IEnderAttuned> validOutputs = getValidEnergyOutputs();
		int startAmount = energy;

		if (validOutputs.size() <= 0 || startAmount <= 0) {
			return 0;
		}
		for (int i = energyTrackerRemote; i < validOutputs.size() && energy > 0; i++) {
			energy = validOutputs.get(i).receiveEnergy(energy, simulate);
		}
		for (int i = 0; i < validOutputs.size() && i < energyTrackerRemote && energy > 0; i++) {
			energy = validOutputs.get(i).receiveEnergy(energy, simulate);
		}
		energyTrackerRemote = incrRemoteTracker(energyTrackerRemote, validOutputs.size());
		return startAmount - energy;
	}

	/* RECEIVE METHODS */
	@Override
	public ItemStack receiveItem(ItemStack stack) {

		for (int side = itemTrackerAdjacent; side < 6 && stack != null && stack.stackSize > 0; side++) {
			if (Utils.isAdjacentInventory(this, side)) {
				stack.stackSize = addToAdjInventory(this, side, stack.copy());
			}
		}
		for (int side = 0; side < itemTrackerAdjacent && stack != null && stack.stackSize > 0; side++) {
			if (Utils.isAdjacentInventory(this, side)) {
				stack.stackSize = addToAdjInventory(this, side, stack.copy());
			}
		}
		incrItemTrackerAdjacent();
		return stack;
	}

	@Override
	public FluidStack receiveFluid(FluidStack fluid, boolean doFill) {

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

	@Override
	public int receiveEnergy(int energy, boolean simulate) {

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

	/* TRACKER METHODS */
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

	public void incItemMode() {

		modeItem++;
		modeItem %= 4;
	}

	public void decItemMode() {

		if (modeItem == 0) {
			modeItem = 4;
		}
		modeItem--;

	}

	public void incFluidMode() {

		modeFluid++;
		modeFluid %= 4;
	}

	public void decFluidMode() {

		if (modeFluid == 0) {
			modeFluid = 4;
		}
		modeFluid--;

	}

	public void incEnergyMode() {

		modeEnergy++;
		modeEnergy %= 4;
	}

	public void decEnergyMode() {

		if (modeEnergy == 0) {
			modeEnergy = 4;
		}
		modeEnergy--;

	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		access = AccessMode.values()[nbt.getByte("Access")];
		owner = nbt.getString("Owner");

		frequency = nbt.getInteger("Frequency");
		isActive = nbt.getBoolean("Active");

		modeItem = nbt.getByte("Item.Mode");
		modeFluid = nbt.getByte("Fluid.Mode");
		modeEnergy = nbt.getByte("Energy.Mode");

		itemTrackerAdjacent = nbt.getInteger("Item.Adj");
		itemTrackerRemote = nbt.getInteger("Item.Rem");
		fluidTrackerAdjacent = nbt.getInteger("Fluid.Adj");
		fluidTrackerRemote = nbt.getInteger("Fluid.Rem");
		energyTrackerAdjacent = nbt.getInteger("Energy.Adj");
		energyTrackerRemote = nbt.getInteger("Energy.Rem");

		addToRegistry();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Access", (byte) access.ordinal());
		nbt.setString("Owner", owner);

		nbt.setInteger("Frequency", frequency);
		nbt.setBoolean("Active", isActive);

		nbt.setByte("Item.Mode", modeItem);
		nbt.setByte("Fluid.Mode", modeFluid);
		nbt.setByte("Energy.Mode", modeEnergy);

		nbt.setInteger("Item.Adj", itemTrackerAdjacent);
		nbt.setInteger("Item.Rem", itemTrackerRemote);
		nbt.setInteger("Fluid.Adj", fluidTrackerAdjacent);
		nbt.setInteger("Fluid.Rem", fluidTrackerRemote);
		nbt.setInteger("Energy.Adj", energyTrackerAdjacent);
		nbt.setInteger("Energy.Rem", energyTrackerRemote);

	}

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();

		payload.addBool(isActive);
		payload.addByte(modeItem);
		payload.addByte(modeFluid);
		payload.addByte(modeEnergy);
		payload.addByte((byte) access.ordinal());
		payload.addInt(frequency);
		payload.addString(owner);

		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		isActive = payload.getBool();
		modeItem = payload.getByte();
		modeFluid = payload.getByte();
		modeEnergy = payload.getByte();
		access = ISecureTile.AccessMode.values()[payload.getByte()];
		frequency = payload.getInt();
		owner = payload.getString();

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		// worldObj.updateAllLightTypes(xCoord, yCoord, zCoord);
	}

	/* ITileInfoPacketHandler */
	@Override
	public void handleTileInfoPacket(CoFHPacket payload, boolean isServer, EntityPlayer thePlayer) {

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
			access = ISecureTile.AccessMode.values()[payload.getByte()];
			frequency = payload.getInt();
			addToRegistry();

			isActive = frequency == -1 ? false : true;

			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());

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

		CoFHPacket myPacket = CoFHTileInfoPacket.newPacket(this);
		if (curList != null) {
			myPacket.addByte((byte) PacketInfoID.NAME_LIST.ordinal());
			myPacket.addInt(curList.size());

			for (Property curProp : curList.values()) {
				myPacket.addString(curProp.getName());
				myPacket.addString(curProp.getString());
			}
		} else {
			myPacket.addByte((byte) PacketInfoID.NAME_LIST.ordinal());
			myPacket.addInt(0);
		}
		PacketHandler.sendTo(myPacket, thePlayer);
	}

	/* GUI METHODS */
	@Override
	public void receiveGuiNetworkData(int i, int j) {

		if (j == 0) {
			canAccess = false;
		} else {
			canAccess = true;
		}
	}

	@Override
	public void sendGuiNetworkData(Container container, ICrafting player) {

		int access = 0;
		if (canPlayerAccess(((EntityPlayer) player).getDisplayName())) {
			access = 1;
		}
		player.sendProgressBarUpdate(container, 0, access);
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

	/* ISidedInventory */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		return SLOTS;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {

		if (frequency == -1 || !canSendItems() || !redstoneControlOrDisable() || inventory[0] != null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {

		return false;
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (frequency == -1 || !canSendFluid() || !redstoneControlOrDisable() || ServerHelper.isClientWorld(worldObj) || resource == null) {
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

	/* IEnergyHandler */
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {

		if (frequency == -1 || !canSendEnergy() || !redstoneControlOrDisable() || ServerHelper.isClientWorld(worldObj)) {
			return 0;
		}
		return sendEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {

		return 0;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {

		return true;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {

		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {

		return 0;
	}

	/* ISecureTile */
	@Override
	public boolean setAccess(AccessMode access) {

		this.access = access;
		sendUpdatePacket(Side.SERVER);

		if (ServerHelper.isClientWorld(worldObj)) {
			setTileInfo(-1);
		}
		return true;
	}

	@Override
	public AccessMode getAccess() {

		return access;
	}

	@Override
	public boolean setOwnerName(String name) {

		if (owner.equals(CoFHProps.DEFAULT_OWNER)) {
			owner = name;
			return true;
		}
		return false;
	}

	@Override
	public String getOwnerName() {

		return owner;
	}

	/* IEnderAttuned */
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
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
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
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		return true;
	}

	@Override
	public boolean canSendItems() {

		return modeItem == TransferMode.SEND.ordinal() || modeItem == TransferMode.SENDRECV.ordinal();
	}

	@Override
	public boolean canSendFluid() {

		return modeFluid == TransferMode.SEND.ordinal() || modeFluid == TransferMode.SENDRECV.ordinal();
	}

	@Override
	public boolean canSendEnergy() {

		return modeEnergy == TransferMode.SEND.ordinal() || modeEnergy == TransferMode.SENDRECV.ordinal();
	}

	@Override
	public boolean canReceiveItems() {

		return modeItem == TransferMode.RECV.ordinal() || modeItem == TransferMode.SENDRECV.ordinal();
	}

	@Override
	public boolean canReceiveFluid() {

		return modeFluid == TransferMode.RECV.ordinal() || modeFluid == TransferMode.SENDRECV.ordinal();
	}

	@Override
	public boolean canReceiveEnergy() {

		return modeEnergy == TransferMode.RECV.ordinal() || modeEnergy == TransferMode.SENDRECV.ordinal();
	}

	@Override
	public boolean currentlyValidToReceiveItems(IEnderAttuned asker) {

		return !asker.equals(this) && redstoneControlOrDisable() && canReceiveItems();
	}

	@Override
	public boolean currentlyValidToReceiveFluid(IEnderAttuned asker) {

		return !asker.equals(this) && redstoneControlOrDisable() && canReceiveFluid();
	}

	@Override
	public boolean currentlyValidToReceiveEnergy(IEnderAttuned asker) {

		return !asker.equals(this) && redstoneControlOrDisable() && canReceiveEnergy();
	}

	@Override
	public boolean currentlyValidToSendItems(IEnderAttuned asker) {

		return !asker.equals(this) && redstoneControlOrDisable() && canSendItems();
	}

	@Override
	public boolean currentlyValidToSendFluid(IEnderAttuned asker) {

		return !asker.equals(this) && redstoneControlOrDisable() && canSendFluid();
	}

	@Override
	public boolean currentlyValidToSendEnergy(IEnderAttuned asker) {

		return !asker.equals(this) && redstoneControlOrDisable() && canSendEnergy();
	}

	public enum TransferMode {
		SEND, RECV, SENDRECV, BLOCKED
	}

}
