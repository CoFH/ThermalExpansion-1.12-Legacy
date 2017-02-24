package cofh.thermalexpansion.plugins.jei;

import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.plugins.jei.category.Categories;
import cofh.thermalexpansion.plugins.jei.category.CategoryBase;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MachineRecipeWrapper extends BlankRecipeWrapper {

	private final Categories type;

	private final List<List<?>> inputs;
	private final List<?> outputs;
	private final int energy;
	private final int duration;
	private final int chance;

	public int getEnergyRequired() {

		return energy;
	}

	public int getDuration() {

		return duration;
	}

	public int getChance() {

		return chance;
	}

	public Categories getType() {

		return type;
	}

	public MachineRecipeWrapper(List<?> input, List<?> output, Categories type, int energy, int duration, int chance) {

		this.type = type;
		this.inputs = getOres(input);
		this.outputs = output;
		this.energy = energy;
		this.duration = duration;
		this.chance = chance;
	}

	public List<List<?>> getOres(List<?> inputs) {

		List<List<?>> oresList = new ArrayList<List<?>>();
		for (Object obj : inputs) {
			boolean handled = false;
			if (obj instanceof ItemStack) {
				ItemStack stack = (ItemStack) obj;
				String oreName = ItemHelper.getOreName(stack);
				if (!oreName.equals("Unknown")) {
					List<ItemStack> stacks = OreDictionary.getOres(oreName);
					if (stacks.size() > 1) {
						if (stack.stackSize > 1) {
							List<ItemStack> dest = new ArrayList<>();
							for (ItemStack tempStack : stacks) {
								dest.add(new ItemStack(tempStack.getItem(), stack.stackSize, tempStack.getItemDamage()));
							}
							oresList.add(dest);
						} else {
							oresList.add(stacks);
						}
						handled = true;
					}
				}
			}
			if (!handled) {
				oresList.add(Collections.singletonList(obj));
			}
		}
		return oresList;
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients) {
		//inputs.
		List<List<ItemStack>> stackInputs = new ArrayList<List<ItemStack>>();
		List<List<FluidStack>> fluidInputs = new ArrayList<List<FluidStack>>();
		for (List<?> list : inputs) {
			if (list.size() > 0 && list.get(0) instanceof ItemStack) {
				stackInputs.add((List<ItemStack>) list);
			} else {
				fluidInputs.add((List<FluidStack>) list);
			}
		}

		ingredients.setInputLists(ItemStack.class, stackInputs);
		ingredients.setInputLists(FluidStack.class, fluidInputs);

		//outputs.
		List<ItemStack> stackOutputs = new ArrayList<ItemStack>();
		List<FluidStack> fluidOutputs = new ArrayList<FluidStack>();
		for (Object obj : outputs) {
			if (obj instanceof ItemStack) {
				stackOutputs.add((ItemStack) obj);
			} else {
				fluidOutputs.add((FluidStack) obj);
			}
		}

		ingredients.setOutputs(ItemStack.class, stackOutputs);
		ingredients.setOutputs(FluidStack.class, fluidOutputs);
	}

	@Nullable
	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {

		CategoryBase base = type.getCategory();
		return base.getTooltips(mouseX, mouseY);
	}
}
