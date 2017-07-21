package cofh.thermalexpansion.plugins;

import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileTEBase;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import javax.annotation.Nullable;
import java.util.function.Function;

public class TopPlugin {

	private TopPlugin() {

	}

	public static final String MOD_ID = "theoneprobe";
	public static final String MOD_NAME = "The One Probe";

	public static void initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";

		boolean enable = ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment);

		if (!enable || !Loader.isModLoaded(MOD_ID)) {
			return;
		}
		try {
			FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "cofh.thermalexpansion.plugins.TopPlugin$InfoProvider");

			ThermalExpansion.LOG.info("Thermal Expansion: " + MOD_NAME + " Plugin Enabled.");
		} catch (Throwable t) {
			ThermalExpansion.LOG.error("Thermal Expansion: " + MOD_NAME + " Plugin encountered an error:", t);
		}
	}

	/* HELPERS */
	public static class InfoProvider implements IProbeInfoProvider, Function<ITheOneProbe, Void> {

		@Override
		public Void apply(@Nullable ITheOneProbe probe) {

			if (probe != null) {
				probe.registerProvider(this);
			}
			return null;
		}

		/* IProbeInfoProvider */
		@Override
		public String getID() {

			return "thermalexpansion.probeplugin";
		}

		@Override
		public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {

			TileEntity te = world.getTileEntity(iProbeHitData.getPos());
			if (te instanceof TileTEBase) {
				TileTEBase tile = (TileTEBase) te;
				tile.provideInfo(iProbeInfo, iProbeHitData.getSideHit(), entityPlayer);
			}
		}
	}

}
