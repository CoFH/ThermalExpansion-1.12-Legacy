package cofh.thermalexpansion.plugins;

import cofh.core.util.ModPlugin;
import cofh.core.util.helpers.ItemHelper;
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
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;

import java.util.ArrayList;
import java.util.Arrays;

public class PluginRustic extends ModPlugin {

	public static final String MOD_ID = "rustic";
	public static final String MOD_NAME = "Rustic";

	public PluginRustic() {

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
			ItemStack beeswax = getItemStack("beeswax");
			ItemStack chiliPepper = getItemStack("chili_pepper");
			ItemStack honeycomb = getItemStack("honeycomb");
			ItemStack grapes = getItemStack("grapes");
			ItemStack ironberries = getItemStack("ironberries");
			ItemStack olives = getItemStack("olives");
			ItemStack tomato = getItemStack("tomato");
			ItemStack wildberries = getItemStack("wildberries");

			ItemStack seedApple = getItemStack("apple_seeds");
			ItemStack seedChiliPepper = getItemStack("chili_pepper_seeds");
			ItemStack seedGrape = getItemStack("grape_stem");
			ItemStack seedTomato = getItemStack("tomato_seeds");

			ItemStack aloeVera = getItemStack("aloe_vera");
			ItemStack bloodOrchid = getItemStack("blood_orchid");
			ItemStack chamomile = getItemStack("chamomile");
			ItemStack cloudsbluff = getItemStack("cloudsbluff");
			ItemStack cohosh = getItemStack("cohosh");
			ItemStack coreRoot = getItemStack("core_root");
			ItemStack deathstalkMushroom = getItemStack("deathstalk_mushroom");
			ItemStack ginseng = getItemStack("ginseng");
			ItemStack horsetail = getItemStack("horsetail");
			ItemStack marshMallow = getItemStack("marsh_mallow");
			ItemStack mooncapMushroom = getItemStack("mooncap_mushroom");
			ItemStack windThistle = getItemStack("wind_thistle");

			ItemStack logOlive = getItemStack("log", 1, 0);
			ItemStack logIronwood = getItemStack("log", 1, 1);

			ItemStack saplingOlive = getItemStack("sapling", 1, 0);
			ItemStack saplingIronwood = getItemStack("sapling", 1, 1);
			ItemStack saplingApple = getItemStack("sapling_apple", 1, 0);

			Block log = getBlock("log");

			Block leaves = getBlock("leaves");
			Block leavesApple = getBlock("leaves_apple");

			Fluid honey = FluidRegistry.getFluid("honey");
			Fluid juiceGrape = FluidRegistry.getFluid("grapejuice");
			Fluid juiceIronberry = FluidRegistry.getFluid("ironberryjuice");
			Fluid juiceWildberry = FluidRegistry.getFluid("wildberryjuice");
			Fluid oilOlive = FluidRegistry.getFluid("oliveoil");

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

				InsolatorManager.addDefaultRecipe(deathstalkMushroom, ItemHelper.cloneStack(deathstalkMushroom, 2), ItemStack.EMPTY, 0, Type.MYCELIUM);
				InsolatorManager.addDefaultRecipe(mooncapMushroom, ItemHelper.cloneStack(mooncapMushroom, 2), ItemStack.EMPTY, 0, Type.MYCELIUM);

				InsolatorManager.addDefaultTreeRecipe(saplingOlive, ItemHelper.cloneStack(logOlive, 4), saplingOlive, 50, Type.TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingIronwood, ItemHelper.cloneStack(logIronwood, 4), saplingIronwood, 50, Type.TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingApple, new ItemStack(Blocks.LOG, 4, 0), saplingApple, 50, Type.TREE);
			}

			/* TRANSPOSER */
			{
				int energy = 2400;

				if (seed_oil != null) {
					TransposerManager.addExtractRecipe(energy, seedApple, ItemStack.EMPTY, new FluidStack(seed_oil, 10), 0, false);
					TransposerManager.addExtractRecipe(energy, seedChiliPepper, ItemStack.EMPTY, new FluidStack(seed_oil, 10), 0, false);
					TransposerManager.addExtractRecipe(energy, seedGrape, ItemStack.EMPTY, new FluidStack(seed_oil, 10), 0, false);
					TransposerManager.addExtractRecipe(energy, seedTomato, ItemStack.EMPTY, new FluidStack(seed_oil, 10), 0, false);
				}
			}

			/* CENTRIFUGE */
			{
				int energy = CentrifugeManager.DEFAULT_ENERGY;

				if (honey != null) {
					CentrifugeManager.addRecipe(energy, honeycomb, Arrays.asList(beeswax), new FluidStack(honey, 250));
				}
				if (juiceGrape != null) {
					CentrifugeManager.addRecipe(energy, grapes, new ArrayList<>(), new FluidStack(juiceGrape, 250));
				}
				if (juiceIronberry != null) {
					CentrifugeManager.addRecipe(energy, ironberries, new ArrayList<>(), new FluidStack(juiceIronberry, 250));
				}
				if (juiceWildberry != null) {
					CentrifugeManager.addRecipe(energy, wildberries, new ArrayList<>(), new FluidStack(juiceWildberry, 250));
				}
				if (oilOlive != null) {
					CentrifugeManager.addRecipe(energy, olives, new ArrayList<>(), new FluidStack(oilOlive, 250));
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
		} catch (Throwable t) {
			ThermalExpansion.LOG.error("Thermal Expansion: " + MOD_NAME + " Plugin encountered an error:", t);
			error = true;
		}
		if (!error) {
			ThermalExpansion.LOG.info("Thermal Expansion: " + MOD_NAME + " Plugin Enabled.");
		}
		return !error;
	}

	/* HELPERS */
	private void addLeafMapping(Block logBlock, Block leafBlock, int metadata) {

		IBlockState logState = logBlock.getStateFromMeta(metadata);

		for (Boolean check_decay : BlockLeaves.CHECK_DECAY.getAllowedValues()) {
			IBlockState leafState = leafBlock.getStateFromMeta(metadata).withProperty(BlockLeaves.DECAYABLE, Boolean.TRUE).withProperty(BlockLeaves.CHECK_DECAY, check_decay);
			TapperManager.addLeafMappingDirect(logState, leafState);
		}
	}

}
