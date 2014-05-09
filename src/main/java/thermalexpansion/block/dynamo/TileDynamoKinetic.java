package thermalexpansion.block.dynamo;

import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;

public class TileDynamoKinetic extends TileDynamoBase implements IPowerReceptor {

	public static void initialize() {

		// guiIds[BlockDynamo.Types.KINETIC.ordinal()] = ThermalExpansion.proxy.registerGui("DynamoKinetic", "dynamo", "TEBase", null, true);
		// GameRegistry.registerTileEntity(TileDynamoKinetic.class, "cofh.thermalexpansion.DynamoKinetic");
	}

	protected PowerHandler powerHandler;

	public TileDynamoKinetic() {

		super();

		powerHandler = new PowerHandler(this, PowerHandler.Type.STORAGE);
		powerHandler.configure(2, 16, 2000, 1000);
	}

	@Override
	public int getType() {

		// return BlockDynamo.Types.KINETIC.ordinal();
		return 0;
	}

	@Override
	protected boolean canGenerate() {

		return fuelRF > 0 ? true : powerHandler.getEnergyStored() >= 1;
	}

	@Override
	protected void generate() {

		if (fuelRF <= 0) {
			fuelRF += 10 * powerHandler.useEnergy(1, 1000, true);
		}
		int energy = calcEnergy();
		energyStorage.modifyEnergyStored(energy);
		fuelRF -= energy;
	}

	@Override
	public IIcon getActiveIcon() {

		return FluidRegistry.LAVA.getIcon();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		powerHandler.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		powerHandler.writeToNBT(nbt);
	}

	/* IPowerReceptor */
	@Override
	public PowerReceiver getPowerReceiver(ForgeDirection side) {

		return powerHandler.getPowerReceiver();
	}

	@Override
	public void doWork(PowerHandler workProvider) {

	}

	@Override
	public World getWorld() {

		return worldObj;
	}

}
