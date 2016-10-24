package cofh.thermalexpansion.block.ender;

import codechicken.lib.util.BlockUtils;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.inventory.IInventoryConnection;
import cofh.api.transport.IEnderEnergyHandler;
import cofh.api.transport.IEnderFluidHandler;
import cofh.api.transport.IEnderItemHandler;
import cofh.core.CoFHProps;
import cofh.core.RegistryEnderAttuned;
import cofh.core.network.PacketCoFHBase;
import cofh.core.network.PacketHandler;
import cofh.core.network.PacketTileInfo;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.RedstoneControlHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileRSControl;
import cofh.thermalexpansion.gui.GuiHandler;
import cofh.thermalexpansion.gui.client.ender.GuiTesseract;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.util.Utils;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;
import java.util.Locale;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileTesseract extends TileRSControl implements ITickable, IEnergyHandler, IEnderEnergyHandler, IEnderFluidHandler, IEnderItemHandler, IFluidHandler,
		IInventoryConnection, ISidedInventory {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileTesseract.class, "thermalexpansion.Tesseract");
	}

	protected static final int[] SLOTS = { 0 };

	public enum PacketInfoID {
		ALTER_NAME_LIST, TILE_INFO
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
	IEnergyReceiver[] adjacentEnergyReceivers = new IEnergyReceiver[6];
	IFluidHandler[] adjacentFluidHandlers = new IFluidHandler[6];

	public int frequency = -1;
	public byte modeItem = (byte) TransferMode.RECV.ordinal();
	public byte modeFluid = (byte) TransferMode.RECV.ordinal();
	public byte modeEnergy = (byte) TransferMode.RECV.ordinal();

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
	public boolean shouldRenderInPass(int pass) {

		return pass == 0;
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
	public void onNeighborTileChange(BlockPos pos) {

		super.onNeighborTileChange(pos);
		updateAdjacentHandler(pos);
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
	public void update() {

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
			if (EnergyHelper.isEnergyReceiverFromSide(tile, EnumFacing.VALUES[i ^ 1])) {
				adjacentEnergyReceivers[i] = (IEnergyReceiver) tile;
			} else {
				adjacentEnergyReceivers[i] = null;
			}
		}
		cached = true;
	}

	protected void updateAdjacentHandler(BlockPos pos) {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		int side = BlockHelper.determineAdjacentSide(this, pos);

		TileEntity tile = worldObj.getTileEntity(pos);
		if (tile instanceof TileTesseract) {
			return;
		}
		if (FluidHelper.isFluidHandler(tile)) {
			adjacentFluidHandlers[side] = (IFluidHandler) tile;
		} else {
			adjacentFluidHandlers[side] = null;
		}
		if (EnergyHelper.isEnergyReceiverFromSide(tile, EnumFacing.VALUES[side ^ 1])) {
			adjacentEnergyReceivers[side] = (IEnergyReceiver) tile;
		} else {
			adjacentEnergyReceivers[side] = null;
		}
	}

	public boolean isOwner(String username) {

		return username != null && username.equals(owner);
	}

	public void addEntry(int theFreq, String freqName) {

		if (ServerHelper.isClientWorld(worldObj)) {
			PacketHandler.sendToServer(PacketTileInfo.newPacket(this).addByte(PacketInfoID.ALTER_NAME_LIST.ordinal()).addBool(false)
					.addString(getChannelString()).addInt(theFreq).addString(freqName));
		}
	}

	public void removeEntry(int theFreq, String freqName) {

		if (ServerHelper.isClientWorld(worldObj)) {
			PacketHandler.sendToServer(PacketTileInfo.newPacket(this).addByte(PacketInfoID.ALTER_NAME_LIST.ordinal()).addBool(true)
					.addString(getChannelString()).addInt(theFreq).addString(freqName));
		}
	}

	public void addToRegistry() {

		if (ServerHelper.isServerWorld(worldObj)) {
			RegistryEnderAttuned.getRegistry().add(this);
		}
	}

	public void removeFromRegistry() {

		if (ServerHelper.isServerWorld(worldObj)) {
			RegistryEnderAttuned.getRegistry().remove(this);
		}
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

		List<IEnderEnergyHandler> validOutputs = RegistryEnderAttuned.getRegistry().getLinkedEnergyOutputs(this);
		int startAmount = energy;

		if (startAmount <= 0) {
			return 0;
		}
		if (validOutputs != null && validOutputs.size() > 0) {
			isSendingEnergy = true;
			IEnderEnergyHandler handler;
			energyTrackerRemote++;
			energyTrackerRemote %= validOutputs.size();

			for (int i = energyTrackerRemote; i < validOutputs.size() && energy > 0; i++) {
				handler = validOutputs.get(i);
				if (handler.canReceiveEnergy()) {
					energy = handler.receiveEnergy(energy, simulate);
				}
				if (energy <= 0) {
					energyTrackerRemote = i;
				}
			}
			for (int i = 0; i < validOutputs.size() && i < energyTrackerRemote && energy > 0; i++) {
				handler = validOutputs.get(i);
				if (handler.canReceiveEnergy()) {
					energy = handler.receiveEnergy(energy, simulate);
				}
				if (energy <= 0) {
					energyTrackerRemote = i;
				}
			}
		}
		isSendingEnergy = false;
		return startAmount - energy;
	}

	int sendFluid(FluidStack fluid, boolean doFill) {

		List<IEnderFluidHandler> validOutputs = RegistryEnderAttuned.getRegistry().getLinkedFluidOutputs(this);
		int startAmount = fluid.amount;

		if (startAmount <= 0) {
			return 0;
		}
		if (validOutputs != null && validOutputs.size() > 0) {
			isSendingFluid = true;
			IEnderFluidHandler handler;
			fluidTrackerRemote++;
			fluidTrackerRemote %= validOutputs.size();

			for (int i = fluidTrackerRemote; i < validOutputs.size() && fluid != null && fluid.amount > 0; i++) {
				handler = validOutputs.get(i);
				if (handler.canReceiveFluid()) {
					fluid = handler.receiveFluid(fluid, doFill);
				}
				if (fluid == null || fluid.amount <= 0) {
					fluidTrackerRemote = i;
				}
			}
			for (int i = 0; i < validOutputs.size() && i < fluidTrackerRemote && fluid != null && fluid.amount > 0; i++) {
				handler = validOutputs.get(i);
				if (handler.canReceiveFluid()) {
					fluid = handler.receiveFluid(fluid, doFill);
				}
				if (fluid == null || fluid.amount <= 0) {
					fluidTrackerRemote = i;
				}
			}
		}
		isSendingFluid = false;
		return startAmount - fluid.amount;
	}

	void sendItem(ItemStack item) {

		List<IEnderItemHandler> validOutputs = RegistryEnderAttuned.getRegistry().getLinkedItemOutputs(this);

		if (validOutputs != null && validOutputs.size() > 0) {
			isSendingItems = true;
			IEnderItemHandler handler;
			itemTrackerRemote++;
			itemTrackerRemote %= validOutputs.size();

			for (int i = itemTrackerRemote; i < validOutputs.size() && item != null && item.stackSize > 0; i++) {
				handler = validOutputs.get(i);
				if (handler.canReceiveItems()) {
					item = handler.receiveItem(item);
				}
				if (item == null || item.stackSize == 0) {
					itemTrackerRemote = i;
				}
			}
			for (int i = 0; i < validOutputs.size() && i < itemTrackerRemote && item != null && item.stackSize > 0; i++) {
				handler = validOutputs.get(i);
				if (handler.canReceiveItems()) {
					item = handler.receiveItem(item);
				}
				if (item == null || item.stackSize == 0) {
					itemTrackerRemote = i;
				}
			}
		}
		if (item != null && item.stackSize > 0) {
			inventory[0] = item;
			worldObj.notifyBlockOfStateChange(getPos(), getBlockType());
		}
		isSendingItems = false;
	}

	/* TRACKER METHODS */
	public void incrEnergyTrackerAdjacent() {

		energyTrackerAdjacent++;
		for (int side = energyTrackerAdjacent; side < 6; side++) {
			if (adjacentEnergyReceivers[side] != null) {
				energyTrackerAdjacent = side;
				return;
			}
		}
		energyTrackerAdjacent %= 6;
		for (int side = 0; side < energyTrackerAdjacent; side++) {
			if (adjacentEnergyReceivers[side] != null) {
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
			if (Utils.isAdjacentOutput(this, EnumFacing.VALUES[side])) {
				itemTrackerAdjacent = side;
				return;
			}
		}
		itemTrackerAdjacent %= 6;
		for (int side = 0; side < itemTrackerAdjacent; side++) {
			if (Utils.isAdjacentOutput(this, EnumFacing.VALUES[side])) {
				itemTrackerAdjacent = side;
				return;
			}
		}
		itemTrackerAdjacent = 0;
	}

	/* HELPER METHODS */
	public int addToAdjInventory(TileEntity tile, EnumFacing from, ItemStack stack) {

		TileEntity tileInventory = BlockHelper.getAdjacentTileEntity(tile, from);

		if (tileInventory instanceof TileTesseract) {
			return stack.stackSize;
		}
		return Utils.addToAdjacentInsertion(this, from, stack);
	}

	public boolean isAdjacentInventory(EnumFacing side) {

		TileEntity tile = BlockHelper.getAdjacentTileEntity(worldObj, getPos(), side);
		return !(tile instanceof TileTesseract) && Utils.isAccessibleOutput(tile, side);
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

	@Override
	protected boolean readPortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		rsMode = RedstoneControlHelper.getControlFromNBT(tag);

		frequency = tag.getInteger("Frequency");
		modeItem = tag.getByte("ModeItems");
		modeFluid = tag.getByte("ModeFluid");
		modeEnergy = tag.getByte("ModeEnergy");

		return true;
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		RedstoneControlHelper.setItemStackTagRS(tag, this);

		tag.setInteger("Frequency", frequency);
		tag.setByte("ModeItems", modeItem);
		tag.setByte("ModeFluid", modeFluid);
		tag.setByte("ModeEnergy", modeEnergy);

		return true;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiTesseract(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this, false, false);
	}

	@Override
	public boolean openGui(EntityPlayer player) {

		if (CoreUtils.isFakePlayer(player)) {
			return true;
		}
		if (canPlayerAccess(player)) {
			player.openGui(ThermalExpansion.instance, GuiHandler.TILE_ID, worldObj, getPos().getX(), getPos().getY(), getPos().getZ());
			return true;
		}
		if (ServerHelper.isServerWorld(worldObj)) {
			player.addChatMessage(new TextComponentTranslation("chat.cofh.secure", getOwnerName()));
		}
		return true;
	}

	@Override
	public void sendGuiNetworkData(Container container, IContainerListener player) {

		player.sendProgressBarUpdate(container, 0, canPlayerAccess(((EntityPlayer) player)) ? 1 : 0);
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
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

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
        return nbt;
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

		isActive = frequency != -1;
	}

	/* ITileInfoPacketHandler */
	@SuppressWarnings("unused")
	@Override
	public void handleTileInfoPacket(PacketCoFHBase payload, boolean isServer, EntityPlayer thePlayer) {

		switch (PacketInfoID.values()[payload.getByte()]) {
		case ALTER_NAME_LIST:
			boolean remove = payload.getBool();
			String channel = payload.getString();
			int freq = payload.getInt();
			String name = payload.getString();
			if (remove) {
				RegistryEnderAttuned.removeChannelFrequency(thePlayer, channel, freq);
			} else {
				RegistryEnderAttuned.updateChannelFrequency(thePlayer, channel, freq, name);
			}
			// FIXME: the below logic only covers a single World.
			// FIXME: players need to register to the channel handler to get these updates
			/*
			 * for (int i = 0; i < worldObj.playerEntities.size(); ++i) { EntityPlayer player = (EntityPlayer) worldObj.playerEntities.get(i); if
			 * (isUseable(player) && player.openContainer instanceof ContainerTEBase) { ContainerTEBase container = (ContainerTEBase) player.openContainer; if
			 * (container.baseTile == this) { if (remove) { RegistryEnderAttuned.removeChannelFrequency(player, channel, freq); } else {
			 * RegistryEnderAttuned.updateChannelFrequency(player, channel, freq, name); } } } }//
			 */

			return;
		case TILE_INFO:
			removeFromRegistry();
			modeItem = payload.getByte();
			modeFluid = payload.getByte();
			modeEnergy = payload.getByte();
			access = AccessMode.values()[payload.getByte()];
			frequency = payload.getInt();
			addToRegistry();

			isActive = frequency != -1;

            BlockUtils.fireBlockUpdate(getWorld(), getPos());
			callNeighborTileChange();
			return;
		}
	}

	/* IInventory */
	@Override
	public int getSizeInventory() {

		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {

		return null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {

		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		sendItem(stack);
	}

	/* IEnderAttuned */
	@Override
	public String getChannelString() {

		return access.isPublic() ? "_public_" : String.valueOf(owner.getName()).toLowerCase(Locale.US);
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

        BlockUtils.fireBlockUpdate(getWorld(), getPos());
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

        BlockUtils.fireBlockUpdate(getWorld(), getPos());
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
			if (adjacentEnergyReceivers[side] != null) {
				energy -= adjacentEnergyReceivers[side].receiveEnergy(EnumFacing.VALUES[side ^ 1], energy, simulate);
			}
		}
		for (int side = 0; side < energyTrackerAdjacent && side < 6 && energy > 0; side++) {
			if (adjacentEnergyReceivers[side] != null) {
				energy -= adjacentEnergyReceivers[side].receiveEnergy(EnumFacing.VALUES[side ^ 1], energy, simulate);
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
				fluid.amount -= adjacentFluidHandlers[side].fill(EnumFacing.VALUES[side ^ 1], fluid, doFill);
			}
		}
		for (int side = 0; side < fluidTrackerAdjacent && side < 6 && fluid.amount > 0; side++) {
			if (adjacentFluidHandlers[side] != null) {
				fluid.amount -= adjacentFluidHandlers[side].fill(EnumFacing.VALUES[side ^ 1], fluid, doFill);
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
            EnumFacing eSide = EnumFacing.VALUES[side];
			if (isAdjacentInventory(eSide)) {
				stack.stackSize = addToAdjInventory(this, eSide, stack.copy());
			}
		}
		for (int side = 0; side < itemTrackerAdjacent && stack != null && stack.stackSize > 0; side++) {
            EnumFacing eSide = EnumFacing.VALUES[side];
			if (isAdjacentInventory(eSide)) {
				stack.stackSize = addToAdjInventory(this, eSide, stack.copy());
			}
		}
		incrItemTrackerAdjacent();
		return stack;
	}

	/* IEnergyHandler */
	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {

		if (frequency == -1 || !redstoneControlOrDisable() || !canSendEnergy() || ServerHelper.isClientWorld(worldObj)) {
			return 0;
		}
		return sendEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {

		return 0;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {

		return 0;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {

		return 0;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {

		return true;
	}

	/* IFluidHandler */
	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {

		if (frequency == -1 || !redstoneControlOrDisable() || !canSendFluid() || ServerHelper.isClientWorld(worldObj) || resource == null) {
			return 0;
		}
		return sendFluid(resource, doFill);
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {

		return null;
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {

		return null;
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {

		return true;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {

		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {

		return CoFHProps.EMPTY_TANK_INFO;
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

	public void setAccessQuick(AccessMode access) {

		this.access = access;
	}

	/* IInventoryConnection */
	@Override
	public ConnectionType canConnectInventory(EnumFacing from) {

		return ConnectionType.FORCE;
	}

	/* ISidedInventory */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		if (frequency == -1 || !redstoneControlOrDisable() || !canSendItems() || inventory[0] != null) {
			return CoFHProps.EMPTY_INVENTORY;
		}
		return SLOTS;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {

        return !(frequency == -1 || !redstoneControlOrDisable() || !canSendItems() || inventory[0] != null);
    }

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {

		return false;
	}

}
