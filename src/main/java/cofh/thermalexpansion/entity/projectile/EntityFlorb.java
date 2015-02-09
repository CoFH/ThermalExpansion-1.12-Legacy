package cofh.thermalexpansion.entity.projectile;

import cofh.core.CoFHProps;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.item.ItemFlorb;
import cpw.mods.fml.common.registry.EntityRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class EntityFlorb extends EntityThrowable {

	public static void initialize() {

		EntityRegistry.registerModEntity(EntityFlorb.class, "florb", CoreUtils.getEntityId(), ThermalExpansion.instance, CoFHProps.ENTITY_TRACKING_DISTANCE, 1,
				true);
	}

	public static ItemStack blockCheck = new ItemStack(Blocks.stone);

	protected Fluid fluid;
	protected float gravity = 0.03F;

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

	protected void setGravity() {

		if (fluid.getDensity() < 0) {
			this.gravity = cofh.lib.util.helpers.MathHelper.minF(0.01F, 0.03F + 0.03F * fluid.getDensity() / 1000F);
		}
	}

	@Override
	protected void entityInit() {

		this.dataWatcher.addObject(16, Integer.valueOf(0));
	}

	public void setSyncFluid() {

		this.dataWatcher.updateObject(16, Integer.valueOf(fluid.getID()));
	}

	public Fluid getSyncFluid() {

		return FluidRegistry.getFluid(dataWatcher.getWatchableObjectInt(16));
	}

	public Fluid getFluid() {

		return fluid;
	}

	public void setFluid(Fluid fluid) {

		this.fluid = fluid;
	}

	@Override
	protected float getGravityVelocity() {

		return gravity;
	}

	@Override
	public void onEntityUpdate() {

		if (fluid == null && ServerHelper.isClientWorld(worldObj)) {
			fluid = getSyncFluid();
		}
		super.onEntityUpdate();
	}

	@Override
	protected void func_145775_I() {

		int i = MathHelper.floor_double(this.boundingBox.minX + 0.001D);
		int j = MathHelper.floor_double(this.boundingBox.minY + 0.001D);
		int k = MathHelper.floor_double(this.boundingBox.minZ + 0.001D);
		int l = MathHelper.floor_double(this.boundingBox.maxX - 0.001D);
		int i1 = MathHelper.floor_double(this.boundingBox.maxY - 0.001D);
		int j1 = MathHelper.floor_double(this.boundingBox.maxZ - 0.001D);

		if (this.worldObj.checkChunksExist(i, j, k, l, i1, j1)) {
			for (int k1 = i; k1 <= l; k1++) {
				for (int l1 = j; l1 <= i1; l1++) {
					for (int i2 = k; i2 <= j1; i2++) {
						Block block = this.worldObj.getBlock(k1, l1, i2);

						if (block != null) {
							block.onEntityCollidedWithBlock(this.worldObj, k1, l1, i2, this);
						}
					}
				}
			}
		}
	}

	@Override
	protected void onImpact(MovingObjectPosition pos) {

		int x = pos.blockX;
		int y = pos.blockY;
		int z = pos.blockZ;

		if (pos.entityHit != null) {
			x = (int) pos.entityHit.posX;
			y = (int) pos.entityHit.posY + 1;
			z = (int) pos.entityHit.posZ;

			pos.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0F);
		}

		if (worldObj.getBlock(x, y, z) != Blocks.snow_layer) {
			switch (pos.sideHit) {
			case 0:
				--y;
				break;
			case 1:
				++y;
				break;
			case 2:
				--z;
				break;
			case 3:
				++z;
				break;
			case 4:
				--x;
				break;
			case 5:
				++x;
				break;
			default:
			}
		}
		if (ServerHelper.isServerWorld(worldObj)) {
			if (getThrower() instanceof EntityPlayer && !((EntityPlayer) getThrower()).canPlayerEdit(x, y, z, pos.sideHit, blockCheck)) {
				ItemFlorb.dropFlorb(getFluid(), worldObj, x, y, z);
				this.setDead();
				return;
			}
			Block block = fluid.getBlock();

			if (fluid.getName() == "water") {
				block = Blocks.flowing_water;
			} else if (fluid.getName() == "lava") {
				block = Blocks.flowing_lava;
			}
			if (worldObj.isAirBlock(x, y, z) || worldObj.getBlock(x, y, z).getMaterial() == Material.fire || worldObj.getBlock(x, y, z) == Blocks.snow_layer) {
				if (fluid.getName() != "water" || !worldObj.getBiomeGenForCoords(x / 16, z / 16).biomeName.toLowerCase().equals("hell")) {
					worldObj.setBlock(x, y, z, block, 0, 3);
					worldObj.markBlockForUpdate(x, y, z);
				}
			} else {
				ItemFlorb.dropFlorb(getFluid(), worldObj, x, y, z);
			}
		}
		this.setDead();
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
