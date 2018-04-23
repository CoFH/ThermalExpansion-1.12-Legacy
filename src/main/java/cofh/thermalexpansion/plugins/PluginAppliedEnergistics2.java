package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.managers.machine.ChargerManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class PluginAppliedEnergistics2 extends PluginTEBase {

	public static final String MOD_ID = "appliedenergistics2";
	public static final String MOD_NAME = "Applied Energistics 2";

	public PluginAppliedEnergistics2() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void registerDelegate() {

		/* PULVERIZER */
		{
			int energy = PulverizerManager.DEFAULT_ENERGY;

			PulverizerManager.addRecipe(energy, new ItemStack(Items.WHEAT), getItemStack("material", 1, 4));
			PulverizerManager.addRecipe(energy, getItemStack("sky_stone_block"), getItemStack("material", 1, 45));

			PulverizerManager.addRecipe(energy, ItemHelper.getOre("oreCertusQuartz"), ItemHelper.getOre("crystalCertusQuartz", 2), ItemHelper.getOre("dustCertusQuartz"), 10);
			PulverizerManager.addRecipe(energy, ItemHelper.getOre("crystalCertusQuartz"), ItemHelper.getOre("dustCertusQuartz"));

			energy = energy * 3 / 4;

			PulverizerManager.addRecipe(energy, new ItemStack(Items.ENDER_PEARL), ItemHelper.getOre("dustEnderPearl"));
			PulverizerManager.addRecipe(energy, ItemHelper.getOre("crystalFluix"), ItemHelper.getOre("dustFluix"));
			PulverizerManager.addRecipe(energy, new ItemStack(Items.QUARTZ), ItemHelper.getOre("dustNetherQuartz"));
		}

		/* INSOLATOR */
		{
			int energy = 120000;
			int water = 5000;

			InsolatorManager.addRecipe(energy, water, getItemStack("crystal_seed"), new ItemStack(Items.GLOWSTONE_DUST), getItemStack("material", 1, 10));
			InsolatorManager.addRecipe(energy, water, getItemStack("crystal_seed", 1, 600), new ItemStack(Items.GLOWSTONE_DUST), getItemStack("material", 1, 11));
			InsolatorManager.addRecipe(energy, water, getItemStack("crystal_seed", 1, 1200), new ItemStack(Items.GLOWSTONE_DUST), getItemStack("material", 1, 12));
		}

		/* CHARGER */
		{
			int energy = ChargerManager.DEFAULT_ENERGY;

			ChargerManager.addRecipe(energy, getItemStack("quartz_ore", 1), getItemStack("charged_quartz_ore", 1));
			ChargerManager.addRecipe(energy, getItemStack("material", 1, 0), getItemStack("material", 1, 1));
		}
	}

}
