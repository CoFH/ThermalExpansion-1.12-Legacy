package cofh.thermalexpansion.item;

import cofh.api.item.IMultiModeItem;
import cofh.core.init.CoreEnchantments;
import cofh.core.item.IEnchantableItem;
import cofh.core.item.ItemMulti;
import cofh.core.key.KeyBindingItemMultiMode;
import cofh.core.util.CoreUtils;
import cofh.core.util.core.IInitializer;
import cofh.core.util.crafting.RecipeUpgrade;
import cofh.core.util.helpers.ChatHelper;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.thermalexpansion.ThermalExpansion;
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
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;
import static cofh.lib.util.helpers.ItemHelper.addRecipe;

public class ItemCapacitor extends ItemMulti implements IInitializer, IMultiModeItem, IEnergyContainerItem, IEnchantableItem {

	public ItemCapacitor() {

		super("thermalexpansion");

		setMaxStackSize(1);
		setUnlocalizedName("capacitor");
		setCreativeTab(ThermalExpansion.tabItems);
	}

	public int getSend(ItemStack stack) {

		if (!capacitorMap.containsKey(ItemHelper.getItemDamage(stack))) {
			return 0;
		}
		return capacitorMap.get(ItemHelper.getItemDamage(stack)).send;
	}

	public int getRecv(ItemStack stack) {

		if (!capacitorMap.containsKey(ItemHelper.getItemDamage(stack))) {
			return 0;
		}
		return capacitorMap.get(ItemHelper.getItemDamage(stack)).recv;
	}

	public int getCapacity(ItemStack stack) {

		if (!capacitorMap.containsKey(ItemHelper.getItemDamage(stack))) {
			return 0;
		}
		int capacity = capacitorMap.get(ItemHelper.getItemDamage(stack)).capacity;
		int enchant = EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.holding, stack);

		return capacity + capacity * enchant / 2;
	}

	public int getBaseCapacity(int metadata) {

		if (!capacitorMap.containsKey(metadata)) {
			return 0;
		}
		return capacitorMap.get(metadata).capacity;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			tooltip.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		if (isActive(stack)) {
			tooltip.add(StringHelper.getInfoText("info.thermalexpansion.capacitor.a." + getMode(stack)));
			tooltip.add(StringHelper.localizeFormat("info.thermalexpansion.capacitor.b.0", StringHelper.getKeyName(KeyBindingItemMultiMode.instance.getKey())));
			tooltip.add(StringHelper.getInfoText("info.thermalexpansion.capacitor.c.0"));
			tooltip.add(StringHelper.getNoticeText("info.thermalexpansion.capacitor.d.0"));
		} else {
			tooltip.add(StringHelper.localizeFormat("info.thermalexpansion.capacitor.b.0", StringHelper.getKeyName(KeyBindingItemMultiMode.instance.getKey())));
			tooltip.add(StringHelper.getInfoText("info.thermalexpansion.capacitor.c.1"));
			tooltip.add(StringHelper.getNoticeText("info.thermalexpansion.capacitor.d.0"));
		}
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

		if (CoreUtils.isFakePlayer(entity)) {
			return;
		}
		if (slot > 8 || !isActive(stack)) {
			return;
		}
		EntityPlayer player = (EntityPlayer) entity;
		Iterable<ItemStack> equipment;

		switch (getMode(stack)) {
			case 0:
				equipment = player.getHeldEquipment();
				break;
			case 1:
				equipment = player.getArmorInventoryList();
				break;
			default:
				equipment = player.getEquipmentAndArmor();
		}
		for (ItemStack equipmentStack : equipment) {
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
	public boolean isDamaged(ItemStack stack) {

		return true;
	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {

		return capacitorMap.get(ItemHelper.getItemDamage(stack)).enchantable;
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
	public int getItemEnchantability() {

		return 10;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(stack, 0);
		}
		return 1D - ((double) stack.getTagCompound().getInteger("Energy") / (double) getMaxEnergyStored(stack));
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

		ChatHelper.sendIndexedChatMessageToPlayer(player, new TextComponentTranslation("info.thermalexpansion.capacitor.a." + getMode(stack)));
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		ModelLoader.setCustomMeshDefinition(this, stack -> new ModelResourceLocation(getRegistryName(), String.format("mode=%s_%s,type=%s", this.getEnergyStored(stack) > 0 && this.isActive(stack) ? 1 : 0, this.getMode(stack), capacitorMap.get(ItemHelper.getItemDamage(stack)).name)));

		for (Map.Entry<Integer, ItemEntry> entry : itemMap.entrySet()) {
			for (int active = 0; active < 2; active++) {
				for (int mode = 0; mode < 3; mode++) {
					ModelBakery.registerItemVariants(this, new ModelResourceLocation(getRegistryName(), String.format("mode=%s_%s,type=%s", active, mode, entry.getValue().name)));
				}
			}
		}
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
		int stored = container.getTagCompound().getInteger("Energy");
		int extract = Math.min(maxExtract, Math.min(stored, getSend(container)));

		if (!simulate && ItemHelper.getItemDamage(container) != CREATIVE) {
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

		return capacitorMap.containsKey(ItemHelper.getItemDamage(stack)) && capacitorMap.get(ItemHelper.getItemDamage(stack)).enchantable && enchantment == CoreEnchantments.holding;
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		capacitorBasic = addCapacitorItem(0, "standard0", SEND[0], RECV[0], CAPACITY[0], EnumRarity.COMMON);
		capacitorHardened = addCapacitorItem(1, "standard1", SEND[1], RECV[1], CAPACITY[1], EnumRarity.COMMON);
		capacitorReinforced = addCapacitorItem(2, "standard2", SEND[2], RECV[2], CAPACITY[2], EnumRarity.UNCOMMON);
		capacitorSignalum = addCapacitorItem(3, "standard3", SEND[3], RECV[3], CAPACITY[3], EnumRarity.UNCOMMON);
		capacitorResonant = addCapacitorItem(4, "standard4", SEND[4], RECV[4], CAPACITY[4], EnumRarity.RARE);

		capacitorCreative = addCapacitorItem(CREATIVE, "creative", SEND[4], 0, CAPACITY[4], EnumRarity.EPIC, false);

		ThermalExpansion.proxy.addIModelRegister(this);

		config();

		return true;
	}

	@Override
	public boolean initialize() {

		// @formatter:off

		addRecipe(ShapedRecipe(capacitorBasic,
				" R ",
				"IXI",
				"RYR",
				'I', "ingotLead",
				'R', "dustRedstone",
				'X', "ingotCopper",
				'Y', "dustSulfur"
		));
		addRecipe(new RecipeUpgrade(capacitorHardened,
				" R ",
				"IXI",
				"RYR",
				'I', "ingotInvar",
				'R', "dustRedstone",
				'X', capacitorBasic,
				'Y', "ingotTin"
		));
		addRecipe(new RecipeUpgrade(capacitorReinforced,
				" R ",
				"IXI",
				"RYR",
				'I', "ingotElectrum",
				'R', "dustRedstone",
				'X', capacitorHardened,
				'Y', "blockGlassHardened"
		));
		addRecipe(new RecipeUpgrade(capacitorSignalum,
				" R ",
				"IXI",
				"RYR",
				'I', "ingotSignalum",
				'R', "dustRedstone",
				'X', capacitorReinforced,
				'Y', "dustCryotheum"
		));
		addRecipe(new RecipeUpgrade(capacitorResonant,
				" R ",
				"IXI",
				"RYR",
				'I', "ingotEnderium",
				'R', "dustRedstone",
				'X', capacitorSignalum,
				'Y', "dustPyrotheum"
		));

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
	public class CapacitorEntry {

		public final String name;
		public final int send;
		public final int recv;
		public final int capacity;
		public final boolean enchantable;

		CapacitorEntry(String name, int send, int recv, int capacity, boolean enchantable) {

			this.name = name;
			this.send = send;
			this.recv = recv;
			this.capacity = capacity;
			this.enchantable = enchantable;
		}
	}

	private void addCapacitorEntry(int metadata, String name, int send, int recv, int capacity, boolean enchantable) {

		capacitorMap.put(metadata, new CapacitorEntry(name, send, recv, capacity, enchantable));
	}

	private ItemStack addCapacitorItem(int metadata, String name, int send, int recv, int capacity, EnumRarity rarity, boolean enchantable) {

		addCapacitorEntry(metadata, name, send, recv, capacity, enchantable);
		return addItem(metadata, name, rarity);
	}

	private ItemStack addCapacitorItem(int metadata, String name, int send, int recv, int capacity, EnumRarity rarity) {

		addCapacitorEntry(metadata, name, send, recv, capacity, true);
		return addItem(metadata, name, rarity);
	}

	private static TIntObjectHashMap<CapacitorEntry> capacitorMap = new TIntObjectHashMap<>();

	public static final int[] CAPACITY = { 1, 4, 9, 16, 25 };
	public static final int[] SEND = { 1, 4, 9, 16, 25 };
	public static final int[] RECV = { 1, 4, 9, 16, 25 };

	static {
		for (int i = 0; i < CAPACITY.length; i++) {
			CAPACITY[i] *= 1000000;
			SEND[i] *= 500;
			RECV[i] *= 2000;
		}
	}

	public static final int CREATIVE = 32000;

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
