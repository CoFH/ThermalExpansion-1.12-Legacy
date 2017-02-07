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

	public boolean isAugmentable() {

		return false;
	}

	@Override
	public boolean enableSecurity() {

		return enableSecurity;
	}

	@Override
	public boolean sendRedstoneUpdates() {

		return true;
	}

	protected void setLevelFlags() {

		level = 0;
		hasAutoInput = false;
		hasAutoOutput = false;

		hasRedstoneControl = true;
		hasAdvRedstoneControl = false;
	}

	/* IReconfigurableFacing */
	@Override
	public boolean setFacing(int side) {

		if (side < 2 || side > 5) {
			return false;
		}
		facing = (byte) side;
		sideCache[facing] = 0;
		markDirty();
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	/* ISidedTexture */
	@Override
	public TextureAtlasSprite getTexture(int side, int layer, int pass) {

		if (layer == 0) {
			return side != facing ? TETextures.DEVICE_SIDE : redstoneControlOrDisable() ? TETextures.DEVICE_ACTIVE[getType()] : TETextures.DEVICE_FACE[getType()];
		} else if (side < 6) {
			return TETextures.CONFIG[sideConfig.sideTex[sideCache[side]]];
		}
		return TETextures.DEVICE_SIDE;
	}

}
