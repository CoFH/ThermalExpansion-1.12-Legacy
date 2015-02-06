package cofh.thermalexpansion.block.cell;

import cofh.core.render.IconRegistry;
import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public class TileCellCreative extends TileCell {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileCellCreative.class, "thermalexpansion.CellCreative");
	}

	public static final byte[] DEFAULT_SIDES = { 1, 1, 1, 1, 1, 1 };

	public TileCellCreative() {

		energyStorage.setEnergyStored(energyStorage.getMaxEnergyStored());
	}

	public TileCellCreative(int metadata) {

		super(metadata);
		energyStorage.setEnergyStored(energyStorage.getMaxEnergyStored());
	}

	@Override
	public byte[] getDefaultSides() {

		return DEFAULT_SIDES.clone();
	}

	@Override
	public void updateEntity() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		if (!cached) {
			onNeighborBlockChange();
		}
		if (redstoneControlOrDisable()) {
			for (int i = 0; i < 6; i++) {
				transferEnergy(i);
			}
		}
	}

	@Override
	protected void transferEnergy(int bSide) {

		if (sideCache[bSide] != 1) {
			return;
		}
		if (adjacentHandlers[bSide] == null) {
			return;
		}
		adjacentHandlers[bSide].receiveEnergy(ForgeDirection.VALID_DIRECTIONS[bSide ^ 1], Math.min(energySend, energyStorage.getEnergyStored()), false);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		energyStorage.setEnergyStored(energyStorage.getMaxEnergyStored());
	}

	/* IEnergyHandler */
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {

		if (from == ForgeDirection.UNKNOWN || sideCache[from.ordinal()] == 2) {
			return Math.min(maxReceive, energyReceive);
		}
		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {

		if (from == ForgeDirection.UNKNOWN || sideCache[from.ordinal()] == 1) {
			return Math.min(maxExtract, energySend);
		}
		return 0;
	}

	/* ISidedTexture */
	@Override
	public IIcon getTexture(int side, int pass) {

		if (pass == 0) {
			return IconRegistry.getIcon("FluidRedstone");
		} else if (pass == 1) {
			return IconRegistry.getIcon("Cell", type * 2);
		} else if (pass == 2) {
			return IconRegistry.getIcon(BlockCell.textureSelection, sideCache[side]);
		}
		return side != facing ? IconRegistry.getIcon(BlockCell.textureSelection, 0) : IconRegistry.getIcon("CellMeterCreative");
	}

}
