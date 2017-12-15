package cofh.thermalexpansion.util;

import cofh.thermalexpansion.entity.projectile.EntityMorb;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BehaviorMorbDispense extends BehaviorDefaultDispenseItem {

    protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {

        World world = source.getWorld();
        IPosition pos = BlockDispenser.getDispensePosition(source);
        EnumFacing facing = source.getBlockState().getValue(BlockDispenser.FACING);

        EntityMorb morb = new EntityMorb(world, pos.getX(), pos.getY(), pos.getZ(), stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound());
        morb.shoot((double) facing.getFrontOffsetX(), (double) ((float) facing.getFrontOffsetY() + 0.1F), (double) facing.getFrontOffsetZ(), getProjectileVelocity(), getProjectileInaccuracy());
        world.spawnEntity(morb);
        stack.shrink(1);
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