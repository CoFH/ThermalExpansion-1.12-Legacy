package thermalexpansion.block;

import cofh.block.TileCoFHBase;
import cofh.network.CoFHPacket;
import cofh.network.CoFHTileInfoPacket;
import cofh.network.ITileInfoPacketHandler;
import cofh.network.ITilePacketHandler;
import cofh.network.PacketHandler;
import cofh.util.BlockHelper;
import cofh.util.ServerHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.GuiHandler;
import thermalexpansion.util.Utils;

public abstract class TileTEBase extends TileCoFHBase implements IInventory, ITileInfoPacketHandler, ITilePacketHandler {

	public ItemStack[] inventory = new ItemStack[0];
	protected String invName = "";

	TileEntity curTile;

	public boolean transferItem(int slot, int amount, int side) {

		if (inventory[slot] == null || slot > inventory.length) {
			return false;
		}
		ItemStack stack = inventory[slot].copy();
		amount = Math.min(amount, stack.stackSize);
		stack.stackSize = amount;

		curTile = BlockHelper.getAdjacentTileEntity(this, side);
		/* Add to Adjacent Inventory */
		if (Utils.isInventory(curTile, side)) {
			inventory[slot].stackSize -= amount - Utils.addToInventory(curTile, side, stack);
			if (inventory[slot].stackSize <= 0) {
				inventory[slot] = null;
			}
			return true;
		}
		/* Add to Adjacent Pipe */
		if (Utils.isPipeTile(curTile)) {
			inventory[slot].stackSize -= Utils.addToPipeTile(curTile, side, stack);
			if (inventory[slot].stackSize <= 0) {
				inventory[slot] = null;
			}
			return true;
		}
		return false;
	}

	public boolean setInvName(String name) {

		if (name.isEmpty()) {
			return false;
		}
		invName = name;
		return true;
	}

	/* GUI METHODS */
	@Override
	public int getInvSlotCount() {

		return inventory.length;
	}

	public boolean hasGui() {

		return true;
	}

	@Override
	public boolean openGui(EntityPlayer player) {

		player.openGui(ThermalExpansion.instance, GuiHandler.TILE_ID, worldObj, xCoord, yCoord, zCoord);
		return hasGui();
	}

	@Override
	public void sendGuiNetworkData(Container container, ICrafting iCrafting) {

		if (iCrafting instanceof EntityPlayer) {
			CoFHPacket guiPacket = getGuiPacket();
			if (guiPacket != null) {
				PacketHandler.sendTo(getGuiPacket(), (EntityPlayer) iCrafting);
			}
		}
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		if (nbt.hasKey("Name")) {
			invName = nbt.getString("Name");
		}
		readInventoryFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);
		nbt.setString("Version", ThermalExpansion.version);

		if (!invName.isEmpty()) {
			nbt.setString("Name", invName);
		}
		writeInventoryToNBT(nbt);
	}

	public void readInventoryFromNBT(NBTTagCompound nbt) {

		NBTTagList list = nbt.getTagList("Inventory", 10);
		inventory = new ItemStack[inventory.length];
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int slot = tag.getInteger("Slot");

			if (slot >= 0 && slot < inventory.length) {
				inventory[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}

	public void writeInventoryToNBT(NBTTagCompound nbt) {

		if (inventory.length <= 0) {
			return;
		}
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("Slot", i);
				inventory[i].writeToNBT(tag);
				list.appendTag(tag);
			}
		}
		nbt.setTag("Inventory", list);
	}

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();
		payload.addString(invName);
		return payload;
	}

	public CoFHPacket getGuiPacket() {

		CoFHPacket payload = CoFHTileInfoPacket.newPacket(this);
		payload.addByte(TEProps.PacketID.GUI.ordinal());
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

	}

	protected void handleFluidPacket(CoFHPacket payload) {

	}

	protected void handleModePacket(CoFHPacket payload) {

	}

	public void sendFluidPacket() {

		PacketHandler.sendToDimension(getFluidPacket(), worldObj.provider.dimensionId);
	}

	public void sendModePacket() {

		if (ServerHelper.isClientWorld(worldObj)) {
			PacketHandler.sendToServer(getModePacket());
		}
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		if (ServerHelper.isClientWorld(worldObj)) {
			invName = payload.getString();
		} else {
			payload.getString();
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
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
		default:
		}
	}

	/* IInventory */
	@Override
	public int getSizeInventory() {

		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {

		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		if (inventory[slot] == null) {
			return null;
		}
		if (inventory[slot].stackSize <= amount) {
			amount = inventory[slot].stackSize;
		}
		ItemStack stack = inventory[slot].splitStack(amount);

		if (inventory[slot].stackSize <= 0) {
			inventory[slot] = null;
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {

		if (inventory[slot] == null) {
			return null;
		}
		ItemStack stack = inventory[slot];
		inventory[slot] = null;
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		inventory[slot] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {

		return invName.isEmpty() ? getName() : invName;
	}

	@Override
	public boolean hasCustomInventoryName() {

		return !invName.isEmpty();
	}

	@Override
	public int getInventoryStackLimit() {

		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {

		return isUseable(player);
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return true;
	}

	/* Energy Config Class */
	public static class EnergyConfig {

		public int minPower = 8;
		public int maxPower = 80;
		public int maxEnergy = 40000;
		public int minPowerLevel = 1 * maxEnergy / 10;
		public int maxPowerLevel = 9 * maxEnergy / 10;
		public int energyRamp = maxPowerLevel / maxPower;

		public EnergyConfig() {

		}

		public EnergyConfig(EnergyConfig config) {

			this.minPower = config.minPower;
			this.maxPower = config.maxPower;
			this.maxEnergy = config.maxEnergy;
			this.minPowerLevel = config.minPowerLevel;
			this.maxPowerLevel = config.maxPowerLevel;
			this.energyRamp = config.energyRamp;
		}

		public EnergyConfig copy() {

			return new EnergyConfig(this);
		}

		public boolean setParams(int minPower, int maxPower, int maxEnergy) {

			this.minPower = minPower;
			this.maxPower = maxPower;
			this.maxEnergy = maxEnergy;
			this.maxPowerLevel = maxEnergy * 8 / 10;
			this.energyRamp = maxPower > 0 ? maxPowerLevel / maxPower : 0;
			this.minPowerLevel = minPower * energyRamp;

			return true;
		}

		public boolean setParamsPower(int maxPower) {

			return setParams(maxPower / 4, maxPower, maxPower * 1200);
		}

		public boolean setParamsPower(int maxPower, int scale) {

			return setParams(maxPower / 4, maxPower, maxPower * 1200 * scale);
		}

		public boolean setParamsEnergy(int maxEnergy) {

			return setParams(maxEnergy / 4800, maxEnergy / 1200, maxEnergy);
		}

		public boolean setParamsEnergy(int maxEnergy, int scale) {

			maxEnergy *= scale;
			return setParams(maxEnergy / 4800, maxEnergy / 1200, maxEnergy);
		}
	}

	/* Side Config Class */
	public static class SideConfig {

		public int numGroup;
		public int[][] slotGroups;
		public boolean[] allowInsertion;
		public boolean[] allowExtraction;
		public int[] sideTex;
	}

	/* Augment Helpers */
	public static String DYNAMO_EFFICIENCY = "dynamoEfficiency";
	public static String DYNAMO_OUTPUT = "dynamoOutput";
	public static String DYNAMO_THROTTLE = "dynamoThrottle";
	public static String ENDER_ENERGY = "enderEnergy";
	public static String ENDER_FLUID = "enderFluid";
	public static String ENDER_ITEM = "enderItem";
	public static String GENERAL_AUTO_TRANSFER = "autoTransfer";
	public static String GENERAL_RECONFIG_SIDES = "reconfigSides";
	public static String GENERAL_RS_CONTROL = "rsControl";
	public static String MACHINE_SECONDARY = "machineSecondary";
	public static String MACHINE_SPEED = "machineSpeed";

	public static byte NUM_DYNAMO_EFFICIENCY = 3;
	public static byte NUM_DYNAMO_OUTPUT = 3;

	public static byte NUM_MACHINE_SECONDARY = 3;
	public static byte NUM_MACHINE_SPEED = 3;

	public static final int[] MACHINE_SPEED_PROCESS_MOD = { 1, 2, 4, 8 };
	public static final int[] MACHINE_SPEED_ENERGY_MOD = { 1, 3, 8, 20 };

}
