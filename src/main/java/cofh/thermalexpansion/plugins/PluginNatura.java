package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.managers.device.TapperManager;
import cofh.thermalexpansion.util.managers.machine.FurnaceManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.SawmillManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class PluginNatura extends PluginTEBase {

	public static final String MOD_ID = "natura";
	public static final String MOD_NAME = "Natura";

	public PluginNatura() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

		ItemStack seedBarley = getItemStack("overworld_seeds", 1, 0);
		ItemStack seedCotton = getItemStack("overworld_seeds", 1, 1);

		ItemStack cropBarley = getItemStack("materials", 1, 0);

		ItemStack cropRaspberry = getItemStack("edibles", 1, 2);
		ItemStack cropBlueberry = getItemStack("edibles", 1, 3);
		ItemStack cropBlackberry = getItemStack("edibles", 1, 4);
		ItemStack cropMaloberry = getItemStack("edibles", 1, 5);

		ItemStack cropBlightberry = getItemStack("edibles", 1, 6);
		ItemStack cropDuskberry = getItemStack("edibles", 1, 7);
		ItemStack cropSkyberry = getItemStack("edibles", 1, 8);
		ItemStack cropStingberry = getItemStack("edibles", 1, 9);

		ItemStack bushRaspberry = getItemStack("overworld_berrybush_raspberry", 1, 0);
		ItemStack bushBlueberry = getItemStack("overworld_berrybush_blueberry", 1, 0);
		ItemStack bushBlackberry = getItemStack("overworld_berrybush_blackberry", 1, 0);
		ItemStack bushMaloberry = getItemStack("overworld_berrybush_maloberry", 1, 0);

		ItemStack bushBlightberry = getItemStack("nether_berrybush_blightberry", 1, 0);
		ItemStack bushDuskberry = getItemStack("nether_berrybush_duskberry", 1, 0);
		ItemStack bushSkyberry = getItemStack("nether_berrybush_skyberry", 1, 0);
		ItemStack bushStingberry = getItemStack("nether_berrybush_stingberry", 1, 0);

		ItemStack saguaroFruit = getItemStack("saguaro_fruit_item");
		ItemStack saguaroCactus = getItemStack("saguaro", 1, 0);
		ItemStack saguaroCactusBaby = getItemStack("saguaro_baby", 1, 0);

		ItemStack plantThornvine = getItemStack("nether_thorn_vines", 1, 0);

		ItemStack glowshroomGreen = getItemStack("nether_glowshroom", 1, 0);
		ItemStack glowshroomBlue = getItemStack("nether_glowshroom", 1, 1);
		ItemStack glowshroomPurple = getItemStack("nether_glowshroom", 1, 2);

		ItemStack logMaple = getItemStack("overworld_logs", 1, 0);
		ItemStack logSilverbell = getItemStack("overworld_logs", 1, 1);
		ItemStack logAmaranth = getItemStack("overworld_logs", 1, 2);
		ItemStack logTigerwood = getItemStack("overworld_logs", 1, 3);
		ItemStack logWillow = getItemStack("overworld_logs2", 1, 0);
		ItemStack logEucalyptus = getItemStack("overworld_logs2", 1, 1);
		ItemStack logHopseed = getItemStack("overworld_logs2", 1, 2);
		ItemStack logSakura = getItemStack("overworld_logs2", 1, 3);
		ItemStack logRedwood = getItemStack("redwood_logs", 1, 1);

		ItemStack logGhostwood = getItemStack("nether_logs", 1, 0);
		ItemStack logFusewood = getItemStack("nether_logs", 1, 2);
		ItemStack logDarkwood = getItemStack("nether_logs", 1, 1);
		ItemStack logBloodwood = getItemStack("nether_logs2", 1, 0);

		ItemStack saplingMaple = getItemStack("overworld_sapling", 1, 0);
		ItemStack saplingSilverbell = getItemStack("overworld_sapling", 1, 1);
		ItemStack saplingAmaranth = getItemStack("overworld_sapling", 1, 2);
		ItemStack saplingTigerwood = getItemStack("overworld_sapling", 1, 3);
		ItemStack saplingWillow = getItemStack("overworld_sapling2", 1, 0);
		ItemStack saplingEucalyptus = getItemStack("overworld_sapling2", 1, 1);
		ItemStack saplingHopseed = getItemStack("overworld_sapling2", 1, 2);
		ItemStack saplingSakura = getItemStack("overworld_sapling2", 1, 3);

		ItemStack saplingGhostwood = getItemStack("nether_sapling", 1, 0);
		ItemStack saplingFusewood = getItemStack("nether_sapling", 1, 1);
		ItemStack saplingDarkwood = getItemStack("nether_sapling", 1, 2);
		ItemStack saplingBloodwood = getItemStack("nether_sapling2", 1, 0);

		ItemStack plankMaple = getItemStack("overworld_planks", 1, 0);
		ItemStack plankSilverbell = getItemStack("overworld_planks", 1, 1);
		ItemStack plankAmaranth = getItemStack("overworld_planks", 1, 2);
		ItemStack plankTigerwood = getItemStack("overworld_planks", 1, 3);
		ItemStack plankWillow = getItemStack("overworld_planks", 1, 4);
		ItemStack plankEucalyptus = getItemStack("overworld_planks", 1, 5);
		ItemStack plankHopseed = getItemStack("overworld_planks", 1, 6);
		ItemStack plankSakura = getItemStack("overworld_planks", 1, 7);
		ItemStack plankRedwood = getItemStack("overworld_planks", 1, 8);

		ItemStack plankGhostwood = getItemStack("nether_planks", 1, 0);
		ItemStack plankBloodwood = getItemStack("nether_planks", 1, 1);
		ItemStack plankDarkwood = getItemStack("nether_planks", 1, 2);
		ItemStack plankFusewood = getItemStack("nether_planks", 1, 3);

		ItemStack bookshelfMaple = getItemStack("overworld_bookshelves", 1, 0);
		ItemStack bookshelfSilverbell = getItemStack("overworld_bookshelves", 1, 1);
		ItemStack bookshelfAmaranth = getItemStack("overworld_bookshelves", 1, 2);
		ItemStack bookshelfTigerwood = getItemStack("overworld_bookshelves", 1, 3);
		ItemStack bookshelfWillow = getItemStack("overworld_bookshelves", 1, 4);
		ItemStack bookshelfEucalyptus = getItemStack("overworld_bookshelves", 1, 5);
		ItemStack bookshelfHopseed = getItemStack("overworld_bookshelves", 1, 6);
		ItemStack bookshelfSakura = getItemStack("overworld_bookshelves", 1, 7);
		ItemStack bookshelfRedwood = getItemStack("overworld_bookshelves", 1, 8);

		ItemStack bookshelfGhostwood = getItemStack("nether_bookshelves", 1, 0);
		ItemStack bookshelfBloodwood = getItemStack("nether_bookshelves", 1, 1);
		ItemStack bookshelfDarkwood = getItemStack("nether_bookshelves", 1, 2);
		ItemStack bookshelfFusewood = getItemStack("nether_bookshelves", 1, 3);

		ItemStack bowlGhostwood = getItemStack("empty_bowls", 1, 0);
		ItemStack bowlBloodwood = getItemStack("empty_bowls", 1, 1);
		ItemStack bowlDarkwood = getItemStack("empty_bowls", 1, 2);
		ItemStack bowlFusewood = getItemStack("empty_bowls", 1, 3);

		ItemStack workbenchMaple = getItemStack("overworld_workbenches", 1, 0);
		ItemStack workbenchSilverbell = getItemStack("overworld_workbenches", 1, 1);
		ItemStack workbenchAmaranth = getItemStack("overworld_workbenches", 1, 2);
		ItemStack workbenchTigerwood = getItemStack("overworld_workbenches", 1, 3);
		ItemStack workbenchWillow = getItemStack("overworld_workbenches", 1, 4);
		ItemStack workbenchEucalyptus = getItemStack("overworld_workbenches", 1, 5);
		ItemStack workbenchHopseed = getItemStack("overworld_workbenches", 1, 6);
		ItemStack workbenchSakura = getItemStack("overworld_workbenches", 1, 7);
		ItemStack workbenchRedwood = getItemStack("overworld_workbenches", 1, 8);

		ItemStack workbenchGhostwood = getItemStack("nether_workbenches", 1, 0);
		ItemStack workbenchBloodwood = getItemStack("nether_workbenches", 1, 1);
		ItemStack workbenchDarkwood = getItemStack("nether_workbenches", 1, 2);
		ItemStack workbenchFusewood = getItemStack("nether_workbenches", 1, 3);

		ItemStack buttonMaple = getItemStack("maple_button");
		ItemStack buttonSilverbell = getItemStack("silverbell_button");
		ItemStack buttonAmaranth = getItemStack("amaranth_button");
		ItemStack buttonTigerwood = getItemStack("tiger_button");
		ItemStack buttonWillow = getItemStack("willow_button");
		ItemStack buttonEucalyptus = getItemStack("eucalyptus_button");
		ItemStack buttonHopseed = getItemStack("hopseed_button");
		ItemStack buttonSakura = getItemStack("sakura_button");
		ItemStack buttonRedwood = getItemStack("redwood_button");

		ItemStack buttonGhostwood = getItemStack("ghostwood_button");
		ItemStack buttonBloodwood = getItemStack("bloodwood_button");
		ItemStack buttonDarkwood = getItemStack("darkwood_button");
		ItemStack buttonFusewood = getItemStack("fusewood_button");

		ItemStack doorEucalyptus = getItemStack("overworld_doors", 1, 0);
		ItemStack doorHopseed = getItemStack("overworld_doors", 1, 1);
		ItemStack doorSakura = getItemStack("overworld_doors", 1, 2);
		ItemStack doorRedwood = getItemStack("overworld_doors", 1, 3);

		ItemStack doorGhostwood = getItemStack("nether_doors", 1, 0);
		ItemStack doorBloodwood = getItemStack("nether_doors", 1, 1);

		ItemStack fenceMaple = getItemStack("maple_fence");
		ItemStack fenceSilverbell = getItemStack("silverbell_fence");
		ItemStack fenceAmaranth = getItemStack("amaranth_fence");
		ItemStack fenceTigerwood = getItemStack("tiger_fence");
		ItemStack fenceWillow = getItemStack("willow_fence");
		ItemStack fenceEucalyptus = getItemStack("eucalyptus_fence");
		ItemStack fenceHopseed = getItemStack("hopseed_fence");
		ItemStack fenceSakura = getItemStack("sakura_fence");
		ItemStack fenceRedwood = getItemStack("redwood_fence");

		ItemStack fenceGhostwood = getItemStack("ghostwood_fence");
		ItemStack fenceBloodwood = getItemStack("bloodwood_fence");
		ItemStack fenceDarkwood = getItemStack("darkwood_fence");
		ItemStack fenceFusewood = getItemStack("fusewood_fence");

		ItemStack fenceGateMaple = getItemStack("maple_fence_gate");
		ItemStack fenceGateSilverbell = getItemStack("silverbell_fence_gate");
		ItemStack fenceGateAmaranth = getItemStack("amaranth_fence_gate");
		ItemStack fenceGateTigerwood = getItemStack("tiger_fence_gate");
		ItemStack fenceGateWillow = getItemStack("willow_fence_gate");
		ItemStack fenceGateEucalyptus = getItemStack("eucalyptus_fence_gate");
		ItemStack fenceGateHopseed = getItemStack("hopseed_fence_gate");
		ItemStack fenceGateSakura = getItemStack("sakura_fence_gate");
		ItemStack fenceGateRedwood = getItemStack("redwood_fence_gate");

		ItemStack fenceGateGhostwood = getItemStack("ghostwood_fence_gate");
		ItemStack fenceGateBloodwood = getItemStack("bloodwood_fence_gate");
		ItemStack fenceGateDarkwood = getItemStack("darkwood_fence_gate");
		ItemStack fenceGateFusewood = getItemStack("fusewood_fence_gate");

		ItemStack pressurePlateMaple = getItemStack("maple_pressure_plate");
		ItemStack pressurePlateSilverbell = getItemStack("silverbell_pressure_plate");
		ItemStack pressurePlateAmaranth = getItemStack("amaranth_pressure_plate");
		ItemStack pressurePlateTigerwood = getItemStack("tiger_pressure_plate");
		ItemStack pressurePlateWillow = getItemStack("willow_pressure_plate");
		ItemStack pressurePlateEucalyptus = getItemStack("eucalyptus_pressure_plate");
		ItemStack pressurePlateHopseed = getItemStack("hopseed_pressure_plate");
		ItemStack pressurePlateSakura = getItemStack("sakura_pressure_plate");
		ItemStack pressurePlateRedwood = getItemStack("redwood_pressure_plate");

		ItemStack pressurePlateGhostwood = getItemStack("ghostwood_pressure_plate");
		ItemStack pressurePlateBloodwood = getItemStack("bloodwood_pressure_plate");
		ItemStack pressurePlateDarkwood = getItemStack("darkwood_pressure_plate");
		ItemStack pressurePlateFusewood = getItemStack("fusewood_pressure_plate");

		ItemStack stairsMaple = getItemStack("overworld_stairs_maple");
		ItemStack stairsSilverbell = getItemStack("overworld_stairs_silverbell");
		ItemStack stairsAmaranth = getItemStack("overworld_stairs_amaranth");
		ItemStack stairsTigerwood = getItemStack("overworld_stairs_tiger");
		ItemStack stairsWillow = getItemStack("overworld_stairs_willow");
		ItemStack stairsEucalyptus = getItemStack("overworld_stairs_eucalyptus");
		ItemStack stairsHopseed = getItemStack("overworld_stairs_hopseed");
		ItemStack stairsSakura = getItemStack("overworld_stairs_sakura");
		ItemStack stairsRedwood = getItemStack("overworld_stairs_redwood");

		ItemStack stairsGhostwood = getItemStack("nether_stairs_ghostwood");
		ItemStack stairsBloodwood = getItemStack("nether_stairs_bloodwood");
		ItemStack stairsDarkwood = getItemStack("nether_stairs_darkwood");
		ItemStack stairsFusewood = getItemStack("nether_stairs_fusewood");

		ItemStack trapDoorMaple = getItemStack("maple_trap_door");
		ItemStack trapDoorSilverbell = getItemStack("silverbell_trap_door");
		ItemStack trapDoorAmaranth = getItemStack("amaranth_trap_door");
		ItemStack trapDoorTigerwood = getItemStack("tiger_trap_door");
		ItemStack trapDoorWillow = getItemStack("willow_trap_door");
		ItemStack trapDoorEucalyptus = getItemStack("eucalyptus_trap_door");
		ItemStack trapDoorHopseed = getItemStack("hopseed_trap_door");
		ItemStack trapDoorSakura = getItemStack("sakura_trap_door");
		ItemStack trapDoorRedwood = getItemStack("redwood_trap_door");

		ItemStack trapDoorGhostwood = getItemStack("ghostwood_trap_door");
		ItemStack trapDoorBloodwood = getItemStack("bloodwood_trap_door");
		ItemStack trapDoorDarkwood = getItemStack("darkwood_trap_door");
		ItemStack trapDoorFusewood = getItemStack("fusewood_trap_door");

		Block blockLog = getBlock("overworld_logs");
		Block blockLog2 = getBlock("overworld_logs2");
		Block blockLogNether = getBlock("nether_logs");

		Block blockLeaves = getBlock("overworld_leaves");
		Block blockLeaves2 = getBlock("overworld_leaves2");
		Block blockLeavesNether = getBlock("nether_leaves");
		Block blockLeavesNether2 = getBlock("nether_leaves2");

		/* FURNACE */
		{
			int energy = FurnaceManager.DEFAULT_ENERGY;

			FurnaceManager.addRecipePyrolysis(energy, ItemHelper.cloneStack(saguaroCactus, 4), new ItemStack(Items.COAL, 1, 1), 50);
		}

		/* SAWMILL */
		{
			int energy = SawmillManager.DEFAULT_ENERGY;

			/* BOOKSHELVES */
			SawmillManager.addBookshelfRecipe(bookshelfMaple, plankMaple);
			SawmillManager.addBookshelfRecipe(bookshelfSilverbell, plankSilverbell);
			SawmillManager.addBookshelfRecipe(bookshelfAmaranth, plankAmaranth);
			SawmillManager.addBookshelfRecipe(bookshelfTigerwood, plankTigerwood);
			SawmillManager.addBookshelfRecipe(bookshelfWillow, plankWillow);
			SawmillManager.addBookshelfRecipe(bookshelfEucalyptus, plankEucalyptus);
			SawmillManager.addBookshelfRecipe(bookshelfHopseed, plankHopseed);
			SawmillManager.addBookshelfRecipe(bookshelfSakura, plankSakura);
			SawmillManager.addBookshelfRecipe(bookshelfRedwood, plankRedwood);

			SawmillManager.addBookshelfRecipe(bookshelfGhostwood, plankGhostwood);
			SawmillManager.addBookshelfRecipe(bookshelfBloodwood, plankBloodwood);
			SawmillManager.addBookshelfRecipe(bookshelfDarkwood, plankDarkwood);
			SawmillManager.addBookshelfRecipe(bookshelfFusewood, plankFusewood);

			/* BOWLS */
			SawmillManager.addButtonRecipe(bowlGhostwood, plankGhostwood);
			SawmillManager.addButtonRecipe(bowlBloodwood, plankBloodwood);
			SawmillManager.addButtonRecipe(bowlDarkwood, plankDarkwood);
			SawmillManager.addButtonRecipe(bowlFusewood, plankFusewood);

			/* BUTTONS */
			SawmillManager.addButtonRecipe(buttonMaple, plankMaple);
			SawmillManager.addButtonRecipe(buttonSilverbell, plankSilverbell);
			SawmillManager.addButtonRecipe(buttonAmaranth, plankAmaranth);
			SawmillManager.addButtonRecipe(buttonTigerwood, plankTigerwood);
			SawmillManager.addButtonRecipe(buttonWillow, plankWillow);
			SawmillManager.addButtonRecipe(buttonEucalyptus, plankEucalyptus);
			SawmillManager.addButtonRecipe(buttonHopseed, plankHopseed);
			SawmillManager.addButtonRecipe(buttonSakura, plankSakura);
			SawmillManager.addButtonRecipe(buttonRedwood, plankRedwood);

			SawmillManager.addButtonRecipe(buttonGhostwood, plankGhostwood);
			SawmillManager.addButtonRecipe(buttonBloodwood, plankBloodwood);
			SawmillManager.addButtonRecipe(buttonDarkwood, plankDarkwood);
			SawmillManager.addButtonRecipe(buttonFusewood, plankFusewood);

			/* DOORS */
			SawmillManager.addDoorRecipe(doorEucalyptus, plankEucalyptus);
			SawmillManager.addDoorRecipe(doorHopseed, plankHopseed);
			SawmillManager.addDoorRecipe(doorSakura, plankSakura);
			SawmillManager.addDoorRecipe(doorRedwood, plankRedwood);
			// SawmillManager.addDoorRecipe( doorRedwoodBark, plankEucalyptus);

			SawmillManager.addDoorRecipe(doorGhostwood, plankGhostwood);
			SawmillManager.addDoorRecipe(doorBloodwood, plankBloodwood);

			/* FENCES */
			SawmillManager.addFenceRecipe(fenceMaple, plankMaple);
			SawmillManager.addFenceRecipe(fenceSilverbell, plankSilverbell);
			SawmillManager.addFenceRecipe(fenceAmaranth, plankAmaranth);
			SawmillManager.addFenceRecipe(fenceTigerwood, plankTigerwood);
			SawmillManager.addFenceRecipe(fenceWillow, plankWillow);
			SawmillManager.addFenceRecipe(fenceEucalyptus, plankEucalyptus);
			SawmillManager.addFenceRecipe(fenceHopseed, plankHopseed);
			SawmillManager.addFenceRecipe(fenceSakura, plankSakura);
			SawmillManager.addFenceRecipe(fenceRedwood, plankRedwood);

			SawmillManager.addFenceRecipe(fenceGhostwood, plankGhostwood);
			SawmillManager.addFenceRecipe(fenceBloodwood, plankBloodwood);
			SawmillManager.addFenceRecipe(fenceDarkwood, plankDarkwood);
			SawmillManager.addFenceRecipe(fenceFusewood, plankFusewood);

			/* FENCE GATES */
			SawmillManager.addFenceGateRecipe(fenceGateMaple, plankMaple);
			SawmillManager.addFenceGateRecipe(fenceGateSilverbell, plankSilverbell);
			SawmillManager.addFenceGateRecipe(fenceGateAmaranth, plankAmaranth);
			SawmillManager.addFenceGateRecipe(fenceGateTigerwood, plankTigerwood);
			SawmillManager.addFenceGateRecipe(fenceGateWillow, plankWillow);
			SawmillManager.addFenceGateRecipe(fenceGateEucalyptus, plankEucalyptus);
			SawmillManager.addFenceGateRecipe(fenceGateHopseed, plankHopseed);
			SawmillManager.addFenceGateRecipe(fenceGateSakura, plankSakura);
			SawmillManager.addFenceGateRecipe(fenceGateRedwood, plankRedwood);

			SawmillManager.addFenceGateRecipe(fenceGateGhostwood, plankGhostwood);
			SawmillManager.addFenceGateRecipe(fenceGateBloodwood, plankBloodwood);
			SawmillManager.addFenceGateRecipe(fenceGateDarkwood, plankDarkwood);
			SawmillManager.addFenceGateRecipe(fenceGateFusewood, plankFusewood);

			/* PRESSURE PLATES */
			SawmillManager.addPressurePlateRecipe(pressurePlateMaple, plankMaple);
			SawmillManager.addPressurePlateRecipe(pressurePlateSilverbell, plankSilverbell);
			SawmillManager.addPressurePlateRecipe(pressurePlateAmaranth, plankAmaranth);
			SawmillManager.addPressurePlateRecipe(pressurePlateTigerwood, plankTigerwood);
			SawmillManager.addPressurePlateRecipe(pressurePlateWillow, plankWillow);
			SawmillManager.addPressurePlateRecipe(pressurePlateEucalyptus, plankEucalyptus);
			SawmillManager.addPressurePlateRecipe(pressurePlateHopseed, plankHopseed);
			SawmillManager.addPressurePlateRecipe(pressurePlateSakura, plankSakura);
			SawmillManager.addPressurePlateRecipe(pressurePlateRedwood, plankRedwood);

			SawmillManager.addPressurePlateRecipe(pressurePlateGhostwood, plankGhostwood);
			SawmillManager.addPressurePlateRecipe(pressurePlateBloodwood, plankBloodwood);
			SawmillManager.addPressurePlateRecipe(pressurePlateDarkwood, plankDarkwood);
			SawmillManager.addPressurePlateRecipe(pressurePlateFusewood, plankFusewood);

			/* STAIRS */
			SawmillManager.addStairsRecipe(stairsMaple, plankMaple);
			SawmillManager.addStairsRecipe(stairsSilverbell, plankSilverbell);
			SawmillManager.addStairsRecipe(stairsAmaranth, plankAmaranth);
			SawmillManager.addStairsRecipe(stairsTigerwood, plankTigerwood);
			SawmillManager.addStairsRecipe(stairsWillow, plankWillow);
			SawmillManager.addStairsRecipe(stairsEucalyptus, plankEucalyptus);
			SawmillManager.addStairsRecipe(stairsHopseed, plankHopseed);
			SawmillManager.addStairsRecipe(stairsSakura, plankSakura);
			SawmillManager.addStairsRecipe(stairsRedwood, plankRedwood);

			SawmillManager.addStairsRecipe(stairsGhostwood, plankGhostwood);
			SawmillManager.addStairsRecipe(stairsBloodwood, plankBloodwood);
			SawmillManager.addStairsRecipe(stairsDarkwood, plankDarkwood);
			SawmillManager.addStairsRecipe(stairsFusewood, plankFusewood);

			/* TRAPDOORS */
			SawmillManager.addTrapdoorRecipe(trapDoorMaple, plankMaple);
			SawmillManager.addTrapdoorRecipe(trapDoorSilverbell, plankSilverbell);
			SawmillManager.addTrapdoorRecipe(trapDoorAmaranth, plankAmaranth);
			SawmillManager.addTrapdoorRecipe(trapDoorTigerwood, plankTigerwood);
			SawmillManager.addTrapdoorRecipe(trapDoorWillow, plankWillow);
			SawmillManager.addTrapdoorRecipe(trapDoorEucalyptus, plankEucalyptus);
			SawmillManager.addTrapdoorRecipe(trapDoorHopseed, plankHopseed);
			SawmillManager.addTrapdoorRecipe(trapDoorSakura, plankSakura);
			SawmillManager.addTrapdoorRecipe(trapDoorRedwood, plankRedwood);

			SawmillManager.addTrapdoorRecipe(trapDoorGhostwood, plankGhostwood);
			SawmillManager.addTrapdoorRecipe(trapDoorBloodwood, plankBloodwood);
			SawmillManager.addTrapdoorRecipe(trapDoorDarkwood, plankDarkwood);
			SawmillManager.addTrapdoorRecipe(trapDoorFusewood, plankFusewood);

			/* WORKBENCHES */
			SawmillManager.addWorkbenchRecipe(workbenchMaple, plankMaple);
			SawmillManager.addWorkbenchRecipe(workbenchSilverbell, plankSilverbell);
			SawmillManager.addWorkbenchRecipe(workbenchAmaranth, plankAmaranth);
			SawmillManager.addWorkbenchRecipe(workbenchTigerwood, plankTigerwood);
			SawmillManager.addWorkbenchRecipe(workbenchWillow, plankWillow);
			SawmillManager.addWorkbenchRecipe(workbenchEucalyptus, plankEucalyptus);
			SawmillManager.addWorkbenchRecipe(workbenchHopseed, plankHopseed);
			SawmillManager.addWorkbenchRecipe(workbenchSakura, plankSakura);
			SawmillManager.addWorkbenchRecipe(workbenchRedwood, plankRedwood);

			SawmillManager.addWorkbenchRecipe(workbenchGhostwood, plankGhostwood);
			SawmillManager.addWorkbenchRecipe(workbenchBloodwood, plankBloodwood);
			SawmillManager.addWorkbenchRecipe(workbenchDarkwood, plankDarkwood);
			SawmillManager.addWorkbenchRecipe(workbenchFusewood, plankFusewood);

			/* BIOMASS */
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(cropBarley, 8), ItemMaterial.dustBiomass, seedBarley, 50);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(saguaroCactus, 4), ItemMaterial.dustBiomass);
		}

		/* INSOLATOR */
		{
			InsolatorManager.addDefaultRecipe(bushRaspberry, ItemHelper.cloneStack(cropRaspberry, 2), bushRaspberry, 100);
			InsolatorManager.addDefaultRecipe(bushBlueberry, ItemHelper.cloneStack(cropBlueberry, 2), bushBlueberry, 100);
			InsolatorManager.addDefaultRecipe(bushBlackberry, ItemHelper.cloneStack(cropBlackberry, 2), bushBlackberry, 100);
			InsolatorManager.addDefaultRecipe(bushMaloberry, ItemHelper.cloneStack(cropMaloberry, 2), bushMaloberry, 100);

			InsolatorManager.addDefaultRecipe(bushBlightberry, ItemHelper.cloneStack(cropBlightberry, 2), bushBlightberry, 100);
			InsolatorManager.addDefaultRecipe(bushDuskberry, ItemHelper.cloneStack(cropDuskberry, 2), bushDuskberry, 100);
			InsolatorManager.addDefaultRecipe(bushSkyberry, ItemHelper.cloneStack(cropSkyberry, 2), bushSkyberry, 100);
			InsolatorManager.addDefaultRecipe(bushStingberry, ItemHelper.cloneStack(cropStingberry, 2), bushStingberry, 100);

			InsolatorManager.addDefaultRecipe(saguaroFruit, saguaroCactusBaby, ItemStack.EMPTY, 0);
			InsolatorManager.addDefaultRecipe(saguaroCactusBaby, ItemHelper.cloneStack(saguaroFruit, 2), saguaroCactus, 100);

			InsolatorManager.addDefaultRecipe(plantThornvine, ItemHelper.cloneStack(plantThornvine, 2), ItemStack.EMPTY, 0);

			InsolatorManager.addDefaultRecipe(glowshroomGreen, ItemHelper.cloneStack(glowshroomGreen, 2), ItemStack.EMPTY, 0);
			InsolatorManager.addDefaultRecipe(glowshroomBlue, ItemHelper.cloneStack(glowshroomBlue, 2), ItemStack.EMPTY, 0);
			InsolatorManager.addDefaultRecipe(glowshroomPurple, ItemHelper.cloneStack(glowshroomPurple, 2), ItemStack.EMPTY, 0);

			InsolatorManager.addDefaultTreeRecipe(saplingMaple, ItemHelper.cloneStack(logMaple, 6), saplingMaple);
			InsolatorManager.addDefaultTreeRecipe(saplingSilverbell, ItemHelper.cloneStack(logSilverbell, 6), saplingSilverbell);
			InsolatorManager.addDefaultTreeRecipe(saplingAmaranth, ItemHelper.cloneStack(logAmaranth, 6), saplingAmaranth);
			InsolatorManager.addDefaultTreeRecipe(saplingTigerwood, ItemHelper.cloneStack(logTigerwood, 6), saplingTigerwood);

			InsolatorManager.addDefaultTreeRecipe(saplingWillow, ItemHelper.cloneStack(logWillow, 6), saplingWillow);
			InsolatorManager.addDefaultTreeRecipe(saplingEucalyptus, ItemHelper.cloneStack(logEucalyptus, 6), saplingEucalyptus);
			InsolatorManager.addDefaultTreeRecipe(saplingHopseed, ItemHelper.cloneStack(logHopseed, 6), saplingHopseed);
			InsolatorManager.addDefaultTreeRecipe(saplingSakura, ItemHelper.cloneStack(logSakura, 6), saplingSakura);

			InsolatorManager.addDefaultTreeRecipe(saplingGhostwood, ItemHelper.cloneStack(logGhostwood, 6), saplingGhostwood);
			InsolatorManager.addDefaultTreeRecipe(saplingFusewood, ItemHelper.cloneStack(logFusewood, 6), saplingFusewood);
			InsolatorManager.addDefaultTreeRecipe(saplingDarkwood, ItemHelper.cloneStack(logDarkwood, 6), saplingDarkwood);
			InsolatorManager.addDefaultTreeRecipe(saplingBloodwood, ItemHelper.cloneStack(logBloodwood, 6), saplingBloodwood);
		}

		/* TRANSPOSER */
		{
			TransposerManager.addExtractRecipe(800, seedBarley, ItemStack.EMPTY, new FluidStack(TFFluids.fluidSeedOil, 50), 0, false);
			TransposerManager.addExtractRecipe(800, seedCotton, ItemStack.EMPTY, new FluidStack(TFFluids.fluidSeedOil, 50), 0, false);
		}

		/* TAPPER */
		{
			TapperManager.addStandardMapping(logMaple, new FluidStack(TFFluids.fluidSap, 100));
			TapperManager.addStandardMapping(logSilverbell, new FluidStack(TFFluids.fluidResin, 50));
			TapperManager.addStandardMapping(logAmaranth, new FluidStack(TFFluids.fluidResin, 50));
			TapperManager.addStandardMapping(logTigerwood, new FluidStack(TFFluids.fluidResin, 50));

			TapperManager.addStandardMapping(logWillow, new FluidStack(TFFluids.fluidResin, 50));
			TapperManager.addStandardMapping(logEucalyptus, new FluidStack(TFFluids.fluidResin, 50));
			TapperManager.addStandardMapping(logHopseed, new FluidStack(TFFluids.fluidResin, 50));
			TapperManager.addStandardMapping(logSakura, new FluidStack(TFFluids.fluidSap, 50));

			TapperManager.addStandardMapping(logGhostwood, new FluidStack(FluidRegistry.LAVA, 25));
			TapperManager.addStandardMapping(logFusewood, new FluidStack(FluidRegistry.LAVA, 25));
			TapperManager.addStandardMapping(logDarkwood, new FluidStack(FluidRegistry.LAVA, 25));

			addLeafMapping(blockLog, 0, blockLeaves, 0);
			addLeafMapping(blockLog, 1, blockLeaves, 1);
			addLeafMapping(blockLog, 2, blockLeaves, 2);
			addLeafMapping(blockLog, 3, blockLeaves, 3);

			addLeafMapping(blockLog2, 0, blockLeaves2, 0);
			addLeafMapping(blockLog2, 1, blockLeaves2, 1);
			addLeafMapping(blockLog2, 2, blockLeaves2, 2);
			addLeafMapping(blockLog2, 3, blockLeaves2, 3);

			addLeafMapping(blockLogNether, 0, blockLeavesNether, 0);
			addLeafMapping(blockLogNether, 2, blockLeavesNether, 2);
			addLeafMapping(blockLogNether, 1, blockLeavesNether2, 0);
		}
	}

}
