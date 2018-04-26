package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.managers.device.TapperManager;
import cofh.thermalexpansion.util.managers.machine.EnchanterManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class PluginEvilCraft extends PluginTEBase {

	public static final String MOD_ID = "evilcraft";
	public static final String MOD_NAME = "EvilCraft";

	public PluginEvilCraft() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

		ItemStack bloodOrb = getItemStack("blood_orb", 1, 1);
		ItemStack bloodWaxedCoal = getItemStack("blood_waxed_coal");
		ItemStack darkGem = getItemStack("dark_gem");
		ItemStack darkPowerGem = getItemStack("dark_power_gem");
		ItemStack poisonSac = getItemStack("poison_sac");

		ItemStack logUndead = getItemStack("undead_log");
		ItemStack saplingUndead = getItemStack("undead_sapling");

		Block blockLogUndead = getBlock("undead_log");
		Block blockLeavesUndead = getBlock("undead_leaves");

		Fluid fluidBlood = FluidRegistry.getFluid("evilcraftblood");

		/* INSOLATOR */
		{
			int energy = InsolatorManager.DEFAULT_ENERGY;

			InsolatorManager.addDefaultTreeRecipe(energy * 2, saplingUndead, ItemHelper.cloneStack(logUndead, 6), new ItemStack(Blocks.DEADBUSH), 100);
		}

		/* TRANSPOSER */
		{
			int energy = 2400;

			if (fluidBlood != null) {
				TransposerManager.addExtractRecipe(energy, logUndead, getItemStack("hardened_blood_shard"), new FluidStack(fluidBlood, 100), 25, false);
			}
		}

		/* ENCHANTER */
		{
			EnchanterManager.addDefaultEnchantmentRecipe(bloodOrb, MOD_ID + ":life_stealing", 1);
			EnchanterManager.addDefaultEnchantmentRecipe(poisonSac, MOD_ID + ":poison_tip", 2);
			EnchanterManager.addDefaultEnchantmentRecipe(bloodWaxedCoal, MOD_ID + ":unusing", 3);
			//			EnchanterManager.addDefaultEnchantmentRecipe(darkGem, MOD_ID + ":breaking", 1);
			//			EnchanterManager.addDefaultEnchantmentRecipe(darkPowerGem, MOD_ID + ":vengeance", 1);
		}

		/* TAPPER */
		{
			if (fluidBlood != null) {
				TapperManager.addItemMapping(logUndead, new FluidStack(fluidBlood, 10));

				TapperManager.addBlockStateMapping(new ItemStack(blockLogUndead, 1, 1), new FluidStack(fluidBlood, 50));

				addLeafMapping(blockLogUndead, 1, blockLeavesUndead, 0);
			}
		}
	}

}
