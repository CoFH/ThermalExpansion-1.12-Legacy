package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.managers.device.TapperManager;
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
		ItemStack buttonTigerwood = getItemStack("tigerwood_button");
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
		ItemStack fenceTigerwood = getItemStack("tigerwood_fence");
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
		ItemStack fenceGateTigerwood = getItemStack("tigerwood_fence_gate");
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
		ItemStack pressurePlateTigerwood = getItemStack("tigerwood_pressure_plate");
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
		ItemStack stairsTigerwood = getItemStack("overworld_stairs_tigerwood");
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
		ItemStack trapDoorTigerwood = getItemStack("tigerwood_trap_door");
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

		/* SAWMILL */
		{
			int energy = SawmillManager.DEFAULT_ENERGY;

			/* BOOKSHELVES */
			SawmillManager.addRecipe(energy, bookshelfMaple, ItemHelper.cloneStack(plankMaple, 4), new ItemStack(Items.BOOK, 3), 25);
			SawmillManager.addRecipe(energy, bookshelfSilverbell, ItemHelper.cloneStack(plankSilverbell, 4), new ItemStack(Items.BOOK, 3), 25);
			SawmillManager.addRecipe(energy, bookshelfAmaranth, ItemHelper.cloneStack(plankAmaranth, 4), new ItemStack(Items.BOOK, 3), 25);
			SawmillManager.addRecipe(energy, bookshelfTigerwood, ItemHelper.cloneStack(plankTigerwood, 4), new ItemStack(Items.BOOK, 3), 25);
			SawmillManager.addRecipe(energy, bookshelfWillow, ItemHelper.cloneStack(plankWillow, 4), new ItemStack(Items.BOOK, 3), 25);
			SawmillManager.addRecipe(energy, bookshelfEucalyptus, ItemHelper.cloneStack(plankEucalyptus, 4), new ItemStack(Items.BOOK, 3), 25);
			SawmillManager.addRecipe(energy, bookshelfHopseed, ItemHelper.cloneStack(plankHopseed, 4), new ItemStack(Items.BOOK, 3), 25);
			SawmillManager.addRecipe(energy, bookshelfSakura, ItemHelper.cloneStack(plankSakura, 4), new ItemStack(Items.BOOK, 3), 25);
			SawmillManager.addRecipe(energy, bookshelfRedwood, ItemHelper.cloneStack(plankRedwood, 4), new ItemStack(Items.BOOK, 3), 25);

			SawmillManager.addRecipe(energy, bookshelfGhostwood, ItemHelper.cloneStack(plankGhostwood, 4), new ItemStack(Items.BOOK, 3), 25);
			SawmillManager.addRecipe(energy, bookshelfBloodwood, ItemHelper.cloneStack(plankBloodwood, 4), new ItemStack(Items.BOOK, 3), 25);
			SawmillManager.addRecipe(energy, bookshelfDarkwood, ItemHelper.cloneStack(plankDarkwood, 4), new ItemStack(Items.BOOK, 3), 25);
			SawmillManager.addRecipe(energy, bookshelfFusewood, ItemHelper.cloneStack(plankFusewood, 4), new ItemStack(Items.BOOK, 3), 25);

			/* WORKBENCHES */
			SawmillManager.addRecipe(energy, workbenchMaple, ItemHelper.cloneStack(plankMaple, 2), ItemMaterial.dustWood);
			SawmillManager.addRecipe(energy, workbenchSilverbell, ItemHelper.cloneStack(plankSilverbell, 2), ItemMaterial.dustWood);
			SawmillManager.addRecipe(energy, workbenchAmaranth, ItemHelper.cloneStack(plankAmaranth, 2), ItemMaterial.dustWood);
			SawmillManager.addRecipe(energy, workbenchTigerwood, ItemHelper.cloneStack(plankTigerwood, 2), ItemMaterial.dustWood);
			SawmillManager.addRecipe(energy, workbenchWillow, ItemHelper.cloneStack(plankWillow, 2), ItemMaterial.dustWood);
			SawmillManager.addRecipe(energy, workbenchEucalyptus, ItemHelper.cloneStack(plankEucalyptus, 2), ItemMaterial.dustWood);
			SawmillManager.addRecipe(energy, workbenchHopseed, ItemHelper.cloneStack(plankHopseed, 2), ItemMaterial.dustWood);
			SawmillManager.addRecipe(energy, workbenchSakura, ItemHelper.cloneStack(plankSakura, 2), ItemMaterial.dustWood);
			SawmillManager.addRecipe(energy, workbenchRedwood, ItemHelper.cloneStack(plankRedwood, 2), ItemMaterial.dustWood);

			SawmillManager.addRecipe(energy, workbenchGhostwood, ItemHelper.cloneStack(plankGhostwood, 2), ItemMaterial.dustWood);
			SawmillManager.addRecipe(energy, workbenchBloodwood, ItemHelper.cloneStack(plankBloodwood, 2), ItemMaterial.dustWood);
			SawmillManager.addRecipe(energy, workbenchDarkwood, ItemHelper.cloneStack(plankDarkwood, 2), ItemMaterial.dustWood);
			SawmillManager.addRecipe(energy, workbenchFusewood, ItemHelper.cloneStack(plankFusewood, 2), ItemMaterial.dustWood);

			/* BOWLS */
			SawmillManager.addRecipe(energy / 2, ItemHelper.cloneStack(bowlGhostwood, 2), plankGhostwood, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy / 2, ItemHelper.cloneStack(bowlBloodwood, 2), plankBloodwood, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy / 2, ItemHelper.cloneStack(bowlDarkwood, 2), plankDarkwood, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy / 2, ItemHelper.cloneStack(bowlFusewood, 2), plankFusewood, ItemMaterial.dustWood, 25);

			/* BUTTONS */
			SawmillManager.addRecipe(energy / 2, ItemHelper.cloneStack(buttonMaple, 2), plankMaple, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy / 2, ItemHelper.cloneStack(buttonSilverbell, 2), plankSilverbell, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy / 2, ItemHelper.cloneStack(buttonAmaranth, 2), plankAmaranth, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy / 2, ItemHelper.cloneStack(buttonTigerwood, 2), plankTigerwood, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy / 2, ItemHelper.cloneStack(buttonWillow, 2), plankWillow, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy / 2, ItemHelper.cloneStack(buttonEucalyptus, 2), plankEucalyptus, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy / 2, ItemHelper.cloneStack(buttonHopseed, 2), plankHopseed, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy / 2, ItemHelper.cloneStack(buttonSakura, 2), plankSakura, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy / 2, ItemHelper.cloneStack(buttonRedwood, 2), plankRedwood, ItemMaterial.dustWood, 25);

			SawmillManager.addRecipe(energy / 2, ItemHelper.cloneStack(buttonGhostwood, 2), plankGhostwood, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy / 2, ItemHelper.cloneStack(buttonBloodwood, 2), plankBloodwood, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy / 2, ItemHelper.cloneStack(buttonDarkwood, 2), plankDarkwood, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy / 2, ItemHelper.cloneStack(buttonFusewood, 2), plankFusewood, ItemMaterial.dustWood, 25);

			/* DOORS */
			SawmillManager.addRecipe(energy, doorEucalyptus, plankEucalyptus, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, doorHopseed, plankHopseed, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, doorSakura, plankSakura, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, doorRedwood, plankRedwood, ItemMaterial.dustWood, 50);
			// SawmillManager.addRecipe(energy, doorRedwoodBark, plankEucalyptus, ItemMaterial.dustWood, 50);

			SawmillManager.addRecipe(energy, doorGhostwood, plankGhostwood, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, doorBloodwood, plankBloodwood, ItemMaterial.dustWood, 50);

			/* FENCES */
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(fenceMaple, 2), plankMaple, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(fenceSilverbell, 2), plankSilverbell, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(fenceAmaranth, 2), plankAmaranth, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(fenceTigerwood, 2), plankTigerwood, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(fenceWillow, 2), plankWillow, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(fenceEucalyptus, 2), plankEucalyptus, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(fenceHopseed, 2), plankHopseed, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(fenceSakura, 2), plankSakura, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(fenceRedwood, 2), plankRedwood, ItemMaterial.dustWood, 25);

			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(fenceGhostwood, 2), plankGhostwood, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(fenceBloodwood, 2), plankBloodwood, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(fenceDarkwood, 2), plankDarkwood, ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(fenceFusewood, 2), plankFusewood, ItemMaterial.dustWood, 25);

			/* FENCE GATES */
			SawmillManager.addRecipe(energy, fenceGateMaple, plankMaple, ItemMaterial.dustWood, 125);
			SawmillManager.addRecipe(energy, fenceGateSilverbell, plankSilverbell, ItemMaterial.dustWood, 125);
			SawmillManager.addRecipe(energy, fenceGateAmaranth, plankAmaranth, ItemMaterial.dustWood, 125);
			SawmillManager.addRecipe(energy, fenceGateTigerwood, plankTigerwood, ItemMaterial.dustWood, 125);
			SawmillManager.addRecipe(energy, fenceGateWillow, plankWillow, ItemMaterial.dustWood, 125);
			SawmillManager.addRecipe(energy, fenceGateEucalyptus, plankEucalyptus, ItemMaterial.dustWood, 125);
			SawmillManager.addRecipe(energy, fenceGateHopseed, plankHopseed, ItemMaterial.dustWood, 125);
			SawmillManager.addRecipe(energy, fenceGateSakura, plankSakura, ItemMaterial.dustWood, 125);
			SawmillManager.addRecipe(energy, fenceGateRedwood, plankRedwood, ItemMaterial.dustWood, 125);

			SawmillManager.addRecipe(energy, fenceGateGhostwood, plankGhostwood, ItemMaterial.dustWood, 125);
			SawmillManager.addRecipe(energy, fenceGateBloodwood, plankBloodwood, ItemMaterial.dustWood, 125);
			SawmillManager.addRecipe(energy, fenceGateDarkwood, plankDarkwood, ItemMaterial.dustWood, 125);
			SawmillManager.addRecipe(energy, fenceGateFusewood, plankFusewood, ItemMaterial.dustWood, 125);

			/* PRESSURE PLATES */
			SawmillManager.addRecipe(energy, pressurePlateMaple, plankMaple, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, pressurePlateSilverbell, plankSilverbell, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, pressurePlateAmaranth, plankAmaranth, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, pressurePlateTigerwood, plankTigerwood, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, pressurePlateWillow, plankWillow, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, pressurePlateEucalyptus, plankEucalyptus, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, pressurePlateHopseed, plankHopseed, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, pressurePlateSakura, plankSakura, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, pressurePlateRedwood, plankRedwood, ItemMaterial.dustWood, 50);

			SawmillManager.addRecipe(energy, pressurePlateGhostwood, plankGhostwood, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, pressurePlateBloodwood, plankBloodwood, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, pressurePlateDarkwood, plankDarkwood, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, pressurePlateFusewood, plankFusewood, ItemMaterial.dustWood, 50);

			/* STAIRS */
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(stairsMaple, 2), plankMaple, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(stairsSilverbell, 2), plankSilverbell, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(stairsAmaranth, 2), plankAmaranth, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(stairsTigerwood, 2), plankTigerwood, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(stairsWillow, 2), plankWillow, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(stairsEucalyptus, 2), plankEucalyptus, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(stairsHopseed, 2), plankHopseed, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(stairsSakura, 2), plankSakura, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(stairsRedwood, 2), plankRedwood, ItemMaterial.dustWood, 50);

			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(stairsGhostwood, 2), plankGhostwood, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(stairsBloodwood, 2), plankBloodwood, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(stairsDarkwood, 2), plankDarkwood, ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(stairsFusewood, 2), plankFusewood, ItemMaterial.dustWood, 50);

			/* TRAPDOORS */
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(trapDoorMaple, 2), plankMaple, ItemMaterial.dustWood, 75);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(trapDoorSilverbell, 2), plankSilverbell, ItemMaterial.dustWood, 75);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(trapDoorAmaranth, 2), plankAmaranth, ItemMaterial.dustWood, 75);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(trapDoorTigerwood, 2), plankTigerwood, ItemMaterial.dustWood, 75);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(trapDoorWillow, 2), plankWillow, ItemMaterial.dustWood, 75);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(trapDoorEucalyptus, 2), plankEucalyptus, ItemMaterial.dustWood, 75);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(trapDoorHopseed, 2), plankHopseed, ItemMaterial.dustWood, 75);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(trapDoorSakura, 2), plankSakura, ItemMaterial.dustWood, 75);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(trapDoorRedwood, 2), plankRedwood, ItemMaterial.dustWood, 75);

			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(trapDoorGhostwood, 2), plankGhostwood, ItemMaterial.dustWood, 75);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(trapDoorBloodwood, 2), plankBloodwood, ItemMaterial.dustWood, 75);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(trapDoorDarkwood, 2), plankDarkwood, ItemMaterial.dustWood, 75);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(trapDoorFusewood, 2), plankFusewood, ItemMaterial.dustWood, 75);
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
			InsolatorManager.addDefaultRecipe(saguaroCactusBaby, ItemHelper.cloneStack(saguaroFruit, 2), saguaroCactus, 0);

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
