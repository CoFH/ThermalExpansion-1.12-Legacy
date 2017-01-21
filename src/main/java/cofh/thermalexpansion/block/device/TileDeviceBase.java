package cofh.thermalexpansion.block.device;

import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TilePowered;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;

public abstract class TileDeviceBase extends TilePowered {

	protected static final SideConfig[] defaultSideConfig = new SideConfig[BlockDevice.Type.values().length];
	private static boolean enableSecurity = true;

	public static void config() {

		String comment = "Enable this to allow for Devices to be securable.";
		enableSecurity = ThermalExpansion.CONFIG.get("Security", "Device.All.Securable", true, comment);
	}

	public TileDeviceBase() {

		sideConfig = defaultSideConfig[this.getType()];
		setDefaultSides();
	}

	@Override
	public String getTileName() {

		return "tile.thermalexpansion.device." + BlockDevice.Type.byMetadata(getType()).getName() + ".name";
	}

	@Override
	public boolean enableSecurity() {

		return enableSecurity;
	}

	@Override
	public boolean sendRedstoneUpdates() {

		return true;
	}

	/* IReconfigurableFacing */
	@Override
	public boolean allowYAxisFacing() {

		return true;
	}

	@Override
	public boolean setFacing(int side) {

		if (side < 0 || side > 5) {
			return false;
		}
		facing = (byte) side;
		sideCache[facing] = 0;
		sideCache[facing ^ 1] = 1;
		markDirty();
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	/* ISidedTexture */
	@Override
	public TextureAtlasSprite getTexture(int side, int pass) {

		if (pass == 0) {
			return side != facing ? TETextures.DEVICE_SIDE : redstoneControlOrDisable() ? TETextures.DEVICE_ACTIVE[getType()] : TETextures.DEVICE_FACE[getType()];
		} else if (side < 6) {
			return TETextures.CONFIG[sideConfig.sideTex[sideCache[side]]];
		}
		return TETextures.DEVICE_SIDE;
	}

}
