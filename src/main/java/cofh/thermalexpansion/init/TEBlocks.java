package cofh.thermalexpansion.init;

import cofh.core.util.core.IInitializer;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.apparatus.BlockApparatus;
import cofh.thermalexpansion.block.device.BlockDevice;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.block.storage.BlockCache;
import cofh.thermalexpansion.block.storage.BlockCell;
import cofh.thermalexpansion.block.storage.BlockStrongbox;
import cofh.thermalexpansion.block.storage.BlockTank;

import java.util.ArrayList;

public class TEBlocks {

	private TEBlocks() {

	}

	public static void preInit() {

		blockMachine = new BlockMachine();
		blockApparatus = new BlockApparatus();
		blockDevice = new BlockDevice();
		blockDynamo = new BlockDynamo();

		blockCell = new BlockCell();
		blockTank = new BlockTank();
		blockCache = new BlockCache();
		blockStrongbox = new BlockStrongbox();

		initList.add(blockMachine);
		initList.add(blockApparatus);
		initList.add(blockDevice);
		initList.add(blockDynamo);

		initList.add(blockCell);
		initList.add(blockTank);
		initList.add(blockCache);
		initList.add(blockStrongbox);

		ThermalExpansion.proxy.addIModelRegister(blockMachine);
		ThermalExpansion.proxy.addIModelRegister(blockApparatus);
		ThermalExpansion.proxy.addIModelRegister(blockDevice);
		ThermalExpansion.proxy.addIModelRegister(blockDynamo);

		ThermalExpansion.proxy.addIModelRegister(blockCell);
		ThermalExpansion.proxy.addIModelRegister(blockTank);
		ThermalExpansion.proxy.addIModelRegister(blockCache);

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

	private static ArrayList<IInitializer> initList = new ArrayList<>();

	/* REFERENCES */
	public static BlockMachine blockMachine;
	public static BlockApparatus blockApparatus;
	public static BlockDevice blockDevice;
	public static BlockDynamo blockDynamo;

	public static BlockCell blockCell;
	public static BlockTank blockTank;
	public static BlockCache blockCache;
	public static BlockStrongbox blockStrongbox;

}
