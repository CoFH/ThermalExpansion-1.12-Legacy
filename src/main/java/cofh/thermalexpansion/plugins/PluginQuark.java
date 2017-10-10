package cofh.thermalexpansion.plugins;

import cofh.core.util.ModPlugin;
import cofh.core.util.helpers.ColorHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager.Type;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import cofh.thermalexpansion.util.managers.machine.SawmillManager;
import cofh.thermalexpansion.util.managers.machine.SmelterManager;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import java.util.Locale;

public class PluginQuark extends ModPlugin {

	public static final String MOD_ID = "quark";
	public static final String MOD_NAME = "Quark";

	public PluginQuark() {

		super(MOD_ID, MOD_NAME);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";
		enable = Loader.isModLoaded(MOD_ID) && ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment);

		if (!enable) {
			return false;
		}
		return !error;
	}

	@Override
	public boolean register() {

		if (!enable) {
			return false;
		}
		try {
			/* PULVERIZER */
			{
				int energy = PulverizerManager.DEFAULT_ENERGY;

				PulverizerManager.addRecipe(energy, getItemStack("biotite_ore", 1), getItemStack("biotite", 3, 0), new ItemStack(Items.ENDER_PEARL), 5);
				PulverizerManager.addRecipe(energy, getItemStack("blaze_lantern", 1), new ItemStack(Items.BLAZE_POWDER, 16));

				PulverizerManager.addRecipe(energy, getItemStack("biome_cobblestone", 1, 0), new ItemStack(Blocks.GRAVEL), new ItemStack(Items.BLAZE_POWDER), 5);
				PulverizerManager.addRecipe(energy, getItemStack("biome_cobblestone", 1, 1), new ItemStack(Blocks.GRAVEL), ItemMaterial.dustBlizz, 5);

				energy = PulverizerManager.DEFAULT_ENERGY * 3 / 4;
				PulverizerManager.addRecipe(energy, getItemStack("soul_sandstone", 1), ItemHelper.cloneStack(Blocks.SOUL_SAND, 2), ItemMaterial.dustSulfur, 40);

				for (int i = 0; i < 2; i++) {
					PulverizerManager.addRecipe(energy, getItemStack("biotite_block", 1, i), getItemStack("biotite", 4, 0));
					PulverizerManager.addRecipe(energy, getItemStack("sandstone_new", 1, i), new ItemStack(Blocks.SAND, 2), ItemMaterial.dustNiter, 40);
					PulverizerManager.addRecipe(energy, getItemStack("sandstone_new", 1, 2 + i), new ItemStack(Blocks.SAND, 2, 1), ItemMaterial.dustNiter, 40);
					PulverizerManager.addRecipe(energy, getItemStack("sandstone_new", 1, 4 + i), new ItemStack(Blocks.SOUL_SAND, 2), ItemMaterial.dustSulfur, 40);
				}

				/* STAIRS */
				PulverizerManager.addRecipe(energy, getItemStack("soul_sandstone_stairs", 1), new ItemStack(Blocks.SOUL_SAND, 2), ItemMaterial.dustSulfur, 20);
				PulverizerManager.addRecipe(energy, getItemStack("biotite_stairs", 1), getItemStack("biotite", 3, 0));
				PulverizerManager.addRecipe(energy, getItemStack("sandstone_bricks_stairs", 1), new ItemStack(Blocks.SAND, 2), ItemMaterial.dustNiter, 20);
				PulverizerManager.addRecipe(energy, getItemStack("red_sandstone_bricks_stairs", 1), new ItemStack(Blocks.SAND, 2, 1), ItemMaterial.dustNiter, 20);
				PulverizerManager.addRecipe(energy, getItemStack("soul_sandstone_bricks_stairs", 1), new ItemStack(Blocks.SOUL_SAND, 2), ItemMaterial.dustSulfur, 20);

				/* SLABS */
				PulverizerManager.addRecipe(energy / 2, getItemStack("soul_sandstone_slab", 1), new ItemStack(Blocks.SOUL_SAND, 1), ItemMaterial.dustSulfur, 20);
				PulverizerManager.addRecipe(energy / 2, getItemStack("biotite_slab", 1), getItemStack("biotite", 2, 0));
				PulverizerManager.addRecipe(energy / 2, getItemStack("sandstone_smooth_slab", 1), new ItemStack(Blocks.SAND, 1), ItemMaterial.dustNiter, 20);
				PulverizerManager.addRecipe(energy / 2, getItemStack("sandstone_bricks_slab", 1), new ItemStack(Blocks.SAND, 1), ItemMaterial.dustNiter, 20);
				PulverizerManager.addRecipe(energy / 2, getItemStack("red_sandstone_smooth_slab", 1), new ItemStack(Blocks.SAND, 1, 1), ItemMaterial.dustNiter, 20);
				PulverizerManager.addRecipe(energy / 2, getItemStack("red_sandstone_bricks_slab", 1), new ItemStack(Blocks.SAND, 1, 1), ItemMaterial.dustNiter, 20);
				PulverizerManager.addRecipe(energy / 2, getItemStack("soul_sandstone_smooth_slab", 1), new ItemStack(Blocks.SOUL_SAND, 1), ItemMaterial.dustSulfur, 20);
				PulverizerManager.addRecipe(energy / 2, getItemStack("soul_sandstone_bricks_slab", 1), new ItemStack(Blocks.SOUL_SAND, 1), ItemMaterial.dustSulfur, 20);

				/* DYES */
				int[] dyeChance = new int[ColorHelper.WOOL_COLOR_CONFIG.length];
				for (int i = 0; i < ColorHelper.WOOL_COLOR_CONFIG.length; i++) {
					dyeChance[i] = 10;
				}
				dyeChance[EnumDyeColor.WHITE.getMetadata()] = 0;
				dyeChance[EnumDyeColor.BROWN.getMetadata()] = 0;
				dyeChance[EnumDyeColor.BLUE.getMetadata()] = 0;
				dyeChance[EnumDyeColor.BLACK.getMetadata()] = 0;

				ItemStack stringStack = ItemHelper.cloneStack(Items.STRING, 4);
				ItemStack brickStack = ItemHelper.cloneStack(Items.BRICK, 3);

				for (int i = 0; i < ColorHelper.WOOL_COLOR_CONFIG.length; i++) {
					if (dyeChance[i] > 0) {
						PulverizerManager.addRecipe(energy, getItemStack("quilted_wool", 1, i), stringStack, new ItemStack(Items.DYE, 1, 15 - i), dyeChance[i]);
						PulverizerManager.addRecipe(energy / 2, getItemStack("colored_flowerpot_" + ColorHelper.WOOL_COLOR_CONFIG[i].toLowerCase(Locale.ENGLISH), 1), brickStack, new ItemStack(Items.DYE, 1, 15 - i), dyeChance[i]);
					} else {
						PulverizerManager.addRecipe(energy, getItemStack("quilted_wool", 1, i), stringStack);
						PulverizerManager.addRecipe(energy / 2, getItemStack("colored_flowerpot_" + ColorHelper.WOOL_COLOR_CONFIG[i].toLowerCase(Locale.ENGLISH), 1), brickStack);
					}
				}
			}

			/* SAWMILL */
			{
				int energy = SawmillManager.DEFAULT_ENERGY * 3 / 2;

				SawmillManager.addRecipe(energy, getItemStack("custom_chest", 1, 0), new ItemStack(Blocks.PLANKS, 4, 1), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, getItemStack("custom_chest", 1, 1), new ItemStack(Blocks.PLANKS, 4, 2), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, getItemStack("custom_chest", 1, 2), new ItemStack(Blocks.PLANKS, 4, 3), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, getItemStack("custom_chest", 1, 3), new ItemStack(Blocks.PLANKS, 4, 4), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, getItemStack("custom_chest", 1, 4), new ItemStack(Blocks.PLANKS, 4, 5), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));

				SawmillManager.addRecipe(energy, getItemStack("custom_chest_trap", 1, 0), new ItemStack(Blocks.PLANKS, 4, 1), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, getItemStack("custom_chest_trap", 1, 1), new ItemStack(Blocks.PLANKS, 4, 2), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, getItemStack("custom_chest_trap", 1, 2), new ItemStack(Blocks.PLANKS, 4, 3), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, getItemStack("custom_chest_trap", 1, 3), new ItemStack(Blocks.PLANKS, 4, 4), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, getItemStack("custom_chest_trap", 1, 4), new ItemStack(Blocks.PLANKS, 4, 5), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));

				SawmillManager.addRecipe(energy, getItemStack("custom_bookshelf", 1, 0), new ItemStack(Blocks.PLANKS, 3, 1), new ItemStack(Items.BOOK, 3), 25);
				SawmillManager.addRecipe(energy, getItemStack("custom_bookshelf", 1, 1), new ItemStack(Blocks.PLANKS, 3, 2), new ItemStack(Items.BOOK, 3), 25);
				SawmillManager.addRecipe(energy, getItemStack("custom_bookshelf", 1, 2), new ItemStack(Blocks.PLANKS, 3, 3), new ItemStack(Items.BOOK, 3), 25);
				SawmillManager.addRecipe(energy, getItemStack("custom_bookshelf", 1, 3), new ItemStack(Blocks.PLANKS, 3, 4), new ItemStack(Items.BOOK, 3), 25);
				SawmillManager.addRecipe(energy, getItemStack("custom_bookshelf", 1, 4), new ItemStack(Blocks.PLANKS, 3, 5), new ItemStack(Items.BOOK, 3), 25);

				SawmillManager.addRecipe(energy, getItemStack("spruce_trapdoor", 2), new ItemStack(Blocks.PLANKS, 1, 1), ItemMaterial.dustWood, 75);
				SawmillManager.addRecipe(energy, getItemStack("birch_trapdoor", 2), new ItemStack(Blocks.PLANKS, 1, 2), ItemMaterial.dustWood, 75);
				SawmillManager.addRecipe(energy, getItemStack("jungle_trapdoor", 2), new ItemStack(Blocks.PLANKS, 1, 3), ItemMaterial.dustWood, 75);
				SawmillManager.addRecipe(energy, getItemStack("acacia_trapdoor", 2), new ItemStack(Blocks.PLANKS, 1, 4), ItemMaterial.dustWood, 75);
				SawmillManager.addRecipe(energy, getItemStack("dark_oak_trapdoor", 2), new ItemStack(Blocks.PLANKS, 1, 5), ItemMaterial.dustWood, 75);

				/* STAIRS */
				SawmillManager.addRecipe(energy, getItemStack("stained_planks_white_stairs", 2), getItemStack("stained_planks", 1, 0), ItemMaterial.dustWood, 50);
				SawmillManager.addRecipe(energy, getItemStack("stained_planks_orange_stairs", 2), getItemStack("stained_planks", 1, 1), ItemMaterial.dustWood, 50);
				SawmillManager.addRecipe(energy, getItemStack("stained_planks_magenta_stairs", 2), getItemStack("stained_planks", 1, 2), ItemMaterial.dustWood, 50);
				SawmillManager.addRecipe(energy, getItemStack("stained_planks_light_blue_stairs", 2), getItemStack("stained_planks", 1, 3), ItemMaterial.dustWood, 50);
				SawmillManager.addRecipe(energy, getItemStack("stained_planks_yellow_stairs", 2), getItemStack("stained_planks", 1, 4), ItemMaterial.dustWood, 50);
				SawmillManager.addRecipe(energy, getItemStack("stained_planks_lime_stairs", 2), getItemStack("stained_planks", 1, 5), ItemMaterial.dustWood, 50);
				SawmillManager.addRecipe(energy, getItemStack("stained_planks_pink_stairs", 2), getItemStack("stained_planks", 1, 6), ItemMaterial.dustWood, 50);
				SawmillManager.addRecipe(energy, getItemStack("stained_planks_gray_stairs", 2), getItemStack("stained_planks", 1, 7), ItemMaterial.dustWood, 50);
				SawmillManager.addRecipe(energy, getItemStack("stained_planks_silver_stairs", 2), getItemStack("stained_planks", 1, 8), ItemMaterial.dustWood, 50);
				SawmillManager.addRecipe(energy, getItemStack("stained_planks_cyan_stairs", 2), getItemStack("stained_planks", 1, 9), ItemMaterial.dustWood, 50);
				SawmillManager.addRecipe(energy, getItemStack("stained_planks_purple_stairs", 2), getItemStack("stained_planks", 1, 10), ItemMaterial.dustWood, 50);
				SawmillManager.addRecipe(energy, getItemStack("stained_planks_blue_stairs", 2), getItemStack("stained_planks", 1, 11), ItemMaterial.dustWood, 50);
				SawmillManager.addRecipe(energy, getItemStack("stained_planks_brown_stairs", 2), getItemStack("stained_planks", 1, 12), ItemMaterial.dustWood, 50);
				SawmillManager.addRecipe(energy, getItemStack("stained_planks_green_stairs", 2), getItemStack("stained_planks", 1, 13), ItemMaterial.dustWood, 50);
				SawmillManager.addRecipe(energy, getItemStack("stained_planks_red_stairs", 2), getItemStack("stained_planks", 1, 14), ItemMaterial.dustWood, 50);
				SawmillManager.addRecipe(energy, getItemStack("stained_planks_black_stairs", 2), getItemStack("stained_planks", 1, 15), ItemMaterial.dustWood, 50);

				energy = SawmillManager.DEFAULT_ENERGY * 3 / 4;

				for (int i = 0; i < ColorHelper.WOOL_COLOR_CONFIG.length; i++) {
					SawmillManager.addRecipe(energy, getItemStack("colored_item_frame", 1, i), new ItemStack(Items.LEATHER, 1), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				}
			}

			/* SMELTER */
			{
				int energy = SmelterManager.DEFAULT_ENERGY;

				SmelterManager.addRecycleRecipe(energy, getItemStack("iron_ladder", 1, 0), new ItemStack(Items.IRON_NUGGET), 3);
			}

			/* INSOLATOR */
			{
				ItemStack glowshroom = getItemStack("glowshroom", 1, 0);

				InsolatorManager.addDefaultRecipe(glowshroom, ItemHelper.cloneStack(glowshroom, 2), ItemStack.EMPTY, 0, Type.MYCELIUM);
			}
		} catch (Throwable t) {
			ThermalExpansion.LOG.error("Thermal Expansion: " + MOD_NAME + " Plugin encountered an error:", t);
			error = true;
		}
		if (!error) {
			ThermalExpansion.LOG.info("Thermal Expansion: " + MOD_NAME + " Plugin Enabled.");
		}
		return !error;
	}

}
