package cofh.thermalexpansion.block.ender;

import cofh.thermalexpansion.block.TileRSControl;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public class TileTesseractBound extends TileRSControl implements ISidedInventory {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileTesseractBound.class, "thermalexpansion.TesseractBound");
	}

	int[] slots = new int[0];
	boolean[] insert = new boolean[27];
	boolean[] extract = new boolean[27];

	@Override
	public String getName() {

		return "tile.thermalexpansion.ender.tesseractBound.name";
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

		return insert[slot];
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {

		return extract[slot];
	}

}
