package cofh.thermalexpansion.plugins.jei.dynamos;

import cofh.api.energy.IEnergyContainerItem;
import cofh.lib.inventory.ComparableItemStack;
import cofh.thermalexpansion.block.dynamo.*;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.fuels.CoolantManager;
import cofh.thermalfoundation.init.TFFluids;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;

import java.util.ArrayList;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public class DynamoFuelCategories {
	public static void initialize(IModRegistry registry) {
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeHandlers(new DynamoFuelHandler());

		initSteamCategory(registry, guiHelper);
		initMagmaticCategory(registry, guiHelper);
		initCompressionCategory(registry, guiHelper);
		initReactantItemCategory(registry, guiHelper);
		initReactantFluidCategory(registry, guiHelper);
		initEnervation(registry, guiHelper);
		initNumismatic(registry, guiHelper);
		initCoolantCategory(registry, guiHelper);
	}

	protected static void initReactantFluidCategory(IModRegistry registry, IGuiHelper guiHelper) {
		DynamoFuelCategory.FuelFluid reactantCategory = new DynamoFuelCategory.FuelFluid(guiHelper, RecipeUidsTE.DYNAMO_REACTANT_FLUID, "reactant.fluid");
		registry.addRecipeCategories(reactantCategory);
		addFluidFuels(registry, reactantCategory, TileDynamoReactant.getReactantFuelFluids(), TileDynamoReactant::getFuelEnergy);
		registry.addRecipeCategoryCraftingItem(BlockDynamo.dynamoReactant, RecipeUidsTE.DYNAMO_REACTANT_FLUID);
	}

	private static void initReactantItemCategory(IModRegistry registry, IGuiHelper guiHelper) {
		DynamoFuelCategory.FuelItem reactantCategory = new DynamoFuelCategory.FuelItem(guiHelper, RecipeUidsTE.DYNAMO_REACTANT_SOLID, "reactant.solid");
		registry.addRecipeCategories(reactantCategory);
		addItemFuels(registry, reactantCategory, TileDynamoReactant.getReactantsStacks(), TileDynamoReactant::getReactantEnergy);
		registry.addRecipeCategoryCraftingItem(BlockDynamo.dynamoReactant, RecipeUidsTE.DYNAMO_REACTANT_SOLID);
	}

	private static void initEnervation(IModRegistry registry, IGuiHelper guiHelper) {
		DynamoFuelCategory.FuelItem enervationCategory = new DynamoFuelCategory.FuelItem(guiHelper, RecipeUidsTE.DYNAMO_ENERVATION, "enervation");
		registry.addRecipeCategories(enervationCategory);
		addItemFuels(registry, enervationCategory, TileDynamoEnervation.getSpecialStacks(), TileDynamoEnervation::getEnergyValue);
		ArrayList<Object> recipes = new ArrayList<>();
		for (Item item : Item.REGISTRY) {
			if (item instanceof IEnergyContainerItem) {
				DynamoFuelWrapper<ItemStack> dynamoFuelWrapper = null;
				try {
					ItemStack stack = new ItemStack(item);
					IEnergyContainerItem energyContainerItem = (IEnergyContainerItem) item;
					int maxEnergyStored = energyContainerItem.getMaxEnergyStored(stack);
					if (maxEnergyStored != 0) {
						energyContainerItem.receiveEnergy(stack, Integer.MAX_VALUE, false);
						int energyValue = TileDynamoEnervation.getEnergyValue(stack);
						if (energyValue > 0) {
							dynamoFuelWrapper = new DynamoFuelWrapper<>(stack, enervationCategory, energyValue);
						}
					}
				} catch (Exception err) {
					err.printStackTrace();
					continue;
				}
				if (dynamoFuelWrapper != null) {
					recipes.add(dynamoFuelWrapper);
				}
			}
		}
		registry.addRecipes(recipes);
		registry.addRecipeCategoryCraftingItem(BlockDynamo.dynamoEnervation, RecipeUidsTE.DYNAMO_ENERVATION);
	}

	protected static void initNumismatic(IModRegistry registry, IGuiHelper guiHelper) {
		DynamoFuelCategory.FuelItem numismaticCategory = new DynamoFuelCategory.FuelItem(guiHelper, RecipeUidsTE.DYNAMO_NUMISMATIC, "numismatic");
		registry.addRecipeCategories(numismaticCategory);
		addItemFuels(registry, numismaticCategory, TileDynamoNumismatic.getFuelStacks(), TileDynamoNumismatic::getEnergyValue);
		registry.addRecipeCategoryCraftingItem(BlockDynamo.dynamoNumismatic, RecipeUidsTE.DYNAMO_NUMISMATIC);
	}

	protected static void initCoolantCategory(IModRegistry registry, IGuiHelper guiHelper) {
		DynamoFuelCategory.FuelFluid coolantCategory = new DynamoFuelCategory.FuelFluid(guiHelper, RecipeUidsTE.COOLANT, "coolant"){
			@Override
			protected IDrawable createAnimatedFlame(IGuiHelper guiHelper) {
				return null;
			}
		};
		registry.addRecipeCategories(coolantCategory);
		registry.addRecipes(CoolantManager.getCoolantFluids().stream().map(fluid -> {
			FluidStack fluidStack = new FluidStack(fluid, 1000);
			return new DynamoFuelWrapper<>(fluidStack, coolantCategory, CoolantManager.getCoolantRF(fluidStack), "TC");
		}).collect(Collectors.toList()));

		registry.addRecipeCategoryCraftingItem(
				UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, TFFluids.fluidCryotheum),
				RecipeUidsTE.COOLANT);
	}

	protected static void initCompressionCategory(IModRegistry registry, IGuiHelper guiHelper) {
		DynamoFuelCategory.FuelFluid compressionCategory = new DynamoFuelCategory.FuelFluid(guiHelper, RecipeUidsTE.DYNAMO_COMPRESSION, "compression");
		registry.addRecipeCategories(compressionCategory);
		addFluidFuels(registry, compressionCategory, TileDynamoCompression.getCompressionFuelFluids(), TileDynamoCompression::getFuelEnergy);
		registry.addRecipeCategoryCraftingItem(BlockDynamo.dynamoCompression, RecipeUidsTE.DYNAMO_COMPRESSION);
	}

	protected static void initSteamCategory(IModRegistry registry, IGuiHelper guiHelper) {
		DynamoFuelCategory.FuelItem steamCategory = new DynamoFuelCategory.FuelItem(guiHelper, RecipeUidsTE.DYNAMO_STEAM, "steam");
		registry.addRecipeCategories(steamCategory);
		Set<ComparableItemStack> overriddenFuelStacks = TileDynamoSteam.getOverriddenFuelStacks();
		ArrayList<DynamoFuelWrapper<ItemStack>> steamFuels = new ArrayList<>();
		for (ComparableItemStack comparableItemStack : overriddenFuelStacks) {
			ItemStack itemStack = comparableItemStack.toItemStack();
			int energyValue = TileDynamoSteam.getEnergyValue(itemStack);
			if (energyValue > 0) {
				steamFuels.add(new DynamoFuelWrapper<>(itemStack, steamCategory, energyValue));
			}
		}

		for (ItemStack itemStack : registry.getIngredientRegistry().getFuels()) {
			if (overriddenFuelStacks.contains(new ComparableItemStack(itemStack))) continue;
			int energyValue = TileDynamoSteam.getEnergyValue(itemStack);
			if (energyValue > 0) {
				steamFuels.add(new DynamoFuelWrapper<>(itemStack, steamCategory, energyValue));
			}
		}
		registry.addRecipes(steamFuels);
		registry.addRecipeCategoryCraftingItem(BlockDynamo.dynamoSteam, RecipeUidsTE.DYNAMO_STEAM);
	}

	protected static void initMagmaticCategory(IModRegistry registry, IGuiHelper guiHelper) {
		DynamoFuelCategory.FuelFluid magmaticCategory = new DynamoFuelCategory.FuelFluid(guiHelper, RecipeUidsTE.DYNAMO_MAGMATIC, "magmatic");
		registry.addRecipeCategories(magmaticCategory);
		addFluidFuels(registry, magmaticCategory, TileDynamoMagmatic.getMagmaticFuelFluids(), TileDynamoMagmatic::getFuelEnergy);
		registry.addRecipeCategoryCraftingItem(BlockDynamo.dynamoMagmatic, RecipeUidsTE.DYNAMO_MAGMATIC);
	}



	private static void addFluidFuels(IModRegistry registry, DynamoFuelCategory.FuelFluid category, Set<Fluid> fluidSet, ToIntFunction<FluidStack> getFuelEnergy) {
		registry.addRecipes(fluidSet.stream().map(fluid -> {
			FluidStack fluidStack = new FluidStack(fluid, 1000);
			return new DynamoFuelWrapper<>(fluidStack, category, getFuelEnergy.applyAsInt(fluidStack));
		}).collect(Collectors.toList()));
	}

	private static void addItemFuels(IModRegistry registry, DynamoFuelCategory.FuelItem category, Set<ComparableItemStack> stackSet, ToIntFunction<ItemStack> getFuelEnergy) {
		registry.addRecipes(stackSet.stream().map(comparableItemStack -> {
			ItemStack itemStack = comparableItemStack.toItemStack();
			return new DynamoFuelWrapper<>(itemStack, category, getFuelEnergy.applyAsInt(itemStack));
		}).collect(Collectors.toList()));
	}
}
