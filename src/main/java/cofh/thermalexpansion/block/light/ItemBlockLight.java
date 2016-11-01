package cofh.thermalexpansion.block.light;

import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
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
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		if (!world.setBlockState(pos, newState, 3)) {
			return false;
		}
		if (world.getBlockState(pos).getBlock() == block) {
            block.onBlockPlacedBy(world, pos, newState, player, stack);
            //block.onPostBlockPlaced(world, x, y, z, metadata);
			TileLight tile = (TileLight) world.getTileEntity(pos);
			side = EnumFacing.VALUES[Math.min(6, side.ordinal() ^ 1)];
			BlockLight.setTileAlignment(tile, player, stack, side, hitX, hitY, hitZ);
		}
		return true;
	}

}
