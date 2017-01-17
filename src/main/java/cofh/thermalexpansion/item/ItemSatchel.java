package cofh.thermalexpansion.item;

import cofh.api.item.IInventoryContainerItem;
import cofh.api.tileentity.ISecurable.AccessMode;
import cofh.core.CoFHProps;
import cofh.core.RegistrySocial;
import cofh.core.enchantment.CoFHEnchantment;
import cofh.core.item.ItemCoFHBase;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.GuiHandler;
import com.mojang.authlib.GameProfile;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class ItemSatchel extends ItemCoFHBase implements IInventoryContainerItem {

	public static ItemStack setDefaultInventoryTag(ItemStack container) {

		if (container.getTagCompound() == null) {
			container.setTagCompound(new NBTTagCompound());
		}
		container.getTagCompound().setBoolean("Accessible", true);
		return container;
	}

	public static boolean needsTag(ItemStack container) {

		return container.getTagCompound() == null || !container.getTagCompound().hasKey("Accessible");
	}

	public static boolean enableSecurity = true;

	public static void configure() {

		String comment = "Enable this to allow for Satchels to be securable.";
		enableSecurity = ThermalExpansion.config.get("Security", "Satchel.All.Securable", enableSecurity, comment);
	}

	//IIcon latch[] = new IIcon[3];

	public ItemSatchel() {

		super("thermalexpansion");
		setMaxStackSize(1);
		setCreativeTab(ThermalExpansion.tabTools);
		setNoRepair();
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {

		if (enable[0]) {
			list.add(setDefaultInventoryTag(new ItemStack(item, 1, 0)));
		}
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
			if (!StringHelper.isShiftKeyDown()) {
				return;
			}
		}
		SecurityHelper.addAccessInformation(stack, list);
		ItemHelper.addInventoryInformation(stack, list);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {//TODO these may be borked.
		if (CoreUtils.isFakePlayer(player)) {
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStack);
		}
		if (needsTag(itemStack)) {
			setDefaultInventoryTag(itemStack);
		}
		if (ServerHelper.isServerWorld(world)) {
			if (SecurityHelper.isSecure(itemStack)) {
				if (SecurityHelper.isDefaultUUID(SecurityHelper.getOwner(itemStack).getId())) {
					SecurityHelper.setOwner(itemStack, player.getGameProfile());
					player.addChatMessage(new TextComponentTranslation("chat.cofh.secureItem"));
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
				}
			}
			if (canPlayerAccess(itemStack, player)) {
				player.openGui(ThermalExpansion.instance, GuiHandler.SATCHEL_ID, world, 0, 0, 0);
			} else if (SecurityHelper.isSecure(itemStack)) {
				player.addChatMessage(new TextComponentTranslation("chat.cofh.secure", SecurityHelper.getOwnerName(itemStack)));
				return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStack);
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		return EnumActionResult.FAIL;
	}

	@Override
	public boolean isFull3D() {

		return false;
	}

	@Override
	public boolean isItemTool(ItemStack stack) {

		return true;
	}

	//@Override
	public boolean requiresMultipleRenderPasses() {

		return true;
	}

	@Override
	public int getItemEnchantability() {

		return 10;
	}

	//@Override
	//public IIcon getIcon(ItemStack stack, int pass) {
	//
	//	if (pass == 0) {
	//		return super.getIcon(stack, pass);
	//	}
	//	if (SecurityHelper.isSecure(stack)) {
	//		return latch[SecurityHelper.getAccess(stack).ordinal()];
	//	}
	//	return latch[0];
	//}

	//@Override
	//public void registerIcons(IIconRegister ir) {
	//
	//	super.registerIcons(ir);
	//
	//	latch[0] = ir.registerIcon(MOD_NAME + ":" + getUnlocalizedName().replace("item." + MOD_NAME + ".", "") + "/" + "LatchPublic");
	//	latch[1] = ir.registerIcon(MOD_NAME + ":" + getUnlocalizedName().replace("item." + MOD_NAME + ".", "") + "/" + "LatchFriends");
	//	latch[2] = ir.registerIcon(MOD_NAME + ":" + getUnlocalizedName().replace("item." + MOD_NAME + ".", "") + "/" + "LatchPrivate");
	//}

	/* HELPERS */
	public static boolean canPlayerAccess(ItemStack stack, EntityPlayer player) {

		if (!SecurityHelper.isSecure(stack)) {
			return true;
		}
		String name = player.getName();
		AccessMode access = SecurityHelper.getAccess(stack);
		if (access.isPublic() || (CoFHProps.enableOpSecureAccess && CoreUtils.isOp(name))) {
			return true;
		}
		GameProfile profile = SecurityHelper.getOwner(stack);
		UUID ownerID = profile.getId();
		if (SecurityHelper.isDefaultUUID(ownerID)) {
			return true;
		}
		UUID otherID = SecurityHelper.getID(player);
		if (ownerID.equals(otherID)) {
			return true;
		}
		return access.isRestricted() && RegistrySocial.playerHasAccess(name, profile);
	}

	public static boolean isEnchanted(ItemStack container) {

		return EnchantmentHelper.getEnchantmentLevel(CoFHEnchantment.holding, container) > 0;
	}

	public static int getStorageIndex(int type, int enchant) {

		return type > 0 ? Math.min(type + enchant, CoFHProps.STORAGE_SIZE.length - 1) : 0;
	}

	public static int getStorageIndex(ItemStack container) {

		int type = container.getItemDamage();
		int enchant = EnchantmentHelper.getEnchantmentLevel(CoFHEnchantment.holding, container);

		return getStorageIndex(type, enchant);
	}

	/* IInventoryContainerItem */
	@Override
	public int getSizeInventory(ItemStack container) {

		return CoFHProps.STORAGE_SIZE[getStorageIndex(container)];
	}

	public enum Types {
		CREATIVE, BASIC, HARDENED, REINFORCED, RESONANT
	}

	public static final String[] NAMES = { "creative", "basic", "hardened", "reinforced", "resonant" };

	public static boolean[] enable = { true, true, true, true, true };

	static {
		String category = "Item.Satchel.";

		enable[0] = ThermalExpansion.config.get(category + StringHelper.titleCase(NAMES[0]), "Enable", enable[0]);
		for (int i = 1; i < Types.values().length; i++) {
			enable[i] = ThermalExpansion.config.get(category + StringHelper.titleCase(NAMES[i]), "Recipe", enable[i]);
		}
	}

}
