package cofh.thermalexpansion.plugins;

import cofh.core.util.ModPlugin;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class PluginElementalDimensions extends ModPlugin {

	public static final String MOD_ID = "elementaldimensions";
	public static final String MOD_NAME = "Elemental Dimensions";

	public PluginElementalDimensions() {

		super(MOD_ID, MOD_NAME);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";
		enable = ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment) && Loader.isModLoaded(MOD_ID);

		if (!enable) {
			return false;
		}
		return !error;
	}

	@Override
	public boolean register() {

		if (!enable) {
			return false;
		}
		try {
			/* PULVERIZER */
			{
				// TODO: Dust Ore support?
			}

			/* CENTRIFUGE */
			{
				CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":ed_blaster", asList(new ItemStack(Items.BLAZE_ROD), ItemHelper.cloneStack(ItemMaterial.dustSulfur)), asList(50, 25), 10);
				CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":ed_dirtzombie", asList(new ItemStack(Items.ROTTEN_FLESH, 2), getItemStack("waterrune_part1")), asList(50, 5), 5);
				CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":ed_ghost", singletonList(getItemStack("spiritrune_part1")), singletonList(25), 5);
				CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":ed_guard", singletonList(getItemStack("earthrune")), singletonList(5), 5);
				CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":ed_spirit", asList(new ItemStack(Items.ROTTEN_FLESH, 2), getItemStack("firerune_part1"), getItemStack("firerune_part3")), asList(50, 5, 5), 5);
				CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":ed_watercreep", singletonList(getItemStack("airrune_part1")), singletonList(5), 5);
			}

		} catch (Throwable t) {
			ThermalExpansion.LOG.error("Thermal Expansion: " + MOD_NAME + " Plugin encountered an error:", t);
			error = true;
		}
		if (!error) {
			ThermalExpansion.LOG.info("Thermal Expansion: " + MOD_NAME + " Plugin Enabled.");
		}
		return !error;
	}

}
