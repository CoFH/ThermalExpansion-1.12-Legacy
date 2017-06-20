package cofh.thermalexpansion.item;

import cofh.api.core.ISecurable.AccessMode;
import cofh.api.item.IInventoryContainerItem;
import cofh.core.init.CoreEnchantments;
import cofh.core.init.CoreProps;
import cofh.core.item.IEnchantableItem;
import cofh.core.item.ItemMulti;
import cofh.core.util.CoreUtils;
import cofh.core.util.RegistrySocial;
import cofh.core.util.core.IInitializer;
import cofh.core.util.crafting.RecipeUpgrade;
import cofh.core.util.helpers.ChatHelper;
import cofh.core.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.GuiHandler;
import com.mojang.authlib.GameProfile;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;
import static cofh.lib.util.helpers.ItemHelper.addRecipe;

public class ItemSatchel extends ItemMulti implements IInitializer, IInventoryContainerItem, IEnchantableItem {

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
	@SideOnly (Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {

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
	@SideOnly (Side.CLIENT)
	public void getSubItems(@Nonnull Item item, CreativeTabs tab, NonNullList<ItemStack> list) {

		for (int metadata : itemList) {
			list.add(setDefaultInventoryTag(new ItemStack(item, 1, metadata)));
		}
	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {

		return true;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && (slotChanged || !ItemHelper.areItemStacksEqualIgnoreTags(oldStack, newStack, "Energy"));
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
				ChatHelper.sendIndexedChatMessageToPlayer(player, new TextComponentTranslation("chat.cofh.secure", SecurityHelper.getOwnerName(stack)));
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

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		ModelLoader.setCustomMeshDefinition(this, new SatchelMeshDefinition());

		for (Map.Entry<Integer, ItemEntry> entry : itemMap.entrySet()) {
			ModelResourceLocation texture = new ModelResourceLocation(modName + ":" + name + "_" + entry.getValue().name, "inventory");
			textureMap.put(entry.getKey(), texture);
			ModelBakery.registerItemVariants(this, texture);
		}
	}

	/* ITEM MESH DEFINITION */
	@SideOnly (Side.CLIENT)
	public class SatchelMeshDefinition implements ItemMeshDefinition {

		public ModelResourceLocation getModelLocation(ItemStack stack) {

			return textureMap.get(ItemHelper.getItemDamage(stack));
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

		return true;
	}

	@Override
	public boolean initialize() {

		// @formatter:off

		addRecipe(ShapedRecipe(satchelBasic,
				" Y ",
				"IXI",
				"Y Y",
				'I', "ingotTin",
				'X', "blockWool",
				'Y', Items.LEATHER
		));
		addRecipe(ShapedRecipe(satchelBasic,
				" Y ",
				"IXI",
				"Y Y",
				'I', "ingotTin",
				'X', "blockWool",
				'Y', "blockRockwool"
		));
		addRecipe(new RecipeUpgrade(satchelHardened,
			" Y ",
				"IXI",
				"Y Y",
				'I', "ingotInvar",
				'X', satchelBasic,
				'Y', "nuggetTin"
		));
		addRecipe(new RecipeUpgrade(satchelReinforced,
				" Y ",
				"IXI",
				"Y Y",
				'I', "ingotElectrum",
				'X', satchelHardened,
				'Y', "nuggetInvar"
		));
		addRecipe(new RecipeUpgrade(satchelSignalum,
				" Y ",
				"IXI",
				"Y Y",
				'I', "ingotSignalum",
				'X', satchelReinforced,
				'Y', "nuggetElectrum"
		));
		addRecipe(new RecipeUpgrade(satchelResonant,
				" Y ",
				"IXI",
				"Y Y",
				'I', "ingotEnderium",
				'X', satchelSignalum,
				'Y', "nuggetSignalum"
		));

		// @formatter:on

		return true;
	}

	@Override
	public boolean postInit() {

		return true;
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
	private static TIntObjectHashMap<ModelResourceLocation> textureMap = new TIntObjectHashMap<>();

	public static final int CREATIVE = 32000;

	/* REFERENCES */
	public static ItemStack satchelBasic;
	public static ItemStack satchelHardened;
	public static ItemStack satchelReinforced;
	public static ItemStack satchelSignalum;
	public static ItemStack satchelResonant;

	public static ItemStack satchelCreative;

}
