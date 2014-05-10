package thermalexpansion.block;

import cofh.api.tileentity.IRedstoneControl;
import cofh.network.CoFHPacket;
import cofh.network.ITilePacketHandler;
import cofh.util.ServerHelper;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.nbt.NBTTagCompound;

import thermalexpansion.network.GenericTEPacket;

public abstract class TileRSBase extends TileTEBase implements IRedstoneControl, ITilePacketHandler {

	protected boolean isPowered;
	protected boolean wasPowered;

	protected ControlMode rsMode;

	public boolean redstoneControlOrDisable() {

		return rsMode.isDisabled() || isPowered == rsMode.getState();
	}

	public boolean sendRedstoneUpdates() {

		return false;
	}

	@Override
	public void onNeighborBlockChange() {

		wasPowered = isPowered;
		isPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);

		if (wasPowered != isPowered && sendRedstoneUpdates()) {
			GenericTEPacket.sendRSPowerUpdatePacketToClients(this, worldObj, xCoord, yCoord, zCoord);
		}
	}

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();

		payload.addBool(isPowered);
		payload.addByte(rsMode.ordinal());
		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		isPowered = payload.getBool();
		rsMode = ControlMode.values()[payload.getByte()];
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		NBTTagCompound rsControl = nbt.getCompoundTag("RS");

		isPowered = rsControl.getBoolean("Powered");
		rsMode = ControlMode.values()[rsControl.getByte("rsMode")];
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		NBTTagCompound rsControl = new NBTTagCompound();

		rsControl.setBoolean("Powered", isPowered);
		rsControl.setByte("rsMode", (byte) rsMode.ordinal());

		nbt.setTag("RS", rsControl);
	}

	/* IRedstoneControl */
	// TODO: A packet needs to be sent here at some point for the GUI button clicks. Determine a good place for this.
	@Override
	public void setPowered(boolean isPowered) {

		wasPowered = this.isPowered;
		this.isPowered = isPowered;
		if (ServerHelper.isClientWorld(worldObj)) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public boolean isPowered() {

		return isPowered;
	}

	@Override
	public void setControl(ControlMode control) {

		rsMode = control;
		sendUpdatePacket(Side.CLIENT);
	}

	@Override
	public ControlMode getControl() {

		return rsMode;
	}

}
