package thermalexpansion.block.simple;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.registry.GameRegistry;

public class TileInvisible extends TileEntity {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileInvisible.class, "cofh.thermalexpansion.Invisible");
	}

	protected int light;
	protected int signal;

	public TileInvisible() {

	}

	public TileInvisible(int light, int signal) {

		this.light = light;
		this.signal = signal;
	}

	@Override
	public boolean canUpdate() {

		return false;
	}

	public void randomDisplayTick() {

	}

}
