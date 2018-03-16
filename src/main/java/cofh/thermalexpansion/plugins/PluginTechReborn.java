package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class PluginTechReborn extends PluginTEBase {

	public static final String MOD_ID = "techreborn";
	public static final String MOD_NAME = "Tech Reborn";

	public PluginTechReborn() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void registerDelegate() {

		/* INSOLATOR */
		{
			ItemStack logRubber = getItemStack("rubber_log", 1, 0);
			ItemStack saplingRubber = getItemStack("rubber_sapling", 1, 0);

			InsolatorManager.addDefaultTreeRecipe(saplingRubber, ItemHelper.cloneStack(logRubber, 6), saplingRubber);
		}

		/* TRANSPOSER */
		{
			int energy = 2000;

			TransposerManager.addFillRecipe(energy, ItemHelper.getOre("ingotHotTungstensteel"), ItemHelper.getOre("ingotTungstensteel"), new FluidStack(TFFluids.fluidCryotheum, 200), false);
		}

		/* CENTRIFUGE */
		{
			int energy = CentrifugeManager.DEFAULT_ENERGY;

			CentrifugeManager.addRecipe(energy * 16, ItemHelper.getOre("dustRedGarnet", 16), asList(ItemHelper.getOre("dustSpessartine", 8), ItemHelper.getOre("dustAlmandine", 5), ItemHelper.getOre("dustPyrope", 3)), null);
			CentrifugeManager.addRecipe(energy * 16, ItemHelper.getOre("dustYellowGarnet", 16), asList(ItemHelper.getOre("dustGrossular", 8), ItemHelper.getOre("dustAndradite", 5), ItemHelper.getOre("dustUvarovite", 3)), null);
			CentrifugeManager.addRecipe(energy, ItemHelper.getOre("dustDarkAshes"), singletonList(ItemHelper.getOre("dustAshes")), null);
		}
	}

}
