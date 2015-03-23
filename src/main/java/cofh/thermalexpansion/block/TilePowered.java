package cofh.thermalexpansion.block;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.MathHelper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TilePowered extends TileRSControl implements IEnergyReceiver {

	protected EnergyStorage energyStorage = new EnergyStorage(0);

	public int getChargeSlot() {

		return inventory.length - 1;
	}

	public boolean hasChargeSlot() {

		return true;
	}

	protected boolean hasEnergy(int energy) {

		return energyStorage.getEnergyStored() >= energy;
	}

	protected boolean drainEnergy(int energy) {

		return hasEnergy(energy) && energyStorage.extractEnergy(energy, false) == energy;
	}

	protected void chargeEnergy() {

		int chargeSlot = getChargeSlot();

		if (hasChargeSlot() && EnergyHelper.isEnergyContainerItem(inventory[chargeSlot])) {
			int energyRequest = Math.min(energyStorage.getMaxReceive(), energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored());
			energyStorage.receiveEnergy(((IEnergyContainerItem) inventory[chargeSlot].getItem()).extractEnergy(inventory[chargeSlot], energyRequest, false),
					false);
			if (inventory[chargeSlot].stackSize <= 0) {
				inventory[chargeSlot] = null;
			}
		}
	}

	public final void setEnergyStored(int quantity) {

		energyStorage.setEnergyStored(quantity);
	}

	/* GUI METHODS */
	public IEnergyStorage getEnergyStorage() {

		return energyStorage;
	}

	public int getScaledEnergyStored(int scale) {

		return MathHelper.round((long) energyStorage.getEnergyStored() * scale / energyStorage.getMaxEnergyStored());
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

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addInt(energyStorage.getEnergyStored());

		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		int energy = payload.getInt();

		if (!isServer) {
			energyStorage.setEnergyStored(energy);
		}
	}

	/* IEnergyHandler */
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {

		return energyStorage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {

		return energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {

		return energyStorage.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {

		return energyStorage.getMaxEnergyStored() > 0;
	}

}
