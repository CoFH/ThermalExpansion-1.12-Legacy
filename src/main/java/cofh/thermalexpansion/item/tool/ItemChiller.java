package cofh.thermalexpansion.item.tool;

import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemChiller extends ItemEnergyContainerBase {

	public int range = 32;

	public ItemChiller() {

		super("chiller");
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
			world.playSoundEffect(coords[0] + 0.5D, coords[1] + 0.5D, coords[2] + 0.5D, "random.orb", 0.2F, MathHelper.RANDOM.nextFloat() * 0.4F + 0.8F);

			if (ServerHelper.isServerWorld(world)) {
				AxisAlignedBB axisalignedbb = BlockHelper.getAdjacentAABBForSide(pos);
				List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
				if (!list.isEmpty()) {
					for (int i = 0; i < list.size(); i++) {
						list.get(i).extinguish();
					}
					success = true;
				} else {
					Block block = world.getBlock(pos.blockX, pos.blockY, pos.blockZ);

					if (world.getBlockMetadata(pos.blockX, pos.blockY, pos.blockZ) == 0 && (block == Blocks.water || block == Blocks.flowing_water)) {
						success = world.setBlock(pos.blockX, pos.blockY, pos.blockZ, Blocks.ice, 0, 3);
					} else if (world.getBlockMetadata(pos.blockX, pos.blockY, pos.blockZ) == 0 && (block == Blocks.lava || block == Blocks.flowing_lava)) {
						success = world.setBlock(pos.blockX, pos.blockY, pos.blockZ, Blocks.obsidian, 0, 3);
					} else if (Blocks.snow_layer.canPlaceBlockAt(world, coords[0], coords[1], coords[2])) {
						success = world.setBlock(coords[0], coords[1], coords[2], Blocks.snow_layer, 0, 3);
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
