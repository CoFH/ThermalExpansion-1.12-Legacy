package thermalexpansion.block.cell;

import cofh.render.IconRegistry;
import cofh.util.ServerHelper;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public class TileCellCreative extends TileCell {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileCellCreative.class, "thermalexpansion.CellCreative");
	}

	public static final byte[] DEFAULT_SIDES = { 1, 1, 1, 1, 1, 1 };

	public TileCellCreative() {

	}

	public TileCellCreative(int metadata) {

		super(metadata);
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

	/* IReconfigurableSides */
	@Override
	public int getNumConfig(int side) {

		return 2;
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
