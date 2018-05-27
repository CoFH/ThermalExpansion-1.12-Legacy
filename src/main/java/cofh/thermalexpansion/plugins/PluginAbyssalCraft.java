package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.managers.machine.SmelterManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class PluginAbyssalCraft extends PluginTEBase {

	public static final String MOD_ID = "abyssalcraft";
	public static final String MOD_NAME = "AbyssalCraft";

	public PluginAbyssalCraft() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

		ItemStack ingotAbyssalnite = ItemHelper.getOre("ingotAbyssalnite");
		ItemStack ingotLiquifiedCoralium = ItemHelper.getOre("ingotLiquifiedCoralium");
		ItemStack ingotDreadium = ItemHelper.getOre("ingotDreadium");

		/* SMELTER */
		{
			int energy = SmelterManager.DEFAULT_ENERGY;

			SmelterManager.addRecycleRecipe(energy, getItemStack("apick"), ingotAbyssalnite, 1);
			SmelterManager.addRecycleRecipe(energy, getItemStack("aaxe"), ingotAbyssalnite, 1);
			SmelterManager.addRecycleRecipe(energy, getItemStack("ashovel"), ingotAbyssalnite, 1);
			SmelterManager.addRecycleRecipe(energy, getItemStack("ahoe"), ingotAbyssalnite, 1);
			SmelterManager.addRecycleRecipe(energy, getItemStack("asword"), ingotAbyssalnite, 1);

			SmelterManager.addRecycleRecipe(energy, getItemStack("ahelmet"), ingotAbyssalnite, 2);
			SmelterManager.addRecycleRecipe(energy, getItemStack("aplate"), ingotAbyssalnite, 4);
			SmelterManager.addRecycleRecipe(energy, getItemStack("alegs"), ingotAbyssalnite, 3);
			SmelterManager.addRecycleRecipe(energy, getItemStack("aboots"), ingotAbyssalnite, 2);

			SmelterManager.addRecycleRecipe(energy, getItemStack("corpick"), ingotLiquifiedCoralium, 1);
			SmelterManager.addRecycleRecipe(energy, getItemStack("coraxe"), ingotLiquifiedCoralium, 1);
			SmelterManager.addRecycleRecipe(energy, getItemStack("corshovel"), ingotLiquifiedCoralium, 1);
			SmelterManager.addRecycleRecipe(energy, getItemStack("corhoe"), ingotLiquifiedCoralium, 1);
			SmelterManager.addRecycleRecipe(energy, getItemStack("corsword"), ingotLiquifiedCoralium, 1);

			SmelterManager.addRecycleRecipe(energy, getItemStack("corhelmet"), ingotLiquifiedCoralium, 2);
			SmelterManager.addRecycleRecipe(energy, getItemStack("corplate"), ingotLiquifiedCoralium, 4);
			SmelterManager.addRecycleRecipe(energy, getItemStack("corlegs"), ingotLiquifiedCoralium, 3);
			SmelterManager.addRecycleRecipe(energy, getItemStack("corboots"), ingotLiquifiedCoralium, 2);

			SmelterManager.addRecycleRecipe(energy, getItemStack("dreadiumpick"), ingotDreadium, 1);
			SmelterManager.addRecycleRecipe(energy, getItemStack("dreadiumaxe"), ingotDreadium, 1);
			SmelterManager.addRecycleRecipe(energy, getItemStack("dreadiumshovel"), ingotDreadium, 1);
			SmelterManager.addRecycleRecipe(energy, getItemStack("dreadiumhoe"), ingotDreadium, 1);
			SmelterManager.addRecycleRecipe(energy, getItemStack("dreadiumsword"), ingotDreadium, 1);

			SmelterManager.addRecycleRecipe(energy, getItemStack("dreadiumhelmet"), ingotDreadium, 2);
			SmelterManager.addRecycleRecipe(energy, getItemStack("dreadiumplate"), ingotDreadium, 4);
			SmelterManager.addRecycleRecipe(energy, getItemStack("dreadiumlegs"), ingotDreadium, 3);
			SmelterManager.addRecycleRecipe(energy, getItemStack("dreadiumboots"), ingotDreadium, 2);
		}

		/* TRANSPOSER */
		{
			int energy = TransposerManager.DEFAULT_ENERGY * 10;
			FluidStack liquidCoralium = FluidRegistry.getFluidStack("liquidcoralium", 20);

			if (liquidCoralium != null) {
				TransposerManager.addFillRecipe(energy, getItemStack("stone", 1, 5), new ItemStack(Blocks.END_STONE), liquidCoralium, false);
				TransposerManager.addFillRecipe(energy, new ItemStack(Blocks.END_STONE), getItemStack("stone", 1, 5), liquidCoralium, false);
				TransposerManager.addFillRecipe(energy, getItemStack("antibeef"), new ItemStack(Items.COOKED_BEEF), liquidCoralium, false);
				TransposerManager.addFillRecipe(energy, getItemStack("antibone"), new ItemStack(Items.BONE, 2), liquidCoralium, false);
				TransposerManager.addFillRecipe(energy, getItemStack("antichicken"), new ItemStack(Items.COOKED_CHICKEN), liquidCoralium, false);
				TransposerManager.addFillRecipe(energy, getItemStack("antiflesh"), new ItemStack(Items.ROTTEN_FLESH, 2), liquidCoralium, false);
				TransposerManager.addFillRecipe(energy, getItemStack("antispidereye"), new ItemStack(Items.SPIDER_EYE, 2), liquidCoralium, false);
				TransposerManager.addFillRecipe(energy, getItemStack("antipork"), new ItemStack(Items.COOKED_PORKCHOP), liquidCoralium, false);
			}
		}
	}

}
