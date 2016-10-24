package cofh.thermalexpansion.block.plate;

import codechicken.lib.util.BlockUtils;
import codechicken.lib.util.ServerUtils;
import codechicken.lib.util.SoundUtils;
import cofh.api.tileentity.IRedstoneControl;
import cofh.api.transport.IEnderDestination;
import cofh.core.RegistryEnderAttuned;
import cofh.core.RegistrySocial;
import cofh.core.network.PacketCoFHBase;
import cofh.core.network.PacketHandler;
import cofh.core.network.PacketTileInfo;
import cofh.lib.util.helpers.EntityHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.core.TeleportChannelRegistry;
import cofh.thermalexpansion.gui.client.plate.GuiPlateTeleport;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.network.PacketTEBase;
import cofh.thermalexpansion.render.particle.ParticlePortal;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TilePlateTeleporter extends TilePlatePoweredBase implements IEnderDestination, IRedstoneControl {

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateTeleporter.class, "cofh.thermalexpansion.PlateTeleporter");
	}

	protected static final int TELEPORT_COST = 100000;
	protected static final int DIMENSION_TELEPORT_COST = 1000000;
	protected static final int PARTICLE_DELAY = 80;
	protected static final int TELEPORT_DELAY = PARTICLE_DELAY + 50;

	private final ThreadLocal<Boolean> internalSet = new ThreadLocal<Boolean>();
	private Integer pendingFrequency;
	protected int frequency = -1;
	protected int destination = -1;

	protected boolean isActive;

	protected boolean isPowered;

	protected boolean playerOnly = false;

	protected ControlMode rsMode = ControlMode.DISABLED;

	public TilePlateTeleporter() {

		super(BlockPlate.Types.TELEPORT, 2000000);
		filterSecure = true;
	}

	protected void teleportEntity(Entity entity, double x, double y, double z) {

		if (entity instanceof EntityLivingBase) {
			entity.setPositionAndUpdate(x, y, z);
		} else {
			entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
		}
        SoundUtils.playSoundAt(entity, SoundCategory.BLOCKS, SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1F, 1F);
	}

	private static boolean posEquals(double x, double y, double z, double X, double Y, double Z) {
		long a = (long) (x * 64);
		long b = (long) (y * 64);
		long c = (long) (z * 64);
		long A = (long) (X * 64);
		long B = (long) (Y * 64);
		long C = (long) (Z * 64);

		return a == A & b == B & c == C;
	}

	@Override
	public void onEntityCollidedWithBlock(Entity entity) {

		if (destination == -1 || entity.worldObj.isRemote) {
			return;
		}
		if (!redstoneControlOrDisable()) {
			return;
		}

		if (entity.timeUntilPortal > 0) {
			if (entity.timeUntilPortal >= entity.getPortalCooldown()) {
				entity.timeUntilPortal = entity.getPortalCooldown() + TELEPORT_DELAY;
				return;
			}
		}
		if (!posEquals(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ, entity.posX, entity.posY, entity.posZ)) {
			entity.timeUntilPortal = 0;
			return;
		}
		if (!RegistryEnderAttuned.getRegistry().hasDestination(this)) {
			if (destination != -1) {
				internalSet.set(Boolean.FALSE);
				clearDestination();
				internalSet.set(null);
			}
			return;
		}
		IEnderDestination dest = RegistryEnderAttuned.getRegistry().getDestination(this, true);
		if (dest == null) {
			return; // destination is invalid (deleted)
		}
		if (dest.isNotValid()) {
            // augment to reach these areas?
		}

		int teleportCost = TELEPORT_COST;
		if (dest.dimension() != dimension()) {
			teleportCost = DIMENSION_TELEPORT_COST;
		}
		if (entity instanceof EntityEnderman) {
			teleportCost *= 2;
		} else if (entity instanceof EntityItem) {
			teleportCost /= 1000;
		}
		if (storage.getEnergyStored() < teleportCost) {
			return;
		}
		Class<? extends Entity> comp = Entity.class;
		if (playerOnly) {
			comp = EntityPlayer.class;
		}
		if (!comp.isInstance(entity)) {
			return;
		}
		l: if (filterSecure && !getAccess().isPublic()) {
			o: if (entity instanceof EntityItem) {
				String name = ((EntityItem) entity).getThrower();
				if (name == null) {
					break o;
				}
				if (getAccess().isRestricted() && RegistrySocial.playerHasAccess(name, getOwner())) {
					break l;
				}
				GameProfile i = ServerUtils.mc().getPlayerProfileCache().getGameProfileForUsername(name);
				if (getOwner().getId().equals(i.getId())) {
					break l;
				}
			} else if (canPlayerAccess((EntityPlayer) entity)) {
				break l;
			}
			return;
		}
		if (entity instanceof EntityLivingBase) {
			if (entity.timeUntilPortal >= -TELEPORT_DELAY) {
				if (entity.timeUntilPortal > -TELEPORT_DELAY) {
					entity.timeUntilPortal--;
					if (entity instanceof EntityPlayerMP && (worldObj.getTotalWorldTime() & 1) == 0) {
						entity.timeUntilPortal++;
					}
				}
				World world = entity.worldObj;
				int i = entity.timeUntilPortal <= -TELEPORT_DELAY ? 100 : 99;
				double x = entity.posX, z = entity.posZ, y = entity.posY;
				y += entity.height * .75;
				int amt = -entity.timeUntilPortal * 5 / PARTICLE_DELAY;
				l: if (i == 100 || amt != ((-entity.timeUntilPortal - 2) * 5 / PARTICLE_DELAY)) {
					if (i != 100 && entity.timeUntilPortal < -PARTICLE_DELAY) {
						break l;
					}
					PacketCoFHBase packet = getModePacket();
					packet.addByte(i);
					if (i == 99) {
						packet.addInt(amt);
					}
					packet.addFloat((float) x);
					packet.addFloat((float) y);
					packet.addFloat((float) z);
					if (i == 100) {
						packet.addInt(entity.getEntityId());
						packet.addInt(dest.x());
						packet.addInt(dest.y());
						packet.addInt(dest.z());
						packet.addInt(dest.dimension());
					}
					TargetPoint targ = new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 50);
					PacketHandler.sendToAllAround(packet, targ);
				}
				if (i == 99) {
					return;
				}
			}
		}
		if (storage.extractEnergy(teleportCost, false) == teleportCost) {
			entity.timeUntilPortal = entity.getPortalCooldown() + TELEPORT_DELAY + 5;
			double x = dest.x() + .5, y = dest.y() + .2, z = dest.z() + .5;
			if (dest.dimension() != dimension()) {
				EntityHelper.transferEntityToDimension(entity, x, y, z, dest.dimension(), ServerUtils.mc().getPlayerList());
			} else {
				teleportEntity(entity, x, y, z);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, Random r) {

		if (!isActive || (world.getTotalWorldTime() % 6) != 0 || !redstoneControlOrDisable()) {
			return;
		}
		double dx = .5 + 0.15D * r.nextGaussian();
		double dz = .5 + 0.15D * r.nextGaussian();
		double[] m = fixPosition(dx, 0, dz);
		ParticlePortal p = new ParticlePortal(world, pos.getX() + m[0], pos.getY() + m[1], pos.getZ() + m[2], 1.0F, 1.0F, 1.0F);
		m = fixVector(p.motionX, p.motionY, p.motionZ);
		//p.setVelocity(m[0], m[1], m[2]);
        p.motionX = m[0];
        p.motionY = m[1];
        p.motionZ = m[2];
		p.setScale(fixVector(0.03, 0.1, 0.03));
		Minecraft.getMinecraft().effectRenderer.addEffect(p);
	}

	@SideOnly(Side.CLIENT)
	protected void addZapParticles(int time, double x, double y, double z) {

		time += 2;
		double dv = time / 2;

		for (int i = time; i-- > 0;) {
			for (int k = time; k-- > 0;) {
				double yV = Math.cos(k * Math.PI / dv), xV, zV;
				xV = Math.pow(Math.sin(i * Math.PI / dv) * yV, 1) * 2;
				zV = Math.pow(Math.cos(i * Math.PI / dv) * yV, 1) * 2;
				yV = Math.pow(Math.sin(k * Math.PI / dv) * 1., 1) * 2;
				Particle spark = new net.minecraft.client.particle.ParticlePortal(worldObj, x, y, z, xV, yV, zV);
				Minecraft.getMinecraft().effectRenderer.addEffect(spark);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	protected void addTeleportParticles(double x, double y, double z, boolean trail) {

		for (int i = 15; i-- > 0;) {
			for (int k = 15; k-- > 0;) {
				double yV = Math.cos(k * Math.PI / 7.5), xV, zV;
				xV = Math.pow(Math.sin(i * Math.PI / 7.5) * yV, 3) * .15;
				zV = Math.pow(Math.cos(i * Math.PI / 7.5) * yV, 3) * .15;
				yV = Math.pow(Math.sin(k * Math.PI / 7.5) * 1., 3) * .15;
				ParticleFirework.Spark spark = new ParticleFirework.Spark(worldObj, x, y, z, xV, yV, zV, Minecraft.getMinecraft().effectRenderer) {

					@Override
					public void moveEntity(double x, double y, double z) {

						motionY += 0.004;
						super.moveEntity(x, y + 0.004, z);
					}
				};
				spark.setTrail(trail);
				spark.setColor(0xE54CFF);
				spark.setColorFade(0x750C9F);
				Minecraft.getMinecraft().effectRenderer.addEffect(spark);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		byte type = payload.getByte();

		switch (type) {
		case 99:
			addZapParticles(payload.getInt(), payload.getFloat(), payload.getFloat(), payload.getFloat());
			break;
		case 100:
			float x = payload.getFloat(),
			y = payload.getFloat(),
			z = payload.getFloat();
			Entity ent = worldObj.getEntityByID(payload.getInt());
			addTeleportParticles(x, y, z, ent instanceof EntityEnderman);
			x = payload.getInt() + .5f;
			y = payload.getInt() + .2f;
			z = payload.getInt() + .5f;
			int dim = payload.getInt();
			if (ent != null && !(ent instanceof EntityPlayer)) {
				if (dim != dimension()) {
					ent.setDead();
				} else {
					ent.setPosition(x, y, z);
				}
			}
			break;
		}
	}

	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addInt(frequency);
		payload.addInt(destination);
		payload.addByte(access.ordinal());

		return payload;
	}

	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {
			frequency = payload.getInt();
			destination = payload.getInt();
			access = AccessMode.values()[payload.getByte()];
		}

		isActive = destination != -1;

        BlockUtils.fireBlockUpdate(worldObj,getPos());
	}

	@Override
	public void handleTileInfoPacket(PacketCoFHBase payload, boolean isServer, EntityPlayer thePlayer) {

		if (isServer) {
			if (!canPlayerAccess(thePlayer)) {
				return;
			}
			internalSet.set(Boolean.TRUE);
			if (payload.getBool()) {
				if (payload.getBool()) {
					int freq = payload.getByte();
					String channel = payload.getString();
					String name = payload.getString();
					int oldFreq = frequency;
					if (freq == 0) {
						freq = TeleportChannelRegistry.getRegistry().findFreeFrequency(channel);
					}
					if (setFrequency(freq)) {
						PacketHandler.sendTo(getPacket(), thePlayer);
						if (freq == -1) {
							TeleportChannelRegistry.getChannels(true).removeFrequency(channel, oldFreq);
							TeleportChannelRegistry.removeChannelFrequency(thePlayer, channel, oldFreq);
						} else {
							TeleportChannelRegistry.getChannels(true).setFrequency(channel, freq, name);
							TeleportChannelRegistry.updateChannelFrequency(thePlayer, channel, freq, name);
						}
					}
				} else {
					setDestination(payload.getInt());
				}
			} else {
				AccessMode newMode = AccessMode.values()[payload.getByte()];
				if (frequency != -1 && access != newMode) {
					int freq = frequency;
					if (setFrequency(-1)) {
						TeleportChannelRegistry.getChannels(true).removeFrequency(getChannelString(), freq);
					}
				}
				access = newMode;
			}
			internalSet.set(null);
		} else {
			super.handleTileInfoPacket(payload, isServer, thePlayer);
		}
	}

	@Override
	public String getChannelString() {

		return access.isPublic() ? "_public_" : String.valueOf(owner.getName()).toLowerCase(Locale.US);
	}

	@Override
	public int getFrequency() {

		return frequency;
	}

	@Override
	public boolean setFrequency(int frequency) {

		if (internalSet.get() == null && !access.isPublic()) {
			return false;
		}
		if (!inWorld || frequency != this.frequency) {
			RegistryEnderAttuned.getRegistry().removeDestination(this);
			int old = this.frequency;
			this.frequency = frequency;
			if (frequency != -1) {
				if (RegistryEnderAttuned.getRegistry().hasDestination(this, false)) {
					this.frequency = old;
					return false;
				}
				RegistryEnderAttuned.getRegistry().addDestination(this);
			}

			markDirty();
            BlockUtils.fireBlockUpdate(worldObj,getPos());
		}
		return true;
	}

	@Override
	public boolean clearFrequency() {

		return setFrequency(-1);
	}

	@Override
	public int getDestination() {

		return destination;
	}

	@Override
	public boolean setDestination(int frequency) {

		if (internalSet.get() == null && !access.isPublic()) {
			return false;
		}
		if (frequency != destination) {
			int old = destination;
			destination = frequency;
			isActive = frequency != -1;
			if (isActive) {
				if (!RegistryEnderAttuned.getRegistry().hasDestination(this, true)) {
					// TODO: ???
					destination = old;
					isActive = old != -1;
					return false;
				}
			}
			markDirty();
            BlockUtils.fireBlockUpdate(worldObj,getPos());
		}
		return true;
	}

	@Override
	public boolean clearDestination() {

		return setDestination(-1);
	}

	@Override
	protected boolean readPortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		if (!getChannelString().equals(tag.getString("Channel"))) {
			// TODO: log message
			return false;
		}
		setDestination(tag.getInteger("Destination"));
		NBTTagCompound rsTag = tag.getCompoundTag("RS");

		rsMode = ControlMode.values()[rsTag.getByte("Mode")];
		return true;
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		tag.setString("Channel", getChannelString());
		tag.setInteger("Destination", destination);
		NBTTagCompound rsTag = new NBTTagCompound();

		rsTag.setByte("Mode", (byte) rsMode.ordinal());
		tag.setTag("RS", rsTag);
		return true;
	}

	@Override
	public void blockBroken() {

		RegistryEnderAttuned.getRegistry().removeDestination(this);
		if (frequency != -1) {
			TeleportChannelRegistry.getChannels(!worldObj.isRemote).removeFrequency(getChannelString(), frequency);
		}
	}

	@Override
	public void cofh_validate() {

		if (pendingFrequency != null) {
			internalSet.set(Boolean.FALSE);
			if (!setFrequency(frequency = pendingFrequency.intValue())) {
				frequency = -1;
			}
			internalSet.set(null);
			pendingFrequency = null;
		}
		super.cofh_validate();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		pendingFrequency = nbt.hasKey("Frequency") ? new Integer(nbt.getInteger("Frequency")) : null;
		destination = nbt.hasKey("Destination") ? nbt.getInteger("Destination") : -1;

		NBTTagCompound rsTag = nbt.getCompoundTag("RS");
		isPowered = rsTag.getBoolean("Power");
		rsMode = ControlMode.values()[rsTag.getByte("Mode")];

		if (!nbt.hasKey("FilterSecure")) {
			filterSecure = true;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("Frequency", frequency);
		nbt.setInteger("Destination", destination);

		NBTTagCompound rsTag = new NBTTagCompound();
		rsTag.setBoolean("Power", isPowered);
		rsTag.setByte("Mode", (byte) rsMode.ordinal());
		nbt.setTag("RS", rsTag);
        return nbt;
	}

	@Override
	public boolean isNotValid() {

		return !inWorld || this.tileEntityInvalid;
	}

	@Override
	public int x() {

		return getPos().getX();
	}

	@Override
	public int y() {

		return getPos().getY();
	}

	@Override
	public int z() {

		return getPos().getZ();
	}

	@Override
	public int dimension() {

		return worldObj.provider.getDimension();
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiPlateTeleport(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this, false, false);
	}

	@Override
	public void onNeighborBlockChange() {

		setPowered(worldObj.isBlockPowered(getPos()));
	}

	public final boolean redstoneControlOrDisable() {

		return rsMode.isDisabled() || isPowered == rsMode.getState();
	}

	/* ISecurable */

	@Override
	public boolean setAccess(AccessMode access) {

		if (worldObj.isRemote && this.access != access) {
			PacketHandler.sendToServer(PacketTileInfo.newPacket(this).addBool(false).addByte(access.ordinal()));
		}
		this.access = access;
		return true;
	}

	/* IRedstoneControl */
	@Override
	public final void setPowered(boolean powered) {

		boolean wasPowered = isPowered;
		isPowered = powered;
		if (wasPowered != isPowered) {
			if (ServerHelper.isClientWorld(worldObj)) {
                BlockUtils.fireBlockUpdate(worldObj,getPos());
			} else {
				PacketTEBase.sendRSPowerUpdatePacketToClients(this, worldObj, getPos());
			}
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
			PacketTEBase.sendRSConfigUpdatePacketToServer(this, getPos());
		} else {
			sendUpdatePacket(Side.CLIENT);
			boolean powered = isPowered;
			isPowered = !powered;
			setPowered(powered);
		}
	}

	@Override
	public final ControlMode getControl() {

		return rsMode;
	}

}
