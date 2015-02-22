package cofh.thermalexpansion.block;

import cofh.api.tileentity.IRedstoneControl;
import cofh.asm.relauncher.CoFHSide;
import cofh.asm.relauncher.Implementable;
import cofh.asm.relauncher.Strippable;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.audio.ISoundSource;
import cofh.lib.audio.SoundTile;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.SoundHelper;
import cofh.thermalexpansion.network.PacketTEBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.audio.ISound;
import net.minecraft.nbt.NBTTagCompound;

@Implementable("buildcraft.api.tiles.IHasWork")
@Strippable(value = "cofh.lib.audio.ISoundSource", side = CoFHSide.SERVER)
public abstract class TileRSControl extends TileInventory implements IRedstoneControl, ISoundSource {

	public boolean isActive;
	protected boolean isPowered;
	protected boolean wasPowered;

	protected ControlMode rsMode = ControlMode.DISABLED;

	@Override
	public void onNeighborBlockChange() {

		wasPowered = isPowered;
		isPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);

		if (wasPowered != isPowered && sendRedstoneUpdates()) {
			PacketTEBase.sendRSPowerUpdatePacketToClients(this, worldObj, xCoord, yCoord, zCoord);
			onRedstoneUpdate();
		}
	}

	protected boolean sendRedstoneUpdates() {

		return false;
	}

	public final boolean redstoneControlOrDisable() {

		return rsMode.isDisabled() || isPowered == rsMode.getState();
	}

	public void onRedstoneUpdate() {

	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		isActive = nbt.getBoolean("Active");
		NBTTagCompound rsTag = nbt.getCompoundTag("RS");

		isPowered = rsTag.getBoolean("Power");
		rsMode = ControlMode.values()[rsTag.getByte("Mode")];
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setBoolean("Active", isActive);
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

			if (isActive && !prevActive) {
				if (getSoundName() != null && !getSoundName().isEmpty()) {
					SoundHelper.playSound(getSound());
				}
			}
		} else {
			payload.getBool();
		}
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
	@SideOnly(Side.CLIENT)
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

	/* IHasWork */
	public boolean hasWork() {

		return isActive;
	}

}
