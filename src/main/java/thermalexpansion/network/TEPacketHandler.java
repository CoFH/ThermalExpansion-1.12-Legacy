package thermalexpansion.network;

import cofh.api.tileentity.IRedstoneControl;
import cofh.core.CoFHProps;
import cofh.network.PacketHandler;

import java.util.logging.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.gui.container.ISetSchematic;

public class TEPacketHandler implements IGeneralPacketHandler {

	public static TEPacketHandler instance = new TEPacketHandler();

	public static int packetID;

	public enum PacketTypes {
		WRITE_SCHEM, RS_POWER_UPDATE, RS_CONFIG_UPDATE, CONFIG_SYNC
	}

	public static void initialize() {

		packetID = PacketHandler.getAvailablePacketIdAndRegister(instance);
	}

	@Override
	public void handlePacket(int id, Payload payload, EntityPlayer player) {

		try {
			int type = payload.getByte();

			switch (PacketTypes.values()[type]) {
			case RS_POWER_UPDATE:
				int coords[] = payload.getCoords();
				IRedstoneControl rs = (IRedstoneControl) player.worldObj.getTileEntity(coords[0], coords[1], coords[2]);
				rs.handlePowerUpdate(payload.getBool());
				return;
			case RS_CONFIG_UPDATE:
				coords = payload.getCoords();
				rs = (IRedstoneControl) player.worldObj.getTileEntity(coords[0], coords[1], coords[2]);
				rs.handleConfigUpdate(payload.getBool(), payload.getBool());
				return;
			case WRITE_SCHEM:
				if (player.openContainer instanceof ISetSchematic) {
					((ISetSchematic) player.openContainer).writeSchematic();
				}
				return;
			case CONFIG_SYNC:
				ThermalExpansion.instance.handleConfigSync(payload);
				return;
			default:
				ThermalExpansion.log.log(Level.SEVERE, "Unknown Packet! Internal: TEPH, ID: " + type);
			}
		} catch (Exception e) {
			ThermalExpansion.log.log(Level.SEVERE, "Packet payload failure! Please check your config files!");
			e.printStackTrace();
		}
	}

	public static void sendRSPowerUpdatePacketToClients(IRedstoneControl rs, World world, int x, int y, int z) {

		PacketUtils.sendToPlayers(Payload.getPayload(packetID).addByte(PacketTypes.RS_POWER_UPDATE.ordinal()).addCoords(x, y, z).addBool(rs.isPowered())
				.getPacket(), world, x, y, z, CoFHProps.NETWORK_UPDATE_RANGE);
	}

	public static void sendRSConfigUpdatePacketToServer(IRedstoneControl rs, int x, int y, int z) {

		PacketUtils.sendToServer(Payload.getPayload(packetID).addByte(PacketTypes.RS_CONFIG_UPDATE.ordinal()).addCoords(x, y, z)
				.addBool(rs.getControlDisable()).addBool(rs.getControlSetting()).getPacket());
	}

	public static void sendCreateSchematicPacketToServer() {

		PacketDispatcher.sendPacketToServer(Payload.getPayload(packetID).addByte(PacketTypes.WRITE_SCHEM.ordinal()).getPacket());
	}

	public static void sendConfigSyncPacketToClient(EntityPlayer player) {

		PacketUtils.sendToPlayer(ThermalExpansion.instance.getConfigSync(packetID).getPacket(), player);
	}

}
