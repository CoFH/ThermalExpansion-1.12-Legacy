package cofh.thermalexpansion.item;

import cofh.api.tileentity.IPortableData;
import cofh.core.item.ItemBase;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.RedprintHelper;
import cofh.thermalexpansion.util.SchematicHelper;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

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
			return SchematicHelper.getOutputName(stack).isEmpty() ? EnumRarity.common : EnumRarity.uncommon;
		}
		if (ItemHelper.getItemDamage(stack) == Types.PATTERN.ordinal()) {
			return EnumRarity.common;
		}
		return RedprintHelper.getName(stack).isEmpty() ? EnumRarity.common : EnumRarity.uncommon;
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
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

		if (player.isSneaking()) {
			if (stack.getTagCompound() != null) {
				world.playSoundAtEntity(player, "random.orb", 0.5F, 0.3F);
			}
			stack.setTagCompound(null);
		}
		player.swingItem();
		return stack;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

		return true;
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

		if (player.isSneaking()) {
			if (stack.getTagCompound() != null) {
				world.playSoundAtEntity(player, "random.orb", 0.5F, 0.3F);
			}
			stack.setTagCompound(null);
		}
		if (stack.getItemDamage() != Types.REDPRINT.ordinal()) {
			return false;
		} else if (ServerHelper.isServerWorld(world)) {
			TileEntity tile = world.getTileEntity(x, y, z);

			if (tile instanceof IPortableData) {
				if (stack.stackTagCompound == null) {
					stack.setTagCompound(new NBTTagCompound());
					((IPortableData) tile).writePortableData(player, stack.stackTagCompound);
					if (stack.stackTagCompound.hasNoTags()) {
						stack.setTagCompound(null);
					} else {
						stack.stackTagCompound.setString("Type", ((IPortableData) tile).getDataType());
						world.playSoundAtEntity(player, "random.orb", 0.5F, 0.7F);
					}
				} else {
					if (stack.stackTagCompound.getString("Type").equals(((IPortableData) tile).getDataType())) {
						((IPortableData) tile).readPortableData(player, stack.stackTagCompound);
					}
				}
			}
		}
		ServerHelper.sendItemUsePacket(stack, player, world, x, y, z, hitSide, hitX, hitY, hitZ);
		return true;
	}

	public enum Types {
		SCHEMATIC, REDPRINT, PATTERN
	}

}
