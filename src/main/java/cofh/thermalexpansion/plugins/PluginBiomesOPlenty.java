package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.managers.device.TapperManager;
import cofh.thermalexpansion.util.managers.machine.ExtruderManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import cofh.thermalexpansion.util.managers.machine.SawmillManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class PluginBiomesOPlenty extends PluginTEBase {

	public static final String MOD_ID = "biomesoplenty";
	public static final String MOD_NAME = "Biomes O' Plenty";

	public PluginBiomesOPlenty() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void registerDelegate() {

		ItemStack sandWhite = getItemStack("white_sand", 1, 0);
		ItemStack sandStoneWhite = getItemStack("white_sandstone", 1, 0);

		ItemStack logYellowAutumn = new ItemStack(Blocks.LOG, 1, 2);
		ItemStack logOrangeAutumn = new ItemStack(Blocks.LOG2, 1, 1);
		ItemStack logBamboo = getItemStack("bamboo", 1, 0);
		ItemStack logMagic = getItemStack("log_1", 1, 5);
		ItemStack logUmbral = getItemStack("log_0", 1, 6);
		// Dead
		ItemStack logFir = getItemStack("log_0", 1, 7);
		// Ethereal

		ItemStack logOrigin = new ItemStack(Blocks.LOG, 1, 0);
		ItemStack logCherry = getItemStack("log_0", 1, 5);
		// White Cherry
		ItemStack logMaple = new ItemStack(Blocks.LOG, 1, 0);
		ItemStack logHellbark = getItemStack("log_2", 1, 7);
		ItemStack logFloweringOak = new ItemStack(Blocks.LOG, 1, 0);
		ItemStack logJacaranda = getItemStack("log_3", 1, 4);
		ItemStack logSacredOak = getItemStack("log_0", 1, 4);

		ItemStack logMangrove = getItemStack("log_1", 1, 6);
		ItemStack logPalm = getItemStack("log_1", 1, 7);
		ItemStack logRedwood = getItemStack("log_2", 1, 4);
		ItemStack logWillow = getItemStack("log_2", 1, 5);
		ItemStack logPine = getItemStack("log_2", 1, 6);
		ItemStack logMahogany = getItemStack("log_3", 1, 5);
		ItemStack logEbony = getItemStack("log_3", 1, 6);
		ItemStack logEucalyptus = getItemStack("log_3", 1, 7);

		ItemStack saplingYellowAutumn = getItemStack("sapling_0", 1, 0);
		ItemStack saplingOrangeAutumn = getItemStack("sapling_0", 1, 1);
		ItemStack saplingBamboo = getItemStack("sapling_0", 1, 2);
		ItemStack saplingMagic = getItemStack("sapling_0", 1, 3);
		ItemStack saplingUmbran = getItemStack("sapling_0", 1, 4);
		ItemStack saplingDead = getItemStack("sapling_0", 1, 5);
		ItemStack saplingFir = getItemStack("sapling_0", 1, 6);
		ItemStack saplingEthereal = getItemStack("sapling_0", 1, 7);

		ItemStack saplingOrigin = getItemStack("sapling_1", 1, 0);
		ItemStack saplingPinkCherry = getItemStack("sapling_1", 1, 1);
		ItemStack saplingWhiteCherry = getItemStack("sapling_1", 1, 2);
		ItemStack saplingMaple = getItemStack("sapling_1", 1, 3);
		ItemStack saplingHellback = getItemStack("sapling_1", 1, 4);
		ItemStack saplingFloweringOak = getItemStack("sapling_1", 1, 5);
		ItemStack saplingJacaranda = getItemStack("sapling_1", 1, 6);
		ItemStack saplingSacredOak = getItemStack("sapling_1", 1, 7);

		ItemStack saplingMangrove = getItemStack("sapling_2", 1, 0);
		ItemStack saplingPalm = getItemStack("sapling_2", 1, 1);
		ItemStack saplingRedwood = getItemStack("sapling_2", 1, 2);
		ItemStack saplingWillow = getItemStack("sapling_2", 1, 3);
		ItemStack saplingPine = getItemStack("sapling_2", 1, 4);
		ItemStack saplingMahogany = getItemStack("sapling_2", 1, 5);
		ItemStack saplingEbony = getItemStack("sapling_2", 1, 6);
		ItemStack saplingEucalyptus = getItemStack("sapling_2", 1, 7);

		Block bamboo = getBlock("bamboo");
		Block blockLog0 = getBlock("log_0");
		Block blockLog1 = getBlock("log_1");
		Block blockLog2 = getBlock("log_2");
		Block blockLog3 = getBlock("log_3");

		Block blockLeaves0 = getBlock("leaves_0");
		Block blockLeaves1 = getBlock("leaves_1");
		Block blockLeaves2 = getBlock("leaves_2");
		Block blockLeaves3 = getBlock("leaves_3");
		Block blockLeaves4 = getBlock("leaves_4");
		Block blockLeaves5 = getBlock("leaves_5");

		/* PULVERIZER */
		{
			int energy = PulverizerManager.DEFAULT_ENERGY * 3 / 4;

			for (int i = 0; i < 3; i++) {
				PulverizerManager.addRecipe(energy, new ItemStack(getBlock("white_sandstone"), 1, i), ItemHelper.cloneStack(sandWhite, 2), ItemMaterial.dustNiter, 40);
			}
			PulverizerManager.addRecipe(energy, new ItemStack(getBlock("white_sandstone_stairs")), ItemHelper.cloneStack(sandWhite, 2), ItemMaterial.dustNiter, 20);
			PulverizerManager.addRecipe(energy, new ItemStack(getBlock("other_slab"), 1, 1), ItemHelper.cloneStack(sandWhite, 1), ItemMaterial.dustNiter, 20);

			/* PLANTS */
			energy = PulverizerManager.DEFAULT_ENERGY / 2;

			PulverizerManager.addRecipe(energy, getItemStack("flower_0", 1, 11), new ItemStack(Items.DYE, 4, 1));
			PulverizerManager.addRecipe(energy, getItemStack("flower_1", 1, 5), new ItemStack(Items.DYE, 4, 1));
			PulverizerManager.addRecipe(energy, getItemStack("flower_0", 1, 8), new ItemStack(Items.DYE, 4, 5));
			PulverizerManager.addRecipe(energy, getItemStack("flower_1", 1, 0), new ItemStack(Items.DYE, 4, 5));
			PulverizerManager.addRecipe(energy, getItemStack("flower_0", 1, 1), new ItemStack(Items.DYE, 4, 6));
			PulverizerManager.addRecipe(energy, getItemStack("flower_0", 1, 3), new ItemStack(Items.DYE, 4, 6));
			PulverizerManager.addRecipe(energy, getItemStack("flower_0", 1, 0), new ItemStack(Items.DYE, 4, 7));
			PulverizerManager.addRecipe(energy, getItemStack("flower_0", 1, 12), new ItemStack(Items.DYE, 4, 8));
			PulverizerManager.addRecipe(energy, getItemStack("flower_0", 1, 6), new ItemStack(Items.DYE, 4, 9));
			PulverizerManager.addRecipe(energy, getItemStack("flower_0", 1, 13), new ItemStack(Items.DYE, 4, 9));
			PulverizerManager.addRecipe(energy, getItemStack("flower_1", 1, 3), new ItemStack(Items.DYE, 4, 9));
			PulverizerManager.addRecipe(energy, getItemStack("mushroom", 1, 3), new ItemStack(Items.DYE, 4, 10));
			PulverizerManager.addRecipe(energy, getItemStack("flower_1", 1, 1), new ItemStack(Items.DYE, 4, 11));
			PulverizerManager.addRecipe(energy, getItemStack("flower_0", 1, 4), new ItemStack(Items.DYE, 4, 12));
			PulverizerManager.addRecipe(energy, getItemStack("flower_1", 1, 4), new ItemStack(Items.DYE, 4, 12));
			PulverizerManager.addRecipe(energy, getItemStack("double_plant", 1, 0), new ItemStack(Items.DYE, 4, 12));
			PulverizerManager.addRecipe(energy, getItemStack("flower_0", 1, 7), new ItemStack(Items.DYE, 4, 13));
			PulverizerManager.addRecipe(energy, getItemStack("flower_0", 1, 5), new ItemStack(Items.DYE, 4, 14));
			PulverizerManager.addRecipe(energy, getItemStack("flower_0", 1, 15), new ItemStack(Items.DYE, 4, 14));
			PulverizerManager.addRecipe(energy, getItemStack("flower_1", 1, 2), getItemStack("blue_dye", 4));
			PulverizerManager.addRecipe(energy, getItemStack("mushroom", 1, 2), getItemStack("blue_dye", 4));
			PulverizerManager.addRecipe(energy, getItemStack("plant_1", 1, 4), getItemStack("brown_dye", 4));
			PulverizerManager.addRecipe(energy, getItemStack("double_plant", 1, 1), getItemStack("brown_dye", 4));
			PulverizerManager.addRecipe(energy, getItemStack("mushroom", 1, 4), getItemStack("brown_dye", 4));
			PulverizerManager.addRecipe(energy, getItemStack("pinecone", 1, 0), getItemStack("brown_dye", 4));
			PulverizerManager.addRecipe(energy, getItemStack("flower_0", 1, 9), getItemStack("white_dye", 4));
			PulverizerManager.addRecipe(energy, getItemStack("flower_0", 1, 14), getItemStack("white_dye", 4));
			PulverizerManager.addRecipe(energy, getItemStack("flower_0", 1, 2), getItemStack("black_dye", 4));
			PulverizerManager.addRecipe(energy, getItemStack("flower_0", 1, 10), getItemStack("black_dye", 4));
		}

		/* SAWMILL */
		{
			int energy = SawmillManager.DEFAULT_ENERGY * 3 / 2;

			/* DOORS */
			SawmillManager.addRecipe(energy, getItemStack("sacred_oak_door"), getItemStack("planks_0", 1, 0), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("cherry_door"), getItemStack("planks_0", 1, 1), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("umbran_door"), getItemStack("planks_0", 1, 2), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("fir_door"), getItemStack("planks_0", 1, 3), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("ethereal_door"), getItemStack("planks_0", 1, 4), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("magic_door"), getItemStack("planks_0", 1, 5), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("mangrove_door"), getItemStack("planks_0", 1, 6), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("palm_door"), getItemStack("planks_0", 1, 7), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("redwood_door"), getItemStack("planks_0", 1, 8), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("willow_door"), getItemStack("planks_0", 1, 9), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("pine_door"), getItemStack("planks_0", 1, 10), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("hellbark_door"), getItemStack("planks_0", 1, 11), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("jacaranda_door"), getItemStack("planks_0", 1, 12), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("mahogany_door"), getItemStack("planks_0", 1, 13), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("ebony_door"), getItemStack("planks_0", 1, 14), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("eucalyptus_door"), getItemStack("planks_0", 1, 15), ItemMaterial.dustWood, 50);

			/* FENCES */
			SawmillManager.addRecipe(energy, getItemStack("sacred_oak_fence"), getItemStack("planks_0", 1, 0), ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, getItemStack("cherry_fence"), getItemStack("planks_0", 1, 1), ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, getItemStack("umbran_fence"), getItemStack("planks_0", 1, 2), ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, getItemStack("fir_fence"), getItemStack("planks_0", 1, 3), ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, getItemStack("ethereal_fence"), getItemStack("planks_0", 1, 4), ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, getItemStack("magic_fence"), getItemStack("planks_0", 1, 5), ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, getItemStack("mangrove_fence"), getItemStack("planks_0", 1, 6), ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, getItemStack("palm_fence"), getItemStack("planks_0", 1, 7), ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, getItemStack("redwood_fence"), getItemStack("planks_0", 1, 8), ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, getItemStack("willow_fence"), getItemStack("planks_0", 1, 9), ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, getItemStack("pine_fence"), getItemStack("planks_0", 1, 10), ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, getItemStack("hellbark_fence"), getItemStack("planks_0", 1, 11), ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, getItemStack("jacaranda_fence"), getItemStack("planks_0", 1, 12), ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, getItemStack("mahogany_fence"), getItemStack("planks_0", 1, 13), ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, getItemStack("ebony_fence"), getItemStack("planks_0", 1, 14), ItemMaterial.dustWood, 25);
			SawmillManager.addRecipe(energy, getItemStack("eucalyptus_fence"), getItemStack("planks_0", 1, 15), ItemMaterial.dustWood, 25);

			/* FENCE GATES */
			SawmillManager.addRecipe(energy, getItemStack("sacred_oak_fence_gate"), getItemStack("planks_0", 1, 0), ItemMaterial.dustWood, 150);
			SawmillManager.addRecipe(energy, getItemStack("cherry_fence_gate"), getItemStack("planks_0", 1, 1), ItemMaterial.dustWood, 150);
			SawmillManager.addRecipe(energy, getItemStack("umbran_fence_gate"), getItemStack("planks_0", 1, 2), ItemMaterial.dustWood, 150);
			SawmillManager.addRecipe(energy, getItemStack("fir_fence_gate"), getItemStack("planks_0", 1, 3), ItemMaterial.dustWood, 150);
			SawmillManager.addRecipe(energy, getItemStack("ethereal_fence_gate"), getItemStack("planks_0", 1, 4), ItemMaterial.dustWood, 150);
			SawmillManager.addRecipe(energy, getItemStack("magic_fence_gate"), getItemStack("planks_0", 1, 5), ItemMaterial.dustWood, 150);
			SawmillManager.addRecipe(energy, getItemStack("mangrove_fence_gate"), getItemStack("planks_0", 1, 6), ItemMaterial.dustWood, 150);
			SawmillManager.addRecipe(energy, getItemStack("palm_fence_gate"), getItemStack("planks_0", 1, 7), ItemMaterial.dustWood, 150);
			SawmillManager.addRecipe(energy, getItemStack("redwood_fence_gate"), getItemStack("planks_0", 1, 8), ItemMaterial.dustWood, 150);
			SawmillManager.addRecipe(energy, getItemStack("willow_fence_gate"), getItemStack("planks_0", 1, 9), ItemMaterial.dustWood, 150);
			SawmillManager.addRecipe(energy, getItemStack("pine_fence_gate"), getItemStack("planks_0", 1, 10), ItemMaterial.dustWood, 150);
			SawmillManager.addRecipe(energy, getItemStack("hellbark_fence_gate"), getItemStack("planks_0", 1, 11), ItemMaterial.dustWood, 150);
			SawmillManager.addRecipe(energy, getItemStack("jacaranda_fence_gate"), getItemStack("planks_0", 1, 12), ItemMaterial.dustWood, 150);
			SawmillManager.addRecipe(energy, getItemStack("mahogany_fence_gate"), getItemStack("planks_0", 1, 13), ItemMaterial.dustWood, 150);
			SawmillManager.addRecipe(energy, getItemStack("ebony_fence_gate"), getItemStack("planks_0", 1, 14), ItemMaterial.dustWood, 150);
			SawmillManager.addRecipe(energy, getItemStack("eucalyptus_fence_gate"), getItemStack("planks_0", 1, 15), ItemMaterial.dustWood, 150);

			/* STAIRS */
			SawmillManager.addRecipe(energy, getItemStack("sacred_oak_stairs", 2), getItemStack("planks_0", 1, 0), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("cherry_stairs", 2), getItemStack("planks_0", 1, 1), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("umbran_stairs", 2), getItemStack("planks_0", 1, 2), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("fir_stairs", 2), getItemStack("planks_0", 1, 3), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("ethereal_stairs", 2), getItemStack("planks_0", 1, 4), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("magic_stairs", 2), getItemStack("planks_0", 1, 5), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("mangrove_stairs", 2), getItemStack("planks_0", 1, 6), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("palm_stairs", 2), getItemStack("planks_0", 1, 7), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("redwood_stairs", 2), getItemStack("planks_0", 1, 8), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("willow_stairs", 2), getItemStack("planks_0", 1, 9), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("pine_stairs", 2), getItemStack("planks_0", 1, 10), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("hellbark_stairs", 2), getItemStack("planks_0", 1, 11), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("jacaranda_stairs", 2), getItemStack("planks_0", 1, 12), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("mahogany_stairs", 2), getItemStack("planks_0", 1, 13), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("ebony_stairs", 2), getItemStack("planks_0", 1, 14), ItemMaterial.dustWood, 50);
			SawmillManager.addRecipe(energy, getItemStack("eucalyptus_stairs", 2), getItemStack("planks_0", 1, 15), ItemMaterial.dustWood, 50);
		}

		/* INSOLATOR */
		{
			String plant = "waterlily";
			for (int i = 0; i < 4; i++) {
				InsolatorManager.addDefaultRecipe(getItemStack(plant, 1, i), getItemStack(plant, 2, i), ItemStack.EMPTY, 0);
			}
			plant = "plant_0";
			for (int i = 0; i < 16; i++) {
				InsolatorManager.addDefaultRecipe(getItemStack(plant, 1, i), getItemStack(plant, 3, i), ItemStack.EMPTY, 0);
			}
			plant = "plant_1";
			for (int i = 0; i < 11; i++) {
				InsolatorManager.addDefaultRecipe(getItemStack(plant, 1, i), getItemStack(plant, 3, i), ItemStack.EMPTY, 0);
			}
			plant = "double_plant";
			for (int i = 0; i < 3; i++) {
				InsolatorManager.addDefaultRecipe(getItemStack(plant, 1, i), getItemStack(plant, 3, i), ItemStack.EMPTY, 0);
			}
			plant = "mushroom";
			for (int i = 0; i < 6; i++) {
				InsolatorManager.addDefaultRecipe(getItemStack(plant, 1, i), getItemStack(plant, 2, i), ItemStack.EMPTY, 0);
			}
			plant = "flower_0";
			for (int i = 0; i < 16; i++) {
				InsolatorManager.addDefaultRecipe(getItemStack(plant, 1, i), getItemStack(plant, 3, i), ItemStack.EMPTY, 0);
			}
			plant = "flower_1";
			for (int i = 0; i < 6; i++) {
				InsolatorManager.addDefaultRecipe(getItemStack(plant, 1, i), getItemStack(plant, 3, i), ItemStack.EMPTY, 0);
			}
			InsolatorManager.addDefaultRecipe(getItemStack("flower_vine"), getItemStack("flower_vine", 2), ItemStack.EMPTY, 0);
			InsolatorManager.addDefaultRecipe(getItemStack("ivy"), getItemStack("ivy", 2), ItemStack.EMPTY, 0);
			InsolatorManager.addDefaultRecipe(getItemStack("tree_moss"), getItemStack("tree_moss", 2), ItemStack.EMPTY, 0);
			InsolatorManager.addDefaultRecipe(getItemStack("willow_vine"), getItemStack("willow_vine", 2), ItemStack.EMPTY, 0);

			InsolatorManager.addDefaultTreeRecipe(saplingYellowAutumn, ItemHelper.cloneStack(logYellowAutumn, 6), saplingYellowAutumn);
			InsolatorManager.addDefaultTreeRecipe(saplingOrangeAutumn, ItemHelper.cloneStack(logOrangeAutumn, 6), saplingOrangeAutumn);
			InsolatorManager.addDefaultTreeRecipe(saplingBamboo, ItemHelper.cloneStack(logBamboo, 6), saplingBamboo);
			InsolatorManager.addDefaultTreeRecipe(saplingMagic, ItemHelper.cloneStack(logMagic, 6), saplingMagic);
			InsolatorManager.addDefaultTreeRecipe(saplingUmbran, ItemHelper.cloneStack(logUmbral, 6), saplingUmbran);
			// Dead
			InsolatorManager.addDefaultTreeRecipe(saplingFir, ItemHelper.cloneStack(logFir, 6), saplingFir);
			// Ethereal

			InsolatorManager.addDefaultTreeRecipe(saplingOrigin, ItemHelper.cloneStack(logOrigin, 6), saplingOrigin);
			InsolatorManager.addDefaultTreeRecipe(saplingPinkCherry, ItemHelper.cloneStack(logCherry, 6), saplingPinkCherry);
			InsolatorManager.addDefaultTreeRecipe(saplingWhiteCherry, ItemHelper.cloneStack(logCherry, 6), saplingWhiteCherry);
			InsolatorManager.addDefaultTreeRecipe(saplingMaple, ItemHelper.cloneStack(logMaple, 6), saplingMaple);
			InsolatorManager.addDefaultTreeRecipe(saplingHellback, ItemHelper.cloneStack(logHellbark, 2), saplingHellback);
			InsolatorManager.addDefaultTreeRecipe(saplingFloweringOak, ItemHelper.cloneStack(logFloweringOak, 6), saplingFloweringOak);
			InsolatorManager.addDefaultTreeRecipe(saplingJacaranda, ItemHelper.cloneStack(logJacaranda, 6), saplingJacaranda);
			// Sacred Oak InsolatorManager.addDefaultTreeRecipe(saplingSacredOak, ItemHelper.cloneStack(logSacredOak, 6), saplingSacredOak);

			InsolatorManager.addDefaultTreeRecipe(saplingMangrove, ItemHelper.cloneStack(logMangrove, 6), saplingMangrove);
			InsolatorManager.addDefaultTreeRecipe(saplingPalm, ItemHelper.cloneStack(logPalm, 6), saplingPalm);
			// InsolatorManager.addDefaultTreeRecipe(saplingRedwood, ItemHelper.cloneStack(logRedwood, 6), saplingRedwood);
			InsolatorManager.addDefaultTreeRecipe(saplingWillow, ItemHelper.cloneStack(logWillow, 6), saplingWillow);
			InsolatorManager.addDefaultTreeRecipe(saplingPine, ItemHelper.cloneStack(logPine, 6), saplingPine);
			InsolatorManager.addDefaultTreeRecipe(saplingMahogany, ItemHelper.cloneStack(logMahogany, 6), saplingMahogany);
			InsolatorManager.addDefaultTreeRecipe(saplingEbony, ItemHelper.cloneStack(logEbony, 6), saplingEbony);
			InsolatorManager.addDefaultTreeRecipe(saplingEucalyptus, ItemHelper.cloneStack(logEucalyptus, 6), saplingEucalyptus);
		}

		/* TRANSPOSER */
		{
			int energy = TransposerManager.DEFAULT_ENERGY;
			FluidStack water = new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);

			TransposerManager.addFillRecipe(energy, new ItemStack(Blocks.DIRT), getItemStack("mud"), water, false);
			TransposerManager.addFillRecipe(energy, getItemStack("dirt"), getItemStack("mud"), water, false);
			TransposerManager.addFillRecipe(energy, getItemStack("dried_sand"), new ItemStack(Blocks.SAND), water, false);
		}

		/* TAPPER */
		{
			// Yellow Autumn
			// Orange Autumn
			TapperManager.addStandardMapping(logBamboo, new FluidStack(FluidRegistry.WATER, 25));
			TapperManager.addStandardMapping(logMagic, new FluidStack(TFFluids.fluidResin, 25));    // TODO: Mana
			TapperManager.addStandardMapping(logUmbral, new FluidStack(TFFluids.fluidResin, 50));
			// Dead
			TapperManager.addStandardMapping(logFir, new FluidStack(TFFluids.fluidResin, 50));
			// Ethereal

			// Origin
			TapperManager.addStandardMapping(logCherry, new FluidStack(TFFluids.fluidSap, 50));
			// White Cherry
			// Maple
			// TapperManager.addStandardMapping(logHellbark, new FluidStack(TFFluids.fluidSap, 50));
			// Flowering Oak
			TapperManager.addStandardMapping(logJacaranda, new FluidStack(TFFluids.fluidSap, 50));
			// TapperManager.addStandardMapping(logSacredOak, new FluidStack(TFFluids.fluidSap, 50));       // TODO: Allow?

			TapperManager.addStandardMapping(logMangrove, new FluidStack(TFFluids.fluidResin, 50));
			// TapperManager.addStandardMapping(logPalm, new FluidStack(TFFluids.fluidSap, 25));            // TODO: Allow?
			// TapperManager.addStandardMapping(logRedwood, new FluidStack(TFFluids.fluidResin, 50));       // TODO: Allow?
			TapperManager.addStandardMapping(logWillow, new FluidStack(TFFluids.fluidResin, 50));
			TapperManager.addStandardMapping(logPine, new FluidStack(TFFluids.fluidResin, 100));
			TapperManager.addStandardMapping(logMahogany, new FluidStack(TFFluids.fluidResin, 25));
			TapperManager.addStandardMapping(logEbony, new FluidStack(TFFluids.fluidResin, 25));
			TapperManager.addStandardMapping(logEucalyptus, new FluidStack(TFFluids.fluidResin, 50));

			addLeafMapping(Blocks.LOG, 2, blockLeaves0, 8);
			addLeafMapping(Blocks.LOG2, 1, blockLeaves0, 9);
			addLeafMapping(bamboo, 0, blockLeaves0, 10);
			addLeafMapping(blockLog1, 5, blockLeaves0, 11);
			addLeafMapping(blockLog0, 6, blockLeaves1, 0);
			// Dead
			addLeafMapping(blockLog0, 7, blockLeaves1, 10);
			// Ethereal

			addLeafMapping(Blocks.LOG, 0, blockLeaves2, 8);
			addLeafMapping(blockLog0, 5, blockLeaves2, 9);
			addLeafMapping(blockLog0, 5, blockLeaves2, 10);
			addLeafMapping(Blocks.LOG, 0, blockLeaves2, 11);
			// Hellbark addLeafMapping(blockLog2, 7, blockLeaves3, 8);
			addLeafMapping(Blocks.LOG, 0, blockLeaves3, 9);
			addLeafMapping(blockLog3, 4, blockLeaves3, 10);
			// Sacred Oak addLeafMapping(blockLog0, 4, blockLeaves3, 11);

			addLeafMapping(blockLog1, 6, blockLeaves4, 8);
			// Palm addLeafMapping(blockLog1, 7, blockLeaves4, 9);
			// Redwood addLeafMapping(blockLog2, 4, blockLeaves4, 10);
			addLeafMapping(blockLog2, 5, blockLeaves4, 11);
			addLeafMapping(blockLog2, 6, blockLeaves5, 8);
			addLeafMapping(blockLog3, 5, blockLeaves5, 9);
			addLeafMapping(blockLog3, 6, blockLeaves5, 10);
			addLeafMapping(blockLog3, 7, blockLeaves5, 11);
		}

		/* EXTRUDER */
		{
			ExtruderManager.addRecipeSedimentary(ExtruderManager.DEFAULT_ENERGY * 4, sandWhite, new FluidStack(FluidRegistry.LAVA, 0), new FluidStack(FluidRegistry.WATER, 1500));
			ExtruderManager.addRecipeSedimentary(ExtruderManager.DEFAULT_ENERGY * 8, sandStoneWhite, new FluidStack(FluidRegistry.LAVA, 0), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME * 2));
		}
	}

}
