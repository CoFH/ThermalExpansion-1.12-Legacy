package cofh.thermalexpansion.block.storage;

import cofh.api.item.IInventoryContainerItem;
import cofh.core.init.CoreEnchantments;
import cofh.core.item.IEnchantableItem;
import cofh.core.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.ItemBlockTEBase;
import cofh.thermalexpansion.util.helpers.ReconfigurableHelper;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockCache extends ItemBlockTEBase implements IInventoryContainerItem, IEnchantableItem {

	public ItemBlockCache(Block block) {

		super(block);
		setMaxStackSize(1);
	}

	@Override
	public ItemStack setDefaultTag(ItemStack stack, int level) {

		ReconfigurableHelper.setFacing(stack, 3);
		stack.getTagCompound().setByte("Level", (byte) level);

		return stack;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.storage.cache.name";
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

		SecurityHelper.addOwnerInformation(stack, tooltip);
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			tooltip.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		SecurityHelper.addAccessInformation(stack, tooltip);
		tooltip.add(StringHelper.getInfoText("info.thermalexpansion.storage.cache"));

		if (isCreative(stack)) {
			tooltip.add(StringHelper.localize("info.cofh.capacity") + ": " + StringHelper.localize("info.cofh.infinite"));
		} else {
			tooltip.add(StringHelper.localize("info.cofh.capacity") + ": " + StringHelper.formatNumber(getSizeInventory(stack)));
		}
		if (!stack.getTagCompound().hasKey("Item")) {
			tooltip.add(StringHelper.localize("info.cofh.empty"));
			return;
		}
		boolean lock = stack.getTagCompound().getBoolean("Lock");

		if (lock) {
			tooltip.add(StringHelper.YELLOW + StringHelper.localize("info.cofh.locked"));
		} else {
			tooltip.add(StringHelper.YELLOW + StringHelper.localize("info.cofh.unlocked"));
		}
		tooltip.add(StringHelper.localize("info.cofh.contents") + ":");

		if (stack.getTagCompound().hasKey("Item")) {
			ItemStack stored = ItemHelper.readItemStackFromNBT(stack.getTagCompound().getCompoundTag("Item"));
			if (isCreative(stack)) {
				tooltip.add("    " + StringHelper.ORANGE + StringHelper.getItemName(stored));
			} else {
				tooltip.add("    " + StringHelper.ORANGE + StringHelper.formatNumber(stored.getCount()) + " " + StringHelper.getItemName(stored));
			}
		}
		// RedstoneControlHelper.addRSControlInformation(stack, tooltip);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {

		return true;
	}

	@Override
	public int getItemEnchantability() {

		return 10;
	}

	/* IInventoryContainerItem */
	@Override
	public int getSizeInventory(ItemStack container) {

		return TileCache.getCapacity(getLevel(container), EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.holding, container));
	}

	/* IEnchantableItem */
	@Override
	public boolean canEnchant(ItemStack stack, Enchantment enchantment) {

		return enchantment == CoreEnchantments.holding;
	}

}
