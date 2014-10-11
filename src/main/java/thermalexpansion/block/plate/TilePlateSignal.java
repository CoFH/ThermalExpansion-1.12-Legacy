package thermalexpansion.block.plate;

import cofh.core.network.PacketCoFHBase;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.Entity;

public class TilePlateSignal extends TilePlateBase {

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateSignal.class, "cofh.thermalexpansion.PlateSignal");
	}

	byte distance = 16;
	byte intensity = 15;

	@Override
	public int getType() {

		return BlockPlate.Types.SIGNAL.ordinal();
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addByte(distance);
		payload.addByte(intensity);

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addByte(distance);
		payload.addByte(intensity);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		distance = payload.getByte();
		intensity = payload.getByte();
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {

			distance = payload.getByte();
			intensity = payload.getByte();
		} else {

		}
	}

	@Override
	public void onEntityCollidedWithBlock(Entity theEntity) {

		int x = 0;
		int y = 0;
		int z = 0;

		switch (alignment) {
		case 0:
			y = -distance;
			break;
		case 1:
			y = distance;
			break;
		case 2:
			z = -distance;
			break;
		case 3:
			z = distance;
			break;
		case 4:
			x = -distance;
			break;
		case 5:
			x = distance;
			break;
		}
		if (worldObj.isAirBlock(xCoord + x, yCoord + y, zCoord + z)) {
			// worldObj.setBlock(xCoord + x, yCoord + y, zCoord + z, TEBlocks.blockSignal.blockID, intensity, 3);
		}
	}

}
