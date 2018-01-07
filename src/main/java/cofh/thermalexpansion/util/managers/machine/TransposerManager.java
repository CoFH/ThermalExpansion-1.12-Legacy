package cofh.thermalexpansion.util.managers.machine;

import cofh.core.init.CoreProps;
import cofh.core.inventory.ComparableItemStackSafeNBT;
import cofh.core.util.ItemWrapper;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.block.storage.BlockCell;
import cofh.thermalexpansion.item.ItemFrame;
import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemFertilizer;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.Arrays.asList;

public class TransposerManager {

	private static Map<List<Integer>, TransposerRecipe> recipeMapFill = new THashMap<>();
	private static Map<ComparableItemStackTransposer, TransposerRecipe> recipeMapExtract = new THashMap<>();
	private static Map<ItemWrapper, ContainerOverride> containerOverrides = new THashMap<>();
	private static Set<ComparableItemStackTransposer> validationSet = new THashSet<>();

	public static final int DEFAULT_ENERGY = 400;

	public static TransposerRecipe getFillRecipe(ItemStack input, FluidStack fluid) {

		return input.isEmpty() || fluid == null ? null : recipeMapFill.get(asList(new ComparableItemStackTransposer(input).hashCode(), FluidHelper.getFluidHash(fluid)));
	}

	public static TransposerRecipe getExtractRecipe(ItemStack input) {

		return input.isEmpty() ? null : recipeMapExtract.get(new ComparableItemStackTransposer(input));
	}

	public static ContainerOverride getContainerOverride(ItemStack input) {

		return input.isEmpty() ? null : containerOverrides.get(new ItemWrapper(input));
	}

	public static boolean fillRecipeExists(ItemStack input, FluidStack fluid) {

		return getFillRecipe(input, fluid) != null;
	}

	public static boolean extractRecipeExists(ItemStack input) {

		return getExtractRecipe(input) != null;
	}

	public static boolean containerOverrideExists(ItemStack input) {

		return getContainerOverride(input) != null;
	}

	public static TransposerRecipe[] getFillRecipeList() {

		return recipeMapFill.values().toArray(new TransposerRecipe[recipeMapFill.size()]);
	}

	public static TransposerRecipe[] getExtractRecipeList() {

		return recipeMapExtract.values().toArray(new TransposerRecipe[recipeMapExtract.size()]);
	}

	public static boolean isItemValid(ItemStack input) {

		return !input.isEmpty() && validationSet.contains(new ComparableItemStackTransposer(input));
	}

	public static void initialize() {

		/* BLOCKS */
		{
			addFillRecipe(4000, new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.MOSSY_COBBLESTONE), new FluidStack(FluidRegistry.WATER, 250), false);
			addFillRecipe(4000, new ItemStack(Blocks.STONEBRICK), new ItemStack(Blocks.STONEBRICK, 1, 1), new FluidStack(FluidRegistry.WATER, 250), false);
			addFillRecipe(4000, new ItemStack(Blocks.SANDSTONE), new ItemStack(Blocks.END_STONE), new FluidStack(TFFluids.fluidEnder, 250), false);
			addFillRecipe(4000, new ItemStack(Items.BRICK), new ItemStack(Items.NETHERBRICK), new FluidStack(FluidRegistry.LAVA, 250), false);

			addExtractRecipe(2400, new ItemStack(Blocks.CACTUS), ItemStack.EMPTY, new FluidStack(FluidRegistry.WATER, 500), 0, false);
			addExtractRecipe(2400, new ItemStack(Blocks.REEDS), new ItemStack(Items.SUGAR, 2), new FluidStack(FluidRegistry.WATER, 250), 0, false);
		}

		/* CONCRETE */
		{
			FluidStack water = new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);

			for (int i = 0; i < 16; i++) {
				addFillRecipe(DEFAULT_ENERGY, new ItemStack(Blocks.CONCRETE_POWDER, 1, i), new ItemStack(Blocks.CONCRETE, 1, i), water, false);
			}
		}

		/* CRYOTHEUM */
		{
			int energy = 2000;

			FluidStack cryoFluid = new FluidStack(TFFluids.fluidCryotheum, 200);

			addFillRecipe(energy, ItemMaterial.crystalRedstone, new ItemStack(Items.REDSTONE, 2), cryoFluid, false);
			addFillRecipe(energy, ItemMaterial.crystalGlowstone, new ItemStack(Items.GLOWSTONE_DUST), cryoFluid, false);
			addFillRecipe(energy, ItemMaterial.crystalEnder, new ItemStack(Items.ENDER_PEARL), cryoFluid, false);

			energy = 400;

			addFillRecipe(energy, new ItemStack(Blocks.ICE), new ItemStack(Blocks.PACKED_ICE), cryoFluid, false);

			addFillRecipe(energy, ItemMaterial.dustCryotheum, new ItemStack(Blocks.ICE), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME), false);
			addFillRecipe(energy, ItemMaterial.dustCryotheum, new ItemStack(Items.REDSTONE, 10), new FluidStack(TFFluids.fluidRedstone, Fluid.BUCKET_VOLUME), false);
			addFillRecipe(energy, ItemMaterial.dustCryotheum, new ItemStack(Items.GLOWSTONE_DUST, 4), new FluidStack(TFFluids.fluidGlowstone, Fluid.BUCKET_VOLUME), false);
			addFillRecipe(energy, ItemMaterial.dustCryotheum, new ItemStack(Items.ENDER_PEARL, 4), new FluidStack(TFFluids.fluidEnder, Fluid.BUCKET_VOLUME), false);
		}

		/* ELEMENTAL */
		{
			FluidStack redstoneFluid = new FluidStack(TFFluids.fluidRedstone, 200);

			addFillRecipe(4000, new ItemStack(Items.GLOWSTONE_DUST), new ItemStack(Items.BLAZE_POWDER), redstoneFluid, false);
			addFillRecipe(4000, new ItemStack(Items.SNOWBALL), ItemHelper.cloneStack(ItemMaterial.dustBlizz, 1), redstoneFluid, false);
			addFillRecipe(4000, new ItemStack(Blocks.SAND), ItemHelper.cloneStack(ItemMaterial.dustBlitz), redstoneFluid, false);
			addFillRecipe(4000, ItemHelper.cloneStack(ItemMaterial.dustObsidian), ItemHelper.cloneStack(ItemMaterial.dustBasalz), redstoneFluid, false);
		}

		/* CELLS */
		{
			FluidStack redstoneFluid = new FluidStack(TFFluids.fluidRedstone, Fluid.BUCKET_VOLUME * 4);

			if (BlockCell.enableClassicRecipes) {
				addFillRecipe(16000, ItemFrame.frameCell2, ItemFrame.frameCell2Filled, redstoneFluid, false);
				addFillRecipe(16000, ItemFrame.frameCell3, ItemFrame.frameCell3Filled, redstoneFluid, false);
				addFillRecipe(16000, ItemFrame.frameCell4, ItemFrame.frameCell4Filled, redstoneFluid, false);
			}
		}

		addFillRecipe(4000, new ItemStack(Blocks.SPONGE, 1, 0), new ItemStack(Blocks.SPONGE, 1, 1), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME), true);
		addFillRecipe(2000, ItemHelper.cloneStack(ItemFertilizer.fertilizerBasic), ItemHelper.cloneStack(ItemFertilizer.fertilizerRich), new FluidStack(TFFluids.fluidSap, 200), false);
		addFillRecipe(400, new ItemStack(Items.BOWL), new ItemStack(Items.MUSHROOM_STEW), new FluidStack(TFFluids.fluidMushroomStew, 250), true);
		addFillRecipe(400, new ItemStack(Items.GLASS_BOTTLE), new ItemStack(Items.EXPERIENCE_BOTTLE), new FluidStack(TFFluids.fluidExperience, 250), false);

		addContainerOverride(new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.BUCKET), 100);
		addContainerOverride(new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.BUCKET), 100);
		addContainerOverride(new ItemStack(Items.MILK_BUCKET), new ItemStack(Items.BUCKET), 100);
		addContainerOverride(new ItemStack(ForgeModContainer.getInstance().universalBucket), new ItemStack(Items.BUCKET), 100);

		/* LOAD POTIONS */
		loadPotions();

		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadPotions() {

		//		/* VANILLA */
		//		{
		//			addDefaultPotionRecipes(PotionTypes.MUNDANE);
		//			addDefaultPotionRecipes(PotionTypes.THICK);
		//			addDefaultPotionRecipes(PotionTypes.AWKWARD);
		//
		//			addDefaultPotionRecipes(PotionTypes.NIGHT_VISION);
		//			addDefaultPotionRecipes(PotionTypes.LONG_NIGHT_VISION);
		//
		//			addDefaultPotionRecipes(PotionTypes.INVISIBILITY);
		//			addDefaultPotionRecipes(PotionTypes.LONG_INVISIBILITY);
		//
		//			addDefaultPotionRecipes(PotionTypes.LEAPING);
		//			addDefaultPotionRecipes(PotionTypes.LONG_LEAPING);
		//			addDefaultPotionRecipes(PotionTypes.STRONG_LEAPING);
		//
		//			addDefaultPotionRecipes(PotionTypes.FIRE_RESISTANCE);
		//			addDefaultPotionRecipes(PotionTypes.LONG_FIRE_RESISTANCE);
		//
		//			addDefaultPotionRecipes(PotionTypes.SWIFTNESS);
		//			addDefaultPotionRecipes(PotionTypes.LONG_SWIFTNESS);
		//			addDefaultPotionRecipes(PotionTypes.STRONG_SWIFTNESS);
		//
		//			addDefaultPotionRecipes(PotionTypes.SLOWNESS);
		//			addDefaultPotionRecipes(PotionTypes.LONG_SLOWNESS);
		//
		//			addDefaultPotionRecipes(PotionTypes.WATER_BREATHING);
		//			addDefaultPotionRecipes(PotionTypes.LONG_WATER_BREATHING);
		//
		//			addDefaultPotionRecipes(PotionTypes.HEALING);
		//			addDefaultPotionRecipes(PotionTypes.STRONG_HEALING);
		//
		//			addDefaultPotionRecipes(PotionTypes.HARMING);
		//			addDefaultPotionRecipes(PotionTypes.STRONG_HARMING);
		//
		//			addDefaultPotionRecipes(PotionTypes.POISON);
		//			addDefaultPotionRecipes(PotionTypes.LONG_POISON);
		//			addDefaultPotionRecipes(PotionTypes.STRONG_POISON);
		//
		//			addDefaultPotionRecipes(PotionTypes.REGENERATION);
		//			addDefaultPotionRecipes(PotionTypes.LONG_REGENERATION);
		//			addDefaultPotionRecipes(PotionTypes.STRONG_REGENERATION);
		//
		//			addDefaultPotionRecipes(PotionTypes.STRENGTH);
		//			addDefaultPotionRecipes(PotionTypes.LONG_STRENGTH);
		//			addDefaultPotionRecipes(PotionTypes.STRONG_STRENGTH);
		//
		//			addDefaultPotionRecipes(PotionTypes.WEAKNESS);
		//			addDefaultPotionRecipes(PotionTypes.LONG_WEAKNESS);
		//		}
		//
		//		/* COFH */
		//		{
		//			addDefaultPotionRecipes(CorePotions.haste);
		//			addDefaultPotionRecipes(CorePotions.hasteLong);
		//			addDefaultPotionRecipes(CorePotions.hasteStrong);
		//
		//			addDefaultPotionRecipes(CorePotions.resistance);
		//			addDefaultPotionRecipes(CorePotions.resistanceLong);
		//			addDefaultPotionRecipes(CorePotions.resistanceStrong);
		//
		//			addDefaultPotionRecipes(CorePotions.levitation);
		//			addDefaultPotionRecipes(CorePotions.levitationLong);
		//
		//			addDefaultPotionRecipes(CorePotions.absorption);
		//			addDefaultPotionRecipes(CorePotions.absorptionLong);
		//			addDefaultPotionRecipes(CorePotions.absorptionStrong);
		//
		//			addDefaultPotionRecipes(CorePotions.wither);
		//			addDefaultPotionRecipes(CorePotions.witherLong);
		//			addDefaultPotionRecipes(CorePotions.witherStrong);
		//		}

		for (PotionType type : PotionType.REGISTRY) {

			if (type != PotionTypes.EMPTY) {
				addDefaultPotionRecipes(type);
			}
		}
	}

	public static void loadRecipes() {

		FluidStack cryoStack = new FluidStack(TFFluids.fluidCryotheum, 200);

		addFillRecipe(2000, ItemHelper.getOre("oreCinnabar"), ItemHelper.cloneStack(ItemMaterial.crystalCinnabar, 2), cryoStack, false);

		if (FluidRegistry.isFluidRegistered("essence")) {
			addFillRecipe(400, new ItemStack(Items.GLASS_BOTTLE), new ItemStack(Items.EXPERIENCE_BOTTLE), new FluidStack(FluidRegistry.getFluid("essence"), 250), false);
		}
		if (FluidRegistry.isFluidRegistered("xpjuice")) {
			addFillRecipe(400, new ItemStack(Items.GLASS_BOTTLE), new ItemStack(Items.EXPERIENCE_BOTTLE), new FluidStack(FluidRegistry.getFluid("xpjuice"), 250), false);
		}
	}

	public static void refresh() {

		Map<List<Integer>, TransposerRecipe> tempFill = new THashMap<>(recipeMapFill.size());
		Map<ComparableItemStackTransposer, TransposerRecipe> tempExtract = new THashMap<>(recipeMapExtract.size());
		Map<ItemWrapper, ContainerOverride> tempOverrides = new THashMap<>(containerOverrides.size());
		Set<ComparableItemStackTransposer> tempSet = new THashSet<>();
		TransposerRecipe tempRecipe;
		ContainerOverride tempOverride;

		for (Entry<List<Integer>, TransposerRecipe> entry : recipeMapFill.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackTransposer input = new ComparableItemStackTransposer(tempRecipe.input);
			tempFill.put(asList(input.hashCode(), FluidHelper.getFluidHash(tempRecipe.fluid)), tempRecipe);
			tempSet.add(input);
		}
		for (Entry<ComparableItemStackTransposer, TransposerRecipe> entry : recipeMapExtract.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackTransposer input = new ComparableItemStackTransposer(tempRecipe.input);
			tempExtract.put(input, tempRecipe);
			tempSet.add(input);
		}
		for (Entry<ItemWrapper, ContainerOverride> entry : containerOverrides.entrySet()) {
			tempOverride = entry.getValue();
			ItemWrapper input = new ItemWrapper(tempOverride.input);
			tempOverrides.put(input, tempOverride);
		}
		recipeMapFill.clear();
		recipeMapExtract.clear();

		recipeMapFill = tempFill;
		recipeMapExtract = tempExtract;

		validationSet.clear();
		validationSet = tempSet;

		containerOverrides.clear();
		containerOverrides = tempOverrides;
	}

	/* ADD RECIPES */
	public static TransposerRecipe addFillRecipe(int energy, ItemStack input, ItemStack output, FluidStack fluid, boolean reversible) {

		if (input.isEmpty() || output.isEmpty() || fluid == null || fluid.amount <= 0 || energy <= 0) {
			return null;
		}
		if (fillRecipeExists(input, fluid)) {
			return null;
		}
		TransposerRecipe recipeFill = new TransposerRecipe(input, output, fluid, energy, 100);
		recipeMapFill.put(asList(new ComparableItemStackTransposer(input).hashCode(), FluidHelper.getFluidHash(fluid)), recipeFill);
		validationSet.add(new ComparableItemStackTransposer(input));

		if (reversible) {
			addExtractRecipe(energy, output, input, fluid, 100, false);
		}
		return recipeFill;
	}

	public static TransposerRecipe addExtractRecipe(int energy, ItemStack input, ItemStack output, FluidStack fluid, int chance, boolean reversible) {

		if (input.isEmpty() || fluid == null || fluid.amount <= 0 || energy <= 0) {
			return null;
		}
		if (extractRecipeExists(input)) {
			return null;
		}
		if (output.isEmpty() && reversible || output.isEmpty() && chance != 0) {
			return null;
		}
		TransposerRecipe recipeExtraction = new TransposerRecipe(input, output, fluid, energy, chance);
		recipeMapExtract.put(new ComparableItemStackTransposer(input), recipeExtraction);
		validationSet.add(new ComparableItemStackTransposer(input));

		if (reversible) {
			addFillRecipe(energy, output, input, fluid, false);
		}
		return recipeExtraction;
	}

	/* REMOVE RECIPES */
	public static TransposerRecipe removeFillRecipe(ItemStack input, FluidStack fluid) {

		return recipeMapFill.remove(asList(new ComparableItemStackTransposer(input).hashCode(), FluidHelper.getFluidHash(fluid)));
	}

	public static TransposerRecipe removeExtractRecipe(ItemStack input) {

		return recipeMapExtract.remove(new ComparableItemStackTransposer(input));
	}

	/* HELPERS */
	public static ContainerOverride addContainerOverride(ItemStack input, ItemStack output, int chance) {

		if (input.isEmpty() || output.isEmpty() || chance <= 0) {
			return null;
		}
		if (containerOverrideExists(input)) {
			return null;
		}
		ContainerOverride override = new ContainerOverride(input, output, chance);
		containerOverrides.put(new ItemWrapper(input), override);

		return override;
	}

	public static void addDefaultPotionRecipes(PotionType type) {

		addFillRecipe(DEFAULT_ENERGY * 2, new ItemStack(Items.GLASS_BOTTLE), PotionUtils.addPotionToItemStack(ItemHelper.cloneStack(Items.POTIONITEM, 1), type), getPotion(CoreProps.BOTTLE_VOLUME, type), true);
		addFillRecipe(DEFAULT_ENERGY * 2, new ItemStack(Items.GLASS_BOTTLE), PotionUtils.addPotionToItemStack(ItemHelper.cloneStack(Items.SPLASH_POTION, 1), type), getSplashPotion(CoreProps.BOTTLE_VOLUME, type), true);
		addFillRecipe(DEFAULT_ENERGY * 2, new ItemStack(Items.GLASS_BOTTLE), PotionUtils.addPotionToItemStack(ItemHelper.cloneStack(Items.LINGERING_POTION, 1), type), getLingeringPotion(CoreProps.BOTTLE_VOLUME, type), true);
		addFillRecipe(DEFAULT_ENERGY, new ItemStack(Items.ARROW), PotionUtils.addPotionToItemStack(new ItemStack(Items.TIPPED_ARROW), type), getLingeringPotion(CoreProps.BOTTLE_VOLUME / 10, type), false);
	}

	public static FluidStack getPotion(int amount, PotionType type) {

		if (type == PotionTypes.WATER) {
			return new FluidStack(FluidRegistry.WATER, amount);
		}
		return addPotionToFluidStack(new FluidStack(TFFluids.fluidPotion, amount), type);
	}

	public static FluidStack getSplashPotion(int amount, PotionType type) {

		return addPotionToFluidStack(new FluidStack(TFFluids.fluidPotionSplash, amount), type);
	}

	public static FluidStack getLingeringPotion(int amount, PotionType type) {

		return addPotionToFluidStack(new FluidStack(TFFluids.fluidPotionLingering, amount), type);
	}

	public static FluidStack addPotionToFluidStack(FluidStack stack, PotionType type) {

		ResourceLocation resourcelocation = PotionType.REGISTRY.getNameForObject(type);

		if (type == PotionTypes.EMPTY) {
			if (stack.tag != null) {
				stack.tag.removeTag("Potion");
				if (stack.tag.hasNoTags()) {
					stack.tag = null;
				}
			}
		} else {
			if (stack.tag == null) {
				stack.tag = new NBTTagCompound();
			}
			stack.tag.setString("Potion", resourcelocation.toString());
		}
		return stack;
	}

	/* RECIPE CLASS */
	public static class TransposerRecipe {

		final ItemStack input;
		final ItemStack output;
		final FluidStack fluid;
		final int energy;
		final int chance;

		public TransposerRecipe(ItemStack input, ItemStack output, FluidStack fluid, int energy, int chance) {

			this.input = input;
			this.output = output;
			this.fluid = fluid;
			this.energy = energy;
			this.chance = chance;
		}

		public ItemStack getInput() {

			return input;
		}

		public ItemStack getOutput() {

			return output;
		}

		public FluidStack getFluid() {

			return fluid;
		}

		public int getEnergy() {

			return energy;
		}

		public int getChance() {

			return chance;
		}

	}

	/* CONTAINER OVERRIDE CLASS */
	public static class ContainerOverride {

		final ItemStack input;
		final ItemStack output;
		final int chance;

		public ContainerOverride(ItemStack input, ItemStack output, int chance) {

			this.input = input;
			this.output = output;
			this.chance = chance;
		}

		public ItemStack getInput() {

			return input;
		}

		public ItemStack getOutput() {

			return output;
		}

		public int getChance() {

			return chance;
		}

	}

	/* ITEMSTACK CLASS */
	public static class ComparableItemStackTransposer extends ComparableItemStackSafeNBT {

		public static final String CROP = "crop";
		public static final String SEED = "seed";
		public static final String GEM = "gem";

		@Override
		public boolean safeOreType(String oreName) {

			return oreName.startsWith(CROP) || oreName.startsWith(SEED) || oreName.startsWith(GEM) || oreName.startsWith(ORE) || oreName.startsWith(DUST) || oreName.startsWith(INGOT) || oreName.startsWith(NUGGET);
		}

		public ComparableItemStackTransposer(ItemStack stack) {

			super(stack);
		}
	}

}
