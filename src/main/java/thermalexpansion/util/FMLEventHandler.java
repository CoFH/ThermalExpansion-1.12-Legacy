package thermalexpansion.util;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLModIdMappingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

import thermalexpansion.block.device.BlockDevice;
import thermalexpansion.block.dynamo.BlockDynamo;
import thermalexpansion.block.machine.BlockMachine;
import thermalexpansion.core.TEAchievements;
import thermalexpansion.core.TEProps;
import thermalexpansion.network.GenericTEPacket;
import thermalexpansion.util.crafting.CrucibleManager;
import thermalexpansion.util.crafting.FurnaceManager;
import thermalexpansion.util.crafting.PulverizerManager;
import thermalexpansion.util.crafting.SawmillManager;
import thermalexpansion.util.crafting.SmelterManager;

public class FMLEventHandler {

	public static FMLEventHandler instance = new FMLEventHandler();

	public static void initialize() {

		FMLCommonHandler.instance().bus().register(instance);
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {

		if (TEProps.enableAchievements) {
			event.player.addStat(TEAchievements.baseTE, 1);
		}
		GenericTEPacket.sendConfigSyncPacketToClient(event.player);
	}

	@EventHandler
	public void handleIdMappingEvent(FMLModIdMappingEvent event) {

		FurnaceManager.refreshRecipes();
		PulverizerManager.refreshRecipes();
		SawmillManager.refreshRecipes();
		SmelterManager.refreshRecipes();
		CrucibleManager.refreshRecipes();

		BlockDevice.refreshItemStacks();
		BlockDynamo.refreshItemStacks();
		BlockMachine.refreshItemStacks();
	}

}
