package cofh.thermalexpansion.init;

import cofh.api.core.IInitializer;
import cofh.thermalexpansion.block.device.BlockDevice;
import cofh.thermalexpansion.block.device.ItemBlockDevice;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.block.dynamo.ItemBlockDynamo;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.block.machine.ItemBlockMachine;
import cofh.thermalexpansion.block.simple.*;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;

public class TEBlocksOld {

	private TEBlocksOld() {

	}

	public static ArrayList<IInitializer> blockList = new ArrayList<IInitializer>();

	public static void preInit() {

		blockMachine = (BlockMachine) addBlock(new BlockMachine());
		blockDevice = (BlockDevice) addBlock(new BlockDevice());
		blockDynamo = addBlock(new BlockDynamo());
		blockFrame = addBlock(new BlockFrame());

		GameRegistry.registerBlock(blockMachine, ItemBlockMachine.class, "Machine");
		GameRegistry.registerBlock(blockDevice, ItemBlockDevice.class, "Device");
		GameRegistry.registerBlock(blockDynamo, ItemBlockDynamo.class, "Dynamo");
		GameRegistry.registerBlock(blockFrame, ItemBlockFrame.class, "Frame");

		GameRegistry.registerBlock(blockAirSignal, null, "FakeAirSignal");
		GameRegistry.registerBlock(blockAirLight, null, "FakeAirLight");
		GameRegistry.registerBlock(blockAirForce, null, "FakeAirForce");
		GameRegistry.registerBlock(blockAirBarrier, null, "FakeAirBarrier");

		for (IInitializer initializer : blockList) {
			initializer.preInit();
		}

	}

	public static void initialize() {

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

	public static BlockMachine blockMachine;
	public static BlockDevice blockDevice;
	public static Block blockDynamo;
	public static Block blockFrame;

	public static Block blockAirSignal;
	public static Block blockAirLight;
	public static Block blockAirForce;
	public static Block blockAirBarrier;

}
