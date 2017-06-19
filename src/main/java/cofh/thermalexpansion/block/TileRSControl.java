package cofh.thermalexpansion.block;

import codechicken.lib.vec.Vector3;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.helpers.RedstoneControlHelper;
import cofh.core.util.tileentity.IRedstoneControl;
import cofh.lib.audio.ISoundSource;
import cofh.lib.audio.SoundTile;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.SoundHelper;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.network.PacketTEBase;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileRSControl extends TileTEBase implements IRedstoneControl, ISoundSource {

	public boolean isActive;

	/* REDSTONE CONTROL */
	protected int powerLevel;
	protected boolean isPowered;
	protected boolean wasPowered;

	protected ControlMode rsMode = ControlMode.DISABLED;

	@Override
	protected boolean readPortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		rsMode = RedstoneControlHelper.getControlFromNBT(tag);
		return true;
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		RedstoneControlHelper.setItemStackTagRS(tag, this);
		return true;
	}

	@Override
	public void blockPlaced() {

		onNeighborBlockChange();
	}

	@Override
	public void onNeighborBlockChange() {

		wasPowered = isPowered;
		powerLevel = worldObj.isBlockIndirectlyGettingPowered(pos);
		isPowered = powerLevel > 0;

		if (wasPowered != isPowered && sendRedstoneUpdates()) {
			PacketTEBase.sendRSPowerUpdatePacketToClients(this, worldObj, pos);
			onRedstoneUpdate();
		}
	}

	public void onRedstoneUpdate() {

	}

	protected boolean sendRedstoneUpdates() {

		return false;
	}

	public final boolean redstoneControlOrDisable() {

		return rsMode.isDisabled() || isPowered == rsMode.getState();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		isActive = nbt.getBoolean("Active");
		NBTTagCompound rsTag = nbt.getCompoundTag("RS");

		isPowered = rsTag.getBoolean("Power");
		powerLevel = rsTag.getByte("Level");
		rsMode = ControlMode.values()[rsTag.getByte("Mode")];
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setBoolean("Active", isActive);
		NBTTagCompound rsTag = new NBTTagCompound();

		rsTag.setBoolean("Power", isPowered);
		rsTag.setByte("Level", (byte) powerLevel);
		rsTag.setByte("Mode", (byte) rsMode.ordinal());
		nbt.setTag("RS", rsTag);
		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getTilePacket() {

		PacketCoFHBase payload = super.getTilePacket();

		payload.addBool(isPowered);
		payload.addByte(rsMode.ordinal());
		payload.addBool(isActive);

		return payload;
	}

	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		isPowered = payload.getBool();
		rsMode = ControlMode.values()[payload.getByte()];

		boolean prevActive = isActive;
		isActive = payload.getBool();

		if (isActive && !prevActive) {
			if (getSoundEvent() != null && TEProps.enableSounds) {
				SoundHelper.playSound(getSound());
			}
		}
	}

	/* IRedstoneControl */
	@Override
	public final void setPowered(boolean isPowered) {

		wasPowered = this.isPowered;
		this.isPowered = isPowered;
		if (ServerHelper.isClientWorld(worldObj)) {
			callBlockUpdate();
		}
	}

	@Override
	public final boolean isPowered() {

		return isPowered;
	}

	@Override
	public final boolean setControl(ControlMode control) {

		rsMode = control;
		if (ServerHelper.isClientWorld(worldObj)) {
			PacketTEBase.sendRSConfigUpdatePacketToServer(this, pos);
		}
		return true;
	}

	@Override
	public final ControlMode getControl() {

		return rsMode;
	}

	/* ISoundSource */
	@Override
	@SideOnly (Side.CLIENT)
	public ISound getSound() {

		return new SoundTile(this, getSoundEvent(), getVolume(), 1.0F, true, 0, Vector3.fromTileCenter(this).vec3());
	}

	@Override
	@SideOnly (Side.CLIENT)
	public boolean shouldPlaySound() {

		return !tileEntityInvalid && isActive;
	}

	/* HELPERS */
	protected float getVolume() {

		return 1.0F;
	}

	protected SoundEvent getSoundEvent() {

		return null;
	}

}
