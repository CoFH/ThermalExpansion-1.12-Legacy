package cofh.thermalexpansion.item.tool;

import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.util.SoundUtils;
import codechicken.lib.vec.Vector3;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.item.TEItems;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

import java.util.List;

public class ItemIgniter extends ItemEnergyContainerBase {

    public int range = 32;

    public ItemIgniter() {

        super("igniter");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (!player.capabilities.isCreativeMode && extractEnergy(stack, energyPerUse, true) != energyPerUse) {
            return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
        }
        RayTraceResult traceResult = player.isSneaking() ? RayTracer.retrace(player, true) : RayTracer.retrace(player, range, true);

        if (traceResult != null) {
            boolean success = false;
            BlockPos pos = traceResult.getBlockPos();
            BlockPos offsetPos = traceResult.getBlockPos().offset(traceResult.sideHit);

            SoundUtils.playSoundAt(new Vector3(offsetPos).add(0.5D), world, SoundCategory.BLOCKS, SoundEvents.ITEM_FLINTANDSTEEL_USE, 0.2F, MathHelper.RANDOM.nextFloat() * 0.4F + 0.8F);

            if (ServerHelper.isServerWorld(world)) {
                IBlockState hitState = world.getBlockState(pos);
                if (hitState.getBlock() == Blocks.TNT) {
                    world.setBlockToAir(pos);
                    ((BlockTNT) hitState.getBlock()).explode(world, pos, hitState.withProperty(BlockTNT.EXPLODE, true), player);
                } else {
                    AxisAlignedBB axisalignedbb = BlockHelper.getAdjacentAABBForSide(traceResult);
                    List<EntityCreeper> list = world.getEntitiesWithinAABB(EntityCreeper.class, axisalignedbb);
                    if (!list.isEmpty()) {
                        for (EntityCreeper creeper : list) {
                            creeper.ignite();
                        }
                        success = true;
                    } else {
                        IBlockState offsetState = world.getBlockState(offsetPos);
                        if (offsetState.getBlock() != Blocks.FIRE && (offsetState.getBlock().isAir(offsetState, world, offsetPos) || offsetState.getMaterial().isReplaceable())) {
                            success = world.setBlockState(offsetPos, Blocks.FIRE.getDefaultState());
                        }
                    }
                }
                if (success) {
                    player.openContainer.detectAndSendChanges();
                    ((EntityPlayerMP) player).updateCraftingInventory(player.openContainer, player.openContainer.getInventory());

                    if (!player.capabilities.isCreativeMode) {
                        extractEnergy(stack, energyPerUse, false);
                    }
                }
            }
            player.swingArm(hand);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }

    public void registerModelVariants() {

        ModelResourceLocation location = new ModelResourceLocation(ThermalExpansion.modId + ":tool", "type=igniter");
        ModelLoader.setCustomModelResourceLocation(TEItems.itemIgniter, 0, location);
    }
}
