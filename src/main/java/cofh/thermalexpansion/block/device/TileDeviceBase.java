package cofh.thermalexpansion.block.device;

import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileReconfigurable;
import cofh.thermalexpansion.block.device.BlockDevice.Type;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileDeviceBase extends TileReconfigurable {

	public static final SideConfig[] SIDE_CONFIGS = new SideConfig[Type.values().length];
	public static final SideConfig[] ALT_SIDE_CONFIGS = new SideConfig[Type.values().length];

	public static final SlotConfig[] SLOT_CONFIGS = new SlotConfig[Type.values().length];
	public static final int[] LIGHT_VALUES = new int[Type.values().length];

	protected static boolean enableSecurity = true;
	public static boolean disableAutoInput = false;
	public static boolean disableAutoOutput = false;

	public static void config() {

		String category = "Device";
		String comment = "If TRUE, Devices are securable.";
		enableSecurity = ThermalExpansion.CONFIG.get(category, "Securable", true, comment);

		comment = "If TRUE, most Devices will no longer have Auto-Input functionality. Not recommended, but knock yourself out.";
		disableAutoInput = ThermalExpansion.CONFIG.get(category, "DisableAutoInput", disableAutoInput, comment);

		comment = "If TRUE, most Devices will no longer have Auto-Output functionality. Not recommended, but knock yourself out.";
		disableAutoOutput = ThermalExpansion.CONFIG.get(category, "DisableAutoOutput", disableAutoOutput, comment);
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

	@Override
	public int getLightValue() {

		return isActive ? LIGHT_VALUES[getType()] : 0;
	}

	@Override
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

		if (disableAutoInput) {
			hasAutoInput = false;
		}
		if (disableAutoOutput) {
			hasAutoOutput = false;
		}
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
	public boolean setFacing(int side, boolean alternate) {

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
	@SideOnly (Side.CLIENT)
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

	/* RENDERING */
	public boolean hasFluidUnderlay() {

		return false;
	}

	public FluidStack getRenderFluid() {

		return null;
	}

	public int getColorMask(BlockRenderLayer layer, EnumFacing side) {

		return 0xFFFFFFFF;
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
