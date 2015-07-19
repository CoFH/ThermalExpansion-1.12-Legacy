package cofh.thermalexpansion.block.device;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileAugmentable;
import cpw.mods.fml.relauncher.Side;

public abstract class TileDeviceBase extends TileAugmentable {

	protected static final SideConfig[] defaultSideConfig = new SideConfig[BlockDevice.Types.values().length];
	public static boolean[] enableSecurity = { true, true, true, true, true, true };

	public static void configure() {

		for (int i = 0; i < BlockDevice.Types.values().length; i++) {
			String name = StringHelper.titleCase(BlockDevice.NAMES[i]);
			String comment = "Enable this to allow for " + name + "s to be securable.";
			enableSecurity[i] = ThermalExpansion.config.get("Security", "Device." + name + ".Securable", enableSecurity[i], comment);
		}
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.device." + BlockDevice.NAMES[getType()] + ".name";
	}

	@Override
	public boolean enableSecurity() {

		return enableSecurity[getType()];
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

}
