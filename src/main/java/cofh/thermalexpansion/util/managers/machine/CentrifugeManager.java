package cofh.thermalexpansion.util.managers.machine;

import cofh.core.init.CoreProps;
import cofh.core.inventory.ComparableItemStackSafeNBT;
import cofh.core.util.helpers.ColorHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.init.TEItems;
import cofh.thermalexpansion.item.ItemMorb;
import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class CentrifugeManager {

	private static Map<ComparableItemStackCentrifuge, CentrifugeRecipe> recipeMap = new THashMap<>();
	private static Map<ComparableItemStackCentrifuge, CentrifugeRecipe> recipeMapMobs = new THashMap<>();

	public static final int DEFAULT_ENERGY = 2000;

	public static CentrifugeRecipe getRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return null;
		}
		ComparableItemStackCentrifuge query = new ComparableItemStackCentrifuge(input);

		CentrifugeRecipe recipe = recipeMap.get(query);

		if (recipe == null) {
			query.metadata = OreDictionary.WILDCARD_VALUE;
			recipe = recipeMap.get(query);
		}
		return recipe;
	}

	public static CentrifugeRecipe getRecipeMob(ItemStack input) {

		if (input.isEmpty() || !input.getItem().equals(TEItems.itemMorb)) {
			return null;
		}
		ComparableItemStackCentrifuge query = new ComparableItemStackCentrifuge(ItemMorb.getGenericMorb(input));
		return recipeMapMobs.get(query);
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static boolean recipeExistsMob(ItemStack input) {

		return getRecipeMob(input) != null;
	}

	public static CentrifugeRecipe[] getRecipeList() {

		return recipeMap.values().toArray(new CentrifugeRecipe[recipeMap.size()]);
	}

	public static CentrifugeRecipe[] getRecipeListMobs() {

		return recipeMapMobs.values().toArray(new CentrifugeRecipe[recipeMapMobs.size()]);
	}

	public static void initialize() {

		int energy = DEFAULT_ENERGY;

		addRecipe(energy, new ItemStack(Items.MAGMA_CREAM), asList(new ItemStack(Items.SLIME_BALL), new ItemStack(Items.BLAZE_POWDER)), null);
		addRecipe(energy, new ItemStack(Items.REEDS), singletonList(new ItemStack(Items.SUGAR, 2)), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME / 4));

		addRecipe(energy * 2, ItemHelper.cloneStack(ItemMaterial.dustElectrum, 2), asList(ItemHelper.cloneStack(ItemMaterial.dustGold), ItemHelper.cloneStack(ItemMaterial.dustSilver)), null);
		addRecipe(energy * 3, ItemHelper.cloneStack(ItemMaterial.dustInvar, 3), asList(ItemHelper.cloneStack(ItemMaterial.dustIron, 2), ItemHelper.cloneStack(ItemMaterial.dustNickel)), null);
		addRecipe(energy * 4, ItemHelper.cloneStack(ItemMaterial.dustBronze, 4), asList(ItemHelper.cloneStack(ItemMaterial.dustCopper, 3), ItemHelper.cloneStack(ItemMaterial.dustTin)), null);
		addRecipe(energy * 2, ItemHelper.cloneStack(ItemMaterial.dustConstantan, 2), asList(ItemHelper.cloneStack(ItemMaterial.dustCopper), ItemHelper.cloneStack(ItemMaterial.dustNickel)), null);
		addRecipe(energy * 4, ItemHelper.cloneStack(ItemMaterial.dustSignalum, 4), asList(ItemHelper.cloneStack(ItemMaterial.dustCopper, 3), ItemHelper.cloneStack(ItemMaterial.dustSilver)), new FluidStack(TFFluids.fluidRedstone, Fluid.BUCKET_VOLUME));
		addRecipe(energy * 4, ItemHelper.cloneStack(ItemMaterial.dustLumium, 4), asList(ItemHelper.cloneStack(ItemMaterial.dustTin, 3), ItemHelper.cloneStack(ItemMaterial.dustSilver)), new FluidStack(TFFluids.fluidGlowstone, Fluid.BUCKET_VOLUME));
		addRecipe(energy * 4, ItemHelper.cloneStack(ItemMaterial.dustEnderium, 4), asList(ItemHelper.cloneStack(ItemMaterial.dustLead, 3), ItemHelper.cloneStack(ItemMaterial.dustPlatinum)), new FluidStack(TFFluids.fluidEnder, Fluid.BUCKET_VOLUME));

		addRecipe(energy * 2, ItemHelper.cloneStack(ItemMaterial.dustPyrotheum, 2), asList(new ItemStack(Items.BLAZE_POWDER), ItemHelper.cloneStack(ItemMaterial.dustSulfur), new ItemStack(Items.REDSTONE)), null);
		addRecipe(energy * 2, ItemHelper.cloneStack(ItemMaterial.dustCryotheum, 2), asList(ItemHelper.cloneStack(ItemMaterial.dustBlizz), new ItemStack(Items.SNOWBALL), new ItemStack(Items.REDSTONE)), null);
		addRecipe(energy * 2, ItemHelper.cloneStack(ItemMaterial.dustAerotheum, 2), asList(ItemHelper.cloneStack(ItemMaterial.dustBlitz), ItemHelper.cloneStack(ItemMaterial.dustNiter), new ItemStack(Items.REDSTONE)), null);
		addRecipe(energy * 2, ItemHelper.cloneStack(ItemMaterial.dustPetrotheum, 2), asList(ItemHelper.cloneStack(ItemMaterial.dustBasalz), ItemHelper.cloneStack(ItemMaterial.dustObsidian), new ItemStack(Items.REDSTONE)), null);

		/* CONCRETE POWDER */
		{
			int[] dyeChance = new int[ColorHelper.WOOL_COLOR_CONFIG.length];
			for (int i = 0; i < ColorHelper.WOOL_COLOR_CONFIG.length; i++) {
				dyeChance[i] = 10;
			}
			dyeChance[EnumDyeColor.WHITE.getMetadata()] = 0;
			dyeChance[EnumDyeColor.BROWN.getMetadata()] = 0;
			dyeChance[EnumDyeColor.BLUE.getMetadata()] = 0;
			dyeChance[EnumDyeColor.BLACK.getMetadata()] = 0;

			ItemStack gravel = new ItemStack(Blocks.GRAVEL);
			ItemStack sand = new ItemStack(Blocks.SAND);

			for (int i = 0; i < ColorHelper.WOOL_COLOR_CONFIG.length; i++) {
				if (dyeChance[i] > 0) {
					addRecipe(energy, new ItemStack(Blocks.CONCRETE_POWDER, 2, i), asList(gravel, sand, new ItemStack(Items.DYE, 1, 15 - i)), asList(100, 100, dyeChance[i]), null);
				} else {
					addRecipe(energy, new ItemStack(Blocks.CONCRETE_POWDER, 2, i), asList(gravel, sand), null);
				}
			}
		}

		/* MOBS */
		loadMobs();

		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadRecipes() {

	}

	public static void loadMobs() {

		/* ANIMALS */
		addDefaultMobRecipe("minecraft:bat", singletonList(new ItemStack(Items.LEATHER)), singletonList(5), 0);
		addDefaultMobRecipe("minecraft:chicken", asList(new ItemStack(Items.FEATHER, 2), new ItemStack(Items.CHICKEN)), asList(50, 100), 2);
		addDefaultMobRecipe("minecraft:cow", asList(new ItemStack(Items.LEATHER, 2), new ItemStack(Items.BEEF, 3)), asList(50, 70), 2);
		addDefaultMobRecipe("minecraft:donkey", singletonList(new ItemStack(Items.LEATHER, 2)), singletonList(50), 2);
		addDefaultMobRecipe("minecraft:horse", singletonList(new ItemStack(Items.LEATHER, 2)), singletonList(50), 2);
		addDefaultMobRecipe("minecraft:llama", singletonList(new ItemStack(Items.LEATHER, 2)), singletonList(50), 2);
		addDefaultMobRecipe("minecraft:mooshroom", asList(new ItemStack(Items.LEATHER, 2), new ItemStack(Items.BEEF, 3)), asList(50, 70), 2);
		addDefaultMobRecipe("minecraft:ocelot", emptyList(), emptyList(), 2);
		addDefaultMobRecipe("minecraft:parrot", singletonList(new ItemStack(Items.FEATHER, 2)), singletonList(80), 2);
		addDefaultMobRecipe("minecraft:pig", singletonList(new ItemStack(Items.PORKCHOP, 3)), singletonList(70), 2);
		addDefaultMobRecipe("minecraft:polar_bear", asList(new ItemStack(Items.FISH, 2), new ItemStack(Items.FISH, 2, 1)), asList(80, 50), 2);
		addDefaultMobRecipe("minecraft:rabbit", asList(new ItemStack(Items.RABBIT_HIDE), new ItemStack(Items.RABBIT), new ItemStack(Items.RABBIT_FOOT)), asList(50, 50, 10), 2);
		addDefaultMobRecipe("minecraft:sheep", singletonList(new ItemStack(Items.MUTTON, 2)), singletonList(80), 2);
		addDefaultMobRecipe("minecraft:skeleton_horse", singletonList(new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage())), singletonList(50), 2);
		addDefaultMobRecipe("minecraft:squid", singletonList(new ItemStack(Items.DYE, 3, EnumDyeColor.BLACK.getDyeDamage())), singletonList(70), 2);
		addDefaultMobRecipe("minecraft:wolf", emptyList(), emptyList(), 2);
		addDefaultMobRecipe("minecraft:zombie_horse", singletonList(new ItemStack(Items.ROTTEN_FLESH, 2)), singletonList(50), 2);

		/* MOBS */
		addDefaultMobRecipe("minecraft:blaze", asList(new ItemStack(Items.BLAZE_ROD), ItemHelper.cloneStack(ItemMaterial.dustSulfur)), asList(50, 25), 10);
		addDefaultMobRecipe("minecraft:cave_spider", asList(new ItemStack(Items.STRING, 2), new ItemStack(Items.SPIDER_EYE)), asList(50, 25), 5);
		addDefaultMobRecipe("minecraft:creeper", singletonList(new ItemStack(Items.GUNPOWDER, 2)), singletonList(50), 5);
		addDefaultMobRecipe("minecraft:elder_guardian", asList(new ItemStack(Items.PRISMARINE_SHARD, 2), new ItemStack(Items.PRISMARINE_CRYSTALS)), asList(50, 50), 10);
		addDefaultMobRecipe("minecraft:enderman", singletonList(new ItemStack(Items.ENDER_PEARL)), singletonList(50), 5);
		addDefaultMobRecipe("minecraft:endermite", emptyList(), emptyList(), 5);
		addDefaultMobRecipe("minecraft:evocation_illager", asList(new ItemStack(Items.TOTEM_OF_UNDYING), new ItemStack(Items.EMERALD)), asList(100, 50), 10);
		addDefaultMobRecipe("minecraft:ghast", asList(new ItemStack(Items.GHAST_TEAR), new ItemStack(Items.GUNPOWDER)), asList(50, 50), 5);
		addDefaultMobRecipe("minecraft:guardian", asList(new ItemStack(Items.PRISMARINE_SHARD, 2), new ItemStack(Items.PRISMARINE_CRYSTALS)), asList(50, 50), 10);
		addDefaultMobRecipe("minecraft:husk", asList(new ItemStack(Items.ROTTEN_FLESH, 2), new ItemStack(Items.IRON_INGOT), new ItemStack(Items.POTATO)), asList(50, 2, 2), 5);
		addDefaultMobRecipe("minecraft:magma_cube", asList(new ItemStack(Items.MAGMA_CREAM), ItemHelper.cloneStack(ItemMaterial.dustSulfur)), asList(50, 25), 2);
		addDefaultMobRecipe("minecraft:shulker", singletonList(new ItemStack(Items.SHULKER_SHELL)), singletonList(50), 5);
		addDefaultMobRecipe("minecraft:silverfish", emptyList(), emptyList(), 5);
		addDefaultMobRecipe("minecraft:skeleton", asList(new ItemStack(Items.ARROW, 2), new ItemStack(Items.BONE, 2)), asList(50, 50), 5);
		addDefaultMobRecipe("minecraft:slime", singletonList(new ItemStack(Items.SLIME_BALL, 2)), singletonList(50), 2);
		addDefaultMobRecipe("minecraft:spider", asList(new ItemStack(Items.STRING, 2), new ItemStack(Items.SPIDER_EYE)), asList(50, 25), 5);
		addDefaultMobRecipe("minecraft:stray", asList(new ItemStack(Items.ARROW, 2), new ItemStack(Items.BONE, 2)), asList(50, 50), 5);
		addDefaultMobRecipe("minecraft:vex", emptyList(), emptyList(), 3);
		addDefaultMobRecipe("minecraft:villager", singletonList(new ItemStack(Items.EMERALD)), singletonList(2), 0);
		addDefaultMobRecipe("minecraft:vindication_illager", singletonList(new ItemStack(Items.EMERALD)), singletonList(50), 5);
		addDefaultMobRecipe("minecraft:witch", asList(new ItemStack(Items.GLOWSTONE_DUST, 2), new ItemStack(Items.GUNPOWDER, 2), new ItemStack(Items.REDSTONE, 2)), asList(40, 40, 40), 5);
		addDefaultMobRecipe("minecraft:wither_skeleton", asList(new ItemStack(Items.COAL, 1), new ItemStack(Items.BONE, 2)), asList(25, 50), 5);
		addDefaultMobRecipe("minecraft:zombie", asList(new ItemStack(Items.ROTTEN_FLESH, 2), new ItemStack(Items.IRON_INGOT), new ItemStack(Items.POTATO)), asList(50, 2, 2), 5);
		addDefaultMobRecipe("minecraft:zombie_pigman", asList(new ItemStack(Items.ROTTEN_FLESH), new ItemStack(Items.GOLD_NUGGET), new ItemStack(Items.GOLD_INGOT)), asList(50, 50, 2), 5);
		addDefaultMobRecipe("minecraft:zombie_villager", asList(new ItemStack(Items.ROTTEN_FLESH, 2), new ItemStack(Items.IRON_INGOT), new ItemStack(Items.POTATO)), asList(50, 2, 2), 5);

		/* THERMAL FOUNDATION */
		addDefaultMobRecipe("thermalfoundation:blizz", asList(ItemHelper.cloneStack(ItemMaterial.rodBlizz), new ItemStack(Items.SNOWBALL, 4)), asList(50, 25), 10);
		addDefaultMobRecipe("thermalfoundation:blitz", asList(ItemHelper.cloneStack(ItemMaterial.rodBlitz), ItemHelper.cloneStack(ItemMaterial.dustNiter, 2)), asList(50, 25), 10);
		addDefaultMobRecipe("thermalfoundation:basalz", asList(ItemHelper.cloneStack(ItemMaterial.rodBasalz), ItemHelper.cloneStack(ItemMaterial.dustObsidian, 2)), asList(50, 25), 10);
	}

	public static void refresh() {

		Map<ComparableItemStackCentrifuge, CentrifugeRecipe> tempMap = new THashMap<>(recipeMap.size());
		Map<ComparableItemStackCentrifuge, CentrifugeRecipe> tempMapMobs = new THashMap<>(recipeMapMobs.size());
		CentrifugeRecipe tempRecipe;

		for (Entry<ComparableItemStackCentrifuge, CentrifugeRecipe> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(new ComparableItemStackCentrifuge(tempRecipe.input), tempRecipe);
		}
		for (Entry<ComparableItemStackCentrifuge, CentrifugeRecipe> entry : recipeMapMobs.entrySet()) {
			tempRecipe = entry.getValue();
			tempMapMobs.put(new ComparableItemStackCentrifuge(tempRecipe.input), tempRecipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;

		recipeMapMobs.clear();
		recipeMapMobs = tempMapMobs;
	}

	/* ADD RECIPES */
	public static CentrifugeRecipe addRecipe(int energy, ItemStack input, List<ItemStack> output, List<Integer> chance, FluidStack fluid) {

		if (input.isEmpty() || (output.isEmpty() && fluid == null) || output.size() > 4 || energy <= 0 || recipeExists(input)) {
			return null;
		}
		CentrifugeRecipe recipe = new CentrifugeRecipe(input, output, chance, fluid, energy);
		recipeMap.put(new ComparableItemStackCentrifuge(input), recipe);
		return recipe;
	}

	public static CentrifugeRecipe addRecipe(int energy, ItemStack input, List<ItemStack> output, FluidStack fluid) {

		if (input.isEmpty() || (output.isEmpty() && fluid == null) || output.size() > 4 || energy <= 0 || recipeExists(input)) {
			return null;
		}
		CentrifugeRecipe recipe = new CentrifugeRecipe(input, output, null, fluid, energy);
		recipeMap.put(new ComparableItemStackCentrifuge(input), recipe);
		return recipe;
	}

	public static CentrifugeRecipe addRecipeMob(int energy, ItemStack input, List<ItemStack> output, List<Integer> chance, FluidStack fluid) {

		if (input.isEmpty() || (output.isEmpty() && fluid == null) || output.size() > 4 || energy <= 0 || recipeExistsMob(input)) {
			return null;
		}
		CentrifugeRecipe recipe = new CentrifugeRecipe(input, output, chance, fluid, energy);
		recipeMapMobs.put(new ComparableItemStackCentrifuge(input), recipe);
		return recipe;
	}

	/* REMOVE RECIPES */
	public static CentrifugeRecipe removeRecipe(ItemStack input) {

		return recipeMap.remove(new ComparableItemStackCentrifuge(input));
	}

	public static CentrifugeRecipe removeRecipeMob(ItemStack input) {

		return recipeMapMobs.remove(new ComparableItemStackCentrifuge(input));
	}

	/* HELPERS */
	public static void addDefaultMobRecipe(String entityId, List<ItemStack> output, List<Integer> chance, int xp) {

		if (!ItemMorb.validMobs.contains(entityId)) {
			return;
		}
		ArrayList<ItemStack> outputStandard = new ArrayList<>(output);
		ArrayList<ItemStack> outputReusable = new ArrayList<>(output);

		ArrayList<Integer> chanceStandard = new ArrayList<>(chance);
		ArrayList<Integer> chanceReusable = new ArrayList<>(chance);

		outputStandard.add(ItemHelper.cloneStack(ItemMorb.morbStandard));
		outputReusable.add(ItemHelper.cloneStack(ItemMorb.morbReusable));

		chanceStandard.add(ItemMorb.REUSE_CHANCE - 5);
		chanceReusable.add(80);

		addRecipeMob(DEFAULT_ENERGY * 2, ItemMorb.setTag(ItemHelper.cloneStack(ItemMorb.morbStandard), entityId, false), outputStandard, chanceStandard, new FluidStack(TFFluids.fluidExperience, xp * CoreProps.MB_PER_XP));
		addRecipeMob(DEFAULT_ENERGY * 2, ItemMorb.setTag(ItemHelper.cloneStack(ItemMorb.morbReusable), entityId, false), outputReusable, chanceReusable, new FluidStack(TFFluids.fluidExperience, xp * CoreProps.MB_PER_XP));
	}

	/* RECIPE CLASS */
	public static class CentrifugeRecipe {

		protected ItemStack input;
		protected List<ItemStack> output;
		protected List<Integer> chance;
		protected FluidStack fluid;
		protected int energy;

		CentrifugeRecipe(ItemStack input, @Nullable List<ItemStack> output, @Nullable List<Integer> chance, @Nullable FluidStack fluid, int energy) {

			this.input = input;
			this.output = new ArrayList<>();
			if (output != null) {
				this.output.addAll(output);
			}
			this.chance = new ArrayList<>();
			if (chance != null) {
				this.chance.addAll(chance);
			} else {
				for (int i = 0; i < this.output.size(); i++) {
					this.chance.add(100);
				}
			}
			this.fluid = fluid;
			this.energy = energy;
		}

		public ItemStack getInput() {

			return input.copy();
		}

		public List<ItemStack> getOutput() {

			return output;
		}

		public List<Integer> getChance() {

			return chance;
		}

		public FluidStack getFluid() {

			return fluid;
		}

		public int getEnergy() {

			return energy;
		}
	}

	/* ITEMSTACK CLASS */
	public static class ComparableItemStackCentrifuge extends ComparableItemStackSafeNBT {

		@Override
		public boolean safeOreType(String oreName) {

			return oreName.startsWith(DUST);
		}

		public ComparableItemStackCentrifuge(ItemStack stack) {

			super(stack);
		}
	}

}
