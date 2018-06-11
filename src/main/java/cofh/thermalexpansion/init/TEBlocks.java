package cofh.thermalexpansion.init;

import cofh.core.util.core.IInitializer;
import cofh.thermalexpansion.block.apparatus.BlockApparatus;
import cofh.thermalexpansion.block.device.BlockDevice;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.block.storage.BlockCache;
import cofh.thermalexpansion.block.storage.BlockCell;
import cofh.thermalexpansion.block.storage.BlockStrongbox;
import cofh.thermalexpansion.block.storage.BlockTank;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class TEBlocks {

	public static final TEBlocks INSTANCE = new TEBlocks();

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

		// blockLight = new BlockLight();

		initList.add(blockMachine);
		initList.add(blockApparatus);
		initList.add(blockDevice);
		initList.add(blockDynamo);

		initList.add(blockCell);
		initList.add(blockTank);
		initList.add(blockCache);
		initList.add(blockStrongbox);

		// initList.add(blockLight);

		for (IInitializer init : initList) {
			init.preInit();
		}
		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}

	/* EVENT HANDLING */
	@SubscribeEvent
	public void registerRecipes(RegistryEvent.Register<IRecipe> event) {

		for (IInitializer init : initList) {
			init.initialize();
		}
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

	// public static BlockLight blockLight;

}
