package thermalexpansion.block.ender;

import cofh.api.tileentity.ISecureTile;
import cofh.core.CoFHProps;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TileRSInventory;

public class TileTesseractBound extends TileRSInventory implements ISecureTile, ISidedInventory {

	String owner = CoFHProps.DEFAULT_OWNER;

	int[] slots = new int[0];
	boolean[] insert = new boolean[27];
	boolean[] extract = new boolean[27];

	public static void initialize() {

		GameRegistry.registerTileEntity(TileTesseract.class, "cofh.thermalexpansion.Tesseract");
		guiId = ThermalExpansion.proxy.registerGui("Tesseract", null, true);
	}

	protected static int guiId;

	/* Client-Side Only */
	public boolean canAccess = true;

	@Override
	public String getName() {

		return "tile.thermalexpansion.tesseract.name";
	}

	@Override
	public int getType() {

		return 1;
	}

	/* ISidedInventory */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		return slots;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {

		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {

		return false;
	}

	/* ISecureTile */
	@Override
	public boolean setAccess(AccessMode access) {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AccessMode getAccess() {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setOwnerName(String name) {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getOwnerName() {

		// TODO Auto-generated method stub
		return null;
	}

}
