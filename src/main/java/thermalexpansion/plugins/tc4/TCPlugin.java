package thermalexpansion.plugins.tc4;

import cpw.mods.fml.common.Loader;

import net.minecraft.item.ItemStack;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.machine.BlockMachine;
import thermalexpansion.block.simple.BlockFrame;
import thermalexpansion.block.simple.BlockGlass;
import thermalexpansion.block.simple.BlockRockwool;
import thermalexpansion.item.TEEquipment;
import thermalexpansion.item.TEFlorbs;
import thermalexpansion.item.TEItems;
import thermalfoundation.block.BlockOre;
import thermalfoundation.block.BlockStorage;
import thermalfoundation.item.TFItems;

public class TCPlugin {

	public static void preInit() {

	}

	public static void initialize() {

	}

	public static void postInit() {

		if (!Loader.isModLoaded("Thaumcraft")) {
			return;
		}
		registerStack(TFItems.dustCoal, "2 Ignis, 1 Potentia, 1 Perditio");
		registerStack(TFItems.dustObsidian, "2 Ignis, 1 Tenebrae, 1 Perditio");
		registerStack(TFItems.dustCopper, "1 Metallum, 1 Perditio");
		registerStack(TFItems.dustTin, "1 Metallum, 1 Perditio");
		registerStack(TFItems.dustSilver, "1 Metallum, 1 Perditio");
		registerStack(TFItems.dustLead, "1 Metallum, 1 Perditio");
		registerStack(TFItems.dustNickel, "1 Metallum, 1 Perditio");
		registerStack(TFItems.dustPlatinum, "1 Metallum, 1 Perditio");
		registerStack(TFItems.dustMithril, "1 Metallum, 1 Perditio");
		registerStack(TFItems.dustElectrum, "1 Metallum, 1 Perditio");
		registerStack(TFItems.dustInvar, "1 Metallum, 1 Perditio");
		registerStack(TFItems.dustBronze, "1 Metallum, 1 Perditio");
		registerStack(TFItems.dustSignalum, "1 Metallum, 1 Perditio");
		registerStack(TFItems.dustLumium, "1 Metallum, 1 Perditio");
		registerStack(TFItems.dustEnderium, "1 Metallum, 1 Perditio");

		registerStack(TFItems.ingotCopper, "3 Metallum, 1 Vacuos");
		registerStack(TFItems.ingotTin, "3 Metallum, 1 Lucrum");
		registerStack(TFItems.ingotSilver, "3 Metallum, 1 Lucrum");
		registerStack(TFItems.ingotLead, "3 Metallum, 1 Tutamen");
		registerStack(TFItems.ingotNickel, "3 Metallum, 1 Vacuos");
		registerStack(TFItems.ingotPlatinum, "3 Metallum, 1 Lucrum");
		registerStack(TFItems.ingotMithril, "3 Metallum, 1 Praecantatio");
		registerStack(TFItems.ingotElectrum, "3 Metallum, 1 Lucrum");
		registerStack(TFItems.ingotInvar, "3 Metallum, 1 Tutamen");
		registerStack(TFItems.ingotBronze, "3 Metallum, 1 Fabrico");
		registerStack(TFItems.ingotSignalum, "3 Metallum, 1 Potentia");
		registerStack(TFItems.ingotLumium, "3 Metallum, 1 Lux");
		registerStack(TFItems.ingotEnderium, "3 Metallum, 1 Eldritch");

		registerStack(TFItems.nuggetCopper, "1 Metallum");
		registerStack(TFItems.nuggetTin, "1 Metallum");
		registerStack(TFItems.nuggetSilver, "1 Metallum");
		registerStack(TFItems.nuggetLead, "1 Metallum");
		registerStack(TFItems.nuggetNickel, "1 Metallum");
		registerStack(TFItems.nuggetPlatinum, "1 Metallum");
		registerStack(TFItems.nuggetMithril, "1 Metallum");
		registerStack(TFItems.nuggetElectrum, "1 Metallum");
		registerStack(TFItems.nuggetInvar, "1 Metallum");
		registerStack(TFItems.nuggetBronze, "1 Metallum");
		registerStack(TFItems.nuggetSignalum, "1 Metallum");
		registerStack(TFItems.nuggetLumium, "1 Metallum");
		registerStack(TFItems.nuggetEnderium, "1 Metallum");

		registerStack(TFItems.gearCopper, "7 Metallum, 2 Machina, 2 Permutatio");
		registerStack(TFItems.gearTin, "7 Metallum, 2 Machina, 2 Vitreus");
		registerStack(TFItems.gearSilver, "7 Metallum, 2 Machina, 2 Vitreus");
		registerStack(TFItems.gearLead, "7 Metallum, 2 Machina, 2 Tutamen");
		registerStack(TFItems.gearNickel, "7 Metallum, 2 Machina, 2 Permutatio");
		registerStack(TFItems.gearPlatinum, "7 Metallum, 2 Machina, 2 Vitreus");
		registerStack(TFItems.gearMithril, "7 Metallum, 2 Machina, 2 Praecantatio");
		registerStack(TFItems.gearElectrum, "7 Metallum, 2 Machina, 2 Lucrum");
		registerStack(TFItems.gearInvar, "7 Metallum, 2 Machina, 2 Tutamen");
		registerStack(TFItems.gearBronze, "7 Metallum, 2 Machina, 2 Fabrico");
		registerStack(TFItems.gearSignalum, "7 Metallum, 2 Machina, 2 Potentia");
		registerStack(TFItems.gearLumium, "7 Metallum, 2 Machina, 2 Lux");
		registerStack(TFItems.gearEnderium, "7 Metallum, 2 Machina, 2 Eldritch");

		registerStack(BlockOre.oreCopper, "2 Metallum, 1 Perditio, 1 Vacuos");
		registerStack(BlockOre.oreTin, "2 Metallum, 1 Perditio, 1 Lucrum");
		registerStack(BlockOre.oreSilver, "2 Metallum, 1 Perditio, 1 Lucrum");
		registerStack(BlockOre.oreLead, "2 Metallum, 1 Perditio, 1 Tutamen");
		registerStack(BlockOre.oreNickel, "2 Metallum, 1 Perditio, 1 Vacuos");
		registerStack(BlockOre.orePlatinum, "2 Metallum, 1 Perditio, 1 Lucrum");
		registerStack(BlockOre.oreMithril, "2 Metallum, 1 Perditio, 1 Praecantatio");

		registerStack(BlockStorage.blockCopper, "11 Metallum, 5 Vacuos");
		registerStack(BlockStorage.blockTin, "11 Metallum, 5 Lucrum");
		registerStack(BlockStorage.blockSilver, "11 Metallum, 5 Lucrum");
		registerStack(BlockStorage.blockLead, "11 Metallum, 5 Tutamen");
		registerStack(BlockStorage.blockNickel, "11 Metallum, 5 Vacuos");
		registerStack(BlockStorage.blockPlatinum, "11 Metallum, 5 Lucrum");
		registerStack(BlockStorage.blockMithril, "11 Metallum, 5 Praecantatio");
		registerStack(BlockStorage.blockElectrum, "11 Metallum, 5 Lucrum");
		registerStack(BlockStorage.blockInvar, "11 Metallum, 5 Tutamen");
		registerStack(BlockStorage.blockBronze, "11 Metallum, 5 Fabrico");
		registerStack(BlockStorage.blockSignalum, "11 Metallum, 5 Potentia");
		registerStack(BlockStorage.blockLumium, "11 Metallum, 5 Lux");
		registerStack(BlockStorage.blockEnderium, "11 Metallum, 5 Eldritch");

		registerStack(TFItems.bucketRedstone, "8 Metallum, 10 Potentia, 8 Machina, 4 Ignis, 2 Aqua");
		registerStack(TFItems.bucketGlowstone, "8 Metallum, 10 Lux, 8 Senses, 4 Ignis, 2 Aqua");
		registerStack(TFItems.bucketEnder, "8 Metallum, 14 Eldritch, 8 Travel, 4 Ignis, 2 Praecantatio, 2 Aqua");
		registerStack(TFItems.bucketPyrotheum, "8 Metallum, 16 Ignis, 14 Potentia, 2 Praecantatio, 2 Aqua");
		registerStack(TFItems.bucketCryotheum, "8 Metallum, 16 Gelum, 14 Potentia, 2 Praecantatio, 2 Aqua");
		registerStack(TFItems.bucketMana, "8 Metallum, 16 Praecantatio, 8 Potentia, 8 Perditio, 2 Aqua");
		registerStack(TFItems.bucketCoal, "8 Metallum, 14 Ignis, 2 Potentia, 2 Vacuos, 2 Aqua");

		registerStack(TFItems.dustSulfur, "3 Ignis, 1 Terra");
		registerStack(TFItems.dustNiter, "3 Aer, 1 Terra");
		registerStack(TFItems.crystalCinnabar, "2 Terra, 1 Permutatio, 1 Venenum");

		registerStack(TFItems.dustPyrotheum, "4 Potentia, 4 Ignis, 1 Magic");
		registerStack(TFItems.dustCryotheum, "4 Potentia, 4 Gelum, 1 Magic");
		registerStack(TFItems.rodBlizz, "4 Gelum, 2 Magic");
		registerStack(TFItems.dustBlizz, "2 Gelum, 1 Magic");

		registerStack(TEFlorbs.florb, "1 Earth, 2 Slime, 1 Void");
		registerStack(TEFlorbs.florbMagmatic, "1 Earth, 1 Ignis, 2 Slime, 1 Void");

		registerStack(TEItems.pneumaticServo, "4 Metallum, 2 Machina, 1 Potentia, 4 Motus");
		registerStack(TEItems.powerCoilGold, "4 Metallum, 2 Machina, 2 Energy");
		registerStack(TEItems.powerCoilSilver, "4 Metallum, 2 Machina, 2 Energy");
		registerStack(TEItems.powerCoilElectrum, "4 Metallum, 2 Machina, 2 Energy");
		registerStack(TEItems.slag, "2 Terra, 2 Perditio");
		registerStack(TEItems.slagRich, "2 Terra, 2 Perditio, 2 Lucrum");
		registerStack(TEItems.sawdust, "1 Arbor, 1 Perditio");
		registerStack(TEItems.sawdustCompressed, "3 Arbor, 3 Perditio");

		registerStack(BlockRockwool.rockWool, "1 Fabrico, 2 Cloth, 2 Entropy");
		registerStack(BlockGlass.glassHardened, "1 Ignis, 1 Armor, 1 Vitreus");

		registerStack(TEEquipment.toolInvarShears, "4 Metallum, 4 Harvest, 2 Armor");
		registerStack(TEEquipment.toolInvarFishingRod, "1 Metallum, 1 Aqua, 1 Tool");
		registerStack(TEEquipment.toolInvarSickle, "5 Metallum, 4 Armor, 4 Harvest");
		registerStack(TEEquipment.toolInvarBattleWrench, "6 Metallum, 1 Tool, 1 Machina, 3 Weapon");

		registerStack(TEItems.toolWrench, "4 Metallum, 1 Tool");
		registerStack(TEItems.toolMultimeter, "4 Metallum, 2 Potentia, 3 Machina, 2 Senses, 2 Tool");

		registerStack(TEItems.diagramSchematic, "3 Mind");

		registerStack(TEItems.capacitorPotato, "1 Hunger, 1 Earth, 1 Crop, 3 Potentia, 1 Mechanism");
		registerStack(TEItems.capacitorBasic, "4 Metallum, 2 Order, 2 Permutatio, 6 Potentia, 3 Machina, 1 Fire");
		registerStack(TEItems.capacitorHardened, "7 Metallum, 2 Order, 2 Permutatio, 9 Potentia, 3 Machina, 1 Ignis, 1 Armor");
		registerStack(TEItems.capacitorReinforced, "10 Metallum, 2 Order, 2 Permutatio, 12 Potentia, 3 Machina, 1 Ignis, 1 Armor, 4 Vitreus");
		registerStack(TEItems.capacitorResonant, "13 Metallum, 2 Order, 2 Permutatio, 15 Potentia, 3 Machina, 1 Ignis, 1 Armor, 4 Vitreus, 1 Eldritch, 2 Magic");

		registerStack(BlockFrame.frameMachineBasic, "4 Metallum, 2 Vitreus, 4 Mechanism");
		registerStack(BlockMachine.furnace, "8 Metallum, 2 Vitreus, 6 Machina, 2 Potentia, 4 Ignis, 2 Fabrico");
		registerStack(BlockMachine.pulverizer, "8 Metallum, 2 Vitreus, 6 Machina, 2 Potentia, 4 Entropy, 2 Motus");
		registerStack(BlockMachine.sawmill, "8 Metallum, 2 Vitreus, 6 Machina, 2 Potentia, 4 Tool, 2 Tree");
		registerStack(BlockMachine.smelter, "8 Metallum, 2 Vitreus, 6 Machina, 2 Potentia, 4 Ignis, 2 Permutatio");
		registerStack(BlockMachine.crucible, "8 Metallum, 2 Vitreus, 6 Machina, 2 Potentia, 4 Ignis, 2 Aqua");
		registerStack(BlockMachine.transposer, "8 Metallum, 2 Vitreus, 6 Machina, 2 Potentia, 4 Aqua, 2 Motus");
		registerStack(BlockMachine.precipitator, "8 Metallum, 2 Vitreus, 6 Machina, 2 Potentia, 4 Gelum, 2 Aqua");
		registerStack(BlockMachine.extruder, "8 Metallum, 2 Vitreus, 6 Machina, 2 Potentia, 4 Entropy, 1 Aqua, 1 Fire");
		registerStack(BlockMachine.accumulator, "8 Metallum, 2 Vitreus, 6 Machina, 2 Potentia, 4 Gelum, 2 Aqua");
		registerStack(BlockMachine.assembler, "8 Metallum, 2 Vitreus, 6 Machina, 2 Potentia, 4 Fabrico, 2 Motus");
		registerStack(BlockMachine.charger, "8 Metallum, 2 Vitreus, 6 Machina, 6 Potentia, 2 Permutatio");

		ThermalExpansion.log.info("Thaumcraft Plugin Enabled.");
	}

	public static void registerStack(ItemStack stack, String aspects) {

		registerStack(stack, parseAspects(aspects));
	}

	public static void registerStack(ItemStack stack, AspectList aspects) {

		if (stack != null) {
			ThaumcraftApi.registerObjectTag(stack, aspects);
		}
	}

	private static AspectList parseAspects(String aspects) {

		AspectList aspectList = new AspectList();
		String[] list = aspects.split(",");

		for (int i = 0; i < list.length; i++) {
			String[] entry = list[i].trim().split(" ");
			entry[1] = entry[1].toLowerCase();
			Aspect aspect = Aspect.getAspect(entry[1]);

			if (aspect != null) {
				aspectList.add(aspect, Integer.parseInt(entry[0]));
			}
		}
		return aspectList;
	}

}
