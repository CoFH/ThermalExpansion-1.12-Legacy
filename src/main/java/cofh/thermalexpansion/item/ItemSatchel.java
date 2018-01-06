package cofh.thermalexpansion.item;

import cofh.api.core.ISecurable.AccessMode;
import cofh.api.item.IInventoryContainerItem;
import cofh.api.item.IMultiModeItem;
import cofh.api.item.INBTCopyIngredient;
import cofh.core.gui.container.InventoryContainerItemWrapper;
import cofh.core.init.CoreEnchantments;
import cofh.core.init.CoreProps;
import cofh.core.item.IEnchantableItem;
import cofh.core.item.ItemMulti;
import cofh.core.key.KeyBindingItemMultiMode;
import cofh.core.util.CoreUtils;
import cofh.core.util.RegistrySocial;
import cofh.core.util.core.IInitializer;
import cofh.core.util.filter.ItemFilterWrapper;
import cofh.core.util.helpers.*;
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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static cofh.core.util.helpers.RecipeHelper.addShapedRecipe;

public class ItemSatchel extends ItemMulti implements IInitializer, IMultiModeItem, IInventoryContainerItem, IEnchantableItem, INBTCopyIngredient {

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

		if (isVoid(stack)) {
			tooltip.add(StringHelper.getInfoText("info.thermalexpansion.satchel.a.v"));
			tooltip.add(StringHelper.getNoticeText("info.thermalexpansion.satchel.a.2"));
			tooltip.add(StringHelper.localizeFormat("info.thermalexpansion.satchel.b." + getMode(stack), StringHelper.getKeyName(KeyBindingItemMultiMode.INSTANCE.getKey())));
			return;
		}
		if (isCreative(stack)) {
			tooltip.add(StringHelper.getInfoText("info.thermalexpansion.satchel.a.c"));
			tooltip.add(StringHelper.localize("info.thermalexpansion.satchel.a.1"));

			ItemHelper.addInventoryInformation(stack, tooltip);
			return;
		}
		tooltip.add(StringHelper.getInfoText("info.thermalexpansion.satchel.a.0"));
		tooltip.add(StringHelper.localize("info.thermalexpansion.satchel.a.1"));
		tooltip.add(StringHelper.getNoticeText("info.thermalexpansion.satchel.a.2"));
		tooltip.add(StringHelper.localizeFormat("info.thermalexpansion.satchel.b." + getMode(stack), StringHelper.getKeyName(KeyBindingItemMultiMode.INSTANCE.getKey())));

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
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {

		if (isVoid(stack) && stack.getTagCompound() != null && stack.getTagCompound().hasKey("Random")) {
			stack.getTagCompound().removeTag("Random");
		}
	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {

		return !isCreative(stack) && !isVoid(stack);
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
				if (player.isSneaking() && ItemHelper.getItemDamage(stack) != CREATIVE) {
					player.openGui(ThermalExpansion.instance, GuiHandler.SATCHEL_FILTER_ID, world, 0, 0, 0);
				} else {
					player.openGui(ThermalExpansion.instance, GuiHandler.SATCHEL_ID, world, 0, 0, 0);
				}
			} else if (SecurityHelper.isSecure(stack)) {
				ChatHelper.sendIndexedChatMessageToPlayer(player, new TextComponentTranslation("chat.cofh.secure.warning", SecurityHelper.getOwnerName(stack)));
				return new ActionResult<>(EnumActionResult.FAIL, stack);
			}
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {

		if (world.isAirBlock(pos)) {
			return EnumActionResult.PASS;
		}
		PlayerInteractEvent event = new PlayerInteractEvent.RightClickBlock(player, hand, pos, side, new Vec3d(hitX, hitY, hitZ));
		if (MinecraftForge.EVENT_BUS.post(event) || event.getResult() == Result.DENY) {
			return EnumActionResult.PASS;
		}
		ItemStack stack = player.getHeldItem(hand);
		if (player.isSneaking() && canPlayerAccess(stack, player)) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
				IItemHandler cap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
				if (ServerHelper.isServerWorld(world)) {
					emptyInventoryIntoTarget(stack, cap);
				}
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;
	}

	//	@Override
	//	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
	//
	//		if (ServerHelper.isServerWorld(world)) {
	//			ItemStack stack = player.getHeldItem(hand);
	//			if (player.isSneaking() && canPlayerAccess(stack, player)) {
	//				TileEntity tile = world.getTileEntity(pos);
	//				if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing)) {
	//					IItemHandler cap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
	//					emptyInventoryIntoTarget(stack, cap);
	//					return EnumActionResult.SUCCESS;
	//				}
	//			}
	//		}
	//		return EnumActionResult.FAIL;
	//	}

	private void emptyInventoryIntoTarget(ItemStack stack, IItemHandler target) {

		InventoryContainerItemWrapper wrapper = new InventoryContainerItemWrapper(stack);
		for (int i = 0; i < getSizeInventory(stack); i++) {
			ItemStack slot = wrapper.getStackInSlot(i);
			if (!slot.isEmpty()) {
				ItemStack remainder = ItemHandlerHelper.insertItem(target, slot, false);
				if (!isCreative(stack)) {
					wrapper.setInventorySlotContents(i, remainder);
				}
			}
		}
		wrapper.markDirty();
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

	public static boolean isVoid(ItemStack stack) {

		return ItemHelper.getItemDamage(stack) == VOID;
	}

	public static int getLevel(ItemStack stack) {

		if (!typeMap.containsKey(ItemHelper.getItemDamage(stack))) {
			return 0;
		}
		return typeMap.get(ItemHelper.getItemDamage(stack)).level;
	}

	public static int getStorageIndex(ItemStack stack) {

		if (isCreative(stack) || isVoid(stack) || !typeMap.containsKey(ItemHelper.getItemDamage(stack))) {
			return 0;
		}
		int level = typeMap.get(ItemHelper.getItemDamage(stack)).level;
		int enchant = EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.holding, stack);

		return Math.min(1 + level + enchant, CoreProps.STORAGE_SIZE.length - 1);
	}

	public static int getFilterSize(ItemStack stack) {

		return CoreProps.FILTER_SIZE[getLevel(stack)];
	}

	public static boolean onItemPickup(EntityItemPickupEvent event, ItemStack stack) {

		if (!canPlayerAccess(stack, event.getEntityPlayer()) || ((ItemSatchel) stack.getItem()).getMode(stack) <= 0 || isCreative(stack)) {
			return false;
		}
		ItemFilterWrapper wrapper = new ItemFilterWrapper(stack, getFilterSize(stack));
		ItemStack eventItem = event.getItem().getItem();

		if (wrapper.getFilter().matches(eventItem)) {
			if (isVoid(stack)) {
				eventItem.setCount(0);
				stack.setAnimationsToGo(5);
				EntityPlayer player = event.getEntityPlayer();
				player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, (MathHelper.RANDOM.nextFloat() - MathHelper.RANDOM.nextFloat()) * 0.7F + 1.0F);
				stack.getTagCompound().setInteger("Random", MathHelper.RANDOM.nextInt());
				return true;
			} else if (!(eventItem.getItem() instanceof IInventoryContainerItem) || ((IInventoryContainerItem) eventItem.getItem()).getSizeInventory(stack) <= 0) {
				int count = eventItem.getCount();
				InventoryContainerItemWrapper inv = new InventoryContainerItemWrapper(stack);
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack slot = inv.getStackInSlot(i);
					if (ItemHandlerHelper.canItemStacksStackRelaxed(eventItem, slot)) {
						int fill = slot.getMaxStackSize() - slot.getCount();
						if (fill > eventItem.getCount()) {
							slot.setCount(slot.getCount() + eventItem.getCount());
						} else {
							slot.setCount(slot.getMaxStackSize());
						}
						eventItem.splitStack(fill);
					} else if (slot.isEmpty()) {
						inv.setInventorySlotContents(i, eventItem.copy());
						eventItem.setCount(0);
					}
					if (eventItem.isEmpty()) {
						break;
					}
				}
				if (eventItem.getCount() != count) {
					stack.setAnimationsToGo(5);
					EntityPlayer player = event.getEntityPlayer();
					player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((MathHelper.RANDOM.nextFloat() - MathHelper.RANDOM.nextFloat()) * 0.7F + 1.0F) * 2.0F);
					inv.markDirty();
				}
			}
		}
		return eventItem.isEmpty();
	}

	/* IMultiModeItem */
	@Override
	public int getNumModes(ItemStack stack) {

		return isCreative(stack) ? 1 : 2;
	}

	@Override
	public void onModeChange(EntityPlayer player, ItemStack stack) {

		player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.4F, 0.8F + 0.4F * getMode(stack));
		ChatHelper.sendIndexedChatMessageToPlayer(player, new TextComponentTranslation("info.thermalexpansion.satchel.c." + getMode(stack)));
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		ModelLoader.setCustomMeshDefinition(this, stack -> new ModelResourceLocation(getRegistryName(), String.format("access=%s,type=%s", SecurityHelper.getAccess(stack).toString().toLowerCase(Locale.US), typeMap.get(ItemHelper.getItemDamage(stack)).name)));

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

		return enchantment == CoreEnchantments.holding;
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		config();

		satchelBasic = addEntryItem(0, "standard0", 0, EnumRarity.COMMON);
		satchelHardened = addEntryItem(1, "standard1", 1, EnumRarity.COMMON);
		satchelReinforced = addEntryItem(2, "standard2", 2, EnumRarity.UNCOMMON);
		satchelSignalum = addEntryItem(3, "standard3", 3, EnumRarity.UNCOMMON);
		satchelResonant = addEntryItem(4, "standard4", 4, EnumRarity.RARE);

		satchelVoid = addEntryItem(VOID, "void", 4, EnumRarity.UNCOMMON);
		satchelCreative = addEntryItem(CREATIVE, "creative", 4, EnumRarity.EPIC);

		ThermalExpansion.proxy.addIModelRegister(this);

		return true;
	}

	@Override
	public boolean register() {

		if (!enable) {
			return false;
		}
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

		addShapedRecipe(satchelVoid,
				" Y ",
				"IXI",
				"Y Y",
				'I', "cobblestone",
				'X', Items.LAVA_BUCKET,
				'Y', Items.LEATHER
		);
		addShapedRecipe(satchelVoid,
				" Y ",
				"IXI",
				"Y Y",
				'I', "cobblestone",
				'X', Items.LAVA_BUCKET,
				'Y', "blockRockwool"
		);

		// @formatter:on

		return true;
	}

	private static void config() {

		String category = "Item.Satchel";
		enable = ThermalExpansion.CONFIG.get(category, "Enable", true);
	}

	/* ENTRY */
	public class TypeEntry {

		public final String name;
		public final int level;

		TypeEntry(String name, int level) {

			this.name = name;
			this.level = level;
		}
	}

	private void addTypeEntry(int metadata, String name, int level) {

		typeMap.put(metadata, new TypeEntry(name, level));
	}

	private ItemStack addEntryItem(int metadata, String name, int level, EnumRarity rarity) {

		addTypeEntry(metadata, name, level);
		return addItem(metadata, name, rarity);
	}

	private static TIntObjectHashMap<TypeEntry> typeMap = new TIntObjectHashMap<>();

	public static final int VOID = 100;
	public static final int CREATIVE = 32000;

	public static boolean enable = true;
	public static boolean enableSecurity = true;

	/* REFERENCES */
	public static ItemStack satchelBasic;
	public static ItemStack satchelHardened;
	public static ItemStack satchelReinforced;
	public static ItemStack satchelSignalum;
	public static ItemStack satchelResonant;

	public static ItemStack satchelVoid;
	public static ItemStack satchelCreative;

}
