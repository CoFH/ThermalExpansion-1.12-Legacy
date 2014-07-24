package thermalexpansion.block;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyStorage;
import cofh.api.tileentity.IRedstoneControl;
import cofh.audio.ISoundSource;
import cofh.audio.SoundTile;
import cofh.network.PacketCoFHBase;
import cofh.util.EnergyHelper;
import cofh.util.ServerHelper;
import cofh.util.SoundHelper;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.client.audio.ISound;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import thermalexpansion.network.PacketTEBase;

public abstract class TileRSControl extends TileInventory implements IEnergyHandler, IRedstoneControl, ISoundSource {

	public boolean isActive;
	protected boolean isPowered;
	protected boolean wasPowered;

	protected ControlMode rsMode = ControlMode.DISABLED;
	protected EnergyStorage energyStorage = new EnergyStorage(0);

	@Override
	public void onNeighborBlockChange() {

		wasPowered = isPowered;
		isPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);

		if (wasPowered != isPowered && sendRedstoneUpdates()) {
			PacketTEBase.sendRSPowerUpdatePacketToClients(this, worldObj, xCoord, yCoord, zCoord);
			onRedstoneUpdate();
		}
	}

	public int getChargeSlot() {

		return inventory.length - 1;
	}

	public boolean hasChargeSlot() {

		return true;
	}

	protected boolean sendRedstoneUpdates() {

		return false;
	}

	public final boolean redstoneControlOrDisable() {

		return rsMode.isDisabled() || isPowered == rsMode.getState();
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

	public void onRedstoneUpdate() {

	}

	public final void setEnergyStored(int quantity) {

		energyStorage.setEnergyStored(quantity);
	}

	/* GUI METHODS */
	public IEnergyStorage getEnergyStorage() {

		return energyStorage;
	}

	public int getScaledEnergyStored(int scale) {

		return energyStorage.getEnergyStored() * scale / energyStorage.getMaxEnergyStored();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		isActive = nbt.getBoolean("Active");
		energyStorage.readFromNBT(nbt);
		NBTTagCompound rsTag = nbt.getCompoundTag("RS");

		isPowered = rsTag.getBoolean("Power");
		rsMode = ControlMode.values()[rsTag.getByte("Mode")];
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setBoolean("Active", isActive);
		energyStorage.writeToNBT(nbt);
		NBTTagCompound rsTag = new NBTTagCompound();

		rsTag.setBoolean("Power", isPowered);
		rsTag.setByte("Mode", (byte) rsMode.ordinal());
		nbt.setTag("RS", rsTag);
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addBool(isPowered);
		payload.addByte(rsMode.ordinal());
		payload.addBool(isActive);
		payload.addInt(energyStorage.getEnergyStored());

		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		isPowered = payload.getBool();
		rsMode = ControlMode.values()[payload.getByte()];

		if (!isServer) {
			boolean prevActive = isActive;

			isActive = payload.getBool();
			energyStorage.setEnergyStored(payload.getInt());

			if (isActive && !prevActive) {
				if (getSoundName() != null && !getSoundName().isEmpty()) {
					SoundHelper.playSound(getSound());
				}
			}
		} else {
			payload.getBool();
			payload.getInt();
		}
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

	/* IRedstoneControl */
	@Override
	public final void setPowered(boolean isPowered) {

		wasPowered = this.isPowered;
		this.isPowered = isPowered;
		if (ServerHelper.isClientWorld(worldObj)) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public final boolean isPowered() {

		return isPowered;
	}

	@Override
	public final void setControl(ControlMode control) {

		rsMode = control;
		if (ServerHelper.isClientWorld(worldObj)) {
			PacketTEBase.sendRSConfigUpdatePacketToServer(this, this.xCoord, this.yCoord, this.zCoord);
		} else {
			sendUpdatePacket(Side.CLIENT);
		}
	}

	@Override
	public final ControlMode getControl() {

		return rsMode;
	}

	/* ISoundSource */
	@Override
	public ISound getSound() {

		return new SoundTile(this, getSoundName(), 1.0F, 1.0F, true, 0, xCoord, yCoord, zCoord);
	}

	public String getSoundName() {

		return "";
	}

	@Override
	public boolean shouldPlaySound() {

		return !tileEntityInvalid && isActive;
	}

}
