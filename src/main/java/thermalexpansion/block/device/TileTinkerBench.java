package thermalexpansion.block.device;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyStorage;
import cofh.core.CoFHProps;
import cofh.network.CoFHPacket;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import thermalexpansion.block.TileInventory;

public class TileTinkerBench extends TileInventory implements IEnergyHandler {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileTinkerBench.class, "thermalexpansion.Workbench");
	}

	EnergyStorage energyStorage;

	public TileTinkerBench() {

		inventory = new ItemStack[18];
		energyStorage = new EnergyStorage(12000, 20 * CoFHProps.TIME_CONSTANT);
	}

	@Override
	public boolean canUpdate() {

		return false;
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.device." + BlockDevice.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return BlockDevice.Types.PLACE_HOLDER.ordinal();
	}

	public IEnergyStorage getEnergyStorage() {

		return energyStorage;
	}

	/* NETWORK METHODS */

	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		energyStorage.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		energyStorage.writeToNBT(nbt);
	}

	/* IEnergyHandler */
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {

		return energyStorage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {

		return 0;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {

		return from.ordinal() != 1;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {

		return energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {

		return energyStorage.getMaxEnergyStored();
	}

}
