package thermalexpansion.block;

import cofh.api.tileentity.IRedstoneControl;
import cofh.network.CoFHPacket;
import cofh.network.ITilePacketHandler;

import net.minecraft.nbt.NBTTagCompound;

import thermalexpansion.network.TEPacketHandler;

public abstract class TileRSInventory extends TileInventory implements IRedstoneControl, ITilePacketHandler {

	protected boolean isPowered;
	protected boolean wasPowered;

	protected ControlMode rsMode;

	public boolean redstoneControlOrDisable() {

		return rsMode.isDisabled() || isPowered == rsMode.getState();
	}

	public boolean sendRedstoneUpdates() {

		return false;
	}

	public void onRedstoneUpdate() {

	}

	@Override
	public void onNeighborBlockChange() {

		wasPowered = isPowered;
		isPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);

		if (wasPowered != isPowered && sendRedstoneUpdates()) {
			TEPacketHandler.sendRSPowerUpdatePacketToClients(this, worldObj, xCoord, yCoord, zCoord);
			onRedstoneUpdate();
		}
	}

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();

		payload.addBool(isPowered);
		payload.addBool(rsDisable);
		payload.addBool(rsSetting);
		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		isPowered = payload.getBool();
		rsDisable = payload.getBool();
		rsSetting = payload.getBool();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		NBTTagCompound rsControl = nbt.getCompoundTag("RS");

		isPowered = rsControl.getBoolean("Powered");
		rsDisable = rsControl.getBoolean("Disable");
		rsSetting = rsControl.getBoolean("Setting");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		NBTTagCompound rsControl = new NBTTagCompound();

		rsControl.setBoolean("Powered", isPowered);
		rsControl.setBoolean("Disable", rsDisable);
		rsControl.setBoolean("Setting", rsSetting);

		nbt.setTag("RS", rsControl);
	}

	/* IRedstoneControl */
	@Override
	public void setPowered(boolean isPowered) {

		wasPowered = this.isPowered;
		this.isPowered = isPowered;
	}

	@Override
	public boolean isPowered() {

		return isPowered;
	}

	@Override
	public void setControl(ControlMode control) {

		rsMode = control;
	}

	@Override
	public ControlMode getControl() {

		return rsMode;
	}
	// @Override
	// public boolean getControlDisable() {
	//
	// return rsDisable;
	// }
	//
	// @Override
	// public boolean getControlSetting() {
	//
	// return rsSetting;
	// }
	//
	// @Override
	// public boolean setControlDisable(boolean disable) {
	//
	// rsDisable = disable;
	// return true;
	// }
	//
	// @Override
	// public boolean setControlSetting(boolean setting) {
	//
	// rsSetting = setting;
	// return true;
	// }
	//
	// @Override
	// public boolean setRedstoneConfig(boolean disable, boolean setting) {
	//
	// rsDisable = disable;
	// rsSetting = setting;
	// TEPacketHandler.sendRSConfigUpdatePacketToServer(this, xCoord, yCoord, zCoord);
	// return true;
	// }
	//
	// @Override
	// public boolean isPowered() {
	//
	// return isPowered;
	// }
	//
	// @Override
	// public void handlePowerUpdate(boolean powered) {
	//
	// isPowered = powered;
	// worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	// }
	//
	// @Override
	// public void handleConfigUpdate(boolean disable, boolean setting) {
	//
	// rsDisable = disable;
	// rsSetting = setting;
	// sendUpdatePacket(Side.CLIENT);
	// }

}
