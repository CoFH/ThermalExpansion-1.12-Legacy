package cofh.thermalexpansion.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.bakery.generation.IBakery;
import cofh.api.fluid.IFluidContainerItem;
import cofh.api.item.IColorableItem;
import cofh.api.item.IMultiModeItem;
import cofh.core.init.CoreEnchantments;
import cofh.core.init.CoreProps;
import cofh.core.item.IEnchantableItem;
import cofh.core.item.ItemMulti;
import cofh.core.key.KeyBindingItemMultiMode;
import cofh.core.util.CoreUtils;
import cofh.core.util.RayTracer;
import cofh.core.util.capabilities.FluidContainerItemWrapper;
import cofh.core.util.core.IInitializer;
import cofh.core.util.helpers.*;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.render.item.ModelReservoir;
import cofh.thermalfoundation.init.TFProps;
import cofh.thermalfoundation.item.ItemMaterial;
import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static cofh.core.util.helpers.RecipeHelper.*;

@Optional.Interface (iface = "baubles.api.IBauble", modid = "baubles")
public class ItemReservoir extends ItemMulti implements IInitializer, IBauble, IColorableItem, IEnchantableItem, IFluidContainerItem, IMultiModeItem, IBakeryProvider {

	public ItemReservoir() {

		super("thermalexpansion");

		setUnlocalizedName("reservoir");
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
		tooltip.add(StringHelper.getInfoText("info.thermalexpansion.reservoir.a.0"));
		tooltip.add(StringHelper.localizeFormat("info.thermalexpansion.reservoir.a." + (getMode(stack) + 1), StringHelper.getKeyName(KeyBindingItemMultiMode.INSTANCE.getKey())));

		if (isActive(stack)) {
			tooltip.add(StringHelper.getNoticeText("info.thermalexpansion.reservoir.d.2"));
			tooltip.add(StringHelper.getDeactivationText("info.thermalexpansion.reservoir.c.1"));
		} else {
			tooltip.add(StringHelper.getActivationText("info.thermalexpansion.reservoir.c.0"));
		}
		tooltip.add(StringHelper.localizeFormat("info.thermalexpansion.reservoir.b.0", StringHelper.getKeyName(KeyBindingItemMultiMode.INSTANCE.getKey())));

		FluidStack fluid = getFluid(stack);
		if (fluid != null) {
			String color = fluid.getFluid().getRarity().rarityColor.toString();
			tooltip.add(StringHelper.localize("info.cofh.fluid") + ": " + color + fluid.getFluid().getLocalizedName(fluid) + StringHelper.LIGHT_GRAY);
			if (isCreative(stack)) {
				tooltip.add(StringHelper.localize("info.cofh.infiniteSource"));
			} else {
				tooltip.add(StringHelper.localize("info.cofh.level") + ": " + StringHelper.formatNumber(fluid.amount) + " / " + StringHelper.formatNumber(getCapacity(stack)) + " mB");
			}
		} else {
			tooltip.add(StringHelper.localize("info.cofh.fluid") + ": " + StringHelper.localize("info.cofh.empty"));

			if (isCreative(stack)) {
				tooltip.add(StringHelper.localize("info.cofh.infiniteSource"));
			} else {
				tooltip.add(StringHelper.localize("info.cofh.level") + ": 0 / " + StringHelper.formatNumber(getCapacity(stack)) + " mB");
			}
		}
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (enable && isInCreativeTab(tab)) {
			for (int metadata : itemList) {
				if (metadata != CREATIVE) {
					items.add(new ItemStack(this, 1, metadata));
				} else {
					if (TFProps.showCreativeItems) {
						items.add(new ItemStack(this, 1, metadata));
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
		Iterable<ItemStack> equipment = Iterables.concat(entity.getEquipmentAndArmor(), BaublesHelper.getBaubles(entity));

		for (ItemStack equipmentStack : equipment) {
			if (equipmentStack.equals(stack) || equipmentStack.getItem() == Items.BUCKET) {
				continue;
			}
			if (FluidHelper.isFluidHandler(equipmentStack)) {
				IFluidHandlerItem handler = FluidUtil.getFluidHandler(equipmentStack);
				if (handler != null && getFluid(stack) != null) {
					drain(stack, handler.fill(new FluidStack(getFluid(stack), Math.min(getFluidAmount(stack), Fluid.BUCKET_VOLUME)), true), true);
				}
			}
		}
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {

		return !EnumEnchantmentType.BREAKABLE.equals(enchantment.type) && super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public boolean isDamageable() {

		return true;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {

		return ItemHelper.getItemDamage(stack) != CREATIVE;
	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && (slotChanged || !ItemHelper.areItemStacksEqualIgnoreTags(oldStack, newStack, CoreProps.FLUID));
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {

		return !isCreative(stack) && getFluidAmount(stack) > 0;
	}

	@Override
	public int getItemEnchantability(ItemStack stack) {

		return 10;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {

		return MathHelper.clamp(1.0D - ((double) getFluidAmount(stack) / (double) getCapacity(stack)), 0.0D, 1.0D);
	}

	@Override
	@Nonnull
	public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {

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
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}
		if (getMode(stack) == BUCKET_FILL) {
			return doBucketFill(stack, world, player, hand);
		}
		if (getMode(stack) == BUCKET_EMPTY) {
			return doBucketEmpty(stack, world, player, hand);
		}
		return ActionResult.newResult(EnumActionResult.FAIL, stack);
	}

	ActionResult<ItemStack> doBucketFill(ItemStack stack, @Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {

		if (getSpace(stack) < Fluid.BUCKET_VOLUME) {
			return ActionResult.newResult(EnumActionResult.PASS, stack);
		}
		RayTraceResult traceResult = RayTracer.retrace(player, true);
		if (traceResult == null || traceResult.sideHit == null || traceResult.typeOfHit != RayTraceResult.Type.BLOCK) {
			return ActionResult.newResult(EnumActionResult.PASS, stack);
		}
		BlockPos pos = traceResult.getBlockPos();
		if (world.isBlockModifiable(player, pos)) {
			if (player.canPlayerEdit(pos, traceResult.sideHit, stack)) {
				FluidActionResult result = FluidUtil.tryPickUpFluid(stack, player, world, pos, traceResult.sideHit);
				if (result.isSuccess() && !player.capabilities.isCreativeMode) {
					player.addStat(StatList.getObjectUseStats(this));
					return ActionResult.newResult(EnumActionResult.SUCCESS, result.getResult());
				}
			}
		}
		return ActionResult.newResult(EnumActionResult.FAIL, stack);
	}

	ActionResult<ItemStack> doBucketEmpty(ItemStack stack, @Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {

		if (getFluidAmount(stack) < Fluid.BUCKET_VOLUME) {
			return ActionResult.newResult(EnumActionResult.PASS, stack);
		}
		RayTraceResult traceResult = this.rayTrace(world, player, false);
		if (traceResult == null || traceResult.typeOfHit != RayTraceResult.Type.BLOCK) {
			return ActionResult.newResult(EnumActionResult.PASS, stack);
		}
		BlockPos pos = traceResult.getBlockPos();
		if (world.isBlockModifiable(player, pos)) {
			BlockPos targetPos = pos.offset(traceResult.sideHit);
			if (player.canPlayerEdit(targetPos, traceResult.sideHit.getOpposite(), stack)) {
				FluidActionResult result = FluidUtil.tryPlaceFluid(player, world, targetPos, stack, new FluidStack(getFluid(stack), Fluid.BUCKET_VOLUME));
				if (result.isSuccess() && !player.capabilities.isCreativeMode) {
					player.addStat(StatList.getObjectUseStats(this));
					return ActionResult.newResult(EnumActionResult.SUCCESS, result.getResult());
				}
			}
		}
		return ActionResult.newResult(EnumActionResult.FAIL, stack);
	}

	/* HELPERS */
	public boolean setActiveState(ItemStack stack, boolean state) {

		if (getFluid(stack) != null) {
			stack.getTagCompound().setBoolean(CoreProps.ACTIVE, state);
			return true;
		}
		stack.getTagCompound().setBoolean(CoreProps.ACTIVE, false);
		return false;
	}

	public int getFluidAmount(ItemStack stack) {

		FluidStack fluid = getFluid(stack);
		return fluid == null ? 0 : fluid.amount;
	}

	public int getSpace(ItemStack stack) {

		return getCapacity(stack) - getFluidAmount(stack);
	}

	/* IBauble */
	@Override
	public BaubleType getBaubleType(ItemStack stack) {

		return BaubleType.TRINKET;
	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase player) {

		World world = player.world;

		if (ServerHelper.isClientWorld(world) || !isActive(stack)) {
			return;
		}
		Iterable<ItemStack> equipment = Iterables.concat(player.getEquipmentAndArmor(), BaublesHelper.getBaubles(player));

		for (ItemStack equipmentStack : equipment) {
			if (equipmentStack.equals(stack) || equipmentStack.getItem() == Items.BUCKET) {
				continue;
			}
			if (FluidHelper.isFluidHandler(equipmentStack)) {
				IFluidHandlerItem handler = FluidUtil.getFluidHandler(equipmentStack);
				if (handler != null && getFluid(stack) != null) {
					drain(stack, handler.fill(new FluidStack(getFluid(stack), Math.min(getFluidAmount(stack), Fluid.BUCKET_VOLUME)), true), true);
				}
			}
		}
	}

	@Override
	public boolean willAutoSync(ItemStack stack, EntityLivingBase player) {

		return true;
	}

	/* IEnchantableItem */
	@Override
	public boolean canEnchant(ItemStack stack, Enchantment enchantment) {

		return enchantment == CoreEnchantments.holding;
	}

	/* IFluidContainerItem */
	@Override
	public FluidStack getFluid(ItemStack container) {

		if (container.getTagCompound() == null) {
			container.setTagCompound(new NBTTagCompound());
		}
		if (!container.getTagCompound().hasKey(CoreProps.FLUID)) {
			return null;
		}
		return FluidStack.loadFluidStackFromNBT(container.getTagCompound().getCompoundTag(CoreProps.FLUID));
	}

	@Override
	public int getCapacity(ItemStack stack) {

		if (!typeMap.containsKey(ItemHelper.getItemDamage(stack))) {
			return 0;
		}
		int capacity = typeMap.get(ItemHelper.getItemDamage(stack)).capacity;
		int enchant = EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.holding, stack);

		return capacity + capacity * enchant / 2;
	}

	@Override
	public int fill(ItemStack container, FluidStack resource, boolean doFill) {

		if (container.getTagCompound() == null) {
			container.setTagCompound(new NBTTagCompound());
		}
		if (resource == null || resource.amount <= 0) {
			return 0;
		}
		int capacity = getCapacity(container);

		if (ItemHelper.getItemDamage(container) == CREATIVE) {
			if (doFill) {
				NBTTagCompound fluidTag = resource.writeToNBT(new NBTTagCompound());
				fluidTag.setInteger(CoreProps.AMOUNT, capacity - Fluid.BUCKET_VOLUME);
				container.getTagCompound().setTag(CoreProps.FLUID, fluidTag);
			}
			return resource.amount;
		}
		if (!doFill) {
			if (!container.getTagCompound().hasKey(CoreProps.FLUID)) {
				return Math.min(capacity, resource.amount);
			}
			FluidStack stack = FluidStack.loadFluidStackFromNBT(container.getTagCompound().getCompoundTag(CoreProps.FLUID));

			if (stack == null) {
				return Math.min(capacity, resource.amount);
			}
			if (!stack.isFluidEqual(resource)) {
				return 0;
			}
			return Math.min(capacity - stack.amount, resource.amount);
		}
		if (!container.getTagCompound().hasKey(CoreProps.FLUID)) {
			NBTTagCompound fluidTag = resource.writeToNBT(new NBTTagCompound());

			if (capacity < resource.amount) {
				fluidTag.setInteger(CoreProps.AMOUNT, capacity);
				container.getTagCompound().setTag(CoreProps.FLUID, fluidTag);
				return capacity;
			}
			fluidTag.setInteger(CoreProps.AMOUNT, resource.amount);
			container.getTagCompound().setTag(CoreProps.FLUID, fluidTag);
			return resource.amount;
		}
		NBTTagCompound fluidTag = container.getTagCompound().getCompoundTag(CoreProps.FLUID);
		FluidStack stack = FluidStack.loadFluidStackFromNBT(fluidTag);

		if (!stack.isFluidEqual(resource)) {
			return 0;
		}
		int filled = capacity - stack.amount;

		if (resource.amount < filled) {
			stack.amount += resource.amount;
			filled = resource.amount;
		} else {
			stack.amount = capacity;
		}
		container.getTagCompound().setTag(CoreProps.FLUID, stack.writeToNBT(fluidTag));
		return filled;
	}

	@Override
	public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {

		if (container.getTagCompound() == null) {
			container.setTagCompound(new NBTTagCompound());
		}
		if (!container.getTagCompound().hasKey(CoreProps.FLUID) || maxDrain == 0) {
			return null;
		}
		FluidStack stack = FluidStack.loadFluidStackFromNBT(container.getTagCompound().getCompoundTag(CoreProps.FLUID));

		if (stack == null) {
			return null;
		}
		boolean creative = isCreative(container);
		int drained = creative ? maxDrain : Math.min(stack.amount, maxDrain);

		if (doDrain && !creative) {
			if (maxDrain >= stack.amount) {
				container.getTagCompound().removeTag(CoreProps.FLUID);
				return stack;
			}
			NBTTagCompound fluidTag = container.getTagCompound().getCompoundTag(CoreProps.FLUID);
			fluidTag.setInteger(CoreProps.AMOUNT, fluidTag.getInteger(CoreProps.AMOUNT) - drained);
			container.getTagCompound().setTag(CoreProps.FLUID, fluidTag);
		}
		stack.amount = drained;
		return stack;
	}

	/* IMultiModeItem */
	@Override
	public void onModeChange(EntityPlayer player, ItemStack stack) {

		switch (getMode(stack)) {
			case BUCKET_FILL:
				player.world.playSound(null, player.getPosition(), SoundEvents.ITEM_BUCKET_FILL, SoundCategory.PLAYERS, 0.6F, 1.0F);
				break;
			case BUCKET_EMPTY:
				player.world.playSound(null, player.getPosition(), SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 0.6F, 1.0F);
				break;
		}
		ChatHelper.sendIndexedChatMessageToPlayer(player, new TextComponentTranslation("info.thermalexpansion.reservoir.d." + getMode(stack)));
	}

	/* CAPABILITIES */
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {

		return new FluidContainerItemWrapper(stack, this);
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		ModelResourceLocation loc = new ModelResourceLocation(getRegistryName(), "inventory");
		ModelLoader.setCustomMeshDefinition(this, stack -> loc);
		ModelRegistryHelper.register(loc, new CCBakeryModel());
		ModelBakery.registerItemKeyGenerator(this, stack -> {
			int colour = ColorHelper.hasColor0(stack) ? 1 : 0;
			int active = isActive(stack) ? 1 : 0;
			int mode = getMode(stack);
			String fluid_hash = "none";
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey(CoreProps.FLUID)) {
				FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag(CoreProps.FLUID));
				if (fluid != null) {
					fluid_hash = Integer.toString(FluidHelper.getFluidHash(fluid));
				}
			}

			return String.format("%s|%s,color0=%s,mode=%s_%s,fluid=%s", getRegistryName(), stack.getMetadata(), colour, active, mode, fluid_hash);
		});
	}

	/* IBakeryProvider */
	@Override
	public IBakery getBakery() {

		return ModelReservoir.INSTANCE;
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		ForgeRegistries.ITEMS.register(setRegistryName("reservoir"));
		ThermalExpansion.proxy.addIModelRegister(this);

		config();

		reservoirBasic = addEntryItem(0, "standard0", CAPACITY[0], EnumRarity.COMMON);
		reservoirHardened = addEntryItem(1, "standard1", CAPACITY[1], EnumRarity.COMMON);
		reservoirReinforced = addEntryItem(2, "standard2", CAPACITY[2], EnumRarity.UNCOMMON);
		reservoirSignalum = addEntryItem(3, "standard3", CAPACITY[3], EnumRarity.UNCOMMON);
		reservoirResonant = addEntryItem(4, "standard4", CAPACITY[4], EnumRarity.RARE);

		reservoirCreative = addEntryItem(CREATIVE, "creative", CAPACITY[4], EnumRarity.EPIC);

		return true;
	}

	@Override
	public boolean initialize() {

		if (!enable) {
			return false;
		}
		// @formatter:off
		addShapedRecipe(reservoirBasic,
				" R ",
				"IXI",
				" Y ",
				'I', "ingotCopper",
				'R', "ingotTin",
				'X', Items.BUCKET,
				'Y', ItemMaterial.redstoneServo
		);

		addShapedUpgradeRecipe(reservoirHardened,
				" R ",
				"IXI",
				" Y ",
				'I', "ingotInvar",
				'R', "blockGlass",
				'X', reservoirBasic,
				'Y', "ingotCopper"
		);
		addShapedUpgradeRecipe(reservoirReinforced,
				" R ",
				"IXI",
				" Y ",
				'I', "ingotElectrum",
				'R', "blockGlassHardened",
				'X', reservoirHardened,
				'Y', "ingotInvar"
		);
		addShapedUpgradeRecipe(reservoirSignalum,
				" R ",
				"IXI",
				" Y ",
				'I', "ingotSignalum",
				'R', "dustCryotheum",
				'X', reservoirReinforced,
				'Y', "ingotElectrum"
		);
		addShapedUpgradeRecipe(reservoirResonant,
				" R ",
				"IXI",
				" Y ",
				'I', "ingotEnderium",
				'R', "dustPyrotheum",
				'X', reservoirSignalum,
				'Y', "ingotSignalum"
		);
		// @formatter:on

		addColorRecipe(reservoirBasic, reservoirBasic, "dye");
		addColorRecipe(reservoirHardened, reservoirHardened, "dye");
		addColorRecipe(reservoirReinforced, reservoirReinforced, "dye");
		addColorRecipe(reservoirSignalum, reservoirSignalum, "dye");
		addColorRecipe(reservoirResonant, reservoirResonant, "dye");

		addColorRemoveRecipe(reservoirBasic, reservoirBasic);
		addColorRemoveRecipe(reservoirHardened, reservoirHardened);
		addColorRemoveRecipe(reservoirReinforced, reservoirReinforced);
		addColorRemoveRecipe(reservoirSignalum, reservoirSignalum);
		addColorRemoveRecipe(reservoirResonant, reservoirResonant);
		return true;
	}

	private static void config() {

		String category = "Item.Reservoir";
		String comment;
		enable = ThermalExpansion.CONFIG.get(category, "Enable", true);

		int capacity = CAPACITY_BASE;
		comment = "Adjust this value to change the amount of Fluid (in mB) stored by a Basic Reservoir. This base value will scale with item level.";
		capacity = ThermalExpansion.CONFIG.getConfiguration().getInt("BaseCapacity", category, capacity, Fluid.BUCKET_VOLUME * 2, capacity * 10, comment);

		for (int i = 0; i < CAPACITY.length; i++) {
			CAPACITY[i] *= capacity;
		}
	}

	/* ENTRY */
	public class TypeEntry {

		public final String name;
		public final int capacity;

		TypeEntry(String name, int capacity) {

			this.name = name;
			this.capacity = capacity;
		}
	}

	private void addEntry(int metadata, String name, int capacity) {

		typeMap.put(metadata, new TypeEntry(name, capacity));
	}

	private ItemStack addEntryItem(int metadata, String name, int capacity, EnumRarity rarity) {

		addEntry(metadata, name, capacity);
		return addItem(metadata, name, rarity);
	}

	private static Int2ObjectOpenHashMap<TypeEntry> typeMap = new Int2ObjectOpenHashMap<>();

	public static final int BUCKET_FILL = 0;
	public static final int BUCKET_EMPTY = 1;

	public static final int CAPACITY_BASE = 10000;
	public static final int CREATIVE = 32000;

	public static final int[] CAPACITY = { 1, 4, 9, 16, 25 };

	public static boolean enable = true;

	/* REFERENCES */
	public static ItemStack reservoirBasic;
	public static ItemStack reservoirHardened;
	public static ItemStack reservoirReinforced;
	public static ItemStack reservoirSignalum;
	public static ItemStack reservoirResonant;

	public static ItemStack reservoirCreative;

}
