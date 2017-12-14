package cofh.thermalexpansion.network;

import cofh.core.network.PacketBase;
import cofh.core.network.PacketHandler;
import cofh.thermalexpansion.ThermalExpansion;
import net.minecraft.entity.player.EntityPlayer;

public class PacketTEBase extends PacketBase {

	public static void initialize() {

		PacketHandler.INSTANCE.registerPacket(PacketTEBase.class);
	}

	public enum PacketTypes {
		ONFIG_SYNC
	}

	@Override
	public void handlePacket(EntityPlayer player, boolean isServer) {

		try {
			int type = getByte();
			switch (PacketTypes.values()[type]) {

				default:
					ThermalExpansion.LOG.error("Unknown Packet! Internal: TEPH, ID: " + type);
			}
		} catch (Exception e) {
			ThermalExpansion.LOG.error("Packet payload failure! Please check your config files!");
			e.printStackTrace();
		}
	}

	//	public static void sendConfigSyncPacketToClient(EntityPlayer player) {
	//
	//		PacketHandler.sendTo(ThermalExpansion.instance.getConfigSync(), player);
	//	}

	public static PacketBase getPacket(PacketTypes type) {

		return new PacketTEBase().addByte(type.ordinal());
	}

}
