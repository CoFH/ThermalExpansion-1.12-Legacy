package thermalexpansion.item;

import cofh.api.item.IInventoryContainerItem;
import cofh.api.tileentity.ISecurable.AccessMode;
import cofh.core.CoFHProps;
import cofh.core.enchantment.CoFHEnchantment;
import cofh.core.item.ItemBase;
import cofh.core.util.CoreUtils;
import cofh.core.util.SocialRegistry;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.gui.GuiHandler;

public class ItemSatchel extends ItemBase implements IInventoryContainerItem {

	public static ItemStack setDefaultInventoryTag(ItemStack container) {

		if (container.stackTagCompound == null) {
			container.setTagCompound(new NBTTagCompound());
		}
		container.stackTagCompound.setBoolean("Accessible", true);
		return container;
	}

	public static boolean enableSecurity = true;

	public static void configure() {

		String comment = "Enable this to allow for Satchels to be securable. (Default: true)";
		enableSecurity = ThermalExpansion.config.get("security", "Satchel.All.Securable", enableSecurity, comment);
	}

	IIcon latch[] = new IIcon[3];

	public ItemSatchel() {

		super("thermalexpansion");
		setMaxStackSize(1);
		setCreativeTab(ThermalExpansion.tabTools);
		setNoRepair();
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {

		// TODO: Creative Satchel
		for (int i = 1; i < Types.values().length; i++) {
			list.add(setDefaultInventoryTag(new ItemStack(item, 1, i)));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack item) {

		return "item.thermalexpansion.satchel." + NAMES[item.getItemDamage()];
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
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

		if (CoreUtils.isFakePlayer(player)) {
			return stack;
		}
		if (SecurityHelper.isSecure(stack)) {
			if (SecurityHelper.getOwnerName(stack).isEmpty()) {
				SecurityHelper.setOwnerName(stack, player.getDisplayName());
			}
		}
		if (ServerHelper.isServerWorld(world)) {
			if (canPlayerAccess(stack, player.getDisplayName())) {
				player.openGui(ThermalExpansion.instance, GuiHandler.SATCHEL_ID, world, 0, 0, 0);
			} else if (SecurityHelper.isSecure(stack)) {
				player.addChatMessage(new ChatComponentText(StringHelper.localize("chat.cofh.secure1") + " " + SecurityHelper.getOwnerName(stack) + "! "
						+ StringHelper.localize("chat.cofh.secure2")));
			}
		}
		return stack;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

		return false;
	}

	@Override
	public boolean isFull3D() {

		return false;
	}

	@Override
	public boolean isItemTool(ItemStack stack) {

		return true;
	}

	@Override
	public boolean requiresMultipleRenderPasses() {

		return true;
	}

	@Override
	public int getItemEnchantability() {

		return 10;
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {

		if (pass == 0) {
			return super.getIcon(stack, pass);
		}
		if (SecurityHelper.isSecure(stack)) {
			return latch[SecurityHelper.getAccess(stack).ordinal()];
		}
		return latch[0];
	}

	@Override
	public void registerIcons(IIconRegister ir) {

		super.registerIcons(ir);

		latch[0] = ir.registerIcon(modName + ":" + getUnlocalizedName().replace("item." + modName + ".", "") + "/" + "LatchPublic");
		latch[1] = ir.registerIcon(modName + ":" + getUnlocalizedName().replace("item." + modName + ".", "") + "/" + "LatchFriends");
		latch[2] = ir.registerIcon(modName + ":" + getUnlocalizedName().replace("item." + modName + ".", "") + "/" + "LatchPrivate");
	}

	/* HELPERS */
	public static boolean canPlayerAccess(ItemStack stack, String name) {

		if (!SecurityHelper.isSecure(stack)) {
			return true;
		}
		AccessMode access = SecurityHelper.getAccess(stack);
		String owner = SecurityHelper.getOwnerName(stack);

		return access.isPublic() || (CoFHProps.enableOpSecureAccess && CoreUtils.isOp(name)) || owner.equals(CoFHProps.DEFAULT_OWNER) || owner.equals(name)
				|| access.isRestricted() && SocialRegistry.playerHasAccess(name, owner);
	}

	public static boolean isEnchanted(ItemStack container) {

		return EnchantmentHelper.getEnchantmentLevel(CoFHEnchantment.enchantmentHolding.effectId, container) > 0;
	}

	public static int getStorageIndex(int type, int enchant) {

		return type > 0 ? Math.min(type + enchant, CoFHProps.STORAGE_SIZE.length - 1) : 0;
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

	public static final String[] NAMES = { "creative", "basic", "hardened", "reinforced", "resonant" };

}
