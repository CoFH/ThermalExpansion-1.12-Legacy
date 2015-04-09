package cofh.thermalexpansion.block.device;

import cofh.core.render.IconRegistry;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileAugmentable;
import cofh.thermalexpansion.core.TEProps;
import cpw.mods.fml.common.registry.GameRegistry;

import gnu.trove.map.hash.THashMap;

import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class TileLexicon extends TileAugmentable {

	static final int TYPE = BlockDevice.Types.LEXICON.ordinal();
	static SideConfig defaultSideConfig = new SideConfig();
	static EnergyConfig energyConfig = new EnergyConfig();

	public static void initialize() {

		defaultSideConfig = new SideConfig();
		defaultSideConfig.numConfig = 3;
		defaultSideConfig.slotGroups = new int[][] { {}, { 0, 1, 2, 3, 4, 5 }, { 6, 7, 8, 9, 10, 11 } };
		defaultSideConfig.allowInsertionSide = new boolean[] { false, true, false };
		defaultSideConfig.allowExtractionSide = new boolean[] { false, false, true };
		defaultSideConfig.sideTex = new int[] { 0, 1, 4 };
		defaultSideConfig.defaultSides = new byte[] { 1, 1, 1, 1, 1, 1 };

		GameRegistry.registerTileEntity(TileLexicon.class, "thermalexpansion.lexicon");
		configure();
	}

	public static void configure() {

		String comment = "Enable this to allow for Lexicons to be securable.";
		enableSecurity = ThermalExpansion.config.get("Security", "Device.Lexicon.Securable", enableSecurity, comment);
	}

	public static boolean enableSecurity = true;

	Map<String, ItemStack> conversions = new THashMap<String, ItemStack>(9);

	ItemStack[] conversionItems = new ItemStack[9];

	public TileLexicon() {

		sideConfig = defaultSideConfig;

		inventory = new ItemStack[12];
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.device." + BlockDevice.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return TYPE;
	}

	/* ISidedTexture */
	@Override
	public IIcon getTexture(int side, int pass) {

		if (pass == 0) {
			return side != facing ? IconRegistry.getIcon("DeviceSide") : isActive && redstoneControlOrDisable() ? IconRegistry.getIcon("DeviceActive",
					getType()) : IconRegistry.getIcon("DeviceFace", getType());
		} else if (side < 6) {
			return IconRegistry.getIcon(TEProps.textureSelection, sideConfig.sideTex[sideCache[side]]);
		}
		return IconRegistry.getIcon("DeviceSide");
	}

}
