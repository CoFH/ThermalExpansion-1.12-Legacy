package cofh.thermalexpansion.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import cofh.api.item.IMultiModeItem;
import cofh.core.init.CoreEnchantments;
import cofh.core.init.CoreProps;
import cofh.core.item.IEnchantableItem;
import cofh.core.item.ItemMultiRF;
import cofh.core.key.KeyBindingItemMultiMode;
import cofh.core.util.CoreUtils;
import cofh.core.util.core.IInitializer;
import cofh.core.util.crafting.FluidIngredientFactory.FluidIngredient;
import cofh.core.util.helpers.*;
import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.redstoneflux.util.EnergyContainerItemWrapper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalfoundation.init.TFProps;
import com.google.common.collect.Iterables;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static cofh.core.util.helpers.RecipeHelper.*;

@Optional.Interface (iface = "baubles.api.IBauble", modid = "baubles")
public class ItemCapacitor extends ItemMultiRF implements IInitializer, IBauble {

	public ItemCapacitor() {

		super("thermalexpansion");

		setUnlocalizedName("capacitor");
		setCreativeTab(ThermalExpansion.tabTools);

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

		if (isCreative(stack)) {
			tooltip.add(StringHelper.localize("info.cofh.charge") + ": 1.21G RF");
			tooltip.add(StringHelper.localize("info.cofh.send") + ": " + StringHelper.formatNumber(getSend(stack)) + " RF/t");
		} else {
			tooltip.add(StringHelper.localize("info.cofh.charge") + ": " + StringHelper.getScaledNumber(getEnergyStored(stack)) + " / " + StringHelper.getScaledNumber(getMaxEnergyStored(stack)) + " RF");
			tooltip.add(StringHelper.localize("info.cofh.send") + "/" + StringHelper.localize("info.cofh.receive") + ": " + StringHelper.formatNumber(getSend(stack)) + "/" + StringHelper.formatNumber(getReceive(stack)) + " RF/t");
		}
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (isInCreativeTab(tab)) {
			for (int metadata : itemList) {
				if (metadata != CREATIVE) {
					if (TFProps.showEmptyItems) {
						items.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(this, 1, metadata), 0));
					}
					if (TFProps.showFullItems) {
						items.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(this, 1, metadata), getBaseCapacity(metadata)));
					}
				} else {
					if (TFProps.showCreativeItems) {
						items.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(this, 1, metadata), getBaseCapacity(metadata)));
					}
				}
			}
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {

		if (ServerHelper.isClientWorld(world) || CoreUtils.isFakePlayer(entity) || !isActive(stack)) {
			return;
		}
		Iterable<ItemStack> equipment;
		EntityPlayer player = (EntityPlayer) entity;

		switch (getMode(stack)) {
			case EQUIPMENT:
				equipment = Iterables.concat(player.getEquipmentAndArmor(), BaublesHelper.getBaubles(player));
				break;
			case INVENTORY:
				equipment = player.inventory.mainInventory;
				break;
			default:
				equipment = Iterables.concat(Arrays.asList(player.inventory.mainInventory, player.inventory.armorInventory, player.inventory.offHandInventory, BaublesHelper.getBaubles(player)));
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
	public int getItemEnchantability(ItemStack stack) {

		return 10;
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
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		return EnumActionResult.FAIL;
	}

	/* HELPERS */
	public boolean setActiveState(ItemStack stack, boolean state) {

		if (getEnergyStored(stack) > 0) {
			stack.getTagCompound().setBoolean(CoreProps.ACTIVE, state);
			return true;
		}
		stack.getTagCompound().setBoolean(CoreProps.ACTIVE, false);
		return false;
	}

	@Override
	protected int getCapacity(ItemStack stack) {

		if (!typeMap.containsKey(ItemHelper.getItemDamage(stack))) {
			return 0;
		}
		int capacity = typeMap.get(ItemHelper.getItemDamage(stack)).capacity;
		int enchant = EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.holding, stack);

		return capacity + capacity * enchant / 2;
	}

	@Override
	protected int getReceive(ItemStack stack) {

		if (!typeMap.containsKey(ItemHelper.getItemDamage(stack))) {
			return 0;
		}
		return typeMap.get(ItemHelper.getItemDamage(stack)).recv;
	}

	public int getSend(ItemStack stack) {

		if (!typeMap.containsKey(ItemHelper.getItemDamage(stack))) {
			return 0;
		}
		return typeMap.get(ItemHelper.getItemDamage(stack)).send;
	}

	public int getBaseCapacity(int metadata) {

		if (!typeMap.containsKey(metadata)) {
			return 0;
		}
		return typeMap.get(metadata).capacity;
	}

	/* IBauble */
	@Override
	public BaubleType getBaubleType(ItemStack stack) {

		return BaubleType.TRINKET;
	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase entity) {

		World world = entity.world;

		if (ServerHelper.isClientWorld(world) || !isActive(stack)) {
			return;
		}
		Iterable<ItemStack> equipment;
		EntityPlayer player = (EntityPlayer) entity;

		switch (getMode(stack)) {
			case EQUIPMENT:
				equipment = Iterables.concat(entity.getEquipmentAndArmor(), BaublesHelper.getBaubles(entity));
				break;
			case INVENTORY:
				equipment = player.inventory.mainInventory;
				break;
			default:
				equipment = Iterables.concat(Arrays.asList(player.inventory.mainInventory, player.inventory.armorInventory, player.inventory.offHandInventory, BaublesHelper.getBaubles(player)));
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
	public boolean willAutoSync(ItemStack stack, EntityLivingBase player) {

		return true;
	}

	/* IEnergyContainerItem */
	@Override
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {

		if (container.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		if (isCreative(container)) {
			return maxExtract;
		}
		int stored = Math.min(container.getTagCompound().getInteger(CoreProps.ENERGY), getMaxEnergyStored(container));
		int extract = Math.min(maxExtract, Math.min(stored, getSend(container)));

		if (!simulate) {
			stored -= extract;
			container.getTagCompound().setInteger(CoreProps.ENERGY, stored);
		}
		return extract;
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

	/* CAPABILITIES */
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {

		return new EnergyContainerItemWrapper(stack, this);
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		ModelLoader.setCustomMeshDefinition(this, stack -> new ModelResourceLocation(getRegistryName(), String.format("color0=%s,mode=%s_%s,type=%s", ColorHelper.hasColor0(stack) ? 1 : 0, this.getEnergyStored(stack) > 0 && this.isActive(stack) ? 1 : 0, this.getMode(stack), typeMap.get(ItemHelper.getItemDamage(stack)).name)));

		for (Map.Entry<Integer, ItemEntry> entry : itemMap.entrySet()) {
			for (int color0 = 0; color0 < 2; color0++) {
				for (int active = 0; active < 2; active++) {
					for (int mode = 0; mode < 3; mode++) {
						ModelBakery.registerItemVariants(this, new ModelResourceLocation(getRegistryName(), String.format("color0=%s,mode=%s_%s,type=%s", color0, active, mode, entry.getValue().name)));
					}
				}
			}
		}
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		ForgeRegistries.ITEMS.register(setRegistryName("capacitor"));
		ThermalExpansion.proxy.addIModelRegister(this);

		config();

		capacitorBasic = addEntryItem(0, "standard0", EnumRarity.COMMON);
		capacitorHardened = addEntryItem(1, "standard1", EnumRarity.COMMON);
		capacitorReinforced = addEntryItem(2, "standard2", EnumRarity.UNCOMMON);
		capacitorSignalum = addEntryItem(3, "standard3", EnumRarity.UNCOMMON);
		capacitorResonant = addEntryItem(4, "standard4", EnumRarity.RARE);

		capacitorCreative = addEntryItem(CREATIVE, "creative", SEND[4] * 10, 0, CAPACITY[4], EnumRarity.EPIC);

		return true;
	}

	@Override
	public boolean initialize() {

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

		addColorRecipe(capacitorBasic, capacitorBasic, "dye");
		addColorRecipe(capacitorHardened, capacitorHardened, "dye");
		addColorRecipe(capacitorReinforced, capacitorReinforced, "dye");
		addColorRecipe(capacitorSignalum, capacitorSignalum, "dye");
		addColorRecipe(capacitorResonant, capacitorResonant, "dye");

		addColorRemoveRecipe(capacitorBasic, capacitorBasic);
		addColorRemoveRecipe(capacitorHardened, capacitorHardened);
		addColorRemoveRecipe(capacitorReinforced, capacitorReinforced);
		addColorRemoveRecipe(capacitorSignalum, capacitorSignalum);
		addColorRemoveRecipe(capacitorResonant, capacitorResonant);
		return true;
	}

	private static void config() {

		String category = "Item.Capacitor";
		String comment;
		enable = ThermalExpansion.CONFIG.get(category, "Enable", true);

		int capacity = CAPACITY_BASE;
		comment = "Adjust this value to change the amount of Energy (in RF) stored by a Basic Flux Capacitor. This base value will scale with item level.";
		capacity = ThermalExpansion.CONFIG.getConfiguration().getInt("BaseCapacity", category, capacity, CAPACITY_MIN, CAPACITY_MAX, comment);

		int recv = XFER_BASE * 2;
		comment = "Adjust this value to change the amount of Energy (in RF/t) that can be received by a Basic Flux Capacitor. This base value will scale with item level.";
		recv = ThermalExpansion.CONFIG.getConfiguration().getInt("BaseReceive", category, recv, XFER_MIN, XFER_MAX, comment);

		int send = XFER_BASE;
		comment = "Adjust this value to change the amount of Energy (in RF/t) that can be sent by a Basic Flux Capacitor. This base value will scale with item level.";
		send = ThermalExpansion.CONFIG.getConfiguration().getInt("BaseSend", category, send, XFER_MIN, XFER_MAX, comment);

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

		TypeEntry(String name, int send, int recv, int capacity) {

			this.name = name;
			this.send = send;
			this.recv = recv;
			this.capacity = capacity;
		}
	}

	private void addEntry(int metadata, String name, int send, int recv, int capacity) {

		typeMap.put(metadata, new TypeEntry(name, send, recv, capacity));
	}

	private ItemStack addEntryItem(int metadata, String name, EnumRarity rarity) {

		addEntry(metadata, name, SEND[metadata], RECV[metadata], CAPACITY[metadata]);
		return addItem(metadata, name, rarity);
	}

	private ItemStack addEntryItem(int metadata, String name, int send, int recv, int capacity, EnumRarity rarity) {

		addEntry(metadata, name, send, recv, capacity);
		return addItem(metadata, name, rarity);
	}

	private static TIntObjectHashMap<TypeEntry> typeMap = new TIntObjectHashMap<>();

	public static final int EQUIPMENT = 0;
	public static final int INVENTORY = 1;

	public static final int CAPACITY_BASE = 1000000;
	public static final int XFER_BASE = 1000;

	public static final int[] CAPACITY = { 1, 4, 9, 16, 25 };
	public static final int[] RECV = { 1, 4, 9, 16, 25 };
	public static final int[] SEND = { 1, 4, 9, 16, 25 };

	public static boolean enable = true;

	/* REFERENCES */
	public static ItemStack capacitorBasic;
	public static ItemStack capacitorHardened;
	public static ItemStack capacitorReinforced;
	public static ItemStack capacitorSignalum;
	public static ItemStack capacitorResonant;

	public static ItemStack capacitorCreative;

}
