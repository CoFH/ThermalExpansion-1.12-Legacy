package thermalexpansion.block.machine;

import cofh.api.core.IAugmentable;
import cofh.network.CoFHPacket;
import cofh.network.CoFHTileInfoPacket;
import cofh.network.ITileInfoPacketHandler;
import cofh.network.ITilePacketHandler;
import cofh.network.PacketHandler;
import cofh.render.IconRegistry;
import cofh.util.BlockHelper;
import cofh.util.ServerHelper;
import cofh.util.TimeTracker;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidContainerRegistry;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TileReconfigurableInventory;
import thermalexpansion.core.TEProps;
import thermalexpansion.util.Utils;

public abstract class TileMachineBase extends TileReconfigurableInventory implements ISidedInventory, ITilePacketHandler, ITileInfoPacketHandler, IAugmentable {

	public static class SideConfig {

		public int numGroup;
		public int[][] slotGroups;
		public boolean[] allowInsertion;
		public boolean[] allowExtraction;
		public int[] sideTex;
	}

	protected static final SideConfig[] defaultSideConfig = new SideConfig[BlockMachine.Types.values().length];
	protected static final int[] lightValue = { 14, 0, 0, 15, 15, 0, 0, 14, 0, 0, 7 };

	protected static final int RATE = 25;
	protected static final int MAX_FLUID_SMALL = FluidContainerRegistry.BUCKET_VOLUME * 4;
	protected static final int MAX_FLUID_LARGE = FluidContainerRegistry.BUCKET_VOLUME * 10;

	SideConfig sideConfig;
	TimeTracker tracker = new TimeTracker();

	boolean wasActive;

	int processMax;
	int processRem;

	/* Augment Variables */
	int level = 0;
	ItemStack[] augments = new ItemStack[3];
	boolean[] augmentStatus = new boolean[3];

	public boolean augmentRSControl = true;
	public boolean augmentReconfigSides = true;
	public boolean augmentAutoTransfer = true;
	int processMod = 1;
	int secondaryChance = 100;

	public TileMachineBase() {

		super();

		sideConfig = defaultSideConfig[getType()];
	}

	public int getMaxInputSlot() {

		return 0;
	}

	public void updateIfChanged(boolean curActive) {

		if (curActive != isActive && isActive == true) {
			sendUpdatePacket(Side.CLIENT);
		} else if (tracker.hasDelayPassed(worldObj, 200) && wasActive) {
			wasActive = false;
			sendUpdatePacket(Side.CLIENT);
		}
	}

	@Override
	public int getLightValue() {

		return isActive ? lightValue[getType()] : 0;
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.machine." + BlockMachine.NAMES[getType()] + ".name";
	}

	/* NETWORK METHODS */
	public CoFHPacket getGuiPacket() {

		CoFHPacket payload = CoFHTileInfoPacket.newPacket(this);
		payload.addByte(TEProps.PacketID.GUI.ordinal());
		payload.addBool(isActive);
		payload.addInt(processMax);
		payload.addInt(processRem);
		payload.addInt(processMod);

		payload.addBool(augmentRSControl);
		payload.addBool(augmentReconfigSides);
		return payload;
	}

	public CoFHPacket getFluidPacket() {

		CoFHPacket payload = CoFHTileInfoPacket.newPacket(this);
		payload.addByte(TEProps.PacketID.FLUID.ordinal());
		return payload;
	}

	public CoFHPacket getModePacket() {

		CoFHPacket payload = CoFHTileInfoPacket.newPacket(this);
		payload.addByte(TEProps.PacketID.MODE.ordinal());
		return payload;
	}

	protected void handleGuiPacket(CoFHPacket payload) {

		isActive = payload.getBool();
		processMax = payload.getInt();
		processRem = payload.getInt();
		processMod = payload.getInt();

		augmentRSControl = payload.getBool();
		augmentReconfigSides = payload.getBool();
	}

	protected void handleFluidPacket(CoFHPacket payload) {

	}

	protected void handleModePacket(CoFHPacket payload) {

	}

	protected void handleAugmentPacket(CoFHPacket payload) {

	}

	public void sendFluidPacket() {

		PacketHandler.sendToDimension(getFluidPacket(), worldObj.provider.dimensionId);
	}

	public void sendModePacket() {

		if (ServerHelper.isClientWorld(worldObj)) {
			PacketHandler.sendToServer(getModePacket());
		}
	}

	/* ITileInfoPacketHandler */
	@Override
	public void handleTileInfoPacket(CoFHPacket payload, boolean isServer, EntityPlayer thePlayer) {

		switch (TEProps.PacketID.values()[payload.getByte()]) {
		case GUI:
			handleGuiPacket(payload);
			return;
		case FLUID:
			handleFluidPacket(payload);
			return;
		case MODE:
			handleModePacket(payload);
			return;
		case AUGMENT:
			handleAugmentPacket(payload);
			return;
		default:
		}
	}

	/* GUI METHODS */
	@Override
	public boolean openGui(EntityPlayer player) {

		player.openGui(ThermalExpansion.instance, 0, worldObj, xCoord, yCoord, zCoord);
		return true;
	}

	public int getScaledProgress(int scale) {

		if (!isActive || processMax <= 0 || processRem <= 0) {
			return 0;
		}
		return scale * (processMax - processRem) / processMax;
	}

	@Override
	public void sendGuiNetworkData(Container container, ICrafting iCrafting) {

		if (iCrafting instanceof EntityPlayer) {
			if (ServerHelper.isServerWorld(worldObj)) {
				PacketHandler.sendTo(getGuiPacket(), (EntityPlayer) iCrafting);
			}
		}
	}

	public boolean canAcceptItem(ItemStack stack, int slot, int side) {

		return true;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		readAugmentsFromNBT(nbt);
		augmentTile();

		processMax = nbt.getInteger("ProcMax");
		processRem = nbt.getInteger("ProcRem");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		writeAugmentsToNBT(nbt);

		nbt.setInteger("ProcMax", processMax);
		nbt.setInteger("ProcRem", processRem);
	}

	public void readAugmentsFromNBT(NBTTagCompound nbt) {

		level = nbt.getInteger("Level");

		NBTTagList list = nbt.getTagList("Augments", 10);
		augments = new ItemStack[augments.length + level];
		augmentStatus = new boolean[augments.length];
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int slot = tag.getInteger("Slot");

			if (slot >= 0 && slot < augments.length) {
				augments[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}

	public void writeAugmentsToNBT(NBTTagCompound nbt) {

		nbt.setInteger("Level", level);

		if (augments.length <= 0) {
			return;
		}
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < augments.length; i++) {
			if (augments[i] != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("Slot", i);
				augments[i].writeToNBT(tag);
				list.appendTag(tag);
			}
		}
		nbt.setTag("Augments", list);
	}

	/* IReconfigurableFacing */
	@Override
	public boolean setFacing(int side) {

		if (side < 0 || side > 5) {
			return false;
		}
		sideCache[side] = 0;
		sideCache[BlockHelper.SIDE_LEFT[side]] = 1;
		sideCache[BlockHelper.SIDE_OPPOSITE[side]] = 1;
		facing = (byte) side;
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	/* IReconfigurableSides */
	@Override
	public int getNumConfig(int side) {

		return sideConfig.numGroup;
	}

	/* ISidedBlockTexture */
	@Override
	public IIcon getTexture(int side, int pass) {

		if (pass == 0) {
			if (side == 0) {
				return IconRegistry.getIcon("MachineBottom");
			} else if (side == 1) {
				return IconRegistry.getIcon("MachineTop");
			}
			return side != facing ? IconRegistry.getIcon("MachineSide") : isActive ? IconRegistry.getIcon("MachineActive", getType()) : IconRegistry.getIcon(
					"MachineFace", getType());
		} else if (side < 6) {
			return IconRegistry.getIcon(TEProps.textureSelection, sideConfig.sideTex[sideCache[side]]);
		}
		return IconRegistry.getIcon("MachineSide");
	}

	/* ISidedInventory */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		return sideConfig.slotGroups[sideCache[side]];
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {

		return sideConfig.allowInsertion[sideCache[side]] ? canAcceptItem(stack, slot, side) : false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {

		return sideConfig.allowExtraction[sideCache[side]];
	}

	/* IUpgradableTile */
	@Override
	public ItemStack[] getAugmentSlots() {

		return augments;
	}

	@Override
	public boolean[] getAugmentStatus() {

		return augmentStatus;
	}

	@Override
	public boolean augmentTile() {

		for (int i = 0; i < augments.length; i++) {
			if (Utils.isAugmentItem(augments[i])) {

			}
		}

		return false;
	}

}
