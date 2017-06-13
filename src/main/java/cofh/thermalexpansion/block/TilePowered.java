package cofh.thermalexpansion.block;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;
import cofh.api.tileentity.IEnergyInfo;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.EnergyHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

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
			if (inventory[chargeSlot].getCount() <= 0) {
				inventory[chargeSlot] = ItemStack.EMPTY;
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

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addBool(isActive);
		payload.addInt(energyStorage.getMaxEnergyStored());
		payload.addInt(energyStorage.getEnergyStored());

		return payload;
	}

	@Override
	public PacketCoFHBase getTilePacket() {

		PacketCoFHBase payload = super.getTilePacket();

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

	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		energyStorage.setEnergyStored(payload.getInt());
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

	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, from);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		if (capability == CapabilityEnergy.ENERGY) {
			return CapabilityEnergy.ENERGY.cast(new net.minecraftforge.energy.IEnergyStorage() {

				@Override
				public int receiveEnergy(int maxReceive, boolean simulate) {

					return TilePowered.this.receiveEnergy(from, maxReceive, simulate);
				}

				@Override
				public int extractEnergy(int maxExtract, boolean simulate) {

					return 0;
				}

				@Override
				public int getEnergyStored() {

					return TilePowered.this.getEnergyStored(from);
				}

				@Override
				public int getMaxEnergyStored() {

					return TilePowered.this.getMaxEnergyStored(from);
				}

				@Override
				public boolean canExtract() {

					return false;
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
