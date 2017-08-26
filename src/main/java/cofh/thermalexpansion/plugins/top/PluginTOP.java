package cofh.thermalexpansion.plugins.top;

import cofh.api.core.ISecurable;
import cofh.core.util.ModPlugin;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileTEBase;
import cofh.thermalexpansion.block.storage.TileCache;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.api.IProbeConfig.ConfigMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import javax.annotation.Nullable;
import java.util.function.Function;

public class PluginTOP extends ModPlugin implements Function<ITheOneProbe, Void> {

	public static final String MOD_ID = "theoneprobe";
	public static final String MOD_NAME = "The One Probe";

	public PluginTOP() {

		super(MOD_ID, MOD_NAME);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";
		enable = Loader.isModLoaded(MOD_ID) && ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment);

		if (!enable) {
			return false;
		}
		FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", PluginTOP.class.getName());

		return !error;
	}

	@Override
	public boolean register() {

		if (!enable) {
			return false;
		}
		if (!error) {
			ThermalExpansion.LOG.info("Thermal Expansion: " + MOD_NAME + " Plugin Enabled.");
		}
		return !error;
	}

	/* Function */
	@Override
	public Void apply(@Nullable ITheOneProbe probe) {

		if (probe != null) {
			probe.registerProbeConfigProvider(new ConfigProvider());
			probe.registerProvider(new InfoProvider());
		}
		return null;
	}

	/* HELPERS */
	public static int chestContentsBorderColor = 0xff006699;

	public static class ConfigProvider implements IProbeConfigProvider {

		/* IProbeConfigProvider */
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
	}

	public static class InfoProvider implements IProbeInfoProvider {

		/* IProbeInfoProvider */
		@Override
		public String getID() {

			return "thermalexpansion.probeplugin";
		}

		@Override
		public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {

			TileEntity tile = world.getTileEntity(data.getPos());
			if (tile instanceof TileTEBase) {
				((TileTEBase) tile).provideInfo(mode, probeInfo, data.getSideHit(), player);
			}
		}
	}

}
