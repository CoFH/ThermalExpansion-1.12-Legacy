package cofh.thermalexpansion.util;

import cofh.thermalexpansion.entity.projectile.EntityFlorb;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class BehaviorFlorbDispense extends BehaviorDefaultDispenseItem {

	protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {

		World world = source.getWorld();
		IPosition pos = BlockDispenser.getDispensePosition(source);
		EnumFacing facing = source.getBlockState().getValue(BlockDispenser.FACING);

		Fluid fluid = stack.hasTagCompound() ? FluidRegistry.getFluid(stack.getTagCompound().getString("Fluid")) : null;

		if (fluid != null) {
			EntityFlorb florb = new EntityFlorb(world, pos.getX(), pos.getY(), pos.getZ(), fluid);
			florb.setThrowableHeading((double) facing.getFrontOffsetX(), (double) ((float) facing.getFrontOffsetY() + 0.1F), (double) facing.getFrontOffsetZ(), getProjectileVelocity(), getProjectileInaccuracy());
			world.spawnEntity(florb);
			stack.shrink(1);
		} else {
			BehaviorDefaultDispenseItem.doDispense(world, stack.splitStack(1), 6, facing, pos);
		}
		return stack;
	}

	protected void playDispenseSound(IBlockSource source) {

		source.getWorld().playEvent(1002, source.getBlockPos(), 0);
	}

	protected float getProjectileInaccuracy() {

		return 6.0F;
	}

	protected float getProjectileVelocity() {

		return 1.1F;
	}

}
