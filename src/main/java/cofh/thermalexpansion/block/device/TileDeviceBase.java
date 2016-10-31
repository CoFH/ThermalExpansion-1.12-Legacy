package cofh.thermalexpansion.block.device;

import cofh.core.render.IconRegistry;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileAugmentable;
import cofh.thermalexpansion.block.device.BlockDevice.Types;
import cofh.thermalexpansion.core.TEProps;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public abstract class TileDeviceBase extends TileAugmentable {

	protected static final SideConfig[] defaultSideConfig = new SideConfig[BlockDevice.Types.values().length];
	public static final boolean[] enableSecurity = new boolean[BlockDevice.Types.values().length];

	public static void configure() {

		for (int i = 0; i < BlockDevice.Types.values().length; i++) {
			String name = StringHelper.titleCase(BlockDevice.NAMES[i]);
			String comment = "Enable this to allow for " + name + "s to be securable.";
			enableSecurity[i] = ThermalExpansion.config.get("Security", "Device." + name + ".Securable", true, comment);
		}
		ThermalExpansion.config.removeProperty("Security", "Device." + StringHelper.titleCase(BlockDevice.NAMES[Types.WORKBENCH_FALSE.ordinal()])
				+ ".Securable");
		ThermalExpansion.config.removeProperty("Security", "Device." + StringHelper.titleCase(BlockDevice.NAMES[Types.PUMP.ordinal()]) + ".Securable");
		ThermalExpansion.config.removeProperty("Security", "Device." + StringHelper.titleCase(BlockDevice.NAMES[Types.EXTENDER.ordinal()]) + ".Securable");
	}

	protected final byte type;

	public TileDeviceBase() {

		this(Types.BREAKER);
		if (getClass() != TileDeviceBase.class) {
			throw new IllegalArgumentException();
		}
	}

	public TileDeviceBase(Types type) {

		this.type = (byte) type.ordinal();

		sideConfig = defaultSideConfig[this.type];
		setDefaultSides();

		augmentStatus = new boolean[4];
		augments = new ItemStack[4];
	}

	@Override
	public int getType() {

		return type;
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.device." + BlockDevice.NAMES[getType()] + ".name";
	}

	@Override
	public boolean enableSecurity() {

		return enableSecurity[getType()];
	}

	@Override
	public boolean sendRedstoneUpdates() {

		return true;
	}

	public void onEntityCollidedWithBlock(Entity entity) {

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
			return side != facing ? IconRegistry.getIcon("DeviceSide") : redstoneControlOrDisable() ? IconRegistry.getIcon("DeviceActive", type) : IconRegistry.getIcon("DeviceFace", type);
		} else if (side < 6) {
			return IconRegistry.getIcon(TEProps.textureSelection, sideConfig.sideTex[sideCache[side]]);
		}
		return IconRegistry.getIcon("DeviceSide");
	}

}
