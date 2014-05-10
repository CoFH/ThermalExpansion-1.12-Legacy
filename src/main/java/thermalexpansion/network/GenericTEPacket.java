package thermalexpansion.network;

import cofh.api.tileentity.IRedstoneControl;
import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.network.CoFHPacket;
import cofh.network.PacketHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.gui.container.ISetSchematic;

public class GenericTEPacket extends CoFHPacket {

	public static void initialize() {

		PacketHandler.instance.registerPacket(GenericTEPacket.class);
	}

	public enum PacketTypes {
		WRITE_SCHEM, RS_POWER_UPDATE, RS_CONFIG_UPDATE, CONFIG_SYNC
	}

	@Override
	public void handlePacket(EntityPlayer player, boolean isServer) {

		try {
			int type = getByte();

			switch (PacketTypes.values()[type]) {
			case RS_POWER_UPDATE:
				int coords[] = getCoords();
				IRedstoneControl rs = (IRedstoneControl) player.worldObj.getTileEntity(coords[0], coords[1], coords[2]);
				rs.setPowered(getBool());
				return;
			case RS_CONFIG_UPDATE:
				coords = getCoords();
				rs = (IRedstoneControl) player.worldObj.getTileEntity(coords[0], coords[1], coords[2]);
				rs.setControl(ControlMode.values()[getByte()]);
				return;
			case WRITE_SCHEM:
				if (player.openContainer instanceof ISetSchematic) {
					((ISetSchematic) player.openContainer).writeSchematic();
				}
				return;
			case CONFIG_SYNC:
				ThermalExpansion.instance.handleConfigSync(this);
				return;
			default:
				ThermalExpansion.log.error("Unknown Packet! Internal: TEPH, ID: " + type);
			}
		} catch (Exception e) {
			ThermalExpansion.log.error("Packet payload failure! Please check your config files!");
			e.printStackTrace();
		}
	}

	public static void sendRSPowerUpdatePacketToClients(IRedstoneControl rs, World world, int x, int y, int z) {

		PacketHandler.sendToAllAround(getPacket(PacketTypes.RS_POWER_UPDATE).addCoords(x, y, z).addBool(rs.isPowered()), world, x, y, z);
	}

	public static void sendRSConfigUpdatePacketToServer(IRedstoneControl rs, int x, int y, int z) {

		PacketHandler.sendToServer(getPacket(PacketTypes.RS_CONFIG_UPDATE).addCoords(x, y, z).addByte(rs.getControl().ordinal()));
	}

	public static void sendCreateSchematicPacketToServer() {

		PacketHandler.sendToServer(getPacket(PacketTypes.WRITE_SCHEM));
	}

	public static void sendConfigSyncPacketToClient(EntityPlayer player) {

		PacketHandler.sendTo(ThermalExpansion.instance.getConfigSync(), player);
	}

	public static CoFHPacket getPacket(PacketTypes theType) {

		return new GenericTEPacket().addByte(theType.ordinal());
	}

}
