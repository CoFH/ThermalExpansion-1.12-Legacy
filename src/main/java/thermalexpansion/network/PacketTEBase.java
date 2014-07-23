package thermalexpansion.network;

import cofh.api.tileentity.IRedstoneControl;
import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.api.tileentity.ISecurable;
import cofh.api.tileentity.ISecurable.AccessMode;
import cofh.network.PacketCoFHBase;
import cofh.network.PacketHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.gui.container.ISchematicContainer;

public class PacketTEBase extends PacketCoFHBase {

	public static void initialize() {

		PacketHandler.instance.registerPacket(PacketTEBase.class);
	}

	public enum PacketTypes {
		RS_POWER_UPDATE, RS_CONFIG_UPDATE, SECURITY_UPDATE, WRITE_SCHEM, CONFIG_SYNC
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
			case SECURITY_UPDATE:
				if (player.openContainer instanceof ISecurable) {
					((ISecurable) player.openContainer).setAccess(AccessMode.values()[getByte()]);
				}
			case WRITE_SCHEM:
				if (player.openContainer instanceof ISchematicContainer) {
					((ISchematicContainer) player.openContainer).writeSchematic();
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

	public static void sendSecurityPacketToServer(ISecurable securable) {

		PacketHandler.sendToServer(getPacket(PacketTypes.SECURITY_UPDATE).addByte(securable.getAccess().ordinal()));
	}

	public static void sendCreateSchematicPacketToServer() {

		PacketHandler.sendToServer(getPacket(PacketTypes.WRITE_SCHEM));
	}

	public static void sendConfigSyncPacketToClient(EntityPlayer player) {

		PacketHandler.sendTo(ThermalExpansion.instance.getConfigSync(), player);
	}

	public static PacketCoFHBase getPacket(PacketTypes theType) {

		return new PacketTEBase().addByte(theType.ordinal());
	}

}
