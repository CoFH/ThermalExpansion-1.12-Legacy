package thermalexpansion.block.device;

import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TileReconfigurableInventory;

public class TileLexicon extends TileReconfigurableInventory {

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

		return 0;
		// return BlockDevice.Types.LEXICON.ordinal();
	}

	@Override
	public boolean openGui(EntityPlayer player) {

		player.openGui(ThermalExpansion.instance, 0, worldObj, xCoord, yCoord, zCoord);
		return true;
	}

	/* IReconfigurableSides */
	@Override
	public int getNumConfig(int side) {

		return 3;
	}

	@Override
	public IIcon getTexture(int side, int pass) {

		// TODO Auto-generated method stub
		return null;
	}

}
