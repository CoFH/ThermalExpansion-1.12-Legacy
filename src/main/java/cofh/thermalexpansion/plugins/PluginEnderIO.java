package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.managers.machine.SmelterManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class PluginEnderIO extends PluginTEBase {

	public static final String MOD_ID = "enderio";
	public static final String MOD_NAME = "Ender IO";

	public PluginEnderIO() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

		ItemStack simpleChassi = ItemHelper.getOre("itemSimpleMachineChassi");
		ItemStack machineChassi = ItemHelper.getOre("itemMachineChassi");
		ItemStack soulChassi = ItemHelper.getOre("itemSoulMachineChassi");

		ItemStack dyeMachine = ItemHelper.getOre("dyeMachine");
		ItemStack dyeSoulMachine = ItemHelper.getOre("dyeSoulMachine");

		ItemStack enderpearl = new ItemStack(Items.ENDER_PEARL);
		ItemStack silicon = ItemHelper.getOre("itemSilicon");
		ItemStack soulsand = new ItemStack(Blocks.SOUL_SAND);

		ItemStack dustGold = ItemHelper.getOre("dustGold");
		ItemStack dustIron = ItemHelper.getOre("dustIron");
		ItemStack dustObsidian = ItemHelper.getOre("dustObsidian", 4);
		ItemStack dustRedstone = ItemHelper.getOre("dustRedstone");
		ItemStack dustSteel = ItemHelper.getOre("dustSteel");

		ItemStack ingotGold = ItemHelper.getOre("ingotGold");
		ItemStack ingotIron = ItemHelper.getOre("ingotIron");
		ItemStack ingotSteel = ItemHelper.getOre("ingotSteel");

		ItemStack ingotConductiveIron = ItemHelper.getOre("ingotConductiveIron");
		ItemStack ingotDarkSteel = ItemHelper.getOre("ingotDarkSteel");
		ItemStack ingotElectricalSteel = ItemHelper.getOre("ingotElectricalSteel");
		ItemStack ingotEnergeticAlloy = ItemHelper.getOre("ingotEnergeticAlloy");
		ItemStack ingotPulsatingIron = ItemHelper.getOre("ingotPulsatingIron");
		ItemStack ingotSoularium = ItemHelper.getOre("ingotSoularium");
		ItemStack ingotVibrantAlloy = ItemHelper.getOre("ingotVibrantAlloy");

		/* SMELTER */
		{
			SmelterManager.addRecipe(3600, simpleChassi, dyeMachine, machineChassi);
			SmelterManager.addRecipe(3600, simpleChassi, dyeSoulMachine, soulChassi);

			SmelterManager.addRecipe(3800, dustIron, dustRedstone, ingotConductiveIron);
			SmelterManager.addRecipe(4200, ingotIron, dustRedstone, ingotConductiveIron);

			SmelterManager.addRecipe(6800, dustIron, enderpearl, ingotPulsatingIron);
			SmelterManager.addRecipe(7200, ingotIron, enderpearl, ingotPulsatingIron);

			SmelterManager.addRecipe(6800, dustGold, soulsand, ingotSoularium);
			SmelterManager.addRecipe(7200, ingotGold, soulsand, ingotSoularium);

			SmelterManager.addRecipe(6800, ItemHelper.getOre("dustEnergeticAlloy"), enderpearl, ingotVibrantAlloy);
			SmelterManager.addRecipe(7200, ingotEnergeticAlloy, enderpearl, ingotVibrantAlloy);

			SmelterManager.addRecipe(6800, dustSteel, silicon, ingotElectricalSteel);
			SmelterManager.addRecipe(7200, ingotSteel, silicon, ingotElectricalSteel);

			SmelterManager.addRecipe(13600, dustSteel, dustObsidian, ingotDarkSteel);
			SmelterManager.addRecipe(14400, ingotSteel, dustObsidian, ingotDarkSteel);
		}
	}

}
