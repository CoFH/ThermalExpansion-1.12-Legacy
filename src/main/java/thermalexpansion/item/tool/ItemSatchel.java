package thermalexpansion.item.tool;

import cofh.api.item.IInventoryContainerItem;
import cofh.core.CoFHProps;
import cofh.enchantment.CoFHEnchantment;
import cofh.item.ItemBase;
import cofh.util.CoreUtils;
import cofh.util.ItemHelper;
import cofh.util.SecurityHelper;
import cofh.util.ServerHelper;
import cofh.util.StringHelper;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.gui.GuiHandler;

public class ItemSatchel extends ItemBase implements IInventoryContainerItem {

	public static ItemStack setDefaultInventoryTag(ItemStack container) {

		container.setTagCompound(new NBTTagCompound());
		container.stackTagCompound.setBoolean("Accessible", true);
		return container;
	}

	public static boolean enableSecurity = true;

	public static void configure() {

		String comment = "Enable this to allow for Satchels to be upgradable to be secure inventories. (Default: true)";
		enableSecurity = ThermalExpansion.config.get("item.security", "Satchel.Secure", enableSecurity, comment);
	}

	public ItemSatchel() {

		super("thermalexpansion");
		setMaxStackSize(1);
		setCreativeTab(ThermalExpansion.tabTools);
		setNoRepair();
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {

		for (int i = 0; i < Types.values().length; i++) {
			list.add(setDefaultInventoryTag(new ItemStack(item, 1, i)));
		}
	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

		if (CoreUtils.isFakePlayer(player)) {
			return stack;
		}
		if (ServerHelper.isServerWorld(world)) {
			player.openGui(ThermalExpansion.instance, GuiHandler.SATCHEL_ID, world, 0, 0, 0);
		}
		return stack;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

		return false;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForInfo());
		}
		if (stack.stackTagCompound == null) {
			return;
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		ItemHelper.addInventoryInformation(stack, list);
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {

		return SecurityHelper.isSecure(stack);
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

	public static boolean isEnchanted(ItemStack container) {

		return EnchantmentHelper.getEnchantmentLevel(CoFHEnchantment.enchantmentHolding.effectId, container) > 0;
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

	public static enum Types {
		CREATIVE, BASIC, HARDENED, REINFORCED, RESONANT
	}

}
