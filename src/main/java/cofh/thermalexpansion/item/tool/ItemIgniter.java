package cofh.thermalexpansion.item.tool;

import cofh.api.energy.IEnergyContainerItem;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemIgniter extends ItemEnergyContainerBase implements IEnergyContainerItem {

	public int range = 32;

	public ItemIgniter() {

		super("igniter");
		setMaxDamage(1);
		setMaxStackSize(1);
		setCreativeTab(ThermalExpansion.tabTools);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

		if (!player.capabilities.isCreativeMode && extractEnergy(stack, energyPerUse, true) != energyPerUse) {
			return stack;
		}
		MovingObjectPosition pos = player.isSneaking() ? BlockHelper.getCurrentMovingObjectPosition(player, true) : BlockHelper.getCurrentMovingObjectPosition(
				player, range, true);

		if (pos != null) {
			boolean success = false;
			int[] coords = BlockHelper.getAdjacentCoordinatesForSide(pos);
			world.playSoundEffect(coords[0] + 0.5D, coords[1] + 0.5D, coords[2] + 0.5D, "fire.ignite", 0.2F, MathHelper.RANDOM.nextFloat() * 0.4F + 0.8F);

			if (ServerHelper.isServerWorld(world)) {
				Block hitBlock = world.getBlock(pos.blockX, pos.blockY, pos.blockZ);
				if (hitBlock == Blocks.tnt) {
					world.setBlockToAir(pos.blockX, pos.blockY, pos.blockZ);
					((BlockTNT) hitBlock).func_150114_a(world, pos.blockX, pos.blockY, pos.blockZ, 1, player);
				} else {
					AxisAlignedBB axisalignedbb = BlockHelper.getAdjacentAABBForSide(pos);
					List<EntityCreeper> list = world.getEntitiesWithinAABB(EntityCreeper.class, axisalignedbb);
					if (!list.isEmpty()) {
						for (int i = 0; i < list.size(); i++) {
							list.get(i).func_146079_cb();
						}
						success = true;
					} else {
						Block block = world.getBlock(coords[0], coords[1], coords[2]);
						if (block != Blocks.fire && (block.isAir(world, coords[0], coords[1], coords[2]) || block.getMaterial().isReplaceable())) {
							success = world.setBlock(coords[0], coords[1], coords[2], Blocks.fire);
						}
					}
				}
				if (success) {
					player.openContainer.detectAndSendChanges();
					((EntityPlayerMP) player).sendContainerAndContentsToPlayer(player.openContainer, player.openContainer.getInventory());

					if (!player.capabilities.isCreativeMode) {
						extractEnergy(stack, energyPerUse, false);
					}
				}
			}
			player.swingItem();
		}
		return stack;
	}

}
