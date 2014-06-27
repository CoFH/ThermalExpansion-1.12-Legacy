package thermalexpansion.block.device;

import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.util.IIcon;

import thermalexpansion.block.TileReconfigurable;

public class TileLexicon extends TileReconfigurable {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileLexicon.class, "thermalexpansion.lexicon");
	}

	public static final int[] SIDE_TEX = new int[] { 0, 1, 4 };

	@Override
	public String getName() {

		return "tile.thermalexpansion.device." + BlockDevice.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return BlockDevice.Types.LEXICON.ordinal();
	}

	/* IReconfigurableSides */
	@Override
	public int getNumConfig(int side) {

		return 3;
	}

	@Override
	public IIcon getTexture(int side, int pass) {

		return null;
	}

}
