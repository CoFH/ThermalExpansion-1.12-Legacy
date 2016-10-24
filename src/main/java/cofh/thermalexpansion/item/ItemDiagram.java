package cofh.thermalexpansion.item;

import codechicken.lib.util.SoundUtils;
import cofh.api.tileentity.IPortableData;
import cofh.core.item.ItemBase;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.helpers.RedprintHelper;
import cofh.thermalexpansion.util.helpers.SchematicHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class ItemDiagram extends ItemBase {

    public ItemDiagram() {

        super("thermalexpansion");
        setCreativeTab(ThermalExpansion.tabItems);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {

        String baseName = StringHelper.localize(getUnlocalizedName(stack) + ".name");

        if (ItemHelper.getItemDamage(stack) == Types.SCHEMATIC.ordinal()) {
            return baseName + SchematicHelper.getOutputName(stack);
        } else if (ItemHelper.getItemDamage(stack) == Types.PATTERN.ordinal()) {
            return ""; // TODO: Implement patterns
        }
        return baseName + RedprintHelper.getName(stack);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {

        if (ItemHelper.getItemDamage(stack) == Types.SCHEMATIC.ordinal()) {
            return SchematicHelper.getOutputName(stack).isEmpty() ? EnumRarity.COMMON : EnumRarity.UNCOMMON;
        }
        if (ItemHelper.getItemDamage(stack) == Types.PATTERN.ordinal()) {
            return EnumRarity.COMMON;
        }
        return RedprintHelper.getName(stack).isEmpty() ? EnumRarity.COMMON : EnumRarity.UNCOMMON;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

        if (ItemHelper.getItemDamage(stack) == Types.SCHEMATIC.ordinal()) {
            SchematicHelper.addSchematicInformation(stack, list);
        } else if (ItemHelper.getItemDamage(stack) == Types.PATTERN.ordinal()) {

        } else {
            RedprintHelper.addRedprintInformation(stack, list);
        }
    }

    @Override
    public boolean isFull3D() {

        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (player.isSneaking()) {
            if (stack.getTagCompound() != null) {
                SoundUtils.playSoundAt(player, SoundCategory.NEUTRAL, SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH, 0.5F, 0.3F);
            }
            stack.setTagCompound(null);
        }
        player.swingArm(hand);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return EnumActionResult.SUCCESS;
    }

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (player.isSneaking()) {
            if (stack.getTagCompound() != null) {
                SoundUtils.playSoundAt(player, SoundCategory.NEUTRAL, SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH, 0.5F, 0.3F);
            }
            stack.setTagCompound(null);
        }
        if (stack.getItemDamage() != Types.REDPRINT.ordinal()) {
            return EnumActionResult.PASS;
        } else if (ServerHelper.isServerWorld(world)) {
            TileEntity tile = world.getTileEntity(pos);

            if (tile instanceof IPortableData) {
                if (stack.getTagCompound() == null) {
                    stack.setTagCompound(new NBTTagCompound());
                    ((IPortableData) tile).writePortableData(player, stack.getTagCompound());
                    if (stack.getTagCompound().hasNoTags()) {
                        stack.setTagCompound(null);
                    } else {
                        stack.getTagCompound().setString("Type", ((IPortableData) tile).getDataType());
                        SoundUtils.playSoundAt(player, SoundCategory.NEUTRAL, SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH, 0.5F, 0.7F);
                    }
                } else {
                    if (stack.getTagCompound().getString("Type").equals(((IPortableData) tile).getDataType())) {
                        ((IPortableData) tile).readPortableData(player, stack.getTagCompound());
                    }
                }
            }
        }
        ServerHelper.sendItemUsePacket(world, pos, side, hand, hitX, hitY, hitZ);
        return EnumActionResult.SUCCESS;
    }

    public enum Types {
        SCHEMATIC,
        REDPRINT,
        PATTERN
    }

}
