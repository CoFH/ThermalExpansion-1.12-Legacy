package cofh.thermalexpansion.block.simple;

import cofh.lib.util.helpers.MathHelper;
import cofh.thermalexpansion.block.TEBlocks;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlockAirForce extends BlockAirBase {

	public static final PropertyDirection FACING = PropertyDirection.create("facing");

    public BlockAirForce() {

		super(materialBarrier);
		disableStats();
	}

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (!world.isRemote && entity instanceof EntityPlayer) {
            absorbFallDamage(entity, pos);
            return;
		}

		int meta = getMetaFromState(state);
		EnumFacing dir = EnumFacing.VALUES[meta ^ 1];
		repositionEntity(world, pos, entity, dir, .1);
		/*
		 * can we dampen sound effects for an entity collided with this?
		 */
	}

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.VALUES[meta]);
    }

    private static void absorbFallDamage(Entity ent, BlockPos pos) {

		if (new AxisAlignedBB(pos).isVecInside(new  Vec3d(ent.posX, ent.posY, ent.posZ))) {
			ent.fallDistance *= 0.4;
			ent.motionY = 0;
		}
	}

	@SuppressWarnings("unused")
	public static void repositionEntity(World world, BlockPos pos, Entity ent, EnumFacing dir, double amount) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        double xO = dir.getFrontOffsetX() * amount;
        double yO = dir.getFrontOffsetY() * amount;
        double zO = dir.getFrontOffsetZ() * amount;
        absorbFallDamage(ent, pos);

		if (ent.getEntityData().getLong("te:conveyor") == world.getTotalWorldTime()) {
			return;
		}
		ent.getEntityData().setLong("te:conveyor", world.getTotalWorldTime());

		{
			if (!world.getCollisionBoxes(ent.getEntityBoundingBox()).isEmpty() || !world.getCollisionBoxes(ent.getEntityBoundingBox().offset(xO * 2, yO * 2, zO * 2)).isEmpty()) {
				xO = yO = zO = 0;
			}
			if (isZero(ent.motionX) && isZero(ent.motionZ)) {
				switch (dir.ordinal() >> 1) {
				case 0:
					xO += clampOffset(world, ent, (x - (ent.prevPosX - .5)) / 20, EnumFacing.EAST);
					zO += clampOffset(world, ent, (z - (ent.prevPosZ - .5)) / 20, EnumFacing.SOUTH);
					break;
				case 1:
					xO += clampOffset(world, ent, (x - (ent.prevPosX - .5)) / 20, EnumFacing.EAST);
					yO += clampOffset(world, ent, (y - (ent.prevPosY - .1)) / 20, EnumFacing.UP);
					break;
				case 2:
					yO += clampOffset(world, ent, (y - (ent.prevPosY - .1)) / 20, EnumFacing.UP);
					zO += clampOffset(world, ent, (z - (ent.prevPosZ - .5)) / 20, EnumFacing.SOUTH);
					break;
				}
			} else if (false) {
				double a = Math.atan2(ent.motionX, ent.motionZ), s = Math.sqrt(ent.motionX * ent.motionX + ent.motionZ * ent.motionZ);
				System.out.format(a + " " + ((ent.rotationYaw * Math.PI / 180) % Math.PI) + "\n");
				double a2 = ent.rotationPitch * Math.PI / 180 * (1 - Math.abs(Math.sin(((ent.rotationYaw * Math.PI / 180) % Math.PI) - a)));
				// FIXME: get correct pitch, when movement is sideways (a/d) don't apply vertical movement
				double v = Math.cos(a2);
				xO += s * Math.sin(a) * v;
				zO += s * Math.cos(a) * v;
				yO += s * Math.sin(a2);
			} else {
				xO += ent.motionX;
				zO += ent.motionZ;
			}
			if (dir == EnumFacing.UP && yO > 0 && MathHelper.floor(ent.prevPosY + yO) != y) {
				if (world.getBlockState(new BlockPos(x, y + 1, z)).getBlock() != TEBlocks.blockAirForce) {
					yO = 0;
				}
			}
			if (ent instanceof EntityLivingBase) {
				((EntityLivingBase) ent).setPositionAndUpdate(ent.prevPosX + xO, ent.prevPosY + yO, ent.prevPosZ + zO);
			} else {
				ent.setLocationAndAngles(ent.prevPosX + xO, ent.prevPosY + yO, ent.prevPosZ + zO, ent.rotationYaw, ent.rotationPitch);
			}
			ent.lastTickPosX = ent.posX - xO;
			ent.lastTickPosY = ent.posY - yO;
			ent.lastTickPosZ = ent.posZ - zO;
			ent.motionX *= .5;
			ent.motionZ *= .5;
			ent.motionY = 0;
			ent.onGround = false;
		}
	}

	private static double clampOffset(World world, Entity ent, double offset, EnumFacing axis) {

		double xO = axis.getFrontOffsetX() * offset;
        double yO = axis.getFrontOffsetY() * offset;
        double zO = axis.getFrontOffsetZ() * offset;
        if (!world.getCollisionBoxes(ent.getEntityBoundingBox()).isEmpty() || !world.getCollisionBoxes(ent.getEntityBoundingBox().offset(xO, yO, zO)).isEmpty()) {
			return 0;
		}
		return offset;
	}

	private static boolean isZero(double x) {

		return -.025 <= x & x <= .025;
	}

}
