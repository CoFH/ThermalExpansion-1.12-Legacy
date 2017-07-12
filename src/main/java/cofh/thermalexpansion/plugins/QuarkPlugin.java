package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import cofh.thermalexpansion.util.managers.machine.SawmillManager;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

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
			ItemStack chestSpruce = getBlockStack("custom_chest", 1, 0);
			ItemStack chestBirch = getBlockStack("custom_chest", 1, 1);
			ItemStack chestJungle = getBlockStack("custom_chest", 1, 2);
			ItemStack chestAcacia = getBlockStack("custom_chest", 1, 3);
			ItemStack chestDarkOak = getBlockStack("custom_chest", 1, 4);

			ItemStack chestTrappedSpruce = getBlockStack("custom_chest_trap", 1, 0);
			ItemStack chestTrappedBirch = getBlockStack("custom_chest_trap", 1, 1);
			ItemStack chestTrappedJungle = getBlockStack("custom_chest_trap", 1, 2);
			ItemStack chestTrappedAcacia = getBlockStack("custom_chest_trap", 1, 3);
			ItemStack chestTrappedDarkOak = getBlockStack("custom_chest_trap", 1, 4);

			ItemStack bookshelfSpruce = getBlockStack("custom_bookshelf", 1, 0);
			ItemStack bookshelfBirch = getBlockStack("custom_bookshelf", 1, 1);
			ItemStack bookshelfJungle = getBlockStack("custom_bookshelf", 1, 2);
			ItemStack bookshelfAcacia = getBlockStack("custom_bookshelf", 1, 3);
			ItemStack bookshelfDarkOak = getBlockStack("custom_bookshelf", 1, 4);

			ItemStack trapdoorSpruce = getBlockStack("spruce_trapdoor", 1);
			ItemStack trapdoorBirch = getBlockStack("birch_trapdoor", 1);
			ItemStack trapdoorJungle = getBlockStack("jungle_trapdoor", 1);
			ItemStack trapdoorAcacia = getBlockStack("acacia_trapdoor", 1);
			ItemStack trapdoorDarkOak = getBlockStack("dark_oak_trapdoor", 1);

			/* PULVERIZER */
			{
				int energy = PulverizerManager.DEFAULT_ENERGY * 3 / 4;

			}

			/* SAWMILL */
			{
				int energy = SawmillManager.DEFAULT_ENERGY * 3 / 2;

				SawmillManager.addRecipe(energy, chestSpruce, new ItemStack(Blocks.PLANKS, 4, 1), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, chestBirch, new ItemStack(Blocks.PLANKS, 4, 2), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, chestJungle, new ItemStack(Blocks.PLANKS, 4, 3), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, chestAcacia, new ItemStack(Blocks.PLANKS, 4, 4), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, chestDarkOak, new ItemStack(Blocks.PLANKS, 4, 5), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));

				SawmillManager.addRecipe(energy, chestTrappedSpruce, new ItemStack(Blocks.PLANKS, 4, 1), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, chestTrappedBirch, new ItemStack(Blocks.PLANKS, 4, 2), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, chestTrappedJungle, new ItemStack(Blocks.PLANKS, 4, 3), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, chestTrappedAcacia, new ItemStack(Blocks.PLANKS, 4, 4), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
				SawmillManager.addRecipe(energy, chestTrappedDarkOak, new ItemStack(Blocks.PLANKS, 4, 5), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));

				SawmillManager.addRecipe(energy, bookshelfSpruce, new ItemStack(Blocks.PLANKS, 3, 1), new ItemStack(Items.BOOK, 3), 25);
				SawmillManager.addRecipe(energy, bookshelfBirch, new ItemStack(Blocks.PLANKS, 3, 2), new ItemStack(Items.BOOK, 3), 25);
				SawmillManager.addRecipe(energy, bookshelfJungle, new ItemStack(Blocks.PLANKS, 3, 3), new ItemStack(Items.BOOK, 3), 25);
				SawmillManager.addRecipe(energy, bookshelfAcacia, new ItemStack(Blocks.PLANKS, 3, 4), new ItemStack(Items.BOOK, 3), 25);
				SawmillManager.addRecipe(energy, bookshelfDarkOak, new ItemStack(Blocks.PLANKS, 3, 5), new ItemStack(Items.BOOK, 3), 25);

				SawmillManager.addRecipe(energy, trapdoorSpruce, new ItemStack(Blocks.PLANKS, 1, 1), ItemMaterial.dustWood, 75);
				SawmillManager.addRecipe(energy, trapdoorBirch, new ItemStack(Blocks.PLANKS, 1, 2), ItemMaterial.dustWood, 75);
				SawmillManager.addRecipe(energy, trapdoorJungle, new ItemStack(Blocks.PLANKS, 1, 3), ItemMaterial.dustWood, 75);
				SawmillManager.addRecipe(energy, trapdoorAcacia, new ItemStack(Blocks.PLANKS, 1, 4), ItemMaterial.dustWood, 75);
				SawmillManager.addRecipe(energy, trapdoorDarkOak, new ItemStack(Blocks.PLANKS, 1, 5), ItemMaterial.dustWood, 75);
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
