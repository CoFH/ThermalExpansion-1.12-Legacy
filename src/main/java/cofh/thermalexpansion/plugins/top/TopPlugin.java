package cofh.thermalexpansion.plugins.top;

import cofh.thermalexpansion.block.TileTEBase;
import com.google.common.base.Function;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import javax.annotation.Nullable;

public class TopPlugin implements IProbeInfoProvider, Function<ITheOneProbe, Void> {

	private static boolean initialized = false;

	public static void initialize() {

		if (initialized) {
			return;
		}
		if (Loader.isModLoaded("theoneprobe")) {
			FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", TopPlugin.class.getName());
		}
		initialized = true;
	}

	@Override
	public Void apply(@Nullable ITheOneProbe probe) {

		if (probe != null) {
			probe.registerProvider(this);
		}
		return null;
	}

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
