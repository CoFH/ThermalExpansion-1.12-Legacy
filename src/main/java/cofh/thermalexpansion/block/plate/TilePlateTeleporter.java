package cofh.thermalexpansion.block.plate;

import cofh.api.transport.IEnderDestination;
import cofh.core.RegistryEnderAttuned;
import cofh.core.network.PacketCoFHBase;
import cofh.core.network.PacketHandler;
import cofh.core.util.CoreUtils;
import cofh.core.util.SocialRegistry;
import cofh.lib.util.helpers.EntityHelper;
import cofh.thermalexpansion.core.TeleportChannelRegistry;
import cofh.thermalexpansion.gui.client.plate.GuiPlateTeleport;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFireworkSparkFX;
import net.minecraft.client.particle.EntityPortalFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class TilePlateTeleporter extends TilePlatePoweredBase implements IEnderDestination {

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateTeleporter.class, "cofh.thermalexpansion.PlateTeleporter");
	}

	protected static final int TELEPORT_COST = 100000;
	protected static final int DIMENSION_TELEPORT_COST = 1000000;
	protected static final int PARTICLE_DELAY = 80;
	protected static final int TELEPORT_DELAY = PARTICLE_DELAY + 50;

	private ThreadLocal<Boolean> internalSet = new ThreadLocal<Boolean>();
	private Integer pendingFrequency;
	protected int frequency = -1;
	protected int destination = -1;

	protected boolean isActive;

	public TilePlateTeleporter() {

		super(BlockPlate.Types.POWERED_TRANSLOCATE, 2000000);
	}

	protected void teleportEntity(Entity entity) {

	}

	@Override
	public void onEntityCollidedWithBlock(Entity entity) {

		if (entity.worldObj.isRemote || entity.timeUntilPortal > TELEPORT_DELAY) {
			entity.timeUntilPortal = entity.getPortalCooldown() + TELEPORT_DELAY;
			return;
		}

		if (destination == -1 || !RegistryEnderAttuned.getRegistry().hasDestination(this)) {
			return;
		}

		int teleportCost = TELEPORT_COST;
		IEnderDestination dest = RegistryEnderAttuned.getRegistry().getDestination(this);
		if (dest.dimension() != dimension()) {
			teleportCost = DIMENSION_TELEPORT_COST;
		}

		if (entity instanceof EntityEnderman) {
			teleportCost *= 2;
		}

		if (storage.getEnergyStored() < teleportCost) {
			return;
		}

		Class<? extends Entity> comp = Entity.class;
		if (!getAccess().isPublic()) {
			comp = EntityPlayer.class;
		}

		if (!comp.isInstance(entity)) {
			return;
		}

		l: if (!getAccess().isPublic()) {
			o: if (entity instanceof EntityItem) {
				String name = ((EntityItem) entity).func_145800_j();
				if (name == null) {
					break o;
				}
				if (getAccess().isRestricted() && SocialRegistry.playerHasAccess(name, getOwner())) {
					break l;
				}
				GameProfile i = MinecraftServer.getServer().func_152358_ax().func_152655_a(name);
				if (getOwner().getId().equals(i.getId())) {
					break l;
				}
			} else if (canPlayerAccess((EntityPlayer) entity)) {
				break l;
			}
			return;
		}

		if (entity instanceof EntityLivingBase) {
			if (entity.timeUntilPortal++ <= TELEPORT_DELAY) {
				if (!(entity instanceof EntityPlayerMP)) {
					entity.timeUntilPortal++;
				}
				World world = entity.worldObj;
				int i = entity.timeUntilPortal >= TELEPORT_DELAY ? 100 : 99;
				double x = entity.posX, z = entity.posZ, y = entity.posY;
				y += entity.height * .75;
				int amt = entity.timeUntilPortal * 5 / PARTICLE_DELAY;
				l: if (i == 100 || amt != ((entity.timeUntilPortal - 2) * 5 / PARTICLE_DELAY)) {
					if (i != 100 && entity.timeUntilPortal > PARTICLE_DELAY) {
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
					TargetPoint targ = new TargetPoint(world.provider.dimensionId, xCoord, yCoord, zCoord, 50);
					PacketHandler.sendToAllAround(packet, targ);
				}
				if (i == 99) {
					return;
				}
			}
			entity.timeUntilPortal = entity.getPortalCooldown() + TELEPORT_DELAY;
		}

		if (storage.extractEnergy(teleportCost, false) == teleportCost) {
			if (dest.dimension() != dimension()) {
				EntityHelper.transferEntityToDimension(entity, dest.dimension(), MinecraftServer.getServer().getConfigurationManager());
			}
			CoreUtils.teleportEntityTo(entity, dest.x() + .5, dest.y() + .2, dest.z() + .5);
		}
	}

	protected void addZapParticles(int time, double x, double y, double z) {

		time += 2;
		double dv = time / 2;

		for (int i = time; i-- > 0;) {
			for (int k = time; k-- > 0;) {
				double yV = Math.cos(k * Math.PI / dv), xV, zV;
				xV = Math.pow(Math.sin(i * Math.PI / dv) * yV, 1) * 2;
				zV = Math.pow(Math.cos(i * Math.PI / dv) * yV, 1) * 2;
				yV = Math.pow(Math.sin(k * Math.PI / dv) * 1., 1) * 2;
				EntityFX spark = new EntityPortalFX(worldObj, x, y, z, xV, yV, zV);
				Minecraft.getMinecraft().effectRenderer.addEffect(spark);
			}
		}
	}

	protected void addTeleportParticles(double x, double y, double z, boolean trail) {

		for (int i = 15; i-- > 0;) {
			for (int k = 15; k-- > 0;) {
				double yV = Math.cos(k * Math.PI / 7.5), xV, zV;
				xV = Math.pow(Math.sin(i * Math.PI / 7.5) * yV, 3) * .15;
				zV = Math.pow(Math.cos(i * Math.PI / 7.5) * yV, 3) * .15;
				yV = Math.pow(Math.sin(k * Math.PI / 7.5) * 1., 3) * .15;
				EntityFireworkSparkFX spark = new EntityFireworkSparkFX(worldObj, x, y, z, xV, yV, zV, Minecraft.getMinecraft().effectRenderer) {

					@Override
					public void moveEntity(double x, double y, double z) {

						motionY += 0.004;
						super.moveEntity(x, y + 0.004, z);
					}
				};
				spark.setTrail(trail);
				spark.setColour(0xE54CFF);
				spark.setFadeColour(0x750C9F);
				Minecraft.getMinecraft().effectRenderer.addEffect(spark);
			}
		}
	}

	@Override
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

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
					int freq = payload.getInt();
					String channel = payload.getString();
					String name = payload.getString();
					if (setFrequency(freq)) {
						if (freq == -1) {
							TeleportChannelRegistry.getChannels(true).removeFrequency(channel, freq);
						} else {
							TeleportChannelRegistry.getChannels(true).setFrequency(channel, freq, name);
						}
					}
				} else {
					setDestination(payload.getInt());
				}
			} else {
				AccessMode newMode = AccessMode.values()[payload.getByte()];
				if (frequency != -1 && access != newMode) {
					if (setFrequency(-1)) {
						TeleportChannelRegistry.getChannels(true).removeFrequency(getChannelString(), frequency);
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

		return access.isPublic() ? "_public_" : String.valueOf(owner.getName()).toLowerCase();
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
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
		return true;
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		tag.setString("Channel", getChannelString());
		tag.setInteger("Destination", destination);
		return true;
	}

	@Override
	public void blockBroken() {

		RegistryEnderAttuned.getRegistry().removeDestination(this);
	}

	@Override
	public void cofh_validate() {

		if (pendingFrequency != null) {
			if (!setFrequency(frequency = pendingFrequency.intValue())) {
				frequency = -1;
			}
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

	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("Frequency", frequency);
		nbt.setInteger("Destination", destination);
	}

	@Override
	public boolean isNotValid() {

		return !inWorld || this.tileEntityInvalid;
	}

	@Override
	public int x() {

		return xCoord;
	}

	@Override
	public int y() {

		return yCoord;
	}

	@Override
	public int z() {

		return zCoord;
	}

	@Override
	public int dimension() {

		return worldObj.provider.dimensionId;
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

}
