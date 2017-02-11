package cofh.thermalexpansion.init;

import cofh.api.core.IInitializer;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.automaton.BlockAutomaton;
import cofh.thermalexpansion.block.device.BlockDevice;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.block.storage.BlockCache;
import cofh.thermalexpansion.block.storage.BlockCell;
import cofh.thermalexpansion.block.storage.BlockTank;

import java.util.ArrayList;

public class TEBlocks {

	private TEBlocks() {

	}

	public static void preInit() {

		blockMachine = new BlockMachine();
		blockAutomaton = new BlockAutomaton();
		blockDevice = new BlockDevice();
		blockDynamo = new BlockDynamo();

		blockCache = new BlockCache();
		blockTank = new BlockTank();
		blockCell = new BlockCell();

		initList.add(blockMachine);
		initList.add(blockAutomaton);
		initList.add(blockDevice);
		initList.add(blockDynamo);

		initList.add(blockCache);
		initList.add(blockTank);
		initList.add(blockCell);

		ThermalExpansion.proxy.addIModelRegister(blockMachine);
		ThermalExpansion.proxy.addIModelRegister(blockAutomaton);
		ThermalExpansion.proxy.addIModelRegister(blockDevice);
		ThermalExpansion.proxy.addIModelRegister(blockDynamo);

		ThermalExpansion.proxy.addIModelRegister(blockCache);
		ThermalExpansion.proxy.addIModelRegister(blockTank);
		ThermalExpansion.proxy.addIModelRegister(blockCell);

		for (IInitializer init : initList) {
			init.preInit();
		}
	}

	public static void initialize() {

		for (IInitializer init : initList) {
			init.initialize();
		}
	}

	public static void postInit() {

		for (IInitializer init : initList) {
			init.postInit();
		}
		initList.clear();
	}

	private static ArrayList<IInitializer> initList = new ArrayList<IInitializer>();

	/* REFERENCES */
	public static BlockMachine blockMachine;
	public static BlockAutomaton blockAutomaton;
	public static BlockDevice blockDevice;
	public static BlockDynamo blockDynamo;

	public static BlockCache blockCache;
	public static BlockTank blockTank;
	public static BlockCell blockCell;

}
