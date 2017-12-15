package cofh.thermalexpansion.entity.projectile;

import cofh.core.init.CoreProps;
import cofh.core.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.item.ItemMorb;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityMorb extends EntityThrowable {

    private static DataParameter<NBTTagCompound> ENTITY_DATA = EntityDataManager.createKey(EntityMorb.class, DataSerializers.COMPOUND_TAG);

    protected static ItemStack blockCheck = new ItemStack(Blocks.STONE);

    protected NBTTagCompound entityData;

    public static void initialize(int id) {

        EntityRegistry.registerModEntity(new ResourceLocation("thermalexpansion:morb"), EntityMorb.class, "morb", id, ThermalExpansion.instance, CoreProps.ENTITY_TRACKING_DISTANCE, 10, true);
    }

    /* Required Constructor */
    public EntityMorb(World world) {

        super(world);
    }

    /* Fluid Constructors */
    public EntityMorb(World world, EntityLivingBase thrower, NBTTagCompound entityData) {

        super(world, thrower);
        this.entityData = entityData;

        setSyncEntity();
    }

    public EntityMorb(World world, double x, double y, double z, NBTTagCompound entityData) {

        super(world, x, y, z);
        this.entityData = entityData;

        setSyncEntity();
    }

    private void setSyncEntity() {

        this.dataManager.set(ENTITY_DATA, entityData);
    }

    private NBTTagCompound getSyncEntity() {

        return dataManager.get(ENTITY_DATA);
    }

    @Override
    public NBTTagCompound getEntityData() {

        return entityData;
    }

    @Override
    public void onEntityUpdate() {

        if ((entityData == null || !entityData.hasKey("id")) && ServerHelper.isClientWorld(world)) {
            entityData = getSyncEntity();
        }
        super.onEntityUpdate();
    }

    @Override
    protected void onImpact(RayTraceResult traceResult) {

        if(entityData == null || !entityData.hasKey("id")) {
            attemptCapture(traceResult);
        }
        else {
            attemptRelease(traceResult);
        }
    }

    private void attemptCapture(RayTraceResult traceResult) {

        BlockPos pos = new BlockPos(traceResult.hitVec);

        if (ServerHelper.isServerWorld(world)) {
            boolean noAccess = traceResult.sideHit != null && getThrower() instanceof EntityPlayer && !((EntityPlayer) getThrower()).canPlayerEdit(pos, traceResult.sideHit, blockCheck);

            if (traceResult.entityHit == null || EntityList.getKey(traceResult.entityHit) == null || !ItemMorb.morbMap.containsKey(EntityList.getKey(traceResult.entityHit).toString()) || noAccess) {
                ItemMorb.dropMorb(null, world, pos);
                this.setDead();
                return;
            }

            NBTTagCompound tag = traceResult.entityHit.serializeNBT();
            ((WorldServer)world).spawnParticle(EnumParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, 2,0, 0, 0, 0.0, 0);
            ItemMorb.dropMorb(tag, world, pos);
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
                ItemMorb.dropMorb(entityData, world, pos);
                this.setDead();
                return;
            }

            Entity entity = EntityList.createEntityFromNBT(entityData, world);
            if(entity == null) {
                this.setDead();
                return;
            }

            double x = traceResult.hitVec.x;
            double y = traceResult.hitVec.y;
            double z = traceResult.hitVec.z;

            if(traceResult.sideHit != null) {
                x += traceResult.sideHit.getFrontOffsetX();
                z += traceResult.sideHit.getFrontOffsetZ();
            }

            entity.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360.0f, 0.0f);
            world.spawnEntity(entity);

            if(entity instanceof EntityLiving) {
                ((EntityLiving) entity).playLivingSound();
            }

            this.setDead();
        }
    }

    @Override
    protected void entityInit() {

        dataManager.register(ENTITY_DATA, new NBTTagCompound());
    }

    public NBTTagCompound getEntity() {

        return entityData;
    }

    /* NBT METHODS */
    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {

        super.readEntityFromNBT(nbt);

        entityData = nbt.getCompoundTag("Data");
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {

        super.writeEntityToNBT(nbt);

        nbt.setTag("Data", entityData);
    }
}
