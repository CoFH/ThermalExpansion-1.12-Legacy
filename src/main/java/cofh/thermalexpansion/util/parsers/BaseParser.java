package cofh.thermalexpansion.util.parsers;

import cofh.core.util.helpers.ItemHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public abstract class BaseParser implements IContentParser {

	public static final String INPUT = "input";
	public static final String OUTPUT = "output";
	public static final String OUTPUT2 = "output2";
	public static final String ENERGY = "energy";
	public static final String CHANCE = "chance";

	public static final String ADD_RECIPE = "add";
	public static final String REMOVE_RECIPE = "remove";

	@Override
	public boolean parseContent(JsonObject content) {

		if (content.isJsonNull()) {
			return false;
		}
		String operation = "";

		if (content.has("operation")) {
			operation = content.get("operation").getAsString();
		}
		if (operation.equals(REMOVE_RECIPE)) {
			return removeRecipe(content);
		}
		return addRecipe(content);
	}

	public abstract boolean addRecipe(JsonObject content);

	public abstract boolean removeRecipe(JsonObject content);

	/* HELPERS */
	public static ItemStack parseItemStack(JsonElement element) {

		if (element == null || element.isJsonNull()) {
			return ItemStack.EMPTY;
		}
		ItemStack stack;
		Item item = null;
		String ore = "";
		int data = 0;
		int count = 1;

		if (element.isJsonPrimitive()) {
			item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(element.getAsString()));
			return item == null ? ItemStack.EMPTY : new ItemStack(item);
		} else {
			JsonObject itemObject = element.getAsJsonObject();

			/* DATA */
			if (itemObject.has("data")) {
				data = itemObject.get("data").getAsInt();
			}
			/* COUNT */
			if (itemObject.has("count")) {
				count = itemObject.get("count").getAsInt();
			}
			/* ORE NAME */
			if (itemObject.has("ore")) {
				ore = itemObject.get("ore").getAsString();
			}
			if (ItemHelper.oreNameExists(ore)) {
				NonNullList<ItemStack> ores = OreDictionary.getOres(ore, false);
				if (!ores.isEmpty()) {
					return ItemHelper.cloneStack(ores.get(0), count);
				}
			}
			/* ITEM */
			if (itemObject.has("item")) {
				item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemObject.get("item").getAsString()));
			}
			if (item == null) {
				return ItemStack.EMPTY;
			}
			stack = new ItemStack(item, count, data);

			/* NBT */
			if (itemObject.has("nbt")) {
				try {
					stack.setTagCompound(JsonToNBT.getTagFromJson(itemObject.get("nbt").getAsString()));
				} catch (NBTException t) {
					return ItemStack.EMPTY;
				}
			}
		}
		return stack;
	}

	public static FluidStack parseFluidStack(JsonElement element) {

		if (element == null || element.isJsonNull()) {
			return null;
		}
		FluidStack stack;
		Fluid fluid = null;
		int amount = Fluid.BUCKET_VOLUME;

		if (element.isJsonPrimitive()) {
			fluid = FluidRegistry.getFluid(element.getAsString());
			return fluid == null ? null : new FluidStack(fluid, amount);
		} else {
			JsonObject fluidObject = element.getAsJsonObject();

			/* AMOUNT */
			if (fluidObject.has("amount")) {
				amount = fluidObject.get("amount").getAsInt();
			}
			/* FLUID */
			if (fluidObject.has("fluid")) {
				fluid = FluidRegistry.getFluid(fluidObject.get("fluid").getAsString());
			}
			if (fluid == null) {
				return null;
			}
			stack = new FluidStack(fluid, amount);

			/* NBT */
			if (fluidObject.has("nbt")) {
				try {
					stack.tag = JsonToNBT.getTagFromJson(fluidObject.get("nbt").getAsString());
				} catch (NBTException t) {
					return null;
				}
			}
		}
		return stack;
	}

}
