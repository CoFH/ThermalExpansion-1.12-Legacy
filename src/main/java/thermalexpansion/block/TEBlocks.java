package thermalexpansion.block;

import cofh.api.core.IInitializer;
import cpw.mods.fml.common.registry.GameRegistry;

import java.util.ArrayList;

import net.minecraft.block.Block;

import thermalexpansion.block.cache.BlockCache;
import thermalexpansion.block.cache.ItemBlockCache;
import thermalexpansion.block.cell.BlockCell;
import thermalexpansion.block.cell.ItemBlockCell;
import thermalexpansion.block.device.BlockDevice;
import thermalexpansion.block.device.ItemBlockDevice;
import thermalexpansion.block.dynamo.BlockDynamo;
import thermalexpansion.block.dynamo.ItemBlockDynamo;
import thermalexpansion.block.ender.BlockEnder;
import thermalexpansion.block.ender.ItemBlockEnder;
import thermalexpansion.block.light.BlockLight;
import thermalexpansion.block.light.ItemBlockLight;
import thermalexpansion.block.machine.BlockMachine;
import thermalexpansion.block.machine.ItemBlockMachine;
import thermalexpansion.block.plate.BlockPlate;
import thermalexpansion.block.plate.ItemBlockPlate;
import thermalexpansion.block.simple.BlockAirBarrier;
import thermalexpansion.block.simple.BlockAirLight;
import thermalexpansion.block.simple.BlockAirSignal;
import thermalexpansion.block.simple.BlockFrame;
import thermalexpansion.block.simple.BlockGlass;
import thermalexpansion.block.simple.BlockRockwool;
import thermalexpansion.block.simple.ItemBlockFrame;
import thermalexpansion.block.simple.ItemBlockGlass;
import thermalexpansion.block.simple.ItemBlockRockwool;
import thermalexpansion.block.sponge.BlockSponge;
import thermalexpansion.block.sponge.ItemBlockSponge;
import thermalexpansion.block.strongbox.BlockStrongbox;
import thermalexpansion.block.strongbox.ItemBlockStrongbox;
import thermalexpansion.block.tank.BlockTank;
import thermalexpansion.block.tank.ItemBlockTank;

public class TEBlocks {

	private TEBlocks() {

	}

	public static ArrayList<IInitializer> blockList = new ArrayList<IInitializer>();

	public static void preInit() {

	}

	public static void initialize() {

		blockMachine = addBlock(new BlockMachine());
		blockDevice = addBlock(new BlockDevice());
		blockDynamo = addBlock(new BlockDynamo());
		blockCell = addBlock(new BlockCell());
		blockTank = addBlock(new BlockTank());
		blockStrongbox = addBlock(new BlockStrongbox());
		blockCache = addBlock(new BlockCache());
		blockTesseract = addBlock(new BlockEnder());
		blockPlate = addBlock(new BlockPlate());
		blockLight = addBlock(new BlockLight());
		blockFrame = addBlock(new BlockFrame());
		blockGlass = addBlock(new BlockGlass());
		blockRockwool = addBlock(new BlockRockwool());
		blockSponge = addBlock(new BlockSponge());

		blockAirSignal = new BlockAirSignal();
		blockAirLight = new BlockAirLight();
		blockAirBarrier = new BlockAirBarrier();

		GameRegistry.registerBlock(blockMachine, ItemBlockMachine.class, "Machine");
		GameRegistry.registerBlock(blockDevice, ItemBlockDevice.class, "Device");
		GameRegistry.registerBlock(blockDynamo, ItemBlockDynamo.class, "Dynamo");
		GameRegistry.registerBlock(blockCell, ItemBlockCell.class, "Cell");
		GameRegistry.registerBlock(blockTank, ItemBlockTank.class, "Tank");
		GameRegistry.registerBlock(blockStrongbox, ItemBlockStrongbox.class, "Strongbox");
		GameRegistry.registerBlock(blockCache, ItemBlockCache.class, "Cache");
		GameRegistry.registerBlock(blockTesseract, ItemBlockEnder.class, "Tesseract");
		GameRegistry.registerBlock(blockPlate, ItemBlockPlate.class, "Plate");
		GameRegistry.registerBlock(blockLight, ItemBlockLight.class, "Light");
		GameRegistry.registerBlock(blockFrame, ItemBlockFrame.class, "Frame");
		GameRegistry.registerBlock(blockGlass, ItemBlockGlass.class, "Glass");
		GameRegistry.registerBlock(blockRockwool, ItemBlockRockwool.class, "Rockwool");
		GameRegistry.registerBlock(blockSponge, ItemBlockSponge.class, "Sponge");

		GameRegistry.registerBlock(blockAirSignal, null, "FakeAirSignal");
		GameRegistry.registerBlock(blockAirLight, null, "FakeAirLight");
		GameRegistry.registerBlock(blockAirBarrier, null, "FakeAirBarrier");

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
	public static Block blockCell;
	public static Block blockTank;
	public static Block blockStrongbox;
	public static Block blockCache;
	public static Block blockTesseract;
	public static Block blockPlate;
	public static Block blockLight;
	public static Block blockFrame;
	public static Block blockGlass;
	public static Block blockRockwool;
	public static Block blockSponge;

	public static Block blockAirSignal;
	public static Block blockAirLight;
	public static Block blockAirBarrier;

}
