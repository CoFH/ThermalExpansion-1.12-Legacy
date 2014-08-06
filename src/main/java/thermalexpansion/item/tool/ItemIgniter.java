package thermalexpansion.item.tool;

import cofh.api.energy.IEnergyContainerItem;
import cofh.core.item.ItemBase;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import thermalexpansion.ThermalExpansion;

public class ItemIgniter extends ItemBase implements IEnergyContainerItem {

	public int maxEnergy = 80000;
	public int maxTransfer = 160;
	public int energyPerUse = 400;
	public int range = 32;

	public ItemIgniter() {

		super("thermalexpansion");
		setMaxDamage(1);
		setMaxStackSize(1);
		setCreativeTab(ThermalExpansion.tabTools);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {

		list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item, 1, 0), 0));
		list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item, 1, 0), maxEnergy));
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		if (stack.stackTagCompound == null) {
			EnergyHelper.setDefaultEnergyTag(stack, 0);
		}
		list.add(StringHelper.localize("info.cofh.charge") + ": " + stack.stackTagCompound.getInteger("Energy") + " / " + maxEnergy + " RF");
		list.add(StringHelper.ORANGE + energyPerUse + " RF " + StringHelper.localize("info.cofh.perUse") + StringHelper.END);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

		if (!player.capabilities.isCreativeMode && extractEnergy(stack, energyPerUse, true) != energyPerUse) {
			return stack;
		}
		MovingObjectPosition pos = player.isSneaking() ? BlockHelper.getCurrentMovingObjectPosition(player) : BlockHelper.getCurrentMovingObjectPosition(
				player, range);

		if (pos != null) {
			int[] coords = BlockHelper.getAdjacentCoordinatesForSide(pos);
			world.playSoundEffect(coords[0] + 0.5D, coords[1] + 0.5D, coords[2] + 0.5D, "fire.ignite", 1.0F, MathHelper.RANDOM.nextFloat() * 0.4F + 0.8F);

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
					} else if (world.getBlock(coords[0], coords[1], coords[2]).isAir(world, coords[0], coords[1], coords[2])) {
						world.setBlock(coords[0], coords[1], coords[2], Blocks.fire);
					}
				}
			}
			if (!player.capabilities.isCreativeMode) {
				extractEnergy(stack, energyPerUse, false);
			}
			player.swingItem();
		}
		return stack;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

		player.swingItem();
		return false;
	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public boolean isDamaged(ItemStack stack) {

		return stack.getItemDamage() != Short.MAX_VALUE;
	}

	@Override
	public int getDisplayDamage(ItemStack stack) {

		if (stack.stackTagCompound == null) {
			EnergyHelper.setDefaultEnergyTag(stack, 0);
		}
		return 1 + maxEnergy - stack.stackTagCompound.getInteger("Energy");
	}

	@Override
	public int getMaxDamage(ItemStack stack) {

		return 1 + maxEnergy;
	}

	/* IEnergyContainerItem */
	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {

		if (container.stackTagCompound == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		int stored = container.stackTagCompound.getInteger("Energy");
		int receive = Math.min(maxReceive, Math.min(maxEnergy - stored, maxTransfer));

		if (!simulate) {
			stored += receive;
			container.stackTagCompound.setInteger("Energy", stored);
		}
		return receive;
	}

	@Override
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {

		if (container.stackTagCompound == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		int stored = container.stackTagCompound.getInteger("Energy");
		int extract = Math.min(maxExtract, stored);

		if (!simulate) {
			stored -= extract;
			container.stackTagCompound.setInteger("Energy", stored);
		}
		return extract;
	}

	@Override
	public int getEnergyStored(ItemStack container) {

		if (container.stackTagCompound == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		return container.stackTagCompound.getInteger("Energy");
	}

	@Override
	public int getMaxEnergyStored(ItemStack container) {

		return maxEnergy;
	}

}
