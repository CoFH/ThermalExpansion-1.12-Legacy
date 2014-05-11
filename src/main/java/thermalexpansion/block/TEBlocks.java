package thermalexpansion.block;

import cofh.api.core.IInitializer;
import cpw.mods.fml.common.registry.GameRegistry;

import java.util.ArrayList;

import net.minecraft.block.Block;

import thermalexpansion.block.device.BlockDevice;
import thermalexpansion.block.device.ItemBlockDevice;
import thermalexpansion.block.dynamo.BlockDynamo;
import thermalexpansion.block.dynamo.ItemBlockDynamo;
import thermalexpansion.block.ender.BlockTesseract;
import thermalexpansion.block.ender.ItemBlockTesseract;
import thermalexpansion.block.energycell.BlockEnergyCell;
import thermalexpansion.block.energycell.ItemBlockEnergyCell;
import thermalexpansion.block.lamp.BlockLamp;
import thermalexpansion.block.lamp.ItemBlockLamp;
import thermalexpansion.block.machine.BlockMachine;
import thermalexpansion.block.machine.ItemBlockMachine;
import thermalexpansion.block.simple.BlockGlass;
import thermalexpansion.block.simple.BlockRockwool;
import thermalexpansion.block.simple.ItemBlockGlass;
import thermalexpansion.block.simple.ItemBlockRockwool;
import thermalexpansion.block.strongbox.BlockStrongbox;
import thermalexpansion.block.strongbox.ItemBlockStrongbox;
import thermalexpansion.block.tank.BlockTank;
import thermalexpansion.block.tank.ItemBlockTank;

public class TEBlocks {

	public static ArrayList<IInitializer> blockList = new ArrayList();

	public static void preInit() {

	}

	public static void initialize() {

		blockMachine = addBlock(new BlockMachine());
		blockDevice = addBlock(new BlockDevice());
		blockDynamo = addBlock(new BlockDynamo());
		blockEnergyCell = addBlock(new BlockEnergyCell());
		blockTank = addBlock(new BlockTank());
		blockStrongbox = addBlock(new BlockStrongbox());

		blockTesseract = addBlock(new BlockTesseract());
		// blockPlate = addBlock(new BlockPlate(ThermalExpansion.config.getBlockId("Plate")));
		blockLamp = addBlock(new BlockLamp());
		blockGlass = addBlock(new BlockGlass());
		blockRockwool = addBlock(new BlockRockwool());
		// blockInvisible = addBlock(new BlockInvisible(ThermalExpansion.config.getBlockId("Invisible")));

		GameRegistry.registerBlock(blockMachine, ItemBlockMachine.class, "Machine");
		GameRegistry.registerBlock(blockDevice, ItemBlockDevice.class, "Device");
		GameRegistry.registerBlock(blockDynamo, ItemBlockDynamo.class, "Dynamo");
		GameRegistry.registerBlock(blockEnergyCell, ItemBlockEnergyCell.class, "EnergyCell");
		GameRegistry.registerBlock(blockTank, ItemBlockTank.class, "Tank");
		GameRegistry.registerBlock(blockStrongbox, ItemBlockStrongbox.class, "Strongbox");
		GameRegistry.registerBlock(blockTesseract, ItemBlockTesseract.class, "Tesseract");
		// GameRegistry.registerBlock(blockPlate, ItemBlockPlate.class, "Plate");
		GameRegistry.registerBlock(blockLamp, ItemBlockLamp.class, "Lamp");
		GameRegistry.registerBlock(blockGlass, ItemBlockGlass.class, "Glass");
		GameRegistry.registerBlock(blockRockwool, ItemBlockRockwool.class, "Rockwool");
		// GameRegistry.registerBlock(blockInvisible, "Invisible");

		for (IInitializer initializer : blockList) {
			initializer.initialize();
		}
	}

	public static void postInit() {

		for (IInitializer initializer : blockList) {
			initializer.postInit();
		}
		blockList.clear();
	}

	public static Block addBlock(Block block) {

		blockList.add((IInitializer) block);
		return block;
	}

	public static Block blockMachine;
	public static Block blockDevice;
	public static Block blockDynamo;
	public static Block blockEnergyCell;
	public static Block blockTank;
	public static Block blockStrongbox;
	public static Block blockTesseract;
	public static Block blockLamp;
	public static Block blockStorage;
	public static Block blockGlass;
	public static Block blockRockwool;
	public static Block blockInvisible;

}
