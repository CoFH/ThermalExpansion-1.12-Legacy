package thermalexpansion.block.dynamo;

import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;

public class TileDynamoManual extends TileDynamoBase {

	public static void initialize() {

		// GameRegistry.registerTileEntity(TileDynamoManual.class, "thermalexpansion.DynamoManual");
	}

	@Override
	public int getType() {

		// return BlockDynamo.Types.MANUAL.ordinal();
		return 0;
	}

	@Override
	protected boolean canGenerate() {

		return fuelRF > 0;
	}

	@Override
	protected void generate() {

		int energy = calcEnergy();
		energyStorage.modifyEnergyStored(energy);
		fuelRF -= energy;
	}

	@Override
	public IIcon getActiveIcon() {

		return FluidRegistry.LAVA.getIcon();
	}

	/* IEnergyHandler */
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {

		return from.ordinal() == facing ? 0 : from == ForgeDirection.UNKNOWN ? energyStorage.receiveEnergy(maxReceive, simulate) : 0;
	}

}
