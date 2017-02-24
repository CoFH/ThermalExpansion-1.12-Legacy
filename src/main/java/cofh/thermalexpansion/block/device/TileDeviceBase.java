package cofh.thermalexpansion.block.device;

import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TilePowered;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;

public abstract class TileDeviceBase extends TilePowered {

	public static final SideConfig[] SIDE_CONFIGS = new SideConfig[BlockDevice.Type.values().length];
	public static final SlotConfig[] SLOT_CONFIGS = new SlotConfig[BlockDevice.Type.values().length];
	public static final int[] LIGHT_VALUES = new int[BlockDevice.Type.values().length];

	private static boolean enableSecurity = true;

	public static void config() {

		String comment = "Enable this to allow for Devices to be securable.";
		enableSecurity = ThermalExpansion.CONFIG.get("Security", "Device.Securable", true, comment);
	}

	public TileDeviceBase() {

		sideConfig = SIDE_CONFIGS[this.getType()];
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

	@Override
	protected boolean setLevel(int level) {

		return false;
	}

	@Override
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
			return side != facing ? TETextures.DEVICE_SIDE : isActive ? TETextures.DEVICE_ACTIVE[getType()] : TETextures.DEVICE_FACE[getType()];
		} else if (side < 6) {
			return TETextures.CONFIG[sideConfig.sideTex[sideCache[side]]];
		}
		return TETextures.DEVICE_SIDE;
	}

	/* IUpgradeable */
	@Override
	public boolean installUpgrade(ItemStack upgrade) {

		return false;
	}

}
