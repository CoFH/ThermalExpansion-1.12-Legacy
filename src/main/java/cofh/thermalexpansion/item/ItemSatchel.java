package cofh.thermalexpansion.item;

import cofh.api.core.ISecurable.AccessMode;
import cofh.api.item.IInventoryContainerItem;
import cofh.api.item.IMultiModeItem;
import cofh.core.init.CoreEnchantments;
import cofh.core.init.CoreProps;
import cofh.core.item.IEnchantableItem;
import cofh.core.item.ItemMulti;
import cofh.core.util.CoreUtils;
import cofh.core.util.RegistrySocial;
import cofh.core.util.core.IInitializer;
import cofh.core.util.helpers.ChatHelper;
import cofh.core.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.GuiHandler;
import com.mojang.authlib.GameProfile;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static cofh.lib.util.helpers.RecipeHelper.addShapedRecipe;

public class ItemSatchel extends ItemMulti implements IInitializer, IMultiModeItem, IInventoryContainerItem, IEnchantableItem {

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

	public ItemSatchel() {

		super("thermalexpansion");

		setMaxStackSize(1);
		setUnlocalizedName("satchel");
		setCreativeTab(ThermalExpansion.tabItems);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			tooltip.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		SecurityHelper.addAccessInformation(stack, tooltip);
		tooltip.add(StringHelper.getInfoText("info.thermalexpansion.storage.satchel"));

		//		if (isCreative(stack)) {
		//
		//		} else {
		//
		//		}
		ItemHelper.addInventoryInformation(stack, tooltip);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (isInCreativeTab(tab)) {
			for (int metadata : itemList) {
				items.add(setDefaultInventoryTag(new ItemStack(this, 1, metadata)));
			}
		}
	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {

		return satchelMap.get(ItemHelper.getItemDamage(stack)).enchantable;
	}

	@Override
	public int getItemEnchantability() {

		return 10;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		ItemStack stack = player.getHeldItem(hand);
		if (CoreUtils.isFakePlayer(player) || hand != EnumHand.MAIN_HAND) {
			return new ActionResult<>(EnumActionResult.FAIL, stack);
		}
		if (needsTag(stack)) {
			setDefaultInventoryTag(stack);
		}
		if (ServerHelper.isServerWorld(world)) {
			if (SecurityHelper.isSecure(stack) && SecurityHelper.isDefaultUUID(SecurityHelper.getOwner(stack).getId())) {
				SecurityHelper.setOwner(stack, player.getGameProfile());
				ChatHelper.sendIndexedChatMessageToPlayer(player, new TextComponentTranslation("chat.cofh.secure.item.success"));
				return new ActionResult<>(EnumActionResult.SUCCESS, stack);
			}
			if (canPlayerAccess(stack, player)) {
				player.openGui(ThermalExpansion.instance, GuiHandler.SATCHEL_ID, world, 0, 0, 0);
			} else if (SecurityHelper.isSecure(stack)) {
				ChatHelper.sendIndexedChatMessageToPlayer(player, new TextComponentTranslation("chat.cofh.secure.warning", SecurityHelper.getOwnerName(stack)));
				return new ActionResult<>(EnumActionResult.FAIL, stack);
			}
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		return EnumActionResult.FAIL;
	}

	/* HELPERS */
	public static boolean canPlayerAccess(ItemStack stack, EntityPlayer player) {

		if (!SecurityHelper.isSecure(stack)) {
			return true;
		}
		String name = player.getName();
		AccessMode access = SecurityHelper.getAccess(stack);
		if (access.isPublic() || (CoreProps.enableOpSecureAccess && CoreUtils.isOp(name))) {
			return true;
		}
		GameProfile profile = SecurityHelper.getOwner(stack);
		UUID ownerID = profile.getId();
		if (SecurityHelper.isDefaultUUID(ownerID)) {
			return true;
		}
		UUID otherID = SecurityHelper.getID(player);
		return ownerID.equals(otherID) || access.isFriendsOnly() && RegistrySocial.playerHasAccess(name, profile);
	}

	public static boolean hasHoldingEnchant(ItemStack stack) {

		return EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.holding, stack) > 0;
	}

	public static boolean isCreative(ItemStack stack) {

		return ItemHelper.getItemDamage(stack) == CREATIVE;
	}

	public static int getLevel(ItemStack stack) {

		if (isCreative(stack) || !satchelMap.containsKey(ItemHelper.getItemDamage(stack))) {
			return 0;
		}
		return satchelMap.get(ItemHelper.getItemDamage(stack)).level;
	}

	public static int getStorageIndex(ItemStack stack) {

		if (isCreative(stack) || !satchelMap.containsKey(ItemHelper.getItemDamage(stack))) {
			return 0;
		}
		int level = satchelMap.get(ItemHelper.getItemDamage(stack)).level;
		int enchant = EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.holding, stack);

		return Math.min(1 + level + enchant, CoreProps.STORAGE_SIZE.length - 1);
	}

	/* IMultiModeItem */
	@Override
	public int getMode(ItemStack stack) {

		return !stack.hasTagCompound() ? 0 : stack.getTagCompound().getInteger("Mode");
	}

	@Override
	public boolean setMode(ItemStack stack, int mode) {

		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setInteger("Mode", mode);
		return false;
	}

	@Override
	public boolean incrMode(ItemStack stack) {

		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		int curMode = getMode(stack);
		curMode++;
		if (curMode >= getNumModes(stack)) {
			curMode = 0;
		}
		stack.getTagCompound().setInteger("Mode", curMode);
		return true;
	}

	@Override
	public boolean decrMode(ItemStack stack) {

		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		int curMode = getMode(stack);
		curMode--;
		if (curMode <= 0) {
			curMode = getNumModes(stack) - 1;
		}
		stack.getTagCompound().setInteger("Mode", curMode);
		return true;
	}

	@Override
	public int getNumModes(ItemStack stack) {

		return 3;
	}

	@Override
	public void onModeChange(EntityPlayer player, ItemStack stack) {

		ChatHelper.sendIndexedChatMessageToPlayer(player, new TextComponentTranslation("info.thermalexpansion.satchel.a." + getMode(stack)));
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		ModelLoader.setCustomMeshDefinition(this, stack -> new ModelResourceLocation(getRegistryName(), String.format("access=%s,type=%s", SecurityHelper.getAccess(stack).toString().toLowerCase(Locale.US), satchelMap.get(ItemHelper.getItemDamage(stack)).name)));

		for (Map.Entry<Integer, ItemEntry> entry : itemMap.entrySet()) {
			for (int i = 0; i < AccessMode.values().length; i++) {
				ModelBakery.registerItemVariants(this, new ModelResourceLocation(getRegistryName(), String.format("access=%s,type=%s", AccessMode.values()[i].toString().toLowerCase(Locale.US), entry.getValue().name)));
			}
		}
	}

	/* IInventoryContainerItem */
	@Override
	public int getSizeInventory(ItemStack container) {

		return CoreProps.STORAGE_SIZE[getStorageIndex(container)];
	}

	/* IEnchantableItem */
	@Override
	public boolean canEnchant(ItemStack stack, Enchantment enchantment) {

		return satchelMap.containsKey(ItemHelper.getItemDamage(stack)) && satchelMap.get(ItemHelper.getItemDamage(stack)).enchantable && enchantment == CoreEnchantments.holding;
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		satchelBasic = addSatchelItem(0, "standard0", 0, EnumRarity.COMMON);
		satchelHardened = addSatchelItem(1, "standard1", 1, EnumRarity.COMMON);
		satchelReinforced = addSatchelItem(2, "standard2", 2, EnumRarity.UNCOMMON);
		satchelSignalum = addSatchelItem(3, "standard3", 3, EnumRarity.UNCOMMON);
		satchelResonant = addSatchelItem(4, "standard4", 4, EnumRarity.RARE);

		satchelCreative = addSatchelItem(CREATIVE, "creative", 0, EnumRarity.EPIC, false);

		ThermalExpansion.proxy.addIModelRegister(this);

		config();

		return true;
	}

	@Override
	public boolean initialize() {

		// @formatter:off

		addShapedRecipe(satchelBasic,
				" Y ",
				"IXI",
				"Y Y",
				'I', "ingotTin",
				'X', "blockWool",
				'Y', Items.LEATHER
		);
		addShapedRecipe(satchelBasic,
				" Y ",
				"IXI",
				"Y Y",
				'I', "ingotTin",
				'X', "blockWool",
				'Y', "blockRockwool"
		);
		// TODO: FIXME.
//		addRecipe(new RecipeUpgrade(satchelHardened,
//			" Y ",
//				"IXI",
//				"Y Y",
//				'I', "ingotInvar",
//				'X', satchelBasic,
//				'Y', "nuggetTin"
//		));
//		addRecipe(new RecipeUpgrade(satchelReinforced,
//				" Y ",
//				"IXI",
//				"Y Y",
//				'I', "ingotElectrum",
//				'X', satchelHardened,
//				'Y', "nuggetInvar"
//		));
//		addRecipe(new RecipeUpgrade(satchelSignalum,
//				" Y ",
//				"IXI",
//				"Y Y",
//				'I', "ingotSignalum",
//				'X', satchelReinforced,
//				'Y', "nuggetElectrum"
//		));
//		addRecipe(new RecipeUpgrade(satchelResonant,
//				" Y ",
//				"IXI",
//				"Y Y",
//				'I', "ingotEnderium",
//				'X', satchelSignalum,
//				'Y', "nuggetSignalum"
//		));
//
//		addRecipe(new RecipeSecure(satchelBasic, " L ", "SXS", " S ", 'L', ItemSecurity.lock, 'S', "nuggetSignalum", 'X', satchelBasic));
//		addRecipe(new RecipeSecure(satchelHardened, " L ", "SXS", " S ", 'L', ItemSecurity.lock, 'S', "nuggetSignalum", 'X', satchelHardened));
//		addRecipe(new RecipeSecure(satchelReinforced, " L ", "SXS", " S ", 'L', ItemSecurity.lock, 'S', "nuggetSignalum", 'X', satchelReinforced));
//		addRecipe(new RecipeSecure(satchelSignalum, " L ", "SXS", " S ", 'L', ItemSecurity.lock, 'S', "nuggetSignalum", 'X', satchelSignalum));
//		addRecipe(new RecipeSecure(satchelResonant, " L ", "SXS", " S ", 'L', ItemSecurity.lock, 'S', "nuggetSignalum", 'X', satchelResonant));
//		addRecipe(new RecipeSecure(satchelCreative, " L ", "SXS", " S ", 'L', ItemSecurity.lock, 'S', "nuggetSignalum", 'X', satchelCreative));

		// @formatter:on

		return true;
	}

	@Override
	public boolean postInit() {

		return true;
	}

	private static void config() {

		String category = "Item.Satchel";
		enable = ThermalExpansion.CONFIG.get(category, "Enable", true);
	}

	/* ENTRY */
	public class SatchelEntry {

		public final String name;
		public final int level;
		public final boolean enchantable;

		SatchelEntry(String name, int level, boolean enchantable) {

			this.name = name;
			this.level = level;
			this.enchantable = enchantable;
		}
	}

	private void addSatchelEntry(int metadata, String name, int level, boolean enchantable) {

		satchelMap.put(metadata, new SatchelEntry(name, level, enchantable));
	}

	private ItemStack addSatchelItem(int metadata, String name, int level, EnumRarity rarity, boolean enchantable) {

		addSatchelEntry(metadata, name, level, enchantable);
		return addItem(metadata, name, rarity);
	}

	private ItemStack addSatchelItem(int metadata, String name, int level, EnumRarity rarity) {

		addSatchelEntry(metadata, name, level, true);
		return addItem(metadata, name, rarity);
	}

	private static TIntObjectHashMap<SatchelEntry> satchelMap = new TIntObjectHashMap<>();

	public static final int CREATIVE = 32000;

	public static boolean enable = true;

	/* REFERENCES */
	public static ItemStack satchelBasic;
	public static ItemStack satchelHardened;
	public static ItemStack satchelReinforced;
	public static ItemStack satchelSignalum;
	public static ItemStack satchelResonant;

	public static ItemStack satchelCreative;

}
