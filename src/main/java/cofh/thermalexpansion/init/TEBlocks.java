package cofh.thermalexpansion.init;

import cofh.api.core.IInitializer;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.automaton.BlockAutomaton;
import cofh.thermalexpansion.block.device.BlockDevice;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.block.machine.BlockMachine;

import java.util.ArrayList;

public class TEBlocks {

	private TEBlocks() {

	}

	public static void preInit() {

		blockMachine = new BlockMachine();
		//blockAutomaton = new BlockAutomaton();
		//blockDevice = new BlockDevice();
		blockDynamo = new BlockDynamo();

		initList.add(blockMachine);
		//initList.add(blockAutomaton);
		//initList.add(blockDevice);
		initList.add(blockDynamo);

		ThermalExpansion.proxy.addIModelRegister(blockMachine);
		//ThermalExpansion.proxy.addIModelRegister(blockAutomaton);
		//ThermalExpansion.proxy.addIModelRegister(blockDevice);
		ThermalExpansion.proxy.addIModelRegister(blockDynamo);

		for (int i = 0; i < initList.size(); i++) {
			initList.get(i).preInit();
		}
	}

	public static void initialize() {

		for (int i = 0; i < initList.size(); i++) {
			initList.get(i).initialize();
		}
	}

	public static void postInit() {

		for (int i = 0; i < initList.size(); i++) {
			initList.get(i).postInit();
		}
		initList.clear();
	}

	static ArrayList<IInitializer> initList = new ArrayList<IInitializer>();

	/* REFERENCES */
	public static BlockMachine blockMachine;
	public static BlockAutomaton blockAutomaton;
	public static BlockDevice blockDevice;
	public static BlockDynamo blockDynamo;

}
