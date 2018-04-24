package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.managers.machine.FurnaceManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import cofh.thermalfoundation.item.ItemFertilizer;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class PluginThaumcraft extends PluginTEBase {

	public static final String MOD_ID = "thaumcraft";
	public static final String MOD_NAME = "Thaumcraft";

	public PluginThaumcraft() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void registerDelegate() {

		ItemStack clusterIron = getItemStack("cluster", 1, 0);
		ItemStack clusterGold = getItemStack("cluster", 1, 1);
		ItemStack clusterCopper = getItemStack("cluster", 1, 2);
		ItemStack clusterTin = getItemStack("cluster", 1, 3);
		ItemStack clusterSilver = getItemStack("cluster", 1, 4);
		ItemStack clusterLead = getItemStack("cluster", 1, 5);
		ItemStack clusterCinnabar = getItemStack("cluster", 1, 6);
		ItemStack clusterQuartz = getItemStack("cluster", 1, 7);

		ItemStack saplingGreatwood = getItemStack("sapling_greatwood");
		ItemStack saplingSilverwood = getItemStack("sapling_silverwood");

		ItemStack logGreatwood = getItemStack("log_greatwood");
		ItemStack logSilverwood = getItemStack("log_silverwood");

		ItemStack cinderpearl = getItemStack("cinderpearl");
		ItemStack quicksilver = getItemStack("quicksilver");
		ItemStack shimmerleaf = getItemStack("shimmerleaf");
		ItemStack vishroom = getItemStack("vishroom");

		/* FURNACE */
		{
			int energy = FurnaceManager.DEFAULT_ENERGY * 3 / 4;

			FurnaceManager.addRecipe(energy, clusterIron, ItemHelper.getOre("ingotIron", 2));
			FurnaceManager.addRecipe(energy, clusterGold, ItemHelper.getOre("ingotGold", 2));
			FurnaceManager.addRecipe(energy, clusterCopper, ItemHelper.getOre("ingotCopper", 2));
			FurnaceManager.addRecipe(energy, clusterTin, ItemHelper.getOre("ingotTin", 2));
			FurnaceManager.addRecipe(energy, clusterSilver, ItemHelper.getOre("ingotSilver", 2));
			FurnaceManager.addRecipe(energy, clusterLead, ItemHelper.getOre("ingotLead", 2));
			FurnaceManager.addRecipe(energy, clusterCinnabar, ItemHelper.cloneStack(quicksilver, 2));
			FurnaceManager.addRecipe(energy, clusterQuartz, ItemHelper.getOre("gemQuartz", 2));

			energy = FurnaceManager.DEFAULT_ENERGY;

			FurnaceManager.addRecipe(energy, ItemMaterial.crystalCinnabar, quicksilver);
		}

		/* PULVERIZER */
		{
			int energy = PulverizerManager.DEFAULT_ENERGY * 3 / 4;

			PulverizerManager.addRecipe(energy, clusterIron, ItemHelper.getOre("dustIron", 3), ItemHelper.getOre("dustNickel"), 20);
			PulverizerManager.addRecipe(energy, clusterGold, ItemHelper.getOre("dustGold", 3), ItemMaterial.crystalCinnabar, 20);
			PulverizerManager.addRecipe(energy, clusterCopper, ItemHelper.getOre("dustCopper", 3), ItemHelper.getOre("dustGold"), 20);
			PulverizerManager.addRecipe(energy, clusterTin, ItemHelper.getOre("dustTin", 3), ItemHelper.getOre("dustIron"), 20);
			PulverizerManager.addRecipe(energy, clusterSilver, ItemHelper.getOre("dustSilver", 3), ItemHelper.getOre("dustLead"), 20);
			PulverizerManager.addRecipe(energy, clusterLead, ItemHelper.getOre("dustLead", 3), ItemHelper.getOre("dustSilver"), 20);
			PulverizerManager.addRecipe(energy, clusterCinnabar, ItemHelper.cloneStack(quicksilver, 3), ItemMaterial.crystalCinnabar, 25);
			PulverizerManager.addRecipe(energy, clusterQuartz, ItemHelper.getOre("gemQuartz", 4));
		}

		/* INSOLATOR */
		{
			InsolatorManager.addDefaultTreeRecipe(saplingGreatwood, ItemHelper.cloneStack(logGreatwood, 6), saplingGreatwood);
			InsolatorManager.addDefaultTreeRecipe(saplingSilverwood, ItemHelper.cloneStack(logSilverwood, 6), saplingSilverwood);

			InsolatorManager.addDefaultRecipe(vishroom, ItemHelper.cloneStack(vishroom, 2), ItemStack.EMPTY, 0);
			InsolatorManager.addRecipe(120000, 4000, cinderpearl, ItemFertilizer.fertilizerFlux, new ItemStack(Items.BLAZE_POWDER), cinderpearl, 100);
			InsolatorManager.addRecipe(120000, 4000, shimmerleaf, ItemFertilizer.fertilizerFlux, quicksilver, shimmerleaf, 100);
		}
	}

}
