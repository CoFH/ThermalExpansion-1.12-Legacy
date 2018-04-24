package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.managers.machine.EnchanterManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import cofh.thermalfoundation.item.ItemFertilizer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class PluginAstralSorcery extends PluginTEBase {

	public static final String MOD_ID = "astralsorcery";
	public static final String MOD_NAME = "Astral Sorcery";

	public PluginAstralSorcery() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void registerDelegate() {

		ItemStack faintAmaranth = getItemStack("blockcustomflower", 1);
		ItemStack dustAstralStarmetal = ItemHelper.getOre("dustAstralStarmetal");

		/* PULVERIZER */
		{
			int energy = PulverizerManager.DEFAULT_ENERGY;

			PulverizerManager.addRecipe(energy, ItemHelper.getOre("oreAquamarine"), ItemHelper.getOre("gemAquamarine", 6));
		}

		/* INSOLATOR */
		{
			InsolatorManager.addRecipe(120000, 4000, faintAmaranth, ItemFertilizer.fertilizerFlux, new ItemStack(Items.ENDER_PEARL), faintAmaranth, 100);
		}

		/* ENCHANTER */
		{
			EnchanterManager.addDefaultEnchantmentRecipe(dustAstralStarmetal, MOD_ID + ":enchantment.as.nightvision", 3);
		}
	}

}
