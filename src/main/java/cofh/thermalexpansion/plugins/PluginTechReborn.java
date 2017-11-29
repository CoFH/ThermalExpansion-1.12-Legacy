package cofh.thermalexpansion.plugins;

import cofh.core.util.ModPlugin;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;

import java.util.Arrays;

public class PluginTechReborn extends ModPlugin {

	public static final String MOD_ID = "techreborn";
	public static final String MOD_NAME = "Tech Reborn";

	public PluginTechReborn() {

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
		return !error;
	}

	@Override
	public boolean register() {

		if (!enable) {
			return false;
		}
		try {

			/* INSOLATOR */
			{
				ItemStack logRubber = getItemStack("rubber_log", 1, 0);
				ItemStack saplingRubber = getItemStack("rubber_sapling", 1, 0);

				InsolatorManager.addDefaultTreeRecipe(saplingRubber, ItemHelper.cloneStack(logRubber, 4), saplingRubber);
			}

			/* TRANSPOSER */
			{
				int energy = 2000;

				TransposerManager.addFillRecipe(energy, ItemHelper.getOre("ingotHotTungstensteel"), ItemHelper.getOre("ingotTungstensteel"), new FluidStack(TFFluids.fluidCryotheum, 200), false);
			}

			/* CENTRIFUGE */
			{
				int energy = CentrifugeManager.DEFAULT_ENERGY;

				CentrifugeManager.addRecipe(energy * 16, ItemHelper.getOre("dustRedGarnet", 16), Arrays.asList(ItemHelper.getOre("dustSpessartine", 8), ItemHelper.getOre("dustAlmandine", 5), ItemHelper.getOre("dustPyrope", 3)), null);
				CentrifugeManager.addRecipe(energy * 16, ItemHelper.getOre("dustYellowGarnet", 16), Arrays.asList(ItemHelper.getOre("dustGrossular", 8), ItemHelper.getOre("dustAndradite", 5), ItemHelper.getOre("dustUvarovite", 3)), null);
				CentrifugeManager.addRecipe(energy, ItemHelper.getOre("dustDarkAshes"), Arrays.asList(ItemHelper.getOre("dustAshes")), null);
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
