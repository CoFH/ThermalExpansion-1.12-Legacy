package cofh.thermalexpansion.block.storage;

import cofh.api.item.IInventoryContainerItem;
import cofh.core.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.ItemBlockTEBase;
import cofh.thermalexpansion.util.ReconfigurableHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockCache extends ItemBlockTEBase implements IInventoryContainerItem {

	public ItemBlockCache(Block block) {

		super(block);
		setMaxStackSize(1);
	}

	@Override
	public ItemStack setDefaultTag(ItemStack stack, int level) {

		ReconfigurableHelper.setFacing(stack, 3);
		// RedstoneControlHelper.setControl(stack, ControlMode.DISABLED);
		stack.getTagCompound().setByte("Level", (byte) level);

		return stack;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.storage.cache.name";
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

		if (isCreative(stack)) {
			tooltip.add(StringHelper.localize("info.cofh.capacity") + ": " + StringHelper.localize("info.cofh.infinite"));
		} else {
			tooltip.add(StringHelper.localize("info.cofh.capacity") + ": " + getSizeInventory(stack));
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
				tooltip.add("    " + StringHelper.ORANGE + stored.stackSize + " " + StringHelper.getItemName(stored));
			}
		}
		// RedstoneControlHelper.addRSControlInformation(stack, tooltip);
	}

	/* IInventoryContainerItem */
	public int getSizeInventory(ItemStack container) {

		return TileCache.getCapacity(getLevel(container));
	}

}
