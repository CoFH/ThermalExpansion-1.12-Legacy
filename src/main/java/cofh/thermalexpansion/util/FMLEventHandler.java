package cofh.thermalexpansion.util;

import cofh.thermalexpansion.block.device.BlockDevice;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.core.TEAchievements;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.network.PacketTEBase;
import cofh.thermalexpansion.util.crafting.ChargerManager;
import cofh.thermalexpansion.util.crafting.CrucibleManager;
import cofh.thermalexpansion.util.crafting.ExtruderManager;
import cofh.thermalexpansion.util.crafting.FurnaceManager;
import cofh.thermalexpansion.util.crafting.PrecipitatorManager;
import cofh.thermalexpansion.util.crafting.PulverizerManager;
import cofh.thermalexpansion.util.crafting.SawmillManager;
import cofh.thermalexpansion.util.crafting.SmelterManager;
import cofh.thermalexpansion.util.crafting.TransposerManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLModIdMappingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;


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
		PacketTEBase.sendConfigSyncPacketToClient(event.player);
		handleIdMappingEvent(null);
	}

	@EventHandler
	public void handleIdMappingEvent(FMLModIdMappingEvent event) {

		FurnaceManager.refreshRecipes();
		PulverizerManager.refreshRecipes();
		SawmillManager.refreshRecipes();
		SmelterManager.refreshRecipes();
		CrucibleManager.refreshRecipes();
		TransposerManager.refreshRecipes();
		PrecipitatorManager.refreshRecipes();
		ExtruderManager.refreshRecipes();
		ChargerManager.refreshRecipes();

		BlockDevice.refreshItemStacks();
		BlockDynamo.refreshItemStacks();
		BlockMachine.refreshItemStacks();
	}

}
