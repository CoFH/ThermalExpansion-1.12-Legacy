package thermalexpansion.block.cache;

import cofh.item.ItemBlockBase;
import cofh.util.ItemHelper;
import cofh.util.StringHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemBlockCache extends ItemBlockBase {

	public ItemBlockCache(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(1);
		setNoRepair();
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.cache." + BlockCache.NAMES[stack.getItemDamage()] + ".name";
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		switch (BlockCache.Types.values()[stack.getItemDamage()]) {
		case CREATIVE:
			return EnumRarity.epic;
		case RESONANT:
			return EnumRarity.rare;
		case REINFORCED:
			return EnumRarity.uncommon;
		default:
			return EnumRarity.common;
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
		if (stack.stackTagCompound == null) {
			list.add(StringHelper.localize("info.cofh.empty"));
			return;
		}
		boolean lock = stack.stackTagCompound.getBoolean("Lock");

		if (lock) {
			list.add(StringHelper.localize("info.cofh.locked"));
		} else {
			list.add(StringHelper.localize("info.cofh.unlocked"));
		}
		list.add(StringHelper.localize("info.cofh.contents") + ":");

		if (stack.stackTagCompound.hasKey("Item")) {
			ItemStack stored = ItemHelper.readItemStackFromNBT(stack.stackTagCompound.getCompoundTag("Item"));
			list.add("    " + StringHelper.BRIGHT_GREEN + stored.stackSize + " " + StringHelper.getItemName(stored));
		}
	}

}
