package cofh.thermalexpansion.item;

import baubles.api.cap.IBaublesItemHandler;
import cofh.api.item.IMultiModeItem;
import cofh.api.item.INBTCopyIngredient;
import cofh.core.init.CoreEnchantments;
import cofh.core.init.CoreProps;
import cofh.core.item.IEnchantableItem;
import cofh.core.item.ItemMulti;
import cofh.core.key.KeyBindingItemMultiMode;
import cofh.core.util.CoreUtils;
import cofh.core.util.core.IInitializer;
import cofh.core.util.helpers.*;
import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.redstoneflux.util.EnergyContainerItemWrapper;
import cofh.thermalexpansion.ThermalExpansion;
import com.google.common.collect.Iterables;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static cofh.core.util.helpers.RecipeHelper.addShapedRecipe;

public class ItemCapacitor extends ItemMulti implements IInitializer, IMultiModeItem, IEnergyContainerItem, IEnchantableItem, INBTCopyIngredient {

	public ItemCapacitor() {

		super("thermalexpansion");

		setUnlocalizedName("capacitor");
		setCreativeTab(ThermalExpansion.tabItems);

		setHasSubtypes(true);
		setMaxStackSize(1);
		setNoRepair();
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			tooltip.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		tooltip.add(StringHelper.getInfoText("info.thermalexpansion.capacitor.a.0"));

		if (isActive(stack)) {
			tooltip.add(StringHelper.getNoticeText("info.thermalexpansion.capacitor.d." + getMode(stack)));
			tooltip.add(StringHelper.getDeactivationText("info.thermalexpansion.capacitor.c.1"));
		} else {
			tooltip.add(StringHelper.getActivationText("info.thermalexpansion.capacitor.c.0"));
		}
		tooltip.add(StringHelper.localizeFormat("info.thermalexpansion.capacitor.b.0", StringHelper.getKeyName(KeyBindingItemMultiMode.INSTANCE.getKey())));

		if (ItemHelper.getItemDamage(stack) == CREATIVE) {
			tooltip.add(StringHelper.localize("info.cofh.charge") + ": 1.21G RF");
			tooltip.add(StringHelper.localize("info.cofh.send") + ": " + StringHelper.formatNumber(getSend(stack)) + " RF/t");
		} else {
			tooltip.add(StringHelper.localize("info.cofh.charge") + ": " + StringHelper.getScaledNumber(getEnergyStored(stack)) + " / " + StringHelper.getScaledNumber(getMaxEnergyStored(stack)) + " RF");
			tooltip.add(StringHelper.localize("info.cofh.send") + "/" + StringHelper.localize("info.cofh.receive") + ": " + StringHelper.formatNumber(getSend(stack)) + "/" + StringHelper.formatNumber(getRecv(stack)) + " RF/t");
		}
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (isInCreativeTab(tab)) {
			for (int metadata : itemList) {
				if (metadata != CREATIVE) {
					items.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(this, 1, metadata), 0));
					items.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(this, 1, metadata), getBaseCapacity(metadata)));
				} else {
					items.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(this, 1, metadata), getBaseCapacity(metadata)));
				}
			}
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isCurrentItem) {

		if (ServerHelper.isClientWorld(world) || CoreUtils.isFakePlayer(entity) || !isActive(stack)) {
			return;
		}
		Iterable<ItemStack> equipment;

		switch (getMode(stack)) {
			case HELD_ITEMS:
				equipment = entity.getHeldEquipment();
				break;
			case WORN_ITEMS:
				equipment = Iterables.concat(entity.getArmorInventoryList(), getBaubles(entity));
				break;
			default:
				equipment = Iterables.concat(entity.getEquipmentAndArmor(), getBaubles(entity));
		}
		for (ItemStack equipmentStack : equipment) {
			if (equipmentStack.equals(stack)) {
				continue;
			}
			if (EnergyHelper.isEnergyContainerItem(equipmentStack)) {
				extractEnergy(stack, ((IEnergyContainerItem) equipmentStack.getItem()).receiveEnergy(equipmentStack, Math.min(getEnergyStored(stack), getSend(stack)), false), false);
			} else if (EnergyHelper.isEnergyHandler(equipmentStack)) {
				IEnergyStorage handler = EnergyHelper.getEnergyHandler(equipmentStack);
				if (handler != null) {
					extractEnergy(stack, handler.receiveEnergy(Math.min(getEnergyStored(stack), getSend(stack)), false), false);
				}
			}
		}
	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {

		return typeMap.get(ItemHelper.getItemDamage(stack)).enchantable;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && (slotChanged || !ItemHelper.areItemStacksEqualIgnoreTags(oldStack, newStack, "Energy"));
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {

		return ItemHelper.getItemDamage(stack) != CREATIVE;
	}

	@Override
	public int getItemEnchantability(ItemStack stack) {

		return 10;
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {

		return CoreProps.RGB_DURABILITY_FLUX;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(stack, 0);
		}
		return 1.0D - ((double) stack.getTagCompound().getInteger("Energy") / (double) getMaxEnergyStored(stack));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		ItemStack stack = player.getHeldItem(hand);
		if (CoreUtils.isFakePlayer(player)) {
			return new ActionResult<>(EnumActionResult.FAIL, stack);
		}
		if (player.isSneaking()) {
			if (setActiveState(stack, !isActive(stack))) {
				if (isActive(stack)) {
					player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.2F, 0.8F);
				} else {
					player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.2F, 0.5F);
				}
			}
		}
		player.swingArm(hand);
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		return EnumActionResult.FAIL;
	}

	/* HELPERS */
	public boolean isActive(ItemStack stack) {

		return stack.getTagCompound() != null && stack.getTagCompound().getBoolean("Active");
	}

	public boolean setActiveState(ItemStack stack, boolean state) {

		if (getEnergyStored(stack) > 0) {
			stack.getTagCompound().setBoolean("Active", state);
			return true;
		}
		stack.getTagCompound().setBoolean("Active", false);
		return false;
	}

	public int getSend(ItemStack stack) {

		if (!typeMap.containsKey(ItemHelper.getItemDamage(stack))) {
			return 0;
		}
		return typeMap.get(ItemHelper.getItemDamage(stack)).send;
	}

	public int getRecv(ItemStack stack) {

		if (!typeMap.containsKey(ItemHelper.getItemDamage(stack))) {
			return 0;
		}
		return typeMap.get(ItemHelper.getItemDamage(stack)).recv;
	}

	public int getCapacity(ItemStack stack) {

		if (!typeMap.containsKey(ItemHelper.getItemDamage(stack))) {
			return 0;
		}
		int capacity = typeMap.get(ItemHelper.getItemDamage(stack)).capacity;
		int enchant = EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.holding, stack);

		return capacity + capacity * enchant / 2;
	}

	public int getBaseCapacity(int metadata) {

		if (!typeMap.containsKey(metadata)) {
			return 0;
		}
		return typeMap.get(metadata).capacity;
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		ModelLoader.setCustomMeshDefinition(this, stack -> new ModelResourceLocation(getRegistryName(), String.format("mode=%s_%s,type=%s", this.getEnergyStored(stack) > 0 && this.isActive(stack) ? 1 : 0, this.getMode(stack), typeMap.get(ItemHelper.getItemDamage(stack)).name)));

		for (Map.Entry<Integer, ItemEntry> entry : itemMap.entrySet()) {
			for (int active = 0; active < 2; active++) {
				for (int mode = 0; mode < 3; mode++) {
					ModelBakery.registerItemVariants(this, new ModelResourceLocation(getRegistryName(), String.format("mode=%s_%s,type=%s", active, mode, entry.getValue().name)));
				}
			}
		}
	}

	/* IMultiModeItem */
	@Override
	public int getNumModes(ItemStack stack) {

		return 3;
	}

	@Override
	public void onModeChange(EntityPlayer player, ItemStack stack) {

		player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 0.4F, (isActive(stack) ? 0.7F : 0.5F) + 0.1F * getMode(stack));
		ChatHelper.sendIndexedChatMessageToPlayer(player, new TextComponentTranslation("info.thermalexpansion.capacitor.d." + getMode(stack)));
	}

	/* IEnergyContainerItem */
	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {

		if (container.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		int stored = container.getTagCompound().getInteger("Energy");
		int receive = Math.min(maxReceive, Math.min(getMaxEnergyStored(container) - stored, getRecv(container)));

		if (!simulate && ItemHelper.getItemDamage(container) != CREATIVE) {
			stored += receive;
			container.getTagCompound().setInteger("Energy", stored);
		}
		return receive;
	}

	@Override
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {

		if (container.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		if (ItemHelper.getItemDamage(container) == CREATIVE) {
			return maxExtract;
		}
		int stored = container.getTagCompound().getInteger("Energy");
		int extract = Math.min(maxExtract, Math.min(stored, getSend(container)));

		if (!simulate) {
			stored -= extract;
			container.getTagCompound().setInteger("Energy", stored);
		}
		return extract;
	}

	@Override
	public int getEnergyStored(ItemStack container) {

		if (container.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		return container.getTagCompound().getInteger("Energy");
	}

	@Override
	public int getMaxEnergyStored(ItemStack container) {

		return getCapacity(container);
	}

	/* IEnchantableItem */
	@Override
	public boolean canEnchant(ItemStack stack, Enchantment enchantment) {

		return typeMap.containsKey(ItemHelper.getItemDamage(stack)) && typeMap.get(ItemHelper.getItemDamage(stack)).enchantable && enchantment == CoreEnchantments.holding;
	}

	/* CAPABILITIES */
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {

		return new EnergyContainerItemWrapper(stack, this);
	}

	/* BAUBLES */
	@CapabilityInject (IBaublesItemHandler.class)
	private static Capability<IBaublesItemHandler> CAPABILITY_BAUBLES = null;

	private static Iterable<ItemStack> getBaubles(Entity entity) {

		if (CAPABILITY_BAUBLES == null) {
			return Collections.emptyList();
		}
		IBaublesItemHandler handler = entity.getCapability(CAPABILITY_BAUBLES, null);

		if (handler == null) {
			return Collections.emptyList();
		}
		return IntStream.range(0, handler.getSlots()).mapToObj(handler::getStackInSlot).filter(stack -> !stack.isEmpty()).collect(Collectors.toList());
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		config();

		capacitorBasic = addEntryItem(0, "standard0", SEND[0], RECV[0], CAPACITY[0], EnumRarity.COMMON);
		capacitorHardened = addEntryItem(1, "standard1", SEND[1], RECV[1], CAPACITY[1], EnumRarity.COMMON);
		capacitorReinforced = addEntryItem(2, "standard2", SEND[2], RECV[2], CAPACITY[2], EnumRarity.UNCOMMON);
		capacitorSignalum = addEntryItem(3, "standard3", SEND[3], RECV[3], CAPACITY[3], EnumRarity.UNCOMMON);
		capacitorResonant = addEntryItem(4, "standard4", SEND[4], RECV[4], CAPACITY[4], EnumRarity.RARE);

		capacitorCreative = addEntryItem(CREATIVE, "creative", SEND_CREATIVE, 0, 0, EnumRarity.EPIC, false);

		ThermalExpansion.proxy.addIModelRegister(this);

		return true;
	}

	@Override
	public boolean register() {

		if (!enable) {
			return false;
		}
		// @formatter:off

		addShapedRecipe(capacitorBasic,
				" R ",
				"IXI",
				"RYR",
				'I', "ingotLead",
				'R', "dustRedstone",
				'X', "ingotCopper",
				'Y', "dustSulfur"
		);

		// @formatter:on

		return true;
	}

	private static void config() {

		String category = "Item.Capacitor";
		String comment;
		enable = ThermalExpansion.CONFIG.get(category, "Enable", true);

		int capacity = CAPACITY_BASE;
		comment = "Adjust this value to change the amount of Energy (in RF) stored by a Basic Flux Capacitor. This base value will scale with item level.";
		capacity = ThermalExpansion.CONFIG.getConfiguration().getInt("BaseCapacity", category, capacity, capacity / 5, capacity * 5, comment);

		int recv = XFER_BASE * 2;
		comment = "Adjust this value to change the amount of Energy (in RF/t) that can be received by a Basic Flux Capacitor. This base value will scale with item level.";
		recv = ThermalExpansion.CONFIG.getConfiguration().getInt("BaseReceive", category, recv, recv / 10, recv * 10, comment);

		int send = XFER_BASE;
		comment = "Adjust this value to change the amount of Energy (in RF/t) that can be sent by a Basic Flux Capacitor. This base value will scale with item level.";
		send = ThermalExpansion.CONFIG.getConfiguration().getInt("BaseSend", category, send, send / 10, send * 10, comment);

		for (int i = 0; i < CAPACITY.length; i++) {
			CAPACITY[i] *= capacity;
			RECV[i] *= recv;
			SEND[i] *= send;
		}
	}

	/* ENTRY */
	public class TypeEntry {

		public final String name;
		public final int send;
		public final int recv;
		public final int capacity;
		public final boolean enchantable;

		TypeEntry(String name, int send, int recv, int capacity, boolean enchantable) {

			this.name = name;
			this.send = send;
			this.recv = recv;
			this.capacity = capacity;
			this.enchantable = enchantable;
		}
	}

	private void addEntry(int metadata, String name, int send, int recv, int capacity, boolean enchantable) {

		typeMap.put(metadata, new TypeEntry(name, send, recv, capacity, enchantable));
	}

	private ItemStack addEntryItem(int metadata, String name, int send, int recv, int capacity, EnumRarity rarity, boolean enchantable) {

		addEntry(metadata, name, send, recv, capacity, enchantable);
		return addItem(metadata, name, rarity);
	}

	private ItemStack addEntryItem(int metadata, String name, int send, int recv, int capacity, EnumRarity rarity) {

		addEntry(metadata, name, send, recv, capacity, true);
		return addItem(metadata, name, rarity);
	}

	private static TIntObjectHashMap<TypeEntry> typeMap = new TIntObjectHashMap<>();

	public static final int HELD_ITEMS = 0;
	public static final int WORN_ITEMS = 1;

	public static final int CAPACITY_BASE = 1000000;
	public static final int XFER_BASE = 1000;
	public static final int CREATIVE = 32000;

	public static final int[] CAPACITY = { 1, 4, 9, 16, 25 };
	public static final int[] RECV = { 1, 4, 9, 16, 25 };
	public static final int[] SEND = { 1, 4, 9, 16, 25 };
	public static final int SEND_CREATIVE = 25 * 10000;

	public static boolean enable = true;

	/* REFERENCES */
	public static ItemStack capacitorBasic;
	public static ItemStack capacitorHardened;
	public static ItemStack capacitorReinforced;
	public static ItemStack capacitorSignalum;
	public static ItemStack capacitorResonant;

	public static ItemStack capacitorCreative;
	public static ItemStack capacitorPotato;

}
