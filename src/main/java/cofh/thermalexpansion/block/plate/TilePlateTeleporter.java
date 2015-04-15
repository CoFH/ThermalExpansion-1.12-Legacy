package cofh.thermalexpansion.block.plate;

import cofh.core.network.PacketCoFHBase;
import cofh.core.network.PacketHandler;
import cofh.core.util.SocialRegistry;
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

public class TilePlateTeleporter extends TilePlatePoweredBase {

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateCharger.class, "cofh.thermalexpansion.PlateTeleporter");
	}

	protected static final int TELEPORT_COST = 500000;
	protected static final int PARTICLE_DELAY = 80;
	protected static final int TELEPORT_DELAY = PARTICLE_DELAY + 50;

	public TilePlateTeleporter() {

		super(BlockPlate.Types.POWERED_TRANSLOCATE, 1000000);
	}

	protected void teleportEntity(Entity ent) {

	}

	@Override
	public void onEntityCollidedWithBlock(Entity theEntity) {

		if (theEntity.worldObj.isRemote || storage.getEnergyStored() < TELEPORT_COST) {
			return;
		}

		if (theEntity.timeUntilPortal > TELEPORT_DELAY) {
			theEntity.timeUntilPortal = theEntity.getPortalCooldown() + TELEPORT_DELAY;
			return;
		}

		Class<? extends Entity> comp = Entity.class;
		if (!getAccess().isPublic()) {
			comp = EntityPlayer.class;
		}

		if (!comp.isInstance(theEntity)) {
			return;
		}

		l: if (!getAccess().isPublic()) {
			o: if (theEntity instanceof EntityItem) {
				String name = ((EntityItem) theEntity).func_145800_j();
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
			} else if (canPlayerAccess((EntityPlayer) theEntity)) {
				break l;
			}
			return;
		}

		boolean isPlayer = false;
		if (theEntity instanceof EntityLivingBase) {
			if (theEntity.timeUntilPortal++ <= TELEPORT_DELAY) {
				isPlayer = theEntity instanceof EntityPlayerMP;
				if (!isPlayer) {
					theEntity.timeUntilPortal++;
				}
				World world = theEntity.worldObj;
				int i = theEntity.timeUntilPortal >= TELEPORT_DELAY ? 100 : 99;
				double x = theEntity.posX, z = theEntity.posZ, y = theEntity.posY;
				y += theEntity.height * .75;
				int amt = theEntity.timeUntilPortal * 5 / PARTICLE_DELAY;
				l: if (i == 100 || amt != ((theEntity.timeUntilPortal - 2) * 5 / PARTICLE_DELAY)) {
					if (i != 100 && theEntity.timeUntilPortal > PARTICLE_DELAY) {
						break l;
					}
					PacketCoFHBase packet = getModePacket();
					packet.addByte(i);
					if (i == 99)
						packet.addInt(amt);
					packet.addFloat((float) x);
					packet.addFloat((float) y);
					packet.addFloat((float) z);
					if (i == 100)
						packet.addBool(theEntity instanceof EntityEnderman);
					TargetPoint targ = new TargetPoint(world.provider.dimensionId, xCoord, yCoord, zCoord, 50);
					PacketHandler.sendToAllAround(packet, targ);
				}
				if (i == 99)
					return;
			}
			theEntity.timeUntilPortal = theEntity.getPortalCooldown() + TELEPORT_DELAY;
		}

		if (storage.extractEnergy(TELEPORT_COST, false) == TELEPORT_COST) {
			if (isPlayer) {
				PacketCoFHBase packet = getModePacket();
				packet.addByte(101);
				packet.addInt(theEntity.getEntityId());
				PacketHandler.sendTo(packet, (EntityPlayer)theEntity);
			}
			;
		}
	}

	protected void addZapParticles(int time, double x, double y, double z) {

		time += 2;
		double dv = time / 2;

		for (int i = time; i --> 0; ) {
			for (int k = time; k --> 0; ) {
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

		for (int i = 15; i --> 0; ) {
			for (int k = 15; k --> 0; ) {
				double yV = Math.cos(k * Math.PI / 7.5), xV, zV;
				xV = Math.pow(Math.sin(i * Math.PI / 7.5) * yV, 3) * .15;
				zV = Math.pow(Math.cos(i * Math.PI / 7.5) * yV, 3) * .15;
				yV = Math.pow(Math.sin(k * Math.PI / 7.5) * 1., 3) * .15;
				EntityFireworkSparkFX spark = new EntityFireworkSparkFX(worldObj,
					x, y, z, xV, yV, zV, Minecraft.getMinecraft().effectRenderer) {

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
			addTeleportParticles(payload.getFloat(), payload.getFloat(), payload.getFloat(), payload.getBool());
			break;
		case 101:
			Entity ent = worldObj.getEntityByID(payload.getInt());
			break;
		}
	}

	@Override
	protected boolean readPortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		return true;
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		return true;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

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
