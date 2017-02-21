package cofh.thermalexpansion.block.storage;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TilePowered;
import cofh.thermalexpansion.gui.client.storage.GuiCell;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class TileCell extends TilePowered implements ITickable, IEnergyProvider {

	public static int[] CAPACITY = { 1, 4, 9, 16, 25 };
	public static byte[] DEFAULT_SIDES = { 2, 1, 1, 1, 1, 1 };

	public static int[] SEND = { 1, 4, 9, 16, 25 };
	public static int[] RECV = { 1, 4, 9, 16, 25 };

	static {
		for (int i = 0; i < CAPACITY.length; i++) {
			CAPACITY[i] *= 2000000;
			SEND[i] *= 1000;
			RECV[i] *= 1000;
		}
	}

	private static boolean enableSecurity = true;

	public static void initialize() {

		GameRegistry.registerTileEntity(TileCell.class, "thermalexpansion.storage_cell");

		config();
	}

	public static void config() {

		String comment = "Enable this to allow for Energy Cells to be securable.";
		enableSecurity = ThermalExpansion.CONFIG.get("Security", "Cell.Securable", enableSecurity, comment);
	}

	private int compareTracker;
	private int meterTracker;
	private int outputTracker;

	public int amountRecv;
	public int amountSend;

	private EnergyStorage energyStorage = new EnergyStorage(getCapacity(0));

	@Override
	public String getTileName() {

		return "tile.thermalexpansion.storage.cell.name";
	}

	@Override
	public int getType() {

		return 0;
	}

	@Override
	public int getComparatorInputOverride() {

		return compareTracker;
	}

	@Override
	public int getLightValue() {

		return Math.min(8, getScaledEnergyStored(9));
	}

	@Override
	public byte[] getDefaultSides() {

		return DEFAULT_SIDES.clone();
	}

	@Override
	public boolean enableSecurity() {

		return enableSecurity;
	}

	@Override
	protected boolean setLevel(int level) {

		if (level >= 0) {
			if (level > 4) {
				level = 4;
			}
			this.level = (byte) level;
		}
		energyStorage.setCapacity(getCapacity(level));
		return true;
	}

	@Override
	protected void setLevelFlags() {

		hasAutoInput = false;
		hasAutoOutput = false;

		hasRedstoneControl = false;
		hasAdvRedstoneControl = false;

		switch (level) {
			default:            // Creative
			case 2:             // Reinforced
				hasRedstoneControl = true;
			case 0:             // Basic;
				hasAutoOutput = true;
		}
	}

	@Override
	protected boolean readPortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		amountSend = tag.getInteger("Send");
		amountRecv = tag.getInteger("Recv");

		return super.readPortableTagInternal(player, tag);
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		tag.setInteger("Send", amountSend);
		tag.setInteger("Recv", amountRecv);

		return super.writePortableTagInternal(player, tag);
	}

	@Override
	public void update() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		if (redstoneControlOrDisable()) {
			transferEnergy();
		}
		if (timeCheck()) {
			int curScale = getScaledEnergyStored(15);
			if (curScale != compareTracker) {
				compareTracker = curScale;
				callNeighborTileChange();
			}
		}
	}

	/* COMMON METHODS */
	protected static int getCapacity(int level) {

		return CAPACITY[MathHelper.clamp(level, 0, 4)];
	}

	protected int getScaledEnergyStored(int scale) {

		return energyStorage.getEnergyStored() * scale / energyStorage.getMaxEnergyStored();
	}

	protected void transferEnergy() {

		for (int i = outputTracker; i < 6 && energyStorage.getEnergyStored() > 0; i++) {
			if (sideCache[i] == 2) {
				energyStorage.modifyEnergyStored(-EnergyHelper.insertEnergyIntoAdjacentEnergyReceiver(this, EnumFacing.VALUES[i], Math.min(amountSend, energyStorage.getEnergyStored()), false));
			}
		}
		for (int i = 0; i < outputTracker && energyStorage.getEnergyStored() > 0; i++) {
			if (sideCache[i] == 2) {
				energyStorage.modifyEnergyStored(-EnergyHelper.insertEnergyIntoAdjacentEnergyReceiver(this, EnumFacing.VALUES[i], Math.min(amountSend, energyStorage.getEnergyStored()), false));
			}
		}
		outputTracker++;
		outputTracker %= 6;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiCell(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		outputTracker = nbt.getByte("Tracker");
		amountSend = nbt.getInteger("Send");
		amountRecv = nbt.getInteger("Recv");

		energyStorage = new EnergyStorage(getCapacity(level));
		energyStorage.readFromNBT(nbt);
		meterTracker = Math.min(8, getScaledEnergyStored(9));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackOut", outputTracker);
		nbt.setInteger("Send", amountSend);
		nbt.setInteger("Recv", amountRecv);
		return nbt;
	}

	/* IEnergyReceiver */
	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {

		if (from == null || sideCache[from.ordinal()] == 2) {
			return energyStorage.extractEnergy(Math.min(maxExtract, amountSend), simulate);
		}
		return 0;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {

		if (from == null || sideCache[from.ordinal()] == 1) {
			return energyStorage.receiveEnergy(Math.min(maxReceive, amountRecv), simulate);
		}
		return 0;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {

		return energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {

		return energyStorage.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {

		return energyStorage.getMaxEnergyStored() > 0;
	}

	/* IReconfigurableSides */
	@Override
	public final boolean decrSide(int side) {

		sideCache[side] += getNumConfig(side) - 1;
		sideCache[side] %= getNumConfig(side);
		sendUpdatePacket(Side.SERVER);
		return true;
	}

	@Override
	public final boolean incrSide(int side) {

		sideCache[side] += 1;
		sideCache[side] %= getNumConfig(side);
		sendUpdatePacket(Side.SERVER);
		return true;
	}

	@Override
	public boolean setSide(int side, int config) {

		if (sideCache[side] == config || config >= getNumConfig(side)) {
			return false;
		}
		sideCache[side] = (byte) config;
		sendUpdatePacket(Side.SERVER);
		return true;
	}

	@Override
	public int getNumConfig(int side) {

		return 3;
	}

	/* ISidedTexture */
	@Override
	public TextureAtlasSprite getTexture(int side, int layer, int pass) {

		if (layer == 0) {
			return TETextures.CELL_SIDE[level];
		} else {
			return TETextures.CELL_CONFIG[sideCache[side]];
		}
		// return TETextures.CELL_METER[Math.min(8, getScaledEnergyStored(9))];
	}

}
