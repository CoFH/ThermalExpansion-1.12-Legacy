package cofh.thermalexpansion.block.storage;

import cofh.api.item.IInventoryContainerItem;
import cofh.core.block.ItemBlockCore;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.util.ReconfigurableHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockCache extends ItemBlockCore implements IInventoryContainerItem {

	public static ItemStack setDefaultTag(ItemStack stack) {

		return setDefaultTag(stack, 0);
	}

	public static ItemStack setDefaultTag(ItemStack stack, int level) {

		ReconfigurableHelper.setFacing(stack, 3);
		// RedstoneControlHelper.setControl(stack, ControlMode.DISABLED);
		stack.getTagCompound().setByte("Level", (byte) level);

		return stack;
	}

	public static byte getLevel(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			setDefaultTag(stack);
		}
		return stack.getTagCompound().getByte("Level");
	}

	public static boolean isCreative(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			setDefaultTag(stack);
		}
		return stack.getTagCompound().getBoolean("Creative");
	}

	public ItemBlockCache(Block block) {

		super(block);
		setMaxStackSize(1);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {

		return StringHelper.localize(getUnlocalizedName(stack)) + " (" + StringHelper.localize("info.thermalexpansion.level." + (isCreative(stack) ? "creative" : getLevel(stack))) + ")";
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.storage.cache.name";
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		if (isCreative(stack)) {
			return EnumRarity.EPIC;
		}
		switch (getLevel(stack)) {
			case 4:
				return EnumRarity.RARE;
			case 3:
			case 2:
				return EnumRarity.UNCOMMON;
			default:
				return EnumRarity.COMMON;
		}
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {

		SecurityHelper.addOwnerInformation(stack, tooltip);
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			tooltip.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		SecurityHelper.addAccessInformation(stack, tooltip);
		tooltip.add(StringHelper.getInfoText("info.thermalexpansion.storage.cache"));

		tooltip.add(StringHelper.localize("info.cofh.capacity") + ": " + getSizeInventory(stack));
		if (!stack.getTagCompound().hasKey("Item")) {
			tooltip.add(StringHelper.localize("info.cofh.empty"));
			return;
		}
		boolean lock = stack.getTagCompound().getBoolean("Lock");

		if (lock) {
			tooltip.add(StringHelper.localize("info.cofh.locked"));
		} else {
			tooltip.add(StringHelper.localize("info.cofh.unlocked"));
		}
		tooltip.add(StringHelper.localize("info.cofh.contents") + ":");

		if (stack.getTagCompound().hasKey("Item")) {
			ItemStack stored = ItemHelper.readItemStackFromNBT(stack.getTagCompound().getCompoundTag("Item"));
			tooltip.add("    " + StringHelper.ORANGE + stored.stackSize + " " + StringHelper.getItemName(stored));
		}
		// RedstoneControlHelper.addRSControlInformation(stack, tooltip);
	}

	/* IInventoryContainerItem */
	public int getSizeInventory(ItemStack container) {

		return TileCache.getCapacity(getLevel(container));
	}

}
