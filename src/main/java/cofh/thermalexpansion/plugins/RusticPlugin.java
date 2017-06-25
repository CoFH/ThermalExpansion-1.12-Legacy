package cofh.thermalexpansion.plugins;

import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.TapperManager;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager.Type;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Arrays;

public class RusticPlugin {

	private RusticPlugin() {

	}

	public static final String MOD_ID = "rustic";
	public static final String MOD_NAME = "Rustic";

	public static void initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";

		boolean enable = ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment);

		if (!enable || !Loader.isModLoaded(MOD_ID)) {
			return;
		}

		ItemStack beeswax = getItem("beeswax");
		ItemStack chiliPepper = getItem("chili_pepper");
		ItemStack honeycomb = getItem("honeycomb");
		ItemStack grapes = getItem("grapes");
		ItemStack ironberries = getItem("ironberries");
		ItemStack olives = getItem("olives");
		ItemStack tomato = getItem("tomato");
		ItemStack wildberries = getItem("wildberries");

		ItemStack seedApple = getItem("apple_seeds");
		ItemStack seedChiliPepper = getItem("chili_pepper_seeds");
		ItemStack seedGrape = getItem("grape_stem");
		ItemStack seedTomato = getItem("tomato_seeds");

		ItemStack aloeVera = getItem("aloe_vera");
		ItemStack bloodOrchid = getItem("blood_orchid");
		ItemStack chamomile = getItem("chamomile");
		ItemStack cloudsbluff = getItem("cloudsbluff");
		ItemStack cohosh = getItem("cohosh");
		ItemStack coreRoot = getItem("core_root");
		ItemStack deathstalkMushroom = getItem("deathstalk_mushroom");
		ItemStack ginseng = getItem("ginseng");
		ItemStack horsetail = getItem("horsetail");
		ItemStack marshMallow = getItem("marsh_mallow");
		ItemStack mooncapMushroom = getItem("mooncap_mushroom");
		ItemStack windThistle = getItem("wind_thistle");

		ItemStack logOlive = getBlockStack("log", 1, 0);
		ItemStack logIronwood = getBlockStack("log", 1, 1);

		ItemStack saplingOlive = getItem("sapling", 1, 0);
		ItemStack saplingIronwood = getItem("sapling", 1, 1);
		ItemStack saplingApple = getItem("sapling_apple", 1, 0);

		Block log = getBlock("log");

		Block leaves = getBlock("leaves");
		Block leavesApple = getBlock("leaves_apple");

		Fluid honey = FluidRegistry.getFluid("honey");
		Fluid grapejuice = FluidRegistry.getFluid("grapejuice");
		Fluid ironberryjuice = FluidRegistry.getFluid("ironberryjuice");
		Fluid oliveoil = FluidRegistry.getFluid("oliveoil");
		Fluid wildberryjuice = FluidRegistry.getFluid("wildberryjuice");

		Fluid seed_oil = FluidRegistry.getFluid("seed.oil");

		/* INSOLATOR */
		{
			InsolatorManager.addDefaultRecipe(seedChiliPepper, ItemHelper.cloneStack(chiliPepper, 2), seedChiliPepper, 100);
			InsolatorManager.addDefaultRecipe(seedGrape, ItemHelper.cloneStack(grapes, 2), seedGrape, 100);
			InsolatorManager.addDefaultRecipe(seedTomato, ItemHelper.cloneStack(tomato, 2), seedTomato, 100);

			InsolatorManager.addDefaultRecipe(aloeVera, ItemHelper.cloneStack(aloeVera, 3), ItemStack.EMPTY, 0);
			InsolatorManager.addDefaultRecipe(bloodOrchid, ItemHelper.cloneStack(bloodOrchid, 3), ItemStack.EMPTY, 0);
			InsolatorManager.addDefaultRecipe(chamomile, ItemHelper.cloneStack(chamomile, 3), ItemStack.EMPTY, 0);
			InsolatorManager.addDefaultRecipe(cloudsbluff, ItemHelper.cloneStack(cloudsbluff, 3), ItemStack.EMPTY, 0);
			InsolatorManager.addDefaultRecipe(cohosh, ItemHelper.cloneStack(cohosh, 3), ItemStack.EMPTY, 0);
			InsolatorManager.addDefaultRecipe(coreRoot, ItemHelper.cloneStack(coreRoot, 3), ItemStack.EMPTY, 0);
			InsolatorManager.addDefaultRecipe(ginseng, ItemHelper.cloneStack(ginseng, 3), ItemStack.EMPTY, 0);
			InsolatorManager.addDefaultRecipe(horsetail, ItemHelper.cloneStack(horsetail, 3), ItemStack.EMPTY, 0);
			InsolatorManager.addDefaultRecipe(marshMallow, ItemHelper.cloneStack(marshMallow, 3), ItemStack.EMPTY, 0);
			InsolatorManager.addDefaultRecipe(windThistle, ItemHelper.cloneStack(windThistle, 3), ItemStack.EMPTY, 0);

			InsolatorManager.addDefaultRecipe(deathstalkMushroom, ItemHelper.cloneStack(deathstalkMushroom, 2), ItemStack.EMPTY, 0, false, Type.MYCELIUM);
			InsolatorManager.addDefaultRecipe(mooncapMushroom, ItemHelper.cloneStack(mooncapMushroom, 2), ItemStack.EMPTY, 0, false, Type.MYCELIUM);

			InsolatorManager.addDefaultTreeRecipe(saplingOlive, ItemHelper.cloneStack(logOlive, 4), saplingOlive, 50, false, Type.TREE);
			InsolatorManager.addDefaultTreeRecipe(saplingIronwood, ItemHelper.cloneStack(logIronwood, 4), saplingIronwood, 50, false, Type.TREE);
			InsolatorManager.addDefaultTreeRecipe(saplingApple, new ItemStack(Blocks.LOG, 4, 0), saplingApple, 50, false, Type.TREE);
		}

		/* TRANSPOSER */
		{
			if (grapejuice != null) {
				TransposerManager.addExtractRecipe(2400, grapes, ItemStack.EMPTY, new FluidStack(grapejuice, 250), 0, false);
			}
			if (ironberryjuice != null) {
				TransposerManager.addExtractRecipe(2400, ironberries, ItemStack.EMPTY, new FluidStack(ironberryjuice, 250), 0, false);
			}
			if (oliveoil != null) {
				TransposerManager.addExtractRecipe(2400, olives, ItemStack.EMPTY, new FluidStack(oliveoil, 250), 0, false);
			}
			if (wildberryjuice != null) {
				TransposerManager.addExtractRecipe(2400, wildberries, ItemStack.EMPTY, new FluidStack(wildberryjuice, 250), 0, false);
			}
			if (seed_oil != null) {
				TransposerManager.addExtractRecipe(2400, seedApple, ItemStack.EMPTY, new FluidStack(seed_oil, 10), 0, false);
				TransposerManager.addExtractRecipe(2400, seedChiliPepper, ItemStack.EMPTY, new FluidStack(seed_oil, 10), 0, false);
				TransposerManager.addExtractRecipe(2400, seedGrape, ItemStack.EMPTY, new FluidStack(seed_oil, 10), 0, false);
				TransposerManager.addExtractRecipe(2400, seedTomato, ItemStack.EMPTY, new FluidStack(seed_oil, 10), 0, false);
			}
		}

		/* CENTRIFUGE */
		{
			if (honey != null) {
				CentrifugeManager.addRecipe(4000, honeycomb, Arrays.asList(beeswax), Arrays.asList(100), new FluidStack(honey, 250));
			}
		}

		/* TAPPER */
		{
			TapperManager.addMapping(logOlive, new FluidStack(TFFluids.fluidResin, 50));
			TapperManager.addMapping(logIronwood, new FluidStack(TFFluids.fluidResin, 50));

			addLeafMapping(log, leaves, 0);
			addLeafMapping(log, leaves, 1);
			addLeafMapping(Blocks.LOG, leavesApple, 0);
		}

		ThermalExpansion.LOG.info("Thermal Expansion: " + MOD_NAME + " Plugin Enabled.");
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

	private static void addLeafMapping(Block logBlock, Block leafBlock, int metadata) {

		IBlockState logState = logBlock.getStateFromMeta(metadata);

		for (Boolean check_decay : BlockLeaves.CHECK_DECAY.getAllowedValues()) {
			IBlockState leafState = leafBlock.getStateFromMeta(metadata).withProperty(BlockLeaves.DECAYABLE, Boolean.TRUE).withProperty(BlockLeaves.CHECK_DECAY, check_decay);
			TapperManager.addLeafMappingDirect(logState, leafState);
		}
	}

}
