package cofh.thermalexpansion.plugins;

import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;

import static java.util.Collections.singletonList;

public class PluginFamiliarFauna extends PluginTEBase {

	public static final String MOD_ID = "familiarfauna";
	public static final String MOD_NAME = "Familiar Fauna";

	public PluginFamiliarFauna() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void registerDelegate() {

		/* CENTRIFUGE */
		{
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":" + MOD_ID + ".deer", singletonList(getItemStack("venison_raw", 3)), singletonList(70), 2);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":" + MOD_ID + ".snail", singletonList(getItemStack("snail_shell")), singletonList(50), 2);
			// CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":" + MOD_ID + ".turkey", singletonList(getItemStack("turkey_leg_raw", 2)), singletonList(60), 2);
		}
	}

}
