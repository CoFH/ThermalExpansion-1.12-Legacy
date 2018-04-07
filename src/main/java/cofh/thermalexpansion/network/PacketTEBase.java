package cofh.thermalexpansion.network;

import cofh.core.network.PacketBase;
import cofh.core.network.PacketHandler;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.EntityPlayer;

public class PacketTEBase extends PacketBase {

	public static void initialize() {

		PacketHandler.INSTANCE.registerPacket(PacketTEBase.class);
	}

	public enum PacketTypes {
		CONFIG_SYNC
	}

	@Override
	public void handlePacket(EntityPlayer player, boolean isServer) {

		try {
			int type = getByte();
			switch (PacketTypes.values()[type]) {
				case CONFIG_SYNC:
					TEProps.handleConfigSync(this);
					return;
				default:
					ThermalExpansion.LOG.error("Unknown Packet! Internal: TEPH, ID: " + type);
			}
		} catch (Exception e) {
			ThermalExpansion.LOG.error("Packet payload failure! Please check your config files!", e);
		}
	}

	public static void sendConfigSyncPacketToClient(EntityPlayer player) {

		PacketHandler.sendTo(TEProps.getConfigSync(), player);
	}

	public static PacketBase getPacket(PacketTypes type) {

		return new PacketTEBase().addByte(type.ordinal());
	}

}
