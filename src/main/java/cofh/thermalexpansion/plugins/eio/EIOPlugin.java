package cofh.thermalexpansion.plugins.eio;

import cofh.asm.relauncher.Strippable;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.crafting.SmelterManager;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class EIOPlugin {

	public static void preInit() {

	}

	public static void initialize() {

	}

	public static void postInit() {

	}

	@Strippable("mod:EnderIO")
	public static void loadComplete() {

		if (ItemHelper.oreNameExists("ingotConductiveIron")) {
			SmelterManager.addAlloyRecipe(6000, new ItemStack(Items.IRON_INGOT), new ItemStack(Items.REDSTONE),
					ItemHelper.cloneStack(ItemHelper.getOre("ingotConductiveIron"), 1));
		}
		if (ItemHelper.oreNameExists("ingotPhasedGold") && ItemHelper.oreNameExists("ingotEnergeticAlloy")) {
			SmelterManager.addAlloyRecipe(6000, ItemHelper.cloneStack(ItemHelper.getOre("ingotEnergeticAlloy"), 1), new ItemStack(Items.ENDER_PEARL),
					ItemHelper.cloneStack(ItemHelper.getOre("ingotPhasedGold"), 1));
		}
		if (ItemHelper.oreNameExists("ingotPhasedIron")) {
			SmelterManager.addAlloyRecipe(6000, new ItemStack(Items.IRON_INGOT), new ItemStack(Items.ENDER_PEARL),
					ItemHelper.cloneStack(ItemHelper.getOre("ingotPhasedIron"), 1));
		}
		if (ItemHelper.oreNameExists("ingotSoularium")) {
			SmelterManager.addAlloyRecipe(6000, new ItemStack(Items.GOLD_INGOT), new ItemStack(Blocks.SOUL_SAND),
					ItemHelper.cloneStack(ItemHelper.getOre("ingotSoularium"), 1));
		}
		if (ItemHelper.oreNameExists("ingotSteel")) {
			if (ItemHelper.oreNameExists("ingotDarkSteel")) {
				SmelterManager.addAlloyRecipe(12000, "ingotSteel", 1, "dustObsidian", 4, ItemHelper.cloneStack(ItemHelper.getOre("ingotDarkSteel"), 1));
				SmelterManager.addAlloyRecipe(12000, "dustSteel", 1, "dustObsidian", 4, ItemHelper.cloneStack(ItemHelper.getOre("ingotDarkSteel"), 1));
			}
			if (ItemHelper.oreNameExists("ingotElectricalSteel")) {
				SmelterManager.addAlloyRecipe(6000, "ingotSteel", 1, "itemSilicon", 1, ItemHelper.cloneStack(ItemHelper.getOre("ingotElectricalSteel"), 1));
				SmelterManager.addAlloyRecipe(6000, "dustSteel", 1, "itemSilicon", 1, ItemHelper.cloneStack(ItemHelper.getOre("ingotElectricalSteel"), 1));
			}
		}
		if (ItemHelper.oreNameExists("ingotRedstoneAlloy")) {
			SmelterManager.addAlloyRecipe(6000, "dustRedstone", 1, "itemSilicon", 1, ItemHelper.cloneStack(ItemHelper.getOre("ingotRedstoneAlloy"), 1));
		}
		ThermalExpansion.log.info("Thermal Expansion: EnderIO Plugin Enabled.");
	}

}
