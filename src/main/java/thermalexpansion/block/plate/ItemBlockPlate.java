package thermalexpansion.block.plate;

import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemBlockPlate extends ItemBlock {

	public ItemBlockPlate(Block block) {

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

		return "tile.thermalexpansion.plate." + BlockPlate.NAMES[ItemHelper.getItemDamage(stack)] + ".name";
	}

	@Override
	public int getMetadata(int i) {

		return i;
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ,
			int metadata) {

		if (super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof TilePlateBase) {
				((TilePlateBase) te).setAlignment(side ^ 1, hitX - 0.5f, hitY - 0.5f, hitZ - 0.5f);
			}
			return true;
		}
		return false;
	}

}
