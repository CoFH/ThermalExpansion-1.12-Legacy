package cofh.thermalexpansion.util.parsers;

import cofh.core.util.helpers.ItemHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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

import java.util.Map;

public abstract class BaseParser implements IContentParser {

	public static final String INPUT = "input";
	public static final String INPUT2 = "input2";
	public static final String OUTPUT = "output";
	public static final String OUTPUT2 = "output2";
	public static final String OUTPUT3 = "output3";
	public static final String OUTPUT4 = "output4";
	public static final String FLUID = "fluid";
	public static final String ENERGY = "energy";
	public static final String ENERGY_MOD = "energy_mod";
	public static final String CHANCE = "chance";
	public static final String TYPE = "type";

	public static final String COMMENT = "//";
	public static final String CONSTANT = "constant";
	public static final String ENTRY = "entry";
	public static final String ORE = "ore";
	public static final String NAME = "name";

	public static final String ITEM = "item";
	public static final String DATA = "data";
	public static final String WILDCARD = "wildcard";
	public static final String COUNT = "count";
	public static final String AMOUNT = "amount";
	public static final String NBT = "nbt";

	protected int parseCount = 0;
	protected int errorCount = 0;

	protected static final Map<String, ItemStack> constants = new Object2ObjectOpenHashMap<>();
	protected static final Map<String, ItemStack> ores = new Object2ObjectOpenHashMap<>();

	@Override
	public boolean parseContent(JsonElement content) {

		if (content.isJsonNull()) {
			return false;
		}
		if (content.isJsonArray()) {
			parseArray(content.getAsJsonArray());
			return true;
		}
		return false;
	}

	public abstract void parseArray(JsonArray contentArray);

	public static boolean hasOre(String oreName) {

		return ores.containsKey(oreName);
	}

	public static ItemStack getOre(String oreName) {

		return ores.get(oreName);
	}

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
			if (itemObject.has(DATA)) {
				data = itemObject.get(DATA).getAsInt();
			} else if (itemObject.has(WILDCARD) && itemObject.get(WILDCARD).getAsBoolean()) {
				data = OreDictionary.WILDCARD_VALUE;
			}
			/* COUNT */
			if (itemObject.has(COUNT)) {
				count = itemObject.get(COUNT).getAsInt();
			}
			/* CONSTANT */
			if (itemObject.has(CONSTANT)) {
				ore = itemObject.get(CONSTANT).getAsString();
				if (constants.containsKey(ore)) {
					return ItemHelper.cloneStack(constants.get(ore), count);
				} else {
					ore = "";
				}
			}
			/* ORE NAME */
			if (itemObject.has(ORE)) {
				ore = itemObject.get(ORE).getAsString();
			}
			if (ItemHelper.oreNameExists(ore)) {
				if (ores.containsKey(ore)) {
					return ItemHelper.cloneStack(ores.get(ore), count);
				}
				NonNullList<ItemStack> ores = OreDictionary.getOres(ore, false);
				if (!ores.isEmpty()) {
					return ItemHelper.cloneStack(ores.get(0), count);
				}
			}
			/* ITEM */
			if (itemObject.has(ITEM)) {
				item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemObject.get(ITEM).getAsString()));
			}
			if (item == null) {
				return ItemStack.EMPTY;
			}
			stack = new ItemStack(item, count, data);

			/* NBT */
			if (itemObject.has(NBT)) {
				try {
					stack.setTagCompound(JsonToNBT.getTagFromJson(itemObject.get(NBT).getAsString()));
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
			if (fluidObject.has(AMOUNT)) {
				amount = fluidObject.get(AMOUNT).getAsInt();
			}
			/* FLUID */
			if (fluidObject.has(FLUID)) {
				fluid = FluidRegistry.getFluid(fluidObject.get(FLUID).getAsString());
			}
			if (fluid == null) {
				return null;
			}
			stack = new FluidStack(fluid, amount);

			/* NBT */
			if (fluidObject.has(NBT)) {
				try {
					stack.tag = JsonToNBT.getTagFromJson(fluidObject.get(NBT).getAsString());
				} catch (NBTException t) {
					return null;
				}
			}
		}
		return stack;
	}

	public static int getChance(JsonElement element) {

		JsonObject chanceObject = element.getAsJsonObject();

		if (chanceObject.has(CHANCE)) {
			return chanceObject.get(CHANCE).getAsInt();
		}
		return 100;
	}

}
