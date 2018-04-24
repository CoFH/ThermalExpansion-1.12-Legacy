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

		ItemStack oreCertusQuartz = getItemStack("quartz_ore", 1);
		ItemStack oreCertusQuartzCharged = getItemStack("charged_quartz_ore", 1);

		ItemStack crystalCertusQuartz = getItemStack("material", 1, 0);
		ItemStack crystalCertusQuartzCharged = getItemStack("material", 1, 1);

		ItemStack dustCertusQuartz = ItemHelper.getOre("dustCertusQuartz");
		ItemStack dustNetherQuartz = ItemHelper.getOre("dustNetherQuartz");

		/* PULVERIZER */
		{
			int energy = PulverizerManager.DEFAULT_ENERGY;

			PulverizerManager.addRecipe(energy, new ItemStack(Items.WHEAT), getItemStack("material", 1, 4));
			PulverizerManager.addRecipe(energy, getItemStack("sky_stone_block"), getItemStack("material", 1, 45));

			PulverizerManager.addRecipe(energy, oreCertusQuartz, ItemHelper.cloneStack(crystalCertusQuartz, 2), dustCertusQuartz, 10);
			PulverizerManager.addRecipe(energy, oreCertusQuartzCharged, ItemHelper.cloneStack(crystalCertusQuartzCharged, 2), dustCertusQuartz, 10);
			PulverizerManager.addRecipe(energy, crystalCertusQuartz, dustCertusQuartz);

			energy = PulverizerManager.DEFAULT_ENERGY / 2;

			PulverizerManager.addRecipe(energy, new ItemStack(Items.ENDER_PEARL), ItemHelper.getOre("dustEnderPearl"));
			PulverizerManager.addRecipe(energy, ItemHelper.getOre("crystalFluix"), ItemHelper.getOre("dustFluix"));
			PulverizerManager.addRecipe(energy, new ItemStack(Items.QUARTZ), dustNetherQuartz);
		}

		/* INSOLATOR */
		{
			int energy = 90000;
			int water = 3000;

			InsolatorManager.addRecipe(energy, water, getItemStack("crystal_seed"), new ItemStack(Items.GLOWSTONE_DUST), getItemStack("material", 1, 10));
			InsolatorManager.addRecipe(energy, water, getItemStack("crystal_seed", 1, 600), new ItemStack(Items.GLOWSTONE_DUST), getItemStack("material", 1, 11));
			InsolatorManager.addRecipe(energy, water, getItemStack("crystal_seed", 1, 1200), new ItemStack(Items.GLOWSTONE_DUST), getItemStack("material", 1, 12));
		}

		/* CHARGER */
		{
			int energy = ChargerManager.DEFAULT_ENERGY;

			ChargerManager.addRecipe(energy, oreCertusQuartz, oreCertusQuartzCharged);
			ChargerManager.addRecipe(energy, crystalCertusQuartz, crystalCertusQuartzCharged);
		}
	}

}
