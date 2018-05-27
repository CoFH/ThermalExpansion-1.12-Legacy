package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import cofh.thermalexpansion.util.managers.machine.SawmillManager;
import cofh.thermalexpansion.util.managers.machine.SmelterManager;
import net.minecraft.item.ItemStack;

public class PluginBetweenlands extends PluginTEBase {

	public static final String MOD_ID = "thebetweenlands";
	public static final String MOD_NAME = "The Betweenlands";

	public PluginBetweenlands() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

		ItemStack gemValonite = ItemHelper.getOre("gemValonite");
		ItemStack ingotOctine = ItemHelper.getOre("ingotOctine");
		ItemStack ingotSyrmorite = ItemHelper.getOre("ingotSyrmorite");

		ItemStack lurkerSkin = getItemStack("items_misc", 1, 4);

		/* PULVERIZER */
		{
			int energy = PulverizerManager.DEFAULT_ENERGY;

			PulverizerManager.addRecipe(energy, getItemStack("scabyst_ore"), getItemStack("items_misc", 4, 39));

			PulverizerManager.addRecipe(energy / 2, getItemStack("aqua_middle_gem"), getItemStack("items_crushed", 1, 45));
			PulverizerManager.addRecipe(energy / 2, getItemStack("crimson_middle_gem"), getItemStack("items_crushed", 1, 28));
			PulverizerManager.addRecipe(energy / 2, getItemStack("green_middle_gem"), getItemStack("items_crushed", 1, 32));

			PulverizerManager.addRecycleRecipe(energy, getItemStack("valonite_helmet"), gemValonite, 2);
			PulverizerManager.addRecycleRecipe(energy, getItemStack("valonite_chestplate"), gemValonite, 4);
			PulverizerManager.addRecycleRecipe(energy, getItemStack("valonite_leggings"), gemValonite, 3);
			PulverizerManager.addRecycleRecipe(energy, getItemStack("valonite_boots"), gemValonite, 2);

			PulverizerManager.addRecycleRecipe(energy, getItemStack("valonite_sword"), gemValonite, 1);
			PulverizerManager.addRecycleRecipe(energy, getItemStack("valonite_shovel"), gemValonite, 1);
			PulverizerManager.addRecycleRecipe(energy, getItemStack("valonite_axe"), gemValonite, 1);
			PulverizerManager.addRecycleRecipe(energy, getItemStack("valonite_pickaxe"), gemValonite, 1);
			PulverizerManager.addRecycleRecipe(energy, getItemStack("valonite_shield"), gemValonite, 3);
		}

		/* SAWMILL */
		{
			int energy = SawmillManager.DEFAULT_ENERGY * 3 / 4;

			SawmillManager.addRecycleRecipe(energy, getItemStack("lurker_skin_helmet"), lurkerSkin, 2);
			SawmillManager.addRecycleRecipe(energy, getItemStack("lurker_skin_chestplate"), lurkerSkin, 4);
			SawmillManager.addRecycleRecipe(energy, getItemStack("lurker_skin_leggings"), lurkerSkin, 3);
			SawmillManager.addRecycleRecipe(energy, getItemStack("lurker_skin_boots"), lurkerSkin, 2);
		}

		/* SMELTER */
		{
			int energy = SmelterManager.DEFAULT_ENERGY;

			SmelterManager.addRecycleRecipe(energy, getItemStack("syrmorite_helmet"), ingotSyrmorite, 2);
			SmelterManager.addRecycleRecipe(energy, getItemStack("syrmorite_chestplate"), ingotSyrmorite, 4);
			SmelterManager.addRecycleRecipe(energy, getItemStack("syrmorite_leggings"), ingotSyrmorite, 3);
			SmelterManager.addRecycleRecipe(energy, getItemStack("syrmorite_boots"), ingotSyrmorite, 2);

			SmelterManager.addRecycleRecipe(energy, getItemStack("octine_sword"), ingotOctine, 1);
			SmelterManager.addRecycleRecipe(energy, getItemStack("octine_shovel"), ingotOctine, 1);
			SmelterManager.addRecycleRecipe(energy, getItemStack("octine_axe"), ingotOctine, 1);
			SmelterManager.addRecycleRecipe(energy, getItemStack("octine_pickaxe"), ingotOctine, 1);
			SmelterManager.addRecycleRecipe(energy, getItemStack("octine_shield"), ingotOctine, 3);

			SmelterManager.addRecycleRecipe(energy, getItemStack("syrmorite_shield"), ingotSyrmorite, 3);
			SmelterManager.addRecycleRecipe(energy, getItemStack("syrmorite_shears"), ingotSyrmorite, 1);

			SmelterManager.addRecycleRecipe(energy, getItemStack("syrmorite_door_item"), ingotSyrmorite, 1, false);
			SmelterManager.addRecycleRecipe(energy, getItemStack("syrmorite_hopper"), ingotSyrmorite, 4, false);
		}

		/* INSOLATOR */
		{

		}

		/* TAPPER */
		{

		}
	}

}
