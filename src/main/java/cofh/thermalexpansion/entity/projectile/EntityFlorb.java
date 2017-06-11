package cofh.thermalexpansion.entity.projectile;

import cofh.core.init.CoreProps;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.item.ItemFlorb;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityFlorb extends EntityThrowable {

	private static DataParameter<String> FLUID = EntityDataManager.createKey(EntityFlorb.class, DataSerializers.STRING);

	protected static ItemStack blockCheck = new ItemStack(Blocks.STONE);

	protected float gravity = 0.03F;
	protected Fluid fluid;

	public static void initialize(int id) {

		EntityRegistry.registerModEntity(EntityFlorb.class, "florb", id, ThermalExpansion.instance, CoreProps.ENTITY_TRACKING_DISTANCE, 1, true);
	}

	/* Required Constructor */
	public EntityFlorb(World world) {

		super(world);
	}

	/* Fluid Constructors */
	public EntityFlorb(World world, Fluid fluid) {

		super(world);
		this.fluid = fluid;

		setGravity();
		setSyncFluid();
	}

	public EntityFlorb(World world, EntityLivingBase entity, Fluid fluid) {

		super(world, entity);
		this.fluid = fluid;

		setHeadingFromThrower(entity, entity.rotationPitch, entity.rotationYaw, 0.0F, 1.5F, 1.0F);

		setGravity();
		setSyncFluid();
	}

	public EntityFlorb(World world, double x, double y, double z, Fluid fluid) {

		super(world, x, y, z);
		this.fluid = fluid;

		setGravity();
		setSyncFluid();
	}

	/* Velocity Sensitive Constructors */
	public EntityFlorb(World world, Fluid fluid, int velocity) {

		this(world, fluid);
		this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, velocity, 1.0F);
	}

	public EntityFlorb(World world, EntityLivingBase entity, Fluid fluid, int velocity) {

		this(world, entity, fluid);
		this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, velocity, 1.0F);
	}

	public EntityFlorb(World world, double x, double y, double z, Fluid fluid, int velocity) {

		this(world, x, y, z, fluid);
		this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, velocity, 1.0F);
	}

	public EntityFlorb setFluid(Fluid fluid) {

		this.fluid = fluid;

		return this;
	}

	private void setGravity() {

		if (fluid.getDensity() < 0) {
			this.gravity = cofh.lib.util.helpers.MathHelper.minF(0.01F, 0.03F + 0.03F * fluid.getDensity() / 1000F);
		}
	}

	private void setSyncFluid() {

		this.dataManager.set(FLUID, fluid.getName());
	}

	private Fluid getSyncFluid() {

		return FluidRegistry.getFluid(dataManager.get(FLUID));
	}

	@Override
	protected void doBlockCollisions() {

		int i = MathHelper.floor_double(this.getEntityBoundingBox().minX + 0.001D);
		int j = MathHelper.floor_double(this.getEntityBoundingBox().minY + 0.001D);
		int k = MathHelper.floor_double(this.getEntityBoundingBox().minZ + 0.001D);
		int l = MathHelper.floor_double(this.getEntityBoundingBox().maxX - 0.001D);
		int i1 = MathHelper.floor_double(this.getEntityBoundingBox().maxY - 0.001D);
		int j1 = MathHelper.floor_double(this.getEntityBoundingBox().maxZ - 0.001D);

		if (this.worldObj.isAreaLoaded(new BlockPos(i, j, k), new BlockPos(l, i1, j1))) {
			for (int k1 = i; k1 <= l; k1++) {
				for (int l1 = j; l1 <= i1; l1++) {
					for (int i2 = k; i2 <= j1; i2++) {
						IBlockState state = this.worldObj.getBlockState(new BlockPos(k1, l1, i2));

						if (state != null) {
							state.getBlock().onEntityCollidedWithBlock(this.worldObj, new BlockPos(k1, l1, i2), state, this);
						}
					}
				}
			}
		}
	}

	@Override
	public void onEntityUpdate() {

		if (fluid == null && ServerHelper.isClientWorld(worldObj)) {
			fluid = getSyncFluid();
		}
		super.onEntityUpdate();
	}

	@Override
	protected void onImpact(RayTraceResult traceResult) {

		BlockPos pos = traceResult.getBlockPos();

		if (traceResult.entityHit != null) {
			pos = traceResult.entityHit.getPosition().add(0, 1, 0);
			traceResult.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0F);
		}

		if (traceResult.sideHit != null && worldObj.getBlockState(pos).getBlock() != Blocks.SNOW_LAYER) {
			pos = pos.offset(traceResult.sideHit);
		}
		if (ServerHelper.isServerWorld(worldObj)) {
			if (traceResult.sideHit != null && getThrower() instanceof EntityPlayer && !((EntityPlayer) getThrower()).canPlayerEdit(pos, traceResult.sideHit, blockCheck)) {
				ItemFlorb.dropFlorb(getFluid(), worldObj, pos);
				this.setDead();
				return;
			}
			Block block = fluid.getBlock();
			IBlockState state = worldObj.getBlockState(pos);

			if ("water".equals(fluid.getName())) {
				block = Blocks.FLOWING_WATER;
			} else if ("lava".equals(fluid.getName())) {
				block = Blocks.FLOWING_LAVA;
			}
			if (worldObj.isAirBlock(pos) || state.getMaterial() == Material.FIRE || state.getBlock() == Blocks.SNOW_LAYER) {
				if (!fluid.getName().equals("water") || !worldObj.getBiome(pos).getBiomeName().toLowerCase().equals("hell")) {
					worldObj.setBlockState(pos, block.getDefaultState(), 3);
					worldObj.notifyBlockUpdate(pos, state, state, 3);
				}
			} else {
				ItemFlorb.dropFlorb(getFluid(), worldObj, pos);
			}
		}
		this.setDead();
	}

	@Override
	protected void entityInit() {

		dataManager.register(FLUID, "");
	}

	@Override
	protected float getGravityVelocity() {

		return gravity;
	}

	public Fluid getFluid() {

		return fluid;
	}

	/* NBT METHODS */
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {

		super.readEntityFromNBT(nbt);

		fluid = FluidRegistry.getFluid(nbt.getString("Fluid"));

		if (fluid == null) {
			fluid = FluidRegistry.WATER;
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {

		super.writeEntityToNBT(nbt);

		nbt.setString("Fluid", fluid.getName());
	}

}
