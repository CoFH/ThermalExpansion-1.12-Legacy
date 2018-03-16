package cofh.thermalexpansion.plugins.top;

import cofh.thermalexpansion.plugins.PluginTEBase;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class PluginTOP extends PluginTEBase {

	public static final String MOD_ID = "theoneprobe";
	public static final String MOD_NAME = "The One Probe";

	public PluginTOP() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

		FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", CompatibilityTOP.class.getName());
	}

	/* HELPERS */
	public static int chestContentsBorderColor = 0xff006699;

}
