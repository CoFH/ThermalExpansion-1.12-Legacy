package cofh.thermalexpansion.block;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;
import cofh.api.tileentity.IEnergyInfo;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.EnergyHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public abstract class TilePowered extends TileReconfigurable implements IEnergyInfo, IEnergyReceiver {

	protected EnergyStorage energyStorage = new EnergyStorage(0);

	protected boolean hasEnergy(int energy) {

		return energyStorage.getEnergyStored() >= energy;
	}

	protected int getEnergySpace() {

		return energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
	}

	protected boolean hasChargeSlot() {

		return true;
	}

	protected void chargeEnergy() {

		if (!hasChargeSlot()) {
			return;
		}
		int chargeSlot = getChargeSlot();

		if (EnergyHelper.isEnergyContainerItem(inventory[chargeSlot])) {
			int energyRequest = Math.min(energyStorage.getMaxReceive(), energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored());
			energyStorage.receiveEnergy(((IEnergyContainerItem) inventory[chargeSlot].getItem()).extractEnergy(inventory[chargeSlot], energyRequest, false), false);
			if (inventory[chargeSlot].stackSize <= 0) {
				inventory[chargeSlot] = null;
			}
		}
	}

	public int getChargeSlot() {

		return inventory.length - 1;
	}

	public final void setEnergyStored(int quantity) {

		energyStorage.setEnergyStored(quantity);
	}

	/* GUI METHODS */
	public IEnergyStorage getEnergyStorage() {

		return energyStorage;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		energyStorage.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		energyStorage.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addInt(energyStorage.getEnergyStored());

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addBool(isActive);
		payload.addInt(energyStorage.getMaxEnergyStored());
		payload.addInt(energyStorage.getEnergyStored());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		isActive = payload.getBool();
		energyStorage.setCapacity(payload.getInt());
		energyStorage.setEnergyStored(payload.getInt());
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

	/* IEnergyInfo */
	@Override
	public int getInfoEnergyPerTick() {

		return 0;
	}

	@Override
	public int getInfoMaxEnergyPerTick() {

		return 0;
	}

	@Override
	public int getInfoEnergyStored() {

		return energyStorage.getEnergyStored();
	}

	@Override
	public int getInfoMaxEnergyStored() {

		return energyStorage.getMaxEnergyStored();
	}

	/* IEnergyReceiver */
	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {

		return energyStorage.receiveEnergy(maxReceive, simulate);
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

}
