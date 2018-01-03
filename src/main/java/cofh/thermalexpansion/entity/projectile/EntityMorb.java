package cofh.thermalexpansion.entity.projectile;

import cofh.core.init.CoreProps;
import cofh.core.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.item.ItemMorb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityMorb extends EntityThrowable {

	private static DataParameter<Byte> TYPE = EntityDataManager.createKey(EntityMorb.class, DataSerializers.BYTE);
	private static DataParameter<NBTTagCompound> ENTITY_DATA = EntityDataManager.createKey(EntityMorb.class, DataSerializers.COMPOUND_TAG);

	protected static ItemStack blockCheck = new ItemStack(Blocks.STONE);

	protected boolean drop = true;
	protected byte type = -1;
	protected NBTTagCompound entityData;

	public static void initialize(int id) {

		EntityRegistry.registerModEntity(new ResourceLocation("thermalexpansion:morb"), EntityMorb.class, "morb", id, ThermalExpansion.instance, CoreProps.ENTITY_TRACKING_DISTANCE, 10, true);
	}

	/* Required Constructor */
	public EntityMorb(World world) {

		super(world);
	}

	public EntityMorb(World world, EntityLivingBase thrower, byte type, NBTTagCompound entityData) {

		super(world, thrower);
		this.type = type;
		this.entityData = entityData;

		if (thrower instanceof EntityPlayer && ((EntityPlayer) thrower).capabilities.isCreativeMode || entityData != null && entityData.getBoolean(ItemMorb.GENERIC)) {
			drop = false;
		}
		setManager();
	}

	public EntityMorb(World world, double x, double y, double z, byte type, NBTTagCompound entityData) {

		super(world, x, y, z);
		this.type = type;
		this.entityData = entityData;

		if (thrower instanceof EntityPlayer && ((EntityPlayer) thrower).capabilities.isCreativeMode || entityData != null && entityData.getBoolean(ItemMorb.GENERIC)) {
			drop = false;
		}
		setManager();
	}

	private void setManager() {

		this.dataManager.set(TYPE, type);
		this.dataManager.set(ENTITY_DATA, entityData);
	}

	public int getType() {

		return type;
	}

	@Override
	public NBTTagCompound getEntityData() {

		return entityData;
	}

	@Override
	public void onEntityUpdate() {

		if (type < 0 && ServerHelper.isClientWorld(world)) {
			type = dataManager.get(TYPE);
		}
		if ((entityData == null || !entityData.hasKey("id")) && ServerHelper.isClientWorld(world)) {
			entityData = dataManager.get(ENTITY_DATA);
		}
		super.onEntityUpdate();
	}

	@Override
	protected void onImpact(RayTraceResult traceResult) {

		if (entityData == null || !entityData.hasKey("id")) {
			attemptCapture(traceResult);
		} else {
			attemptRelease(traceResult);
		}
	}

	private void attemptCapture(RayTraceResult traceResult) {

		BlockPos pos = new BlockPos(traceResult.hitVec);

		if (ServerHelper.isServerWorld(world)) {
			boolean noAccess = traceResult.sideHit != null && getThrower() instanceof EntityPlayer && !((EntityPlayer) getThrower()).canPlayerEdit(pos, traceResult.sideHit, blockCheck);

			if (traceResult.entityHit == null || EntityList.getKey(traceResult.entityHit) == null || !ItemMorb.validMobs.contains(EntityList.getKey(traceResult.entityHit).toString()) || noAccess) {
				ItemMorb.dropMorb(type, null, world, pos);
				this.setDead();
				return;
			}
			NBTTagCompound tag = traceResult.entityHit.serializeNBT();
			((WorldServer) world).spawnParticle(EnumParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, 2, 0, 0, 0, 0.0, 0);
			ItemMorb.dropMorb(type, tag, world, pos);
			traceResult.entityHit.setDead();
			this.setDead();
		}
	}

	private void attemptRelease(RayTraceResult traceResult) {

		BlockPos pos = new BlockPos(traceResult.hitVec);

		if (traceResult.entityHit != null) {
			pos = traceResult.entityHit.getPosition().add(0, 1, 0);
			traceResult.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0F);
		}
		if (ServerHelper.isServerWorld(world)) {
			if (traceResult.sideHit != null && getThrower() instanceof EntityPlayer && !((EntityPlayer) getThrower()).canPlayerEdit(pos, traceResult.sideHit, blockCheck)) {
				ItemMorb.dropMorb(type, entityData, world, pos);
				this.setDead();
				return;
			}
			double x = traceResult.hitVec.x;
			double y = traceResult.hitVec.y;
			double z = traceResult.hitVec.z;

			if (traceResult.sideHit != null) {
				x += traceResult.sideHit.getFrontOffsetX();
				z += traceResult.sideHit.getFrontOffsetZ();
			}
			spawnCreature(world, entityData, x, y, z);

			if (drop && (world.rand.nextInt(100) < ItemMorb.REUSE_CHANCE || type > 0)) {
				ItemMorb.dropMorb(type, null, world, pos);
			}
			this.setDead();
		}
	}

	@Override
	protected void entityInit() {

		dataManager.register(TYPE, type);
		dataManager.register(ENTITY_DATA, new NBTTagCompound());
	}

	public NBTTagCompound getEntity() {

		return entityData;
	}

	/* NBT METHODS */
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {

		super.readEntityFromNBT(nbt);

		drop = nbt.getBoolean("Drop");
		type = nbt.getByte("Type");
		entityData = nbt.getCompoundTag("EntityData");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {

		super.writeEntityToNBT(nbt);

		nbt.setBoolean("Drop", drop);
		nbt.setByte("Type", type);
		nbt.setTag("EntityData", entityData);
	}

	/* HELPERS */
	public static Entity spawnCreature(World world, NBTTagCompound entityData, double x, double y, double z) {

		if (entityData.getBoolean(ItemMorb.GENERIC)) {
			ItemMonsterPlacer.spawnCreature(world, new ResourceLocation(entityData.getString("id")), x, y, z);
		} else {
			Entity entity = EntityList.createEntityFromNBT(entityData, world);
			if (entity == null) {
				return null;
			}
			entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
			world.spawnEntity(entity);
			if (entity instanceof EntityLiving) {
				((EntityLiving) entity).playLivingSound();
			}
		}
		return null;
	}

}
