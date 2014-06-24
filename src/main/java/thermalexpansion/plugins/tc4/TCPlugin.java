package thermalexpansion.plugins.tc4;

import cpw.mods.fml.common.Loader;

import net.minecraft.item.ItemStack;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.cell.BlockCell;
import thermalexpansion.block.device.BlockDevice;
import thermalexpansion.block.dynamo.BlockDynamo;
import thermalexpansion.block.ender.BlockTesseract;
import thermalexpansion.block.lamp.BlockLamp;
import thermalexpansion.block.machine.BlockMachine;
import thermalexpansion.block.simple.BlockFrame;
import thermalexpansion.block.simple.BlockGlass;
import thermalexpansion.block.simple.BlockRockwool;
import thermalexpansion.block.strongbox.BlockStrongbox;
import thermalexpansion.block.tank.BlockTank;
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
		registerStack(TFItems.bucketGlowstone, "8 Metallum, 10 Light, 8 Senses, 4 Ignis, 2 Aqua");
		registerStack(TFItems.bucketEnder, "8 Metallum, 14 Eldritch, 8 Travel, 4 Ignis, 2 Praecantatio, 2 Aqua");
		registerStack(TFItems.bucketPyrotheum, "8 Metallum, 16 Ignis, 14 Potentia, 2 Praecantatio, 2 Aqua");
		registerStack(TFItems.bucketCryotheum, "8 Metallum, 16 Gelum, 14 Potentia, 2 Praecantatio, 2 Aqua");
		registerStack(TFItems.bucketMana, "8 Metallum, 16 Praecantatio, 8 Potentia, 8 Perditio, 2 Aqua");
		registerStack(TFItems.bucketCoal, "8 Metallum, 14 Ignis, 2 Potentia, 2 Vacuos, 2 Aqua");

		registerStack(TFItems.dustSulfur, "3 Ignis, 1 Terra");
		registerStack(TFItems.dustNiter, "3 Aer, 1 Terra");
		registerStack(TFItems.crystalCinnabar, "2 Terra, 1 Permutatio, 1 Venenum");

		registerStack(TFItems.dustPyrotheum, "4 Energy, 4 Fire, 1 Magic");
		registerStack(TFItems.dustCryotheum, "4 Energy, 4 Cold, 1 Magic");
		registerStack(TFItems.rodBlizz, "4 Cold, 2 Magic");
		registerStack(TFItems.dustBlizz, "2 Cold, 1 Magic");

		registerStack(TEFlorbs.florb, "1 Earth, 2 Slime, 1 Void");
		registerStack(TEFlorbs.florbMagmatic, "1 Earth, 1 Fire, 2 Slime, 1 Void");

		registerStack(TEItems.pneumaticServo, "4 Metal, 2 Mechanism, 1 Energy, 4 Motion");
		registerStack(TEItems.powerCoilGold, "4 Metal, 2 Mechanism, 2 Energy");
		registerStack(TEItems.powerCoilSilver, "4 Metal, 2 Mechanism, 2 Energy");
		registerStack(TEItems.powerCoilElectrum, "4 Metal, 2 Mechanism, 2 Energy");
		registerStack(TEItems.slag, "2 Terra, 2 Perditio");
		registerStack(TEItems.slagRich, "2 Terra, 2 Perditio, 2 Lucrum");
		registerStack(TEItems.sawdust, "1 Arbor, 1 Perditio");
		registerStack(TEItems.sawdustCompressed, "3 Arbor, 3 Perditio");

		registerStack(BlockRockwool.rockWool, "1 Craft, 2 Cloth, 2 Entropy");
		registerStack(BlockGlass.glassHardened, "1 Fire, 1 Armor, 1 Crystal");

		registerStack(TEEquipment.toolInvarShears, new AspectList().add(Aspect.METAL, 4).add(Aspect.HARVEST, 4).add(Aspect.ARMOR, 2));
		registerStack(TEEquipment.toolInvarFishingRod, new AspectList().add(Aspect.METAL, 1).add(Aspect.WATER, 1).add(Aspect.TOOL, 1));
		registerStack(TEEquipment.toolInvarSickle, new AspectList().add(Aspect.METAL, 5).add(Aspect.ARMOR, 4).add(Aspect.HARVEST, 4));
		registerStack(TEEquipment.toolInvarBattleWrench,
				new AspectList().add(Aspect.METAL, 6).add(Aspect.TOOL, 1).add(Aspect.MECHANISM, 1).add(Aspect.WEAPON, 3));

		registerStack(TEItems.toolWrench, new AspectList().add(Aspect.METAL, 4).add(Aspect.TOOL, 1));
		registerStack(TEItems.toolMultimeter,
				new AspectList().add(Aspect.METAL, 4).add(Aspect.ENERGY, 2).add(Aspect.MECHANISM, 3).add(Aspect.SENSES, 2).add(Aspect.TOOL, 2));

		registerStack(BlockFrame.frameMachineBasic, new AspectList().add(Aspect.METAL, 4).add(Aspect.CRYSTAL, 2).add(Aspect.MECHANISM, 4));
		registerStack(
				BlockMachine.furnace,
				new AspectList().add(Aspect.METAL, 8).add(Aspect.CRYSTAL, 2).add(Aspect.MECHANISM, 6).add(Aspect.ENERGY, 2).add(Aspect.FIRE, 4)
						.add(Aspect.CRAFT, 2));
		registerStack(
				BlockMachine.pulverizer,
				new AspectList().add(Aspect.METAL, 8).add(Aspect.CRYSTAL, 2).add(Aspect.MECHANISM, 6).add(Aspect.ENERGY, 2).add(Aspect.ENTROPY, 4)
						.add(Aspect.MOTION, 2));
		registerStack(
				BlockMachine.sawmill,
				new AspectList().add(Aspect.METAL, 8).add(Aspect.CRYSTAL, 2).add(Aspect.MECHANISM, 6).add(Aspect.ENERGY, 2).add(Aspect.TOOL, 4)
						.add(Aspect.TREE, 2));
		registerStack(
				BlockMachine.smelter,
				new AspectList().add(Aspect.METAL, 8).add(Aspect.CRYSTAL, 2).add(Aspect.MECHANISM, 6).add(Aspect.ENERGY, 2).add(Aspect.FIRE, 4)
						.add(Aspect.EXCHANGE, 2));
		registerStack(
				BlockMachine.crucible,
				new AspectList().add(Aspect.METAL, 8).add(Aspect.CRYSTAL, 2).add(Aspect.MECHANISM, 6).add(Aspect.ENERGY, 2).add(Aspect.FIRE, 4)
						.add(Aspect.WATER, 2));
		registerStack(
				BlockMachine.transposer,
				new AspectList().add(Aspect.METAL, 8).add(Aspect.CRYSTAL, 2).add(Aspect.MECHANISM, 6).add(Aspect.ENERGY, 2).add(Aspect.WATER, 4)
						.add(Aspect.MOTION, 2));
		registerStack(BlockMachine.precipitator, new AspectList().add(Aspect.METAL, 8).add(Aspect.CRYSTAL, 2).add(Aspect.MECHANISM, 6).add(Aspect.ENERGY, 2)
				.add(Aspect.COLD, 4).add(Aspect.WATER, 2));
		registerStack(
				BlockMachine.extruder,
				new AspectList().add(Aspect.METAL, 10).add(Aspect.CRYSTAL, 4).add(Aspect.MECHANISM, 6).add(Aspect.MOTION, 2).add(Aspect.ENTROPY, 4)
						.add(Aspect.WATER, 1).add(Aspect.FIRE, 1));
		registerStack(BlockMachine.accumulator, new AspectList().add(Aspect.METAL, 10).add(Aspect.CRYSTAL, 4).add(Aspect.MECHANISM, 6).add(Aspect.MOTION, 2)
				.add(Aspect.WATER, 4).add(Aspect.CRAFT, 1).add(Aspect.VOID, 1));
		registerStack(
				BlockMachine.assembler,
				new AspectList().add(Aspect.METAL, 8).add(Aspect.CRYSTAL, 2).add(Aspect.MECHANISM, 6).add(Aspect.ENERGY, 2).add(Aspect.CRAFT, 4)
						.add(Aspect.MOTION, 2));
		registerStack(
				BlockMachine.charger,
				new AspectList().add(Aspect.METAL, 8).add(Aspect.CRYSTAL, 2).add(Aspect.MECHANISM, 6).add(Aspect.ENERGY, 2).add(Aspect.ENERGY, 4)
						.add(Aspect.EXCHANGE, 2));

		registerStack(BlockDevice.workbench, new AspectList().add(Aspect.VOID, 4).add(Aspect.METAL, 8).add(Aspect.MIND, 1).add(Aspect.CRAFT, 1));
		registerStack(
				BlockDevice.activator,
				new AspectList().add(Aspect.MOTION, 8).add(Aspect.METAL, 6).add(Aspect.MECHANISM, 4).add(Aspect.ENERGY, 1).add(Aspect.CRYSTAL, 1)
						.add(Aspect.TOOL, 6).add(Aspect.VOID, 2));
		registerStack(
				BlockDevice.breaker,
				new AspectList().add(Aspect.MOTION, 8).add(Aspect.METAL, 6).add(Aspect.MECHANISM, 4).add(Aspect.ENERGY, 1).add(Aspect.CRYSTAL, 1)
						.add(Aspect.MINE, 6).add(Aspect.TOOL, 2));
		registerStack(
				BlockDevice.nullifier,
				new AspectList().add(Aspect.METAL, 6).add(Aspect.MECHANISM, 2).add(Aspect.ENERGY, 1).add(Aspect.MOTION, 4).add(Aspect.CRYSTAL, 1)
						.add(Aspect.FIRE, 3).add(Aspect.VOID, 6));

		registerStack(
				BlockDynamo.dynamoSteam,
				new AspectList().add(Aspect.METAL, 6).add(Aspect.MECHANISM, 4).add(Aspect.ENERGY, 4).add(Aspect.MOTION, 4).add(Aspect.FIRE, 4)
						.add(Aspect.WATER, 2));
		registerStack(BlockDynamo.dynamoMagmatic, new AspectList().add(Aspect.METAL, 6).add(Aspect.MECHANISM, 4).add(Aspect.ENERGY, 4).add(Aspect.MOTION, 4)
				.add(Aspect.FIRE, 4).add(Aspect.ENTROPY, 2));
		registerStack(BlockDynamo.dynamoCompression, new AspectList().add(Aspect.METAL, 6).add(Aspect.MECHANISM, 4).add(Aspect.ENERGY, 8).add(Aspect.MOTION, 4)
				.add(Aspect.WATER, 2));
		registerStack(BlockDynamo.dynamoReactant, new AspectList().add(Aspect.METAL, 6).add(Aspect.MECHANISM, 4).add(Aspect.ENERGY, 6).add(Aspect.MOTION, 4)
				.add(Aspect.FIRE, 2).add(Aspect.WATER, 2));

		registerStack(BlockFrame.frameCellBasic,
				new AspectList().add(Aspect.METAL, 6).add(Aspect.ORDER, 2).add(Aspect.CRYSTAL, 2).add(Aspect.ENERGY, 5).add(Aspect.MECHANISM, 3));
		registerStack(BlockFrame.frameCellReinforcedEmpty, new AspectList().add(Aspect.METAL, 6).add(Aspect.FIRE, 2).add(Aspect.ARMOR, 2)
				.add(Aspect.CRYSTAL, 6));
		registerStack(BlockFrame.frameCellReinforcedFull, new AspectList().add(Aspect.METAL, 6).add(Aspect.FIRE, 6).add(Aspect.ARMOR, 2).add(Aspect.CRYSTAL, 6)
				.add(Aspect.ENERGY, 20).add(Aspect.MECHANISM, 16).add(Aspect.WATER, 2));

		registerStack(
				BlockCell.cellBasic,
				new AspectList().add(Aspect.METAL, 10).add(Aspect.ORDER, 2).add(Aspect.CRYSTAL, 2).add(Aspect.ENERGY, 7).add(Aspect.MECHANISM, 5)
						.add(Aspect.EXCHANGE, 3));
		registerStack(
				BlockCell.cellHardened,
				new AspectList().add(Aspect.METAL, 16).add(Aspect.ORDER, 2).add(Aspect.CRYSTAL, 2).add(Aspect.ENERGY, 10).add(Aspect.MECHANISM, 5)
						.add(Aspect.EXCHANGE, 3).add(Aspect.ARMOR, 2));
		registerStack(
				BlockCell.cellReinforced,
				new AspectList().add(Aspect.METAL, 10).add(Aspect.FIRE, 6).add(Aspect.ARMOR, 2).add(Aspect.CRYSTAL, 6).add(Aspect.ENERGY, 25)
						.add(Aspect.MECHANISM, 20).add(Aspect.WATER, 2).add(Aspect.ORDER, 2));
		registerStack(
				BlockCell.cellResonant,
				new AspectList().add(Aspect.METAL, 16).add(Aspect.FIRE, 6).add(Aspect.ARMOR, 2).add(Aspect.CRYSTAL, 6).add(Aspect.ENERGY, 25)
						.add(Aspect.MECHANISM, 20).add(Aspect.WATER, 2).add(Aspect.ORDER, 2).add(Aspect.ELDRITCH, 2));

		registerStack(BlockTank.tankBasic, new AspectList().add(Aspect.VOID, 4).add(Aspect.CRYSTAL, 4).add(Aspect.METAL, 2));
		registerStack(BlockTank.tankHardened, new AspectList().add(Aspect.VOID, 8).add(Aspect.CRYSTAL, 4).add(Aspect.METAL, 8).add(Aspect.ARMOR, 2));
		registerStack(BlockTank.tankReinforced,
				new AspectList().add(Aspect.VOID, 12).add(Aspect.CRYSTAL, 6).add(Aspect.METAL, 8).add(Aspect.ARMOR, 4).add(Aspect.FIRE, 2));
		registerStack(
				BlockTank.tankResonant,
				new AspectList().add(Aspect.VOID, 16).add(Aspect.CRYSTAL, 6).add(Aspect.METAL, 14).add(Aspect.ARMOR, 4).add(Aspect.FIRE, 2)
						.add(Aspect.ELDRITCH, 2));

		registerStack(BlockStrongbox.strongboxBasic, new AspectList().add(Aspect.VOID, 4).add(Aspect.METAL, 6).add(Aspect.CRYSTAL, 2));
		registerStack(BlockStrongbox.strongboxHardened, new AspectList().add(Aspect.VOID, 8).add(Aspect.METAL, 12).add(Aspect.CRYSTAL, 2).add(Aspect.ARMOR, 2));
		registerStack(BlockStrongbox.strongboxReinforced,
				new AspectList().add(Aspect.VOID, 12).add(Aspect.METAL, 12).add(Aspect.CRYSTAL, 4).add(Aspect.ARMOR, 4).add(Aspect.FIRE, 2));
		registerStack(BlockStrongbox.strongboxResonant, new AspectList().add(Aspect.VOID, 16).add(Aspect.METAL, 18).add(Aspect.CRYSTAL, 4).add(Aspect.ARMOR, 4)
				.add(Aspect.FIRE, 2).add(Aspect.ELDRITCH, 2));

		registerStack(BlockFrame.frameTesseractEmpty, new AspectList().add(Aspect.METAL, 6).add(Aspect.FIRE, 2).add(Aspect.ARMOR, 2).add(Aspect.CRYSTAL, 6)
				.add(Aspect.ELDRITCH, 4));
		registerStack(
				BlockFrame.frameTesseractFull,
				new AspectList().add(Aspect.METAL, 6).add(Aspect.FIRE, 6).add(Aspect.ARMOR, 2).add(Aspect.CRYSTAL, 6).add(Aspect.ELDRITCH, 18)
						.add(Aspect.TRAVEL, 8).add(Aspect.MAGIC, 2).add(Aspect.WATER, 2));
		registerStack(
				BlockTesseract.tesseract,
				new AspectList().add(Aspect.METAL, 6).add(Aspect.FIRE, 6).add(Aspect.ARMOR, 2).add(Aspect.CRYSTAL, 6).add(Aspect.ELDRITCH, 18)
						.add(Aspect.TRAVEL, 12).add(Aspect.MAGIC, 2).add(Aspect.WATER, 2));

		registerStack(
				BlockLamp.lamp,
				new AspectList().add(Aspect.FIRE, 5).add(Aspect.ARMOR, 1).add(Aspect.CRYSTAL, 2).add(Aspect.ENERGY, 2).add(Aspect.METAL, 2)
						.add(Aspect.MECHANISM, 1).add(Aspect.LIGHT, 10).add(Aspect.SENSES, 2).add(Aspect.WATER, 2));

		registerStack(TEItems.diagramSchematic, "3 Mind");

		registerStack(TEItems.capacitorPotato, "1 Hunger, 1 Earth, 1 Crop, 3 Energy, 1 Mechanism");
		registerStack(TEItems.capacitorBasic, "4 Metal, 2 Order, 2 Exchange, 6 Energy, 3 Mechanism, 1 Fire");
		registerStack(TEItems.capacitorHardened, "7 Metal, 2 Order, 2 Exchange, 9 Energy, 3 Mechanism, 1 Fire, 1 Armor");
		registerStack(TEItems.capacitorReinforced, "10 Metal, 2 Order, 2 Exchange, 12 Energy, 3 Mechanism, 1 Fire, 1 Armor, 4 Crystal");
		registerStack(TEItems.capacitorResonant, "13 Metal, 2 Order, 2 Exchange, 15 Energy, 3 Mechanism, 1 Fire, 1 Armor, 4 Crystal, 1 Eldritch, 2 Magic");

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
