package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.managers.machine.SmelterManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class PluginEnderIO extends PluginTEBase {

	public static final String MOD_ID = "enderio";
	public static final String MOD_NAME = "Ender IO";

	public PluginEnderIO() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void registerDelegate() {

		/* SMELTER */
		{
			SmelterManager.addRecipe(3800, ItemHelper.getOre("dustIron"), ItemHelper.getOre("dustRedstone"), ItemHelper.getOre("ingotConductiveIron"));
			SmelterManager.addRecipe(4200, ItemHelper.getOre("ingotIron"), ItemHelper.getOre("dustRedstone"), ItemHelper.getOre("ingotConductiveIron"));

			SmelterManager.addRecipe(6800, ItemHelper.getOre("dustIron"), ItemHelper.getOre("enderpearl"), ItemHelper.getOre("ingotPulsatingIron"));
			SmelterManager.addRecipe(7200, ItemHelper.getOre("ingotIron"), ItemHelper.getOre("enderpearl"), ItemHelper.getOre("ingotPulsatingIron"));

			SmelterManager.addRecipe(6800, ItemHelper.getOre("dustGold"), new ItemStack(Blocks.SOUL_SAND), ItemHelper.getOre("ingotSoularium"));
			SmelterManager.addRecipe(7200, ItemHelper.getOre("ingotGold"), new ItemStack(Blocks.SOUL_SAND), ItemHelper.getOre("ingotSoularium"));

			SmelterManager.addRecipe(6800, ItemHelper.getOre("dustEnergeticAlloy"), ItemHelper.getOre("enderpearl"), ItemHelper.getOre("ingotVibrantAlloy"));
			SmelterManager.addRecipe(7200, ItemHelper.getOre("ingotEnergeticAlloy"), ItemHelper.getOre("enderpearl"), ItemHelper.getOre("ingotVibrantAlloy"));
		}
	}

}
