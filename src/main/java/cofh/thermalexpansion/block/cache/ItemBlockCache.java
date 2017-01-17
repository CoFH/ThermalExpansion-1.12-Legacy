package cofh.thermalexpansion.block.cache;

import cofh.core.block.ItemBlockCore;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockCache extends ItemBlockCore {

	public ItemBlockCache(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(1);
		setNoRepair();
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {

		if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("Item")) {
			return super.getItemStackLimit(stack);
		}
		return 64;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.cache." + BlockCache.NAMES[ItemHelper.getItemDamage(stack)] + ".name";
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		switch (BlockCache.Types.values()[ItemHelper.getItemDamage(stack)]) {
			case CREATIVE:
				return EnumRarity.EPIC;
			case RESONANT:
				return EnumRarity.RARE;
			case REINFORCED:
				return EnumRarity.UNCOMMON;
			default:
				return EnumRarity.COMMON;
		}
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		list.add(StringHelper.localize("info.cofh.capacity") + ": " + TileCache.CAPACITY[ItemHelper.getItemDamage(stack)]);
		if (stack.getTagCompound() == null) {
			list.add(StringHelper.localize("info.cofh.empty"));
			return;
		}
		boolean lock = stack.getTagCompound().getBoolean("Lock");

		if (lock) {
			list.add(StringHelper.localize("info.cofh.locked"));
		} else {
			list.add(StringHelper.localize("info.cofh.unlocked"));
		}
		list.add(StringHelper.localize("info.cofh.contents") + ":");

		if (stack.getTagCompound().hasKey("Item")) {
			ItemStack stored = ItemHelper.readItemStackFromNBT(stack.getTagCompound().getCompoundTag("Item"));
			list.add("    " + StringHelper.BRIGHT_GREEN + stored.stackSize + " " + StringHelper.getItemName(stored));
		}
	}

}
