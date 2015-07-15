package cofh.thermalexpansion.block.workbench;

import cpw.mods.fml.common.registry.GameRegistry;

public class TileWorkbenchCreative extends TileWorkbench {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileWorkbenchCreative.class, "thermalexpansion.WorkbenchCreative");
	}

	public TileWorkbenchCreative() {

	}

	public TileWorkbenchCreative(int metadata) {

		super(metadata);
	}

	// TODO: crafting stuff~
}
