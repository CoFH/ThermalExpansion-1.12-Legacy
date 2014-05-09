package thermalexpansion.block.device;

import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TileReconfigurableInventory;

public class TileLexicon extends TileReconfigurableInventory {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileLexicon.class, "cofh.thermalexpansion.lexicon");
		guiId = ThermalExpansion.proxy.registerGui("Lexicon", "device", true);
	}

	protected static int guiId;

	public static final int[] SIDE_TEX = new int[] { 0, 1, 4 };

	@Override
	public String getName() {

		return "tile.thermalexpansion.device." + BlockDevice.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return BlockDevice.Types.LEXICON.ordinal();
	}

	@Override
	public boolean openGui(EntityPlayer player) {

		player.openGui(ThermalExpansion.instance, guiId, worldObj, xCoord, yCoord, zCoord);
		return true;
	}

	/* IReconfigurableSides */
	@Override
	public int getNumConfig(int side) {

		return 3;
	}

	@Override
	public IIcon getBlockTexture(int side, int pass) {

		// TODO Auto-generated method stub
		return null;
	}

}
