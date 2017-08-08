package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ColorHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import cofh.thermalexpansion.util.managers.machine.SawmillManager;
import cofh.thermalexpansion.util.managers.machine.SmelterManager;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Locale;

public class QuarkPlugin {

	private QuarkPlugin() {

	}

	public static final String MOD_ID = "quark";
	public static final String MOD_NAME = "Quark";

	public static void initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";

		boolean enable = ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment);

		if (!enable || !Loader.isModLoaded(MOD_ID)) {
			return;
		}
		try {

			/* PULVERIZER */
			{
				int energy = PulverizerManager.DEFAULT_ENERGY;

				PulverizerManager.addRecipe(energy, getBlockStack("blaze_lantern", 1, 0), new ItemStack(Items.BLAZE_POWDER, 16));
				PulverizerManager.addRecipe(energy, new ItemStack(Items.REEDS), new ItemStack(Items.SUGAR, 2));

				energy = PulverizerManager.DEFAULT_ENERGY * 3 / 4;

				int[] dyeChance = new int[ColorHelper.WOOL_COLOR_CONFIG.length];
				for (int i = 0; i < ColorHelper.WOOL_COLOR_CONFIG.length; i++) {
					dyeChance[i] = 10;
				}
				dyeChance[EnumDyeColor.WHITE.getMetadata()] = 0;
				dyeChance[EnumDyeColor.BROWN.getMetadata()] = 0;
				dyeChance[EnumDyeColor.BLUE.getMetadata()] = 0;
				dyeChance[EnumDyeColor.GREEN.getMetadata()] = 0;
				dyeChance[EnumDyeColor.BLACK.getMetadata()] = 0;

				ItemStack stringStack = ItemHelper.cloneStack(Items.STRING, 4);
				ItemStack brickStack = ItemHelper.cloneStack(Items.BRICK, 3);

				for (int i = 0; i < ColorHelper.WOOL_COLOR_CONFIG.length; i++) {
					if (dyeChance[i] > 0) {
						PulverizerManager.addRecipe(energy, getBlockStack("quilted_wool", 1, i), stringStack, new ItemStack(Items.DYE, 1, 15 - i), dyeChance[i]);
						PulverizerManager.addRecipe(energy / 2, getBlockStack("colored_flowerpot_" + ColorHelper.WOOL_COLOR_CONFIG[i].toLowerCase(Locale.ENGLISH), 1), brickStack, new ItemStack(Items.DYE, 1, 15 - i), dyeChance[i]);
					} else {
						PulverizerManager.addRecipe(energy, getBlockStack("quilted_wool", 1, i), stringStack);
						PulverizerManager.addRecipe(energy / 2, getBlockStack("colored_flowerpot_" + ColorHelper.WOOL_COLOR_CONFIG[i].toLowerCase(Locale.ENGLISH), 1), brickStack);
					}
				}
			}

			/* SAWMILL */
			{
				int energy = SawmillManager.DEFAULT_ENERGY * 3 / 2;

				SawmillManager.addRecipe(energy, getBlockStack("custom_chest", 1, 0), new ItemStack(Blocks.PLANKS, 4, 1), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, getBlockStack("custom_chest", 1, 1), new ItemStack(Blocks.PLANKS, 4, 2), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, getBlockStack("custom_chest", 1, 2), new ItemStack(Blocks.PLANKS, 4, 3), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, getBlockStack("custom_chest", 1, 3), new ItemStack(Blocks.PLANKS, 4, 4), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, getBlockStack("custom_chest", 1, 4), new ItemStack(Blocks.PLANKS, 4, 5), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));

				SawmillManager.addRecipe(energy, getBlockStack("custom_chest_trap", 1, 0), new ItemStack(Blocks.PLANKS, 4, 1), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, getBlockStack("custom_chest_trap", 1, 1), new ItemStack(Blocks.PLANKS, 4, 2), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, getBlockStack("custom_chest_trap", 1, 2), new ItemStack(Blocks.PLANKS, 4, 3), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, getBlockStack("custom_chest_trap", 1, 3), new ItemStack(Blocks.PLANKS, 4, 4), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, getBlockStack("custom_chest_trap", 1, 4), new ItemStack(Blocks.PLANKS, 4, 5), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));

				SawmillManager.addRecipe(energy, getBlockStack("custom_bookshelf", 1, 0), new ItemStack(Blocks.PLANKS, 3, 1), new ItemStack(Items.BOOK, 3), 25);
				SawmillManager.addRecipe(energy, getBlockStack("custom_bookshelf", 1, 1), new ItemStack(Blocks.PLANKS, 3, 2), new ItemStack(Items.BOOK, 3), 25);
				SawmillManager.addRecipe(energy, getBlockStack("custom_bookshelf", 1, 2), new ItemStack(Blocks.PLANKS, 3, 3), new ItemStack(Items.BOOK, 3), 25);
				SawmillManager.addRecipe(energy, getBlockStack("custom_bookshelf", 1, 3), new ItemStack(Blocks.PLANKS, 3, 4), new ItemStack(Items.BOOK, 3), 25);
				SawmillManager.addRecipe(energy, getBlockStack("custom_bookshelf", 1, 4), new ItemStack(Blocks.PLANKS, 3, 5), new ItemStack(Items.BOOK, 3), 25);

				SawmillManager.addRecipe(energy, getBlockStack("spruce_trapdoor", 2), new ItemStack(Blocks.PLANKS, 1, 1), ItemMaterial.dustWood, 75);
				SawmillManager.addRecipe(energy, getBlockStack("birch_trapdoor", 2), new ItemStack(Blocks.PLANKS, 1, 2), ItemMaterial.dustWood, 75);
				SawmillManager.addRecipe(energy, getBlockStack("jungle_trapdoor", 2), new ItemStack(Blocks.PLANKS, 1, 3), ItemMaterial.dustWood, 75);
				SawmillManager.addRecipe(energy, getBlockStack("acacia_trapdoor", 2), new ItemStack(Blocks.PLANKS, 1, 4), ItemMaterial.dustWood, 75);
				SawmillManager.addRecipe(energy, getBlockStack("dark_oak_trapdoor", 2), new ItemStack(Blocks.PLANKS, 1, 5), ItemMaterial.dustWood, 75);

				energy = SawmillManager.DEFAULT_ENERGY * 3 / 4;

				for (int i = 0; i < ColorHelper.WOOL_COLOR_CONFIG.length; i++) {
					SawmillManager.addRecipe(energy, getBlockStack("colored_item_frame", 1, i), new ItemStack(Items.LEATHER, 1), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				}
			}

			/* SMELTER */
			{
				int energy = SmelterManager.DEFAULT_ENERGY;

				SmelterManager.addRecycleRecipe(energy, getBlockStack("iron_ladder", 1, 0), new ItemStack(Items.IRON_NUGGET), 3);
			}

			ThermalExpansion.LOG.info("Thermal Expansion: " + MOD_NAME + " Plugin Enabled.");
		} catch (Throwable t) {
			ThermalExpansion.LOG.error("Thermal Expansion: " + MOD_NAME + " Plugin encountered an error:", t);
		}
	}

	/* HELPERS */
	private static ItemStack getBlockStack(String name, int amount, int meta) {

		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MOD_ID + ":" + name));
		return block != null ? new ItemStack(block, amount, meta) : ItemStack.EMPTY;
	}

	private static ItemStack getBlockStack(String name, int amount) {

		return getBlockStack(name, amount, 0);
	}

	private static Block getBlock(String name) {

		return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MOD_ID + ":" + name));
	}

	private static ItemStack getItem(String name, int amount, int meta) {

		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MOD_ID + ":" + name));
		return item != null ? new ItemStack(item, amount, meta) : ItemStack.EMPTY;
	}

	private static ItemStack getItem(String name) {

		return getItem(name, 1, 0);
	}

}
