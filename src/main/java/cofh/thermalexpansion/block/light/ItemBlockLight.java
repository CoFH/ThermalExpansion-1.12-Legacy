package cofh.thermalexpansion.block.light;

import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemBlockLight extends ItemBlock {

	public static ItemStack setDefaultTag(ItemStack container, int style) {

		NBTTagCompound tag = new NBTTagCompound();
		tag.setByte("Style", (byte) style);
		container.setTagCompound(tag);
		return container;
	}

	public ItemBlockLight(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {

		return StringHelper.localize(getUnlocalizedName(stack));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.light." + BlockLight.NAMES[ItemHelper.getItemDamage(stack) % BlockLight.Types.values().length] + ".name";
	}

	@Override
	public int getMetadata(int i) {

		return i % BlockLight.Types.values().length;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		list.add(StringHelper.getInfoText("info.thermalexpansion.light.0"));
		list.add(StringHelper.getInfoText("info.thermalexpansion.light.1"));
		list.add(StringHelper.getNoticeText("info.thermalexpansion.multimeter"));
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ,
			int metadata) {

		if (!world.setBlock(x, y, z, field_150939_a, metadata, 3)) {
			return false;
		}

		if (world.getBlock(x, y, z) == field_150939_a) {
			field_150939_a.onBlockPlacedBy(world, x, y, z, player, stack);
			field_150939_a.onPostBlockPlaced(world, x, y, z, metadata);
			TileLight tile = (TileLight) world.getTileEntity(x, y, z);
			side = Math.min(6, side ^ 1);
			switch (tile.style) {
			case 1: // plate
			case 2: // button
			case 3: // tall
				tile.alignment = (byte) side;
				break;
			case 4: // wide
				int l = MathHelper.floor_float(player.rotationYaw * 4.0F / 360.0F + 0.5f) & 1;
				tile.alignment = (byte) (side | (l << 3));
				break;
			case 5: // torch
				tile.alignment = (byte) side;
				break;
			case 0:
			default:
				break;
			}
		}

		return true;
	}

}
