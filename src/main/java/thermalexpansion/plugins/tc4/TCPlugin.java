package thermalexpansion.plugins.tc4;

import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.device.BlockDevice;
import thermalexpansion.block.dynamo.BlockDynamo;
import thermalexpansion.block.ender.BlockTesseract;
import thermalexpansion.block.energycell.BlockEnergyCell;
import thermalexpansion.block.lamp.BlockLamp;
import thermalexpansion.block.machine.BlockMachine;
import thermalexpansion.block.simple.BlockGlass;
import thermalexpansion.block.simple.BlockRockwool;
import thermalexpansion.block.strongbox.BlockStrongbox;
import thermalexpansion.block.tank.BlockTank;
import thermalexpansion.item.TEEquipment;
import thermalexpansion.item.TEItems;
import cofh.block.world.BlockOre;
import cofh.block.world.BlockStorage;
import cofh.item.CoFHWorldItems;
import cpw.mods.fml.common.Loader;

public class TCPlugin {

	public static void preInit() {

	}

	public static void initialize() {

	}

	public static void postInit() {

		if (Loader.isModLoaded("Thaumcraft")) {
			registerStack(CoFHWorldItems.dustCoal, new AspectList().add(Aspect.FIRE, 2).add(Aspect.ENERGY, 1).add(Aspect.ENTROPY, 1));
			registerStack(CoFHWorldItems.dustObsidian, new AspectList().add(Aspect.FIRE, 2).add(Aspect.DARKNESS, 1).add(Aspect.ENTROPY, 1));
			registerStack(CoFHWorldItems.dustNickel, new AspectList().add(Aspect.METAL, 1).add(Aspect.ENTROPY, 1));
			registerStack(CoFHWorldItems.dustPlatinum, new AspectList().add(Aspect.METAL, 1).add(Aspect.ENTROPY, 1));
			registerStack(CoFHWorldItems.dustElectrum, new AspectList().add(Aspect.METAL, 1).add(Aspect.ENTROPY, 1));
			registerStack(CoFHWorldItems.dustInvar, new AspectList().add(Aspect.METAL, 1).add(Aspect.ENTROPY, 1));
			registerStack(CoFHWorldItems.dustEnderium, new AspectList().add(Aspect.METAL, 1).add(Aspect.ENTROPY, 1));

			registerStack(BlockOre.oreNickel, new AspectList().add(Aspect.METAL, 2).add(Aspect.ENTROPY, 1).add(Aspect.VOID, 1));

			registerStack(CoFHWorldItems.ingotNickel, new AspectList().add(Aspect.METAL, 3).add(Aspect.VOID, 1));
			registerStack(CoFHWorldItems.ingotPlatinum, new AspectList().add(Aspect.METAL, 3).add(Aspect.GREED, 1));
			registerStack(CoFHWorldItems.ingotElectrum, new AspectList().add(Aspect.METAL, 3).add(Aspect.GREED, 1));
			registerStack(CoFHWorldItems.ingotInvar, new AspectList().add(Aspect.METAL, 3).add(Aspect.ARMOR, 1));
			registerStack(CoFHWorldItems.ingotEnderium, new AspectList().add(Aspect.METAL, 3).add(Aspect.ELDRITCH, 1));

			registerStack(CoFHWorldItems.nuggetCopper, new AspectList().add(Aspect.METAL, 1));
			registerStack(CoFHWorldItems.nuggetTin, new AspectList().add(Aspect.METAL, 1));
			registerStack(CoFHWorldItems.nuggetSilver, new AspectList().add(Aspect.METAL, 1));
			registerStack(CoFHWorldItems.nuggetLead, new AspectList().add(Aspect.METAL, 1));
			registerStack(CoFHWorldItems.nuggetNickel, new AspectList().add(Aspect.METAL, 1));
			registerStack(CoFHWorldItems.nuggetPlatinum, new AspectList().add(Aspect.METAL, 1));
			registerStack(CoFHWorldItems.nuggetElectrum, new AspectList().add(Aspect.METAL, 1));
			registerStack(CoFHWorldItems.nuggetInvar, new AspectList().add(Aspect.METAL, 1));
			registerStack(CoFHWorldItems.nuggetEnderium, new AspectList().add(Aspect.METAL, 1));

			registerStack(CoFHWorldItems.florb, new AspectList().add(Aspect.VOID, 1).add(Aspect.SLIME, 2).add(Aspect.EARTH, 1));
			registerStack(CoFHWorldItems.florbMagmatic, new AspectList().add(Aspect.VOID, 1).add(Aspect.SLIME, 2).add(Aspect.EARTH, 1).add(Aspect.FIRE, 1));

			// registerStack(BlockStorage.blockCopper, new AspectList().add(Aspect.METAL, 11).add(Aspect.VOID, 5));
			// registerStack(BlockStorage.blockTin, new AspectList().add(Aspect.METAL, 11).add(Aspect.GREED, 5));
			// registerStack(BlockStorage.blockSilver, new AspectList().add(Aspect.METAL, 11).add(Aspect.GREED, 5));
			// registerStack(BlockStorage.blockLead, new AspectList().add(Aspect.METAL, 11).add(Aspect.ARMOR, 5));
			registerStack(BlockStorage.blockNickel, new AspectList().add(Aspect.METAL, 11).add(Aspect.VOID, 5));
			registerStack(BlockStorage.blockPlatinum, new AspectList().add(Aspect.METAL, 11).add(Aspect.GREED, 5));
			registerStack(BlockStorage.blockElectrum, new AspectList().add(Aspect.METAL, 11).add(Aspect.GREED, 5));
			registerStack(BlockStorage.blockInvar, new AspectList().add(Aspect.METAL, 11).add(Aspect.ARMOR, 5));
			// registerStack(BlockStorage.blockBronze, new AspectList().add(Aspect.METAL, 11).add(Aspect.CRAFT, 5));
			registerStack(BlockStorage.blockEnderium, new AspectList().add(Aspect.METAL, 11).add(Aspect.ELDRITCH, 5));

			registerStack(CoFHWorldItems.bucketRedstone,
					new AspectList().add(Aspect.ENERGY, 10).add(Aspect.MECHANISM, 8).add(Aspect.FIRE, 4).add(Aspect.WATER, 2));
			registerStack(CoFHWorldItems.bucketGlowstone, new AspectList().add(Aspect.LIGHT, 10).add(Aspect.SENSES, 2).add(Aspect.FIRE, 4).add(Aspect.WATER, 2));
			registerStack(CoFHWorldItems.bucketEnder, new AspectList().add(Aspect.ELDRITCH, 14).add(Aspect.TRAVEL, 8).add(Aspect.MAGIC, 2).add(Aspect.FIRE, 4)
					.add(Aspect.WATER, 2));
			registerStack(CoFHWorldItems.bucketPyrotheum, new AspectList().add(Aspect.ENERGY, 14).add(Aspect.FIRE, 16).add(Aspect.MAGIC, 2)
					.add(Aspect.WATER, 2));
			registerStack(CoFHWorldItems.bucketCryotheum, new AspectList().add(Aspect.ENERGY, 14).add(Aspect.COLD, 16).add(Aspect.MAGIC, 2)
					.add(Aspect.WATER, 2));
			registerStack(CoFHWorldItems.bucketCoal, new AspectList().add(Aspect.FIRE, 14).add(Aspect.ENERGY, 2).add(Aspect.WATER, 2));

			registerStack(
					CoFHWorldItems.bucketRedstone,
					new AspectList().add(Aspect.ENERGY, 5).add(Aspect.MECHANISM, 4).add(Aspect.FIRE, 2).add(Aspect.WATER, 1).add(Aspect.METAL, 8)
							.add(Aspect.VOID, 1));
			registerStack(CoFHWorldItems.bucketGlowstone, new AspectList().add(Aspect.LIGHT, 5).add(Aspect.SENSES, 1).add(Aspect.FIRE, 2).add(Aspect.WATER, 1)
					.add(Aspect.METAL, 8).add(Aspect.VOID, 1));
			registerStack(CoFHWorldItems.bucketEnder, new AspectList().add(Aspect.ELDRITCH, 7).add(Aspect.TRAVEL, 4).add(Aspect.MAGIC, 1).add(Aspect.FIRE, 2)
					.add(Aspect.WATER, 1).add(Aspect.METAL, 8).add(Aspect.VOID, 1));
			registerStack(CoFHWorldItems.bucketPyrotheum, new AspectList().add(Aspect.ENERGY, 7).add(Aspect.FIRE, 8).add(Aspect.MAGIC, 1).add(Aspect.WATER, 1)
					.add(Aspect.METAL, 8).add(Aspect.VOID, 1));
			registerStack(CoFHWorldItems.bucketCryotheum, new AspectList().add(Aspect.ENERGY, 7).add(Aspect.COLD, 8).add(Aspect.MAGIC, 1).add(Aspect.WATER, 1)
					.add(Aspect.METAL, 8).add(Aspect.VOID, 1));
			registerStack(CoFHWorldItems.bucketCoal,
					new AspectList().add(Aspect.FIRE, 6).add(Aspect.ENERGY, 1).add(Aspect.WATER, 1).add(Aspect.METAL, 8).add(Aspect.VOID, 1));

			registerStack(TEItems.slag, new AspectList().add(Aspect.EARTH, 2).add(Aspect.ENTROPY, 2));
			registerStack(TEItems.slagRich, new AspectList().add(Aspect.EARTH, 2).add(Aspect.ENTROPY, 2).add(Aspect.GREED, 2));
			registerStack(CoFHWorldItems.dustSulfur, new AspectList().add(Aspect.FIRE, 3).add(Aspect.EARTH, 1));
			registerStack(CoFHWorldItems.dustNiter, new AspectList().add(Aspect.EARTH, 1).add(Aspect.AIR, 3));
			registerStack(TEItems.crystalCinnabar, new AspectList().add(Aspect.EARTH, 2).add(Aspect.EXCHANGE, 1).add(Aspect.POISON, 1));
			registerStack(TEItems.woodchips, new AspectList().add(Aspect.TREE, 1).add(Aspect.ENTROPY, 1));
			registerStack(TEItems.sawdust, new AspectList().add(Aspect.TREE, 1).add(Aspect.ENTROPY, 1));
			registerStack(TEItems.sawdustCompressed, new AspectList().add(Aspect.TREE, 3).add(Aspect.ENTROPY, 3));
			registerStack(CoFHWorldItems.dustPyrotheum, new AspectList().add(Aspect.ENERGY, 4).add(Aspect.FIRE, 4).add(Aspect.MAGIC, 1));
			registerStack(CoFHWorldItems.dustCryotheum, new AspectList().add(Aspect.ENERGY, 4).add(Aspect.COLD, 4).add(Aspect.MAGIC, 1));
			registerStack(CoFHWorldItems.rodBlizz, new AspectList().add(Aspect.COLD, 4).add(Aspect.MAGIC, 2));
			registerStack(CoFHWorldItems.dustBlizz, new AspectList().add(Aspect.COLD, 2).add(Aspect.MAGIC, 1));

			registerStack(TEItems.pneumaticServo, new AspectList().add(Aspect.METAL, 4).add(Aspect.MECHANISM, 2).add(Aspect.ENERGY, 1).add(Aspect.MOTION, 4));
			registerStack(TEItems.powerCoilGold, new AspectList().add(Aspect.METAL, 1).add(Aspect.MECHANISM, 2).add(Aspect.ENERGY, 2));
			registerStack(TEItems.powerCoilSilver, new AspectList().add(Aspect.METAL, 1).add(Aspect.MECHANISM, 2).add(Aspect.ENERGY, 2));
			registerStack(TEItems.powerCoilElectrum, new AspectList().add(Aspect.METAL, 1).add(Aspect.MECHANISM, 2).add(Aspect.ENERGY, 2));

			registerStack(CoFHWorldItems.gearCopper, new AspectList().add(Aspect.METAL, 7).add(Aspect.EXCHANGE, 2).add(Aspect.MECHANISM, 2));
			registerStack(CoFHWorldItems.gearTin, new AspectList().add(Aspect.METAL, 7).add(Aspect.CRYSTAL, 2).add(Aspect.MECHANISM, 2));
			registerStack(CoFHWorldItems.gearElectrum, new AspectList().add(Aspect.METAL, 7).add(Aspect.GREED, 2).add(Aspect.MECHANISM, 2));
			registerStack(CoFHWorldItems.gearInvar, new AspectList().add(Aspect.METAL, 7).add(Aspect.ARMOR, 2).add(Aspect.MECHANISM, 2));
			registerStack(CoFHWorldItems.gearBronze, new AspectList().add(Aspect.METAL, 7).add(Aspect.TOOL, 2).add(Aspect.MECHANISM, 2));

			registerStack(BlockRockwool.rockWool, new AspectList().add(Aspect.CRAFT, 1).add(Aspect.CLOTH, 2).add(Aspect.ENTROPY, 2));
			registerStack(BlockGlass.glassHardened, new AspectList().add(Aspect.FIRE, 1).add(Aspect.ARMOR, 1).add(Aspect.CRYSTAL, 1));

			registerStack(TEEquipment.toolInvarShears, new AspectList().add(Aspect.METAL, 4).add(Aspect.HARVEST, 4).add(Aspect.ARMOR, 2));
			registerStack(TEEquipment.toolInvarFishingRod, new AspectList().add(Aspect.METAL, 1).add(Aspect.WATER, 1).add(Aspect.TOOL, 1));
			registerStack(TEEquipment.toolInvarSickle, new AspectList().add(Aspect.METAL, 5).add(Aspect.ARMOR, 4).add(Aspect.HARVEST, 4));
			registerStack(TEEquipment.toolInvarBattleWrench,
					new AspectList().add(Aspect.METAL, 6).add(Aspect.TOOL, 1).add(Aspect.MECHANISM, 1).add(Aspect.WEAPON, 3));

			registerStack(TEItems.toolWrench, new AspectList().add(Aspect.METAL, 4).add(Aspect.TOOL, 1));
			registerStack(TEItems.toolMultimeter, new AspectList().add(Aspect.METAL, 4).add(Aspect.ENERGY, 2).add(Aspect.MECHANISM, 3).add(Aspect.SENSES, 2)
					.add(Aspect.TOOL, 2));

			registerStack(BlockMachine.machineFrame, new AspectList().add(Aspect.METAL, 4).add(Aspect.CRYSTAL, 2).add(Aspect.MECHANISM, 4));
			registerStack(BlockMachine.furnace, new AspectList().add(Aspect.METAL, 8).add(Aspect.CRYSTAL, 2).add(Aspect.MECHANISM, 6).add(Aspect.ENERGY, 2)
					.add(Aspect.FIRE, 4).add(Aspect.CRAFT, 2));
			registerStack(BlockMachine.pulverizer, new AspectList().add(Aspect.METAL, 8).add(Aspect.CRYSTAL, 2).add(Aspect.MECHANISM, 6).add(Aspect.ENERGY, 2)
					.add(Aspect.ENTROPY, 4).add(Aspect.MOTION, 2));
			registerStack(BlockMachine.sawmill, new AspectList().add(Aspect.METAL, 8).add(Aspect.CRYSTAL, 2).add(Aspect.MECHANISM, 6).add(Aspect.ENERGY, 2)
					.add(Aspect.TOOL, 4).add(Aspect.TREE, 2));
			registerStack(BlockMachine.smelter, new AspectList().add(Aspect.METAL, 8).add(Aspect.CRYSTAL, 2).add(Aspect.MECHANISM, 6).add(Aspect.ENERGY, 2)
					.add(Aspect.FIRE, 4).add(Aspect.EXCHANGE, 2));
			registerStack(BlockMachine.crucible, new AspectList().add(Aspect.METAL, 8).add(Aspect.CRYSTAL, 2).add(Aspect.MECHANISM, 6).add(Aspect.ENERGY, 2)
					.add(Aspect.FIRE, 4).add(Aspect.WATER, 2));
			registerStack(BlockMachine.transposer, new AspectList().add(Aspect.METAL, 8).add(Aspect.CRYSTAL, 2).add(Aspect.MECHANISM, 6).add(Aspect.ENERGY, 2)
					.add(Aspect.WATER, 4).add(Aspect.MOTION, 2));
			registerStack(
					BlockMachine.iceGen,
					new AspectList().add(Aspect.METAL, 8).add(Aspect.CRYSTAL, 2).add(Aspect.MECHANISM, 6).add(Aspect.ENERGY, 2).add(Aspect.COLD, 4)
							.add(Aspect.WATER, 2));
			registerStack(BlockMachine.rockGen, new AspectList().add(Aspect.METAL, 10).add(Aspect.CRYSTAL, 4).add(Aspect.MECHANISM, 6).add(Aspect.MOTION, 2)
					.add(Aspect.ENTROPY, 4).add(Aspect.WATER, 1).add(Aspect.FIRE, 1));
			registerStack(BlockMachine.waterGen, new AspectList().add(Aspect.METAL, 10).add(Aspect.CRYSTAL, 4).add(Aspect.MECHANISM, 6).add(Aspect.MOTION, 2)
					.add(Aspect.WATER, 4).add(Aspect.CRAFT, 1).add(Aspect.VOID, 1));
			registerStack(BlockMachine.assembler, new AspectList().add(Aspect.METAL, 8).add(Aspect.CRYSTAL, 2).add(Aspect.MECHANISM, 6).add(Aspect.ENERGY, 2)
					.add(Aspect.CRAFT, 4).add(Aspect.MOTION, 2));
			registerStack(BlockMachine.charger, new AspectList().add(Aspect.METAL, 8).add(Aspect.CRYSTAL, 2).add(Aspect.MECHANISM, 6).add(Aspect.ENERGY, 2)
					.add(Aspect.ENERGY, 4).add(Aspect.EXCHANGE, 2));

			registerStack(BlockDevice.workbench, new AspectList().add(Aspect.VOID, 4).add(Aspect.METAL, 8).add(Aspect.MIND, 1).add(Aspect.CRAFT, 1));
			registerStack(BlockDevice.activator, new AspectList().add(Aspect.MOTION, 8).add(Aspect.METAL, 6).add(Aspect.MECHANISM, 4).add(Aspect.ENERGY, 1)
					.add(Aspect.CRYSTAL, 1).add(Aspect.TOOL, 6).add(Aspect.VOID, 2));
			registerStack(
					BlockDevice.breaker,
					new AspectList().add(Aspect.MOTION, 8).add(Aspect.METAL, 6).add(Aspect.MECHANISM, 4).add(Aspect.ENERGY, 1).add(Aspect.CRYSTAL, 1)
							.add(Aspect.MINE, 6).add(Aspect.TOOL, 2));
			registerStack(BlockDevice.nullifier, new AspectList().add(Aspect.METAL, 6).add(Aspect.MECHANISM, 2).add(Aspect.ENERGY, 1).add(Aspect.MOTION, 4)
					.add(Aspect.CRYSTAL, 1).add(Aspect.FIRE, 3).add(Aspect.VOID, 6));

			registerStack(BlockDynamo.dynamoSteam, new AspectList().add(Aspect.METAL, 6).add(Aspect.MECHANISM, 4).add(Aspect.ENERGY, 4).add(Aspect.MOTION, 4)
					.add(Aspect.FIRE, 4).add(Aspect.WATER, 2));
			registerStack(BlockDynamo.dynamoMagmatic, new AspectList().add(Aspect.METAL, 6).add(Aspect.MECHANISM, 4).add(Aspect.ENERGY, 4)
					.add(Aspect.MOTION, 4).add(Aspect.FIRE, 4).add(Aspect.ENTROPY, 2));
			registerStack(BlockDynamo.dynamoCompression,
					new AspectList().add(Aspect.METAL, 6).add(Aspect.MECHANISM, 4).add(Aspect.ENERGY, 8).add(Aspect.MOTION, 4).add(Aspect.WATER, 2));
			registerStack(BlockDynamo.dynamoReactant, new AspectList().add(Aspect.METAL, 6).add(Aspect.MECHANISM, 4).add(Aspect.ENERGY, 6)
					.add(Aspect.MOTION, 4).add(Aspect.FIRE, 2).add(Aspect.WATER, 2));

			registerStack(BlockEnergyCell.cellBasicFrame,
					new AspectList().add(Aspect.METAL, 6).add(Aspect.ORDER, 2).add(Aspect.CRYSTAL, 2).add(Aspect.ENERGY, 5).add(Aspect.MECHANISM, 3));
			registerStack(BlockEnergyCell.cellReinforcedFrameEmpty,
					new AspectList().add(Aspect.METAL, 6).add(Aspect.FIRE, 2).add(Aspect.ARMOR, 2).add(Aspect.CRYSTAL, 6));
			registerStack(
					BlockEnergyCell.cellReinforcedFrameFull,
					new AspectList().add(Aspect.METAL, 6).add(Aspect.FIRE, 6).add(Aspect.ARMOR, 2).add(Aspect.CRYSTAL, 6).add(Aspect.ENERGY, 20)
							.add(Aspect.MECHANISM, 16).add(Aspect.WATER, 2));

			registerStack(BlockEnergyCell.cellBasic, new AspectList().add(Aspect.METAL, 10).add(Aspect.ORDER, 2).add(Aspect.CRYSTAL, 2).add(Aspect.ENERGY, 7)
					.add(Aspect.MECHANISM, 5).add(Aspect.EXCHANGE, 3));
			registerStack(
					BlockEnergyCell.cellHardened,
					new AspectList().add(Aspect.METAL, 16).add(Aspect.ORDER, 2).add(Aspect.CRYSTAL, 2).add(Aspect.ENERGY, 10).add(Aspect.MECHANISM, 5)
							.add(Aspect.EXCHANGE, 3).add(Aspect.ARMOR, 2));
			registerStack(BlockEnergyCell.cellReinforced, new AspectList().add(Aspect.METAL, 10).add(Aspect.FIRE, 6).add(Aspect.ARMOR, 2)
					.add(Aspect.CRYSTAL, 6).add(Aspect.ENERGY, 25).add(Aspect.MECHANISM, 20).add(Aspect.WATER, 2).add(Aspect.ORDER, 2));
			registerStack(BlockEnergyCell.cellResonant, new AspectList().add(Aspect.METAL, 16).add(Aspect.FIRE, 6).add(Aspect.ARMOR, 2).add(Aspect.CRYSTAL, 6)
					.add(Aspect.ENERGY, 25).add(Aspect.MECHANISM, 20).add(Aspect.WATER, 2).add(Aspect.ORDER, 2).add(Aspect.ELDRITCH, 2));

			registerStack(BlockTank.tankBasic, new AspectList().add(Aspect.VOID, 4).add(Aspect.CRYSTAL, 4).add(Aspect.METAL, 2));
			registerStack(BlockTank.tankHardened, new AspectList().add(Aspect.VOID, 8).add(Aspect.CRYSTAL, 4).add(Aspect.METAL, 8).add(Aspect.ARMOR, 2));
			registerStack(BlockTank.tankReinforced,
					new AspectList().add(Aspect.VOID, 12).add(Aspect.CRYSTAL, 6).add(Aspect.METAL, 8).add(Aspect.ARMOR, 4).add(Aspect.FIRE, 2));
			registerStack(
					BlockTank.tankResonant,
					new AspectList().add(Aspect.VOID, 16).add(Aspect.CRYSTAL, 6).add(Aspect.METAL, 14).add(Aspect.ARMOR, 4).add(Aspect.FIRE, 2)
							.add(Aspect.ELDRITCH, 2));

			registerStack(BlockStrongbox.strongboxBasic, new AspectList().add(Aspect.VOID, 4).add(Aspect.METAL, 6).add(Aspect.CRYSTAL, 2));
			registerStack(BlockStrongbox.strongboxHardened,
					new AspectList().add(Aspect.VOID, 8).add(Aspect.METAL, 12).add(Aspect.CRYSTAL, 2).add(Aspect.ARMOR, 2));
			registerStack(BlockStrongbox.strongboxReinforced,
					new AspectList().add(Aspect.VOID, 12).add(Aspect.METAL, 12).add(Aspect.CRYSTAL, 4).add(Aspect.ARMOR, 4).add(Aspect.FIRE, 2));
			registerStack(
					BlockStrongbox.strongboxResonant,
					new AspectList().add(Aspect.VOID, 16).add(Aspect.METAL, 18).add(Aspect.CRYSTAL, 4).add(Aspect.ARMOR, 4).add(Aspect.FIRE, 2)
							.add(Aspect.ELDRITCH, 2));

			registerStack(BlockTesseract.tesseractFrameEmpty,
					new AspectList().add(Aspect.METAL, 6).add(Aspect.FIRE, 2).add(Aspect.ARMOR, 2).add(Aspect.CRYSTAL, 6).add(Aspect.ELDRITCH, 4));
			registerStack(
					BlockTesseract.tesseractFrameFull,
					new AspectList().add(Aspect.METAL, 6).add(Aspect.FIRE, 6).add(Aspect.ARMOR, 2).add(Aspect.CRYSTAL, 6).add(Aspect.ELDRITCH, 18)
							.add(Aspect.TRAVEL, 8).add(Aspect.MAGIC, 2).add(Aspect.WATER, 2));
			registerStack(
					BlockTesseract.tesseract,
					new AspectList().add(Aspect.METAL, 6).add(Aspect.FIRE, 6).add(Aspect.ARMOR, 2).add(Aspect.CRYSTAL, 6).add(Aspect.ELDRITCH, 18)
							.add(Aspect.TRAVEL, 12).add(Aspect.MAGIC, 2).add(Aspect.WATER, 2));

			registerStack(
					BlockLamp.lampFrame,
					new AspectList().add(Aspect.FIRE, 1).add(Aspect.ARMOR, 1).add(Aspect.CRYSTAL, 2).add(Aspect.ENERGY, 2).add(Aspect.METAL, 2)
							.add(Aspect.MECHANISM, 1));
			registerStack(
					BlockLamp.lamp,
					new AspectList().add(Aspect.FIRE, 5).add(Aspect.ARMOR, 1).add(Aspect.CRYSTAL, 2).add(Aspect.ENERGY, 2).add(Aspect.METAL, 2)
							.add(Aspect.MECHANISM, 1).add(Aspect.LIGHT, 10).add(Aspect.SENSES, 2).add(Aspect.WATER, 2));

			registerStack(TEItems.diagramSchematic, new AspectList().add(Aspect.MIND, 3));

			registerStack(TEItems.capacitorPotato,
					new AspectList().add(Aspect.HUNGER, 1).add(Aspect.EARTH, 1).add(Aspect.CROP, 1).add(Aspect.ENERGY, 3).add(Aspect.MECHANISM, 1));
			registerStack(
					TEItems.capacitorBasic,
					new AspectList().add(Aspect.METAL, 4).add(Aspect.ORDER, 2).add(Aspect.EXCHANGE, 2).add(Aspect.ENERGY, 6).add(Aspect.MECHANISM, 3)
							.add(Aspect.FIRE, 1));
			registerStack(TEItems.capacitorHardened, new AspectList().add(Aspect.METAL, 7).add(Aspect.ORDER, 2).add(Aspect.EXCHANGE, 2).add(Aspect.ENERGY, 9)
					.add(Aspect.MECHANISM, 3).add(Aspect.FIRE, 1).add(Aspect.ARMOR, 1));
			registerStack(
					TEItems.capacitorReinforced,
					new AspectList().add(Aspect.METAL, 10).add(Aspect.ORDER, 2).add(Aspect.EXCHANGE, 2).add(Aspect.ENERGY, 12).add(Aspect.MECHANISM, 3)
							.add(Aspect.FIRE, 1).add(Aspect.ARMOR, 1).add(Aspect.CRYSTAL, 4));
			registerStack(TEItems.capacitorResonant, new AspectList().add(Aspect.METAL, 13).add(Aspect.ORDER, 2).add(Aspect.EXCHANGE, 2).add(Aspect.ENERGY, 15)
					.add(Aspect.MECHANISM, 3).add(Aspect.FIRE, 2).add(Aspect.ARMOR, 1).add(Aspect.CRYSTAL, 4).add(Aspect.ELDRITCH, 1).add(Aspect.MAGIC, 2));

			ThermalExpansion.log.info("Thaumcraft Plugin Enabled.");
		}
	}

	public static void registerStack(ItemStack theStack, AspectList theAspects) {

		if (theStack != null) {
			ThaumcraftApi.registerObjectTag(theStack, theAspects);
		}
	}

}
