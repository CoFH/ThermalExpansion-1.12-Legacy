package cofh.thermalexpansion.item.tool;

import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.util.SoundUtils;
import codechicken.lib.vec.Vector3;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.item.TEItems;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

import java.util.List;

public class ItemChiller extends ItemEnergyContainerBase {

    public int range = 32;

    public ItemChiller() {

        super("chiller");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (!player.capabilities.isCreativeMode && extractEnergy(stack, energyPerUse, true) != energyPerUse) {
            return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
        }
        RayTraceResult traceResult = player.isSneaking() ? RayTracer.retrace(player, true) : RayTracer.retrace(player, range, true);

        if (traceResult != null) {
            boolean success = false;
            BlockPos pos = traceResult.getBlockPos();
            BlockPos offsetPos = traceResult.getBlockPos().offset(traceResult.sideHit);

            int[] coords = BlockHelper.getAdjacentCoordinatesForSide(traceResult);
            SoundUtils.playSoundAt(new Vector3(offsetPos).add(0.5), world, SoundCategory.BLOCKS, SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH, 0.2F, MathHelper.RANDOM.nextFloat() * 0.4F + 0.8F);

            if (ServerHelper.isServerWorld(world)) {
                AxisAlignedBB axisalignedbb = BlockHelper.getAdjacentAABBForSide(traceResult);
                List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
                if (!list.isEmpty()) {
                    for (EntityLivingBase livingBase : list) {
                        livingBase.extinguish();
                    }
                    success = true;
                } else {
                    IBlockState state = world.getBlockState(pos);
                    Block block = state.getBlock();

                    if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER) && block.getMetaFromState(state) == 0) {
                        success = world.setBlockState(pos, Blocks.ICE.getDefaultState(), 3);
                    } else if ((block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) && block.getMetaFromState(state) == 0) {
                        success = world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState(), 3);
                    } else if (block != Blocks.SNOW_LAYER && Blocks.SNOW_LAYER.canPlaceBlockAt(world, offsetPos)) {
                        success = world.setBlockState(offsetPos, Blocks.SNOW_LAYER.getDefaultState(), 3);
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

        ModelResourceLocation location = new ModelResourceLocation(ThermalExpansion.modId + ":tool", "type=chiller");
        ModelLoader.setCustomModelResourceLocation(TEItems.itemChiller, 0, location);
    }
}
