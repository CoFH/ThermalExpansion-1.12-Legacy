package thermalexpansion.item.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import thermalexpansion.ThermalExpansion;
import cofh.item.ItemBase;
import cofh.util.fluid.BucketHandler;

public class ItemBucket extends ItemBase {

	Item container = Items.bucket;

	public ItemBucket() {

		super("thermalexpansion");
		setMaxStackSize(1);
		setContainerItem(container);
		setCreativeTab(ThermalExpansion.tabTools);
	}

	public ItemBucket(Item container) {

		super("thermalexpansion");
		setMaxStackSize(1);
		this.container = container;
		setContainerItem(container);
		setCreativeTab(ThermalExpansion.tabTools);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

		MovingObjectPosition pos = this.getMovingObjectPositionFromPlayer(world, player, false);

		if (pos == null || pos.typeOfHit != MovingObjectType.BLOCK) {
			return stack;
		}
		int x = pos.blockX;
		int y = pos.blockY;
		int z = pos.blockZ;

		switch (pos.sideHit) {
		case 0:
			--y;
			break;
		case 1:
			++y;
			break;
		case 2:
			--z;
			break;
		case 3:
			++z;
			break;
		case 4:
			--x;
			break;
		case 5:
			++x;
			break;
		}
		if (!player.canPlayerEdit(x, y, z, pos.sideHit, stack) || !world.isAirBlock(x, y, z) && world.getBlock(x, y, z).getMaterial().isSolid()) {
			return stack;
		}
		if (BucketHandler.emptyBucket(world, x, y, z, stack)) {
			if (!player.capabilities.isCreativeMode) {
				return new ItemStack(container);
			}
		}
		return stack;
	}

}
