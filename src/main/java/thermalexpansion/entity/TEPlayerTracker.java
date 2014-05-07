package thermalexpansion.entity;

import net.minecraft.entity.player.EntityPlayer;
import thermalexpansion.core.TEAchievements;
import thermalexpansion.core.TEProps;
import thermalexpansion.network.TEPacketHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class TEPlayerTracker implements IPlayerTracker {

	public static TEPlayerTracker instance = new TEPlayerTracker();

	public static void initialize() {

		GameRegistry.registerPlayerTracker(instance);
	}

	@Override
	public void onPlayerLogin(EntityPlayer player) {

		if (TEProps.enableAchievements) {
			player.addStat(TEAchievements.baseTE, 1);
		}
		TEPacketHandler.sendConfigSyncPacketToClient(player);
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {

	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {

	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {

	}

}
