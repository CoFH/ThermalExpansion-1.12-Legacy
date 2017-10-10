package cofh.thermalexpansion.block.device;

import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TilePowered;
import cofh.thermalexpansion.block.device.BlockDevice.Type;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;

public abstract class TileDeviceBase extends TilePowered {

	public static final SideConfig[] SIDE_CONFIGS = new SideConfig[Type.values().length];
	public static final SlotConfig[] SLOT_CONFIGS = new SlotConfig[Type.values().length];
	public static final int[] LIGHT_VALUES = new int[Type.values().length];

	private static boolean enableSecurity = true;

	public static void config() {

		String category = "Device";
		String comment = "If TRUE, Devices are securable.";
		enableSecurity = ThermalExpansion.CONFIG.get(category, "Securable", true, comment);
	}

	public TileDeviceBase() {

		sideConfig = SIDE_CONFIGS[this.getType()];
		slotConfig = SLOT_CONFIGS[this.getType()];
		setDefaultSides();
		hasRedstoneControl = true;
	}

	@Override
	public String getTileName() {

		return "tile.thermalexpansion.device." + Type.byMetadata(getType()).getName() + ".name";
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

	@Override
	public void setDefaultSides() {

		sideCache = getDefaultSides();
		sideCache[facing] = 0;
	}

	@Override
	protected boolean setLevel(int level) {

		return false;
	}

	@Override
	protected void setLevelFlags() {

		level = 0;
		hasRedstoneControl = true;
	}

	protected void updateIfChanged(boolean curActive) {

		if (curActive != isActive) {
			if (LIGHT_VALUES[getType()] != 0) {
				updateLighting();
			}
			sendTilePacket(Side.CLIENT);
		}
	}

	/* IReconfigurableFacing */
	@Override
	public boolean setFacing(int side) {

		if (side < 2 || side > 5) {
			return false;
		}
		facing = (byte) side;
		sideCache[facing] = 0;
		markChunkDirty();
		sendTilePacket(Side.CLIENT);
		return true;
	}

	/* ISidedTexture */
	@Override
	public int getNumPasses() {

		return 2;
	}

	@Override
	public TextureAtlasSprite getTexture(int side, int pass) {

		if (pass == 0) {
			if (side == 0) {
				return TETextures.DEVICE_BOTTOM;
			} else if (side == 1) {
				return TETextures.DEVICE_TOP;
			}
			return side != facing ? TETextures.DEVICE_SIDE : isActive ? TETextures.DEVICE_ACTIVE[getType()] : TETextures.DEVICE_FACE[getType()];
		} else if (side < 6) {
			return TETextures.CONFIG[sideConfig.sideTypes[sideCache[side]]];
		}
		return TETextures.DEVICE_SIDE;
	}

	/* IUpgradeable */
	@Override
	public boolean canUpgrade(ItemStack upgrade) {

		return false;
	}

	@Override
	public boolean installUpgrade(ItemStack upgrade) {

		return false;
	}

}
