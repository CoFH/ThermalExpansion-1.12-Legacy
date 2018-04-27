package cofh.thermalexpansion.block;

import cofh.api.tileentity.IRedstoneControl;
import cofh.core.audio.ISoundSource;
import cofh.core.audio.SoundTile;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.network.PacketCore;
import cofh.core.util.helpers.RedstoneControlHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.core.util.helpers.SoundHelper;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileRSControl extends TileTEBase implements IRedstoneControl, ISoundSource {

	public boolean isActive;
	public boolean wasActive;

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
		powerLevel = world.isBlockIndirectlyGettingPowered(pos);
		isPowered = powerLevel > 0;

		if (wasPowered != isPowered && sendRedstoneUpdates()) {
			PacketCore.sendRSPowerUpdatePacketToClients(this, world, pos);
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

		isActive = nbt.getBoolean(CoreProps.ACTIVE);
		NBTTagCompound rsTag = nbt.getCompoundTag("RS");

		isPowered = rsTag.getBoolean("Power");
		powerLevel = rsTag.getByte("Level");
		rsMode = ControlMode.values()[rsTag.getByte(CoreProps.MODE)];
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setBoolean(CoreProps.ACTIVE, isActive);
		NBTTagCompound rsTag = new NBTTagCompound();

		rsTag.setBoolean("Power", isPowered);
		rsTag.setByte("Level", (byte) powerLevel);
		rsTag.setByte(CoreProps.MODE, (byte) rsMode.ordinal());
		nbt.setTag("RS", rsTag);
		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
	@Override
	public PacketBase getTilePacket() {

		PacketBase payload = super.getTilePacket();

		payload.addBool(isPowered);
		payload.addByte(rsMode.ordinal());
		payload.addBool(isActive);

		return payload;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketBase payload) {

		super.handleTilePacket(payload);

		isPowered = payload.getBool();
		rsMode = ControlMode.values()[payload.getByte()];

		boolean curActive = isActive;
		isActive = payload.getBool();

		if (!curActive && isActive) {
			if (getSoundEvent() != null && enableSounds()) {
				SoundHelper.playSound(getSound());
			}
		}
	}

	/* IRedstoneControl */
	@Override
	public final void setPowered(boolean isPowered) {

		wasPowered = this.isPowered;
		this.isPowered = isPowered;
		if (ServerHelper.isClientWorld(world)) {
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
		if (ServerHelper.isClientWorld(world)) {
			PacketCore.sendRSConfigUpdatePacketToServer(this, pos);
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

		return new SoundTile(this, getSoundEvent(), getVolume(), 1.0F, true, 0, new Vec3d(pos).addVector(0.5, 0.5, 0.5));
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
