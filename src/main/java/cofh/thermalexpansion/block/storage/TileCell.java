package cofh.thermalexpansion.block.storage;

import cofh.api.item.IUpgradeItem;
import cofh.api.item.IUpgradeItem.UpgradeType;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.EnergyHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.impl.EnergyStorage;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TilePowered;
import cofh.thermalexpansion.gui.client.storage.GuiCell;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileCell extends TilePowered implements ITickable, IEnergyProvider {

	public static final int CAPACITY_BASE = 2000000;
	public static final int XFER_BASE = 1000;
	public static final int[] XFER_SCALE = { 1, 4, 9, 16, 25 };

	public static final int[] CAPACITY = { 1, 4, 9, 16, 25 };
	public static final byte[] DEFAULT_SIDES = { 2, 1, 1, 1, 1, 1 };
	public static final byte[] CREATIVE_SIDES = { 2, 2, 2, 2, 2, 2 };

	public static final int[] RECV = { 1, 4, 9, 16, 25 };
	public static final int[] SEND = { 1, 4, 9, 16, 25 };

	private static boolean enableSecurity = true;

	public static void initialize() {

		GameRegistry.registerTileEntity(TileCell.class, "thermalexpansion:storage_cell");

		config();
	}

	public static void config() {

		String category = "Storage.Cell";
		String comment = "If TRUE, Energy Cells are securable.";
		enableSecurity = ThermalExpansion.CONFIG.get(category, "Securable", enableSecurity, comment);

		comment = "If TRUE, Energy Cells are enabled.";
		BlockCell.enable = ThermalExpansion.CONFIG.get(category, "Enable", true, comment);

		comment = "If TRUE, 'Classic' Crafting is enabled - Non-Creative Upgrade Kits WILL NOT WORK in a Crafting Grid.";
		BlockCell.enableClassicRecipes = ThermalExpansion.CONFIG.get(category, "ClassicCrafting", BlockCell.enableClassicRecipes, comment);

		comment = "If TRUE, Energy Cells can be upgraded in a Crafting Grid using Kits. If Classic Crafting is enabled, only the Creative Conversion Kit may be used in this fashion.";
		BlockCell.enableUpgradeKitCrafting = ThermalExpansion.CONFIG.get(category, "UpgradeKitCrafting", BlockCell.enableUpgradeKitCrafting, comment);

		int capacity = CAPACITY_BASE;
		comment = "Adjust this value to change the amount of Energy (in RF) stored by a Basic Cell. This base value will scale with block level.";
		capacity = ThermalExpansion.CONFIG.getConfiguration().getInt("BaseCapacity", category, capacity, capacity / 5, capacity * 5, comment);

		int recv = XFER_BASE;
		comment = "Adjust this value to change the amount of Energy (in RF/t) that can be received by a Basic Cell. This base value will scale with block level.";
		recv = ThermalExpansion.CONFIG.getConfiguration().getInt("BaseReceive", category, recv, recv / 10, recv * 10, comment);

		int send = XFER_BASE;
		comment = "Adjust this value to change the amount of Energy (in RF/t) that can be sent by a Basic Cell. This base value will scale with block level.";
		send = ThermalExpansion.CONFIG.getConfiguration().getInt("BaseSend", category, send, send / 10, send * 10, comment);

		for (int i = 0; i < CAPACITY.length; i++) {
			CAPACITY[i] *= capacity;
			RECV[i] *= recv;
			SEND[i] *= send;
		}
	}

	private int compareTracker;
	private int meterTracker;
	private int outputTracker;

	public byte enchantHolding;
	public int amountRecv;
	public int amountSend;

	public TileCell() {

		super();

		energyStorage = new EnergyStorage(getCapacity(0, 0));
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

		return MathHelper.clamp(getScaledEnergyStored(9), 0, 8);
	}

	@Override
	public byte[] getDefaultSides() {

		return DEFAULT_SIDES.clone();
	}

	@Override
	public boolean enableSecurity() {

		return enableSecurity;
	}

	/* IUpgradeable */
	@Override
	public boolean canUpgrade(ItemStack upgrade) {

		if (!AugmentHelper.isUpgradeItem(upgrade)) {
			return false;
		}
		UpgradeType uType = ((IUpgradeItem) upgrade.getItem()).getUpgradeType(upgrade);
		int uLevel = ((IUpgradeItem) upgrade.getItem()).getUpgradeLevel(upgrade);

		switch (uType) {
			case INCREMENTAL:
				if (uLevel == level + 1) {
					return !BlockCell.enableClassicRecipes;
				}
				break;
			case FULL:
				if (uLevel > level) {
					return !BlockCell.enableClassicRecipes;
				}
				break;
			case CREATIVE:
				return !isCreative;
		}
		return false;
	}

	@Override
	public boolean installUpgrade(ItemStack upgrade) {

		boolean isCreative = this.isCreative;
		boolean installUpgrade = super.installUpgrade(upgrade);
		if (installUpgrade && !isCreative && this.isCreative) {
			for (int i = 0; i < 6; i++) {
				sideCache[i] = 2;
			}
			sendTilePacket(Side.CLIENT);
			callNeighborTileChange();
		}
		return installUpgrade;
	}

	@Override
	protected boolean setLevel(int level) {

		int curLevel = this.level;

		if (super.setLevel(level)) {
			energyStorage.setCapacity(getCapacity(level, enchantHolding));
			amountRecv = amountRecv * XFER_SCALE[level] / XFER_SCALE[curLevel];
			amountSend = amountSend * XFER_SCALE[level] / XFER_SCALE[curLevel];

			if (isCreative) {
				energyStorage.setEnergyStored(energyStorage.getMaxEnergyStored());
			}
			if (world != null) {
				updateTrackers();
			}
			return true;
		}
		return false;
	}

	@Override
	protected boolean readPortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		amountRecv = tag.getInteger("Recv") * XFER_SCALE[level] / 1000;
		amountSend = tag.getInteger("Send") * XFER_SCALE[level] / 1000;

		return super.readPortableTagInternal(player, tag);
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		tag.setInteger("Recv", amountRecv * 1000 / XFER_SCALE[level]);
		tag.setInteger("Send", amountSend * 1000 / XFER_SCALE[level]);

		return super.writePortableTagInternal(player, tag);
	}

	@Override
	protected int getNumAugmentSlots(int level) {

		return 0;
	}

	@Override
	public void update() {

		if (redstoneControlOrDisable()) {
			transferEnergy();
		}
		if (timeCheck()) {
			updateTrackers();
		}
	}

	/* COMMON METHODS */
	public static int getCapacity(int level, int enchant) {

		return CAPACITY[MathHelper.clamp(level, 0, 4)] + (CAPACITY[MathHelper.clamp(level, 0, 4)] * enchant) / 2;
	}

	public int getScaledEnergyStored(int scale) {

		return energyStorage.getEnergyStored() * scale / energyStorage.getMaxEnergyStored();
	}

	protected void transferEnergy() {

		for (int i = outputTracker; i < 6 && energyStorage.getEnergyStored() > 0; i++) {
			if (sideCache[i] == 2) {
				if (isCreative) {
					EnergyHelper.insertEnergyIntoAdjacentEnergyReceiver(this, EnumFacing.VALUES[i], amountSend, false);
				} else {
					energyStorage.modifyEnergyStored(-EnergyHelper.insertEnergyIntoAdjacentEnergyReceiver(this, EnumFacing.VALUES[i], Math.min(amountSend, energyStorage.getEnergyStored()), false));
				}
			}
		}
		for (int i = 0; i < outputTracker && energyStorage.getEnergyStored() > 0; i++) {
			if (sideCache[i] == 2) {
				if (isCreative) {
					EnergyHelper.insertEnergyIntoAdjacentEnergyReceiver(this, EnumFacing.VALUES[i], amountSend, false);
				} else {
					energyStorage.modifyEnergyStored(-EnergyHelper.insertEnergyIntoAdjacentEnergyReceiver(this, EnumFacing.VALUES[i], Math.min(amountSend, energyStorage.getEnergyStored()), false));
				}
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
			updateLighting();
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

		enchantHolding = nbt.getByte("EncHolding");

		super.readFromNBT(nbt);

		outputTracker = nbt.getByte("Tracker");
		amountRecv = nbt.getInteger("Recv");
		amountSend = nbt.getInteger("Send");

		energyStorage = new EnergyStorage(getCapacity(level, enchantHolding));
		energyStorage.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("EncHolding", enchantHolding);
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

		payload.addByte(enchantHolding);
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
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketCoFHBase payload) {

		super.handleTilePacket(payload);

		enchantHolding = payload.getByte();
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

	/* ISidedInventory */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		return CoreProps.EMPTY_INVENTORY;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {

		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {

		return false;
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
		return isCreative ? TETextures.CELL_METER_C : TETextures.CELL_METER[MathHelper.clamp(getScaledEnergyStored(9), 0, 8)];
	}

	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return capability != CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, from));
	}

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
