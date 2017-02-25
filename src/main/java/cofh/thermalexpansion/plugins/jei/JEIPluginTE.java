package cofh.thermalexpansion.plugins.jei;

import cofh.thermalexpansion.plugins.jei.charger.ChargerRecipeCategory;
import cofh.thermalexpansion.plugins.jei.compactor.CompactorRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crucible.CrucibleRecipeCategory;
import cofh.thermalexpansion.plugins.jei.furnace.FurnaceRecipeCategory;
import cofh.thermalexpansion.plugins.jei.insolator.InsolatorRecipeCategory;
import cofh.thermalexpansion.plugins.jei.pulverizer.PulverizerRecipeCategory;
import cofh.thermalexpansion.plugins.jei.refinery.RefineryRecipeCategory;
import cofh.thermalexpansion.plugins.jei.sawmill.SawmillRecipeCategory;
import cofh.thermalexpansion.plugins.jei.smelter.SmelterRecipeCategory;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class JEIPluginTE extends BlankModPlugin {

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {

	}

	@Override
	public void register(IModRegistry registry) {

		FurnaceRecipeCategory.initialize(registry);
		PulverizerRecipeCategory.initialize(registry);
		SawmillRecipeCategory.initialize(registry);
		SmelterRecipeCategory.initialize(registry);
		InsolatorRecipeCategory.initialize(registry);
		CompactorRecipeCategory.initialize(registry);
		CrucibleRecipeCategory.initialize(registry);
		RefineryRecipeCategory.initialize(registry);

		ChargerRecipeCategory.initialize(registry);
	}

}
