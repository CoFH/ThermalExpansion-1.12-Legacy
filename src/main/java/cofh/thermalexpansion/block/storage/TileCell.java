package cofh.thermalexpansion.block.storage;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.core.network.PacketCoFHBase;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class TileCell extends TilePowered implements ITickable, IEnergyProvider {

	public static final int[] CAPACITY = { 1, 4, 9, 16, 25 };
	public static final byte[] DEFAULT_SIDES = { 2, 1, 1, 1, 1, 1 };

	public static final int[] SEND = { 1, 4, 9, 16, 25 };
	public static final int[] RECV = { 1, 4, 9, 16, 25 };

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

		String category = "Storage.Cell";
		BlockCell.enable = ThermalExpansion.CONFIG.get(category, "Enable", true);
	}

	private int compareTracker;
	private int meterTracker;
	private int outputTracker;

	public int amountRecv;
	public int amountSend;

	public TileCell() {

		energyStorage = new EnergyStorage(getCapacity(0));
		setDefaultSides();
		enableAutoOutput = true;
	}

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

		int curLevel = this.level;

		if (super.setLevel(level)) {
			energyStorage.setCapacity(getCapacity(level));
			amountRecv = amountRecv * RECV[level] / RECV[curLevel];
			amountSend = amountSend * SEND[level] / SEND[curLevel];

			if (isCreative) {
				energyStorage.setEnergyStored(energyStorage.getMaxEnergyStored());
			}
			return true;
		}
		return false;
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
			updateTrackers();
		}
	}

	/* COMMON METHODS */
	public static int getCapacity(int level) {

		return CAPACITY[MathHelper.clamp(level, 0, 4)];
	}

	public int getScaledEnergyStored(int scale) {

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

	protected void updateTrackers() {

		int curScale = getScaledEnergyStored(15);
		if (curScale != compareTracker) {
			compareTracker = curScale;
			callNeighborTileChange();
		}
		curScale = Math.min(8, getScaledEnergyStored(9));
		if (meterTracker != curScale) {
			meterTracker = curScale;
			sendTilePacket(Side.CLIENT);
		}
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
		amountRecv = nbt.getInteger("Recv");
		amountSend = nbt.getInteger("Send");

		energyStorage = new EnergyStorage(getCapacity(level));
		energyStorage.readFromNBT(nbt);
		meterTracker = Math.min(8, getScaledEnergyStored(9));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackOut", outputTracker);
		nbt.setInteger("Recv", amountRecv);
		nbt.setInteger("Send", amountSend);
		return nbt;
	}

	/* NETWORK METHODS */

	/* CLIENT -> SERVER */
	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addInt(MathHelper.clamp(amountRecv, 0, RECV[level]));
		payload.addInt(MathHelper.clamp(amountSend, 0, SEND[level]));

		return payload;
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		amountRecv = payload.getInt();
		amountSend = payload.getInt();
	}

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addInt(amountRecv);
		payload.addInt(amountSend);

		return payload;
	}

	@Override
	public PacketCoFHBase getTilePacket() {

		PacketCoFHBase payload = super.getTilePacket();

		payload.addInt(amountRecv);
		payload.addInt(amountSend);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		amountRecv = payload.getInt();
		amountSend = payload.getInt();
	}

	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		amountRecv = payload.getInt();
		amountSend = payload.getInt();
	}

	/* IEnergyReceiver */
	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {

		if (from == null || sideCache[from.ordinal()] == 2) {
			if (isCreative) {
				return maxExtract;
			}
			return energyStorage.extractEnergy(Math.min(maxExtract, amountSend), simulate);
		}
		return 0;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {

		if (from == null || sideCache[from.ordinal()] == 1) {
			if (isCreative) {
				return maxReceive;
			}
			return energyStorage.receiveEnergy(Math.min(maxReceive, amountRecv), simulate);
		}
		return 0;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {

		return isCreative ? energyStorage.getMaxEnergyStored() : energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {

		return energyStorage.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {

		return true;
	}

	/* IReconfigurableSides */
	@Override
	public final boolean decrSide(int side) {

		sideCache[side] += getNumConfig(side) - 1;
		sideCache[side] %= getNumConfig(side);
		sendConfigPacket();
		return true;
	}

	@Override
	public final boolean incrSide(int side) {

		sideCache[side] += 1;
		sideCache[side] %= getNumConfig(side);
		sendConfigPacket();
		return true;
	}

	@Override
	public boolean setSide(int side, int config) {

		if (sideCache[side] == config || config >= getNumConfig(side)) {
			return false;
		}
		sideCache[side] = (byte) config;
		sendConfigPacket();
		return true;
	}

	@Override
	public int getNumConfig(int side) {

		return 3;
	}

	/* ISidedTexture */
	@Override
	public int getNumPasses() {

		return 4;
	}

	@Override
	public TextureAtlasSprite getTexture(int side, int pass) {

		if (pass == 0) {
			return TETextures.CELL_CENTER_1;
		} else if (pass == 1) {
			return isCreative ? TETextures.CELL_SIDE_C : TETextures.CELL_SIDE[level];
		} else if (pass == 2) {
			return TETextures.CELL_CONFIG[sideCache[side]];
		}
		if (side != facing) {
			return TETextures.CONFIG_NONE;
		}
		return isCreative ? TETextures.CELL_METER_C :TETextures.CELL_METER[Math.min(8, getScaledEnergyStored(9))];
	}

	/* CAPABILITIES */
	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		if (capability == CapabilityEnergy.ENERGY) {
			return CapabilityEnergy.ENERGY.cast(new net.minecraftforge.energy.IEnergyStorage() {
				@Override
				public int receiveEnergy(int maxReceive, boolean simulate) {

					return TileCell.this.receiveEnergy(from, maxReceive, simulate);
				}

				@Override
				public int extractEnergy(int maxExtract, boolean simulate) {

					return TileCell.this.extractEnergy(from, maxExtract, simulate);
				}

				@Override
				public int getEnergyStored() {

					return TileCell.this.getEnergyStored(from);
				}

				@Override
				public int getMaxEnergyStored() {

					return TileCell.this.getMaxEnergyStored(from);
				}

				@Override
				public boolean canExtract() {

					return true;
				}

				@Override
				public boolean canReceive() {

					return true;
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
