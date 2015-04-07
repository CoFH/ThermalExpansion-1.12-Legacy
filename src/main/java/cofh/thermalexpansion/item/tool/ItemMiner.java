package cofh.thermalexpansion.item.tool;

import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemMiner extends ItemEnergyContainerBase {

	static final int MAX_DURATION = 72000;
	int energyPerUse = 1;
	int range = 128;

	public ItemMiner() {

		super("miningBeam");
		setMaxDamage(1);
		setMaxStackSize(1);
		setCreativeTab(ThermalExpansion.tabTools);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {

		if (MAX_DURATION - count < 20) {
			return;
		}
		if (ServerHelper.isServerWorld(player.worldObj)) {
			doBeam(stack, player.worldObj, player);

			if (!player.capabilities.isCreativeMode) {
				extractEnergy(stack, energyPerUse, false);
			}
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {

		return MAX_DURATION;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {

		return EnumAction.bow;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

		return false;
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

		boolean r = doItemUse(stack, world, player);
		if (r) { // HACK: forge is fucking stupid with this method
			ServerHelper.sendItemUsePacket(stack, player, world, x, y, z, hitSide, hitX, hitY, hitZ);
		}
		return r;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

		doItemUse(stack, world, player);
		return stack;
	}

	public boolean doItemUse(ItemStack stack, World world, EntityPlayer player) {

		if (!player.capabilities.isCreativeMode && extractEnergy(stack, energyPerUse, true) != energyPerUse) {
			return false;
		}
		player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
		return false;
	}

	public boolean doBeam(ItemStack stack, World world, EntityPlayer player) {

		MovingObjectPosition pos = BlockHelper.getCurrentMovingObjectPosition(player, range, true);

		if (!player.capabilities.isCreativeMode && extractEnergy(stack, energyPerUse, true) != energyPerUse) {
			player.stopUsingItem();
			return false;
		}
		if (pos != null) {
			Block block = world.getBlock(pos.blockX, pos.blockY, pos.blockZ);

			int x = pos.blockX;
			int y = pos.blockY;
			int z = pos.blockZ;
			List<ItemStack> drops = new ArrayList<ItemStack>();

			switch (pos.sideHit) {
			case 0:
			case 1:
				for (x = pos.blockX - 1; x <= pos.blockX + 1; x++) {
					for (z = pos.blockZ - 1; z <= pos.blockZ + 1; z++) {
						if (block.getPlayerRelativeBlockHardness(player, world, x, y, z) > -1F) {
							drops.addAll(BlockHelper.breakBlock(world, x, y, z, block, 0, true, false));
						}
					}
				}
				break;
			case 2:
			case 3:
				for (x = pos.blockX - 1; x <= pos.blockX + 1; x++) {
					for (y = pos.blockY - 1; y <= pos.blockY + 1; y++) {
						if (block.getPlayerRelativeBlockHardness(player, world, x, y, z) > -1F) {
							drops.addAll(BlockHelper.breakBlock(world, x, y, z, block, 0, true, false));
						}
					}
				}
				break;
			default:
				for (y = pos.blockY - 1; y <= pos.blockY + 1; y++) {
					for (z = pos.blockZ - 1; z <= pos.blockZ + 1; z++) {
						if (block.getPlayerRelativeBlockHardness(player, world, x, y, z) > -1F) {
							drops.addAll(BlockHelper.breakBlock(world, x, y, z, block, 0, true, false));
						}
					}
				}
				break;
			}
			for (int drop = 0; drop < drops.size(); drop++) {
				EntityItem item = new EntityItem(world, pos.blockX, pos.blockY, pos.blockZ, drops.get(drop));
				item.delayBeforeCanPickup = 10;
				world.spawnEntityInWorld(item);
			}
		}
		return false;
	}
}
