package thermalexpansion.block.strongbox;

import cofh.api.item.IInventoryContainerItem;
import cofh.core.CoFHProps;
import cofh.enchantment.CoFHEnchantment;
import cofh.util.ItemHelper;
import cofh.util.StringHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBlockStrongbox extends ItemBlock implements IInventoryContainerItem {

	public ItemBlockStrongbox(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(1);
		setNoRepair();
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {

		return StringHelper.localize(getUnlocalizedName(stack));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.strongbox." + BlockStrongbox.NAMES[stack.getItemDamage()] + ".name";
	}

	@Override
	public int getMetadata(int i) {

		return i;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		switch (BlockStrongbox.Types.values()[stack.getItemDamage()]) {
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

		if (stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("Owner")) {
			list.add(StringHelper.localize("info.cofh.owner") + ": " + StringHelper.localize("info.cofh.none"));
			return;
		} else {
			list.add(StringHelper.localize("info.cofh.owner") + ": " + stack.stackTagCompound.getString("Owner"));
		}
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForInfo());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		ItemHelper.addInventoryInformation(stack, list);
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {

		return true;
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack stack) {

		location.invulnerable = true;
		location.isImmuneToFire = true;
		return null;
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entity) {

		entity.age = 0;
		return false;
	}

	@Override
	public boolean isItemTool(ItemStack stack) {

		return true;
	}

	@Override
	public int getItemEnchantability() {

		return 10;
	}

	public static int getStorageIndex(int type, int enchant) {

		return type > 0 ? 2 * type + enchant : 0;
	}

	public static int getStorageIndex(ItemStack container) {

		int type = container.getItemDamage();
		int enchant = EnchantmentHelper.getEnchantmentLevel(CoFHEnchantment.enchantmentHolding.effectId, container);

		return getStorageIndex(type, enchant);
	}

	/* IInventoryContainerItem */
	@Override
	public int getSizeInventory(ItemStack container) {

		return CoFHProps.STORAGE_SIZE[getStorageIndex(container)];
	}

}
