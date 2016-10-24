package cofh.thermalexpansion.block.strongbox;

import cofh.api.item.IInventoryContainerItem;
import cofh.core.CoFHProps;
import cofh.core.enchantment.CoFHEnchantment;
import cofh.core.item.ItemBlockBase;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;

import java.util.List;

import cofh.thermalexpansion.block.EnumType;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.Constants;

public class ItemBlockStrongbox extends ItemBlockBase implements IInventoryContainerItem {

	public ItemBlockStrongbox(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(1);
		setNoRepair();
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {

		if (stack.getTagCompound() != null && !(!stack.getTagCompound().hasKey("Inventory", Constants.NBT.TAG_LIST)
				|| stack.getTagCompound().getTagList("Inventory", stack.getTagCompound().getId()).tagCount() <= 0)) {
			return super.getItemStackLimit(stack);
		}
		return 64;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.strongbox." + BlockStrongbox.NAMES[ItemHelper.getItemDamage(stack)] + ".name";
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		switch (EnumType.values()[ItemHelper.getItemDamage(stack)]) {
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

		SecurityHelper.addOwnerInformation(stack, list);
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		SecurityHelper.addAccessInformation(stack, list);
		ItemHelper.addInventoryInformation(stack, list);
	}

	@Override
	public int getItemEnchantability() {

		return 10;
	}

	@Override
	public boolean isItemTool(ItemStack stack) {

		return true;
	}

	public static int getStorageIndex(int type, int enchant) {

		return type > 0 ? 2 * type + enchant : 0;
	}

	public static int getStorageIndex(ItemStack container) {

		int type = container.getItemDamage();
		int enchant = EnchantmentHelper.getEnchantmentLevel(CoFHEnchantment.holding, container);

		return getStorageIndex(type, enchant);
	}

	/* IInventoryContainerItem */
	@Override
	public int getSizeInventory(ItemStack container) {

		return CoFHProps.STORAGE_SIZE[Math.min(12, getStorageIndex(container))];
	}

}
