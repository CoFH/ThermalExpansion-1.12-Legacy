package cofh.thermalexpansion.plugins.top;

import cofh.api.core.ISecurable;
import cofh.thermalexpansion.block.TileTEBase;
import cofh.thermalexpansion.block.storage.TileCache;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.api.IProbeConfig.ConfigMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Function;

public class CompatibilityTOP implements Function<ITheOneProbe, Void> {

	/* Function */
	@Override
	public Void apply(@Nullable ITheOneProbe probe) {

		if (probe != null) {
			/* IProbeConfigProvider */
			probe.registerProbeConfigProvider(new IProbeConfigProvider() {
				@Override
				public void getProbeConfig(IProbeConfig config, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {

				}

				@Override
				public void getProbeConfig(IProbeConfig config, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {

					TileEntity tile = world.getTileEntity(data.getPos());

					if (tile instanceof ISecurable && !((ISecurable) tile).canPlayerAccess(player)) {
						config.showChestContents(ConfigMode.NOT);
						config.showRedstone(ConfigMode.NOT);
						config.showTankSetting(ConfigMode.NOT);

						config.setRFMode(0);
						config.setTankMode(0);
					}
					if (tile instanceof TileCache) {
						config.showChestContents(ConfigMode.NOT);
					}
				}
			});

			/* IProbeInfoProvider */
			probe.registerProvider(new IProbeInfoProvider() {
				@Override
				public String getID() {

					return "thermalexpansion.topplugin";
				}

				@Override
				public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {

					TileEntity tile = world.getTileEntity(data.getPos());
					if (tile instanceof TileTEBase) {
						((TileTEBase) tile).provideInfo(mode, probeInfo, data.getSideHit(), player);
					}
				}
			});
		}
		return null;
	}
}
