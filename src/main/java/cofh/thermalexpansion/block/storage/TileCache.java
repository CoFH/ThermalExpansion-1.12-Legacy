package cofh.thermalexpansion.block.storage;

import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileAugmentableSecure;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileCache extends TileAugmentableSecure implements ITickable {

	public static int[] CAPACITY = { 1, 4, 9, 16, 25 };

	static {
		for (int i = 0; i < CAPACITY.length; i++) {
			CAPACITY[i] *= 20000;
		}
	}

	private static boolean enableSecurity = true;

	public static void initialize() {

		GameRegistry.registerTileEntity(TileTank.class, "thermalexpansion:storage_cache");

		config();
	}

	public static void config() {

		String comment = "Enable this to allow for Caches to be securable.";
		enableSecurity = ThermalExpansion.CONFIG.get("Security", "Cache.Securable", true, comment);
	}

	int compareTracker;

	@Override
	public String getTileName() {

		return "tile.thermalexpansion.storage.cache.name";
	}

	@Override
	public int getType() {

		return 0;
	}

	@Override
	public int getComparatorInputOverride() {

		return compareTracker;
	}

	@Override
	public boolean enableSecurity() {

		return enableSecurity;
	}

	@Override
	public void update() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		//		transferFluid();
		//
		//		if (timeCheck()) {
		//			int curScale = getScaledFluidStored(15);
		//			if (curScale != compareTracker) {
		//				compareTracker = curScale;
		//				callNeighborTileChange();
		//			}
		//			if (!cached) {
		//				updateAdjacentHandlers();
		//			}
		//		}
	}

}
