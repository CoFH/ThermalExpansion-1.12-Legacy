package cofh.thermalexpansion.plugins.jei;

import cofh.thermalexpansion.plugins.jei.compactor.CompactorRecipeCategory;
import cofh.thermalexpansion.plugins.jei.furnace.FurnaceRecipeCategory;
import cofh.thermalexpansion.plugins.jei.insolator.InsolatorRecipeCategory;
import cofh.thermalexpansion.plugins.jei.pulverizer.PulverizerRecipeCategory;
import cofh.thermalexpansion.plugins.jei.sawmill.SawmillRecipeCategory;
import cofh.thermalexpansion.plugins.jei.smelter.SmelterRecipeCategory;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.util.ResourceLocation;

@JEIPlugin
public class JEIPluginTE extends BlankModPlugin {

	public static final ResourceLocation JEI_HANDLER_TEXTURE = new ResourceLocation("thermalexpansion:textures/gui/jei_handler.png");

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
	}

}
