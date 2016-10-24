package cofh.thermalexpansion.block.plate;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;
import cofh.core.network.PacketCoFHBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TilePlatePoweredBase extends TilePlateBase implements IEnergyReceiver {

	protected EnergyStorage storage;
	protected ItemStack energySlot;

	protected TilePlatePoweredBase(BlockPlate.Types type, int storage) {

		super(type);
		this.storage = new EnergyStorage(storage);
	}

	@Override
	public boolean setFacing(int facing) {

		return false;
	}

	@Override
	void setAlignment(int side, float hitX, float hitY, float hitZ) {

		alignment = (byte) side;
		direction = 7;
	}

	@Override
	public boolean canRotate() {

		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);
		storage.readFromNBT(nbt);
		energySlot = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("stack"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);
		storage.writeToNBT(nbt);
		if (energySlot != null) {
			nbt.setTag("stack", energySlot.writeToNBT(new NBTTagCompound()));
		}
		return nbt;
	}

	public ItemStack getEnergySlot() {

		return energySlot;
	}

	public void setEnergySlot(ItemStack stack) {

		energySlot = stack;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addInt(storage.getEnergyStored());

		return payload;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		storage.setEnergyStored(payload.getInt());
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {

		return from.ordinal() == alignment;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {

		return storage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int getEnergyStored(EnumFacing from) {

		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {

		return storage.getMaxEnergyStored();
	}

	public IEnergyStorage getEnergyStorage() {

		return storage;
	}

}
