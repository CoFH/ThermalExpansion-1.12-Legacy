package thermalexpansion.plugins.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

import thermalexpansion.core.TEProps;
import thermalexpansion.gui.client.device.GuiWorkbench;
import thermalexpansion.plugins.nei.handlers.RecipeHandlerCrucible;
import thermalexpansion.plugins.nei.handlers.RecipeHandlerFurnace;
import thermalexpansion.plugins.nei.handlers.RecipeHandlerPulverizer;
import thermalexpansion.plugins.nei.handlers.RecipeHandlerSawmill;
import thermalexpansion.plugins.nei.handlers.RecipeHandlerSmelter;
import thermalexpansion.plugins.nei.handlers.RecipeHandlerTransposer;

public class NEIThermalExpansionConfig implements IConfigureNEI {

	/* IConfigureNEI */
	@Override
	public void loadConfig() {

		API.registerNEIGuiHandler(NEIGuiHandler.instance);

		API.registerRecipeHandler(RecipeHandlerFurnace.instance);
		API.registerUsageHandler(RecipeHandlerFurnace.instance);

		API.registerRecipeHandler(RecipeHandlerPulverizer.instance);
		API.registerUsageHandler(RecipeHandlerPulverizer.instance);

		API.registerRecipeHandler(RecipeHandlerSawmill.instance);
		API.registerUsageHandler(RecipeHandlerSawmill.instance);

		API.registerRecipeHandler(RecipeHandlerSmelter.instance);
		API.registerUsageHandler(RecipeHandlerSmelter.instance);

		API.registerRecipeHandler(RecipeHandlerCrucible.instance);
		API.registerUsageHandler(RecipeHandlerCrucible.instance);

		API.registerRecipeHandler(RecipeHandlerTransposer.instance);
		API.registerUsageHandler(RecipeHandlerTransposer.instance);

		API.registerGuiOverlayHandler(GuiWorkbench.class, new NEIRecipeOverlayHandler(), "crafting");
	}

	@Override
	public String getName() {

		return TEProps.NAME;
	}

	@Override
	public String getVersion() {

		return TEProps.VERSION;
	}

}
