package cofh.thermalexpansion.plugins;

import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalfoundation.item.ItemFertilizer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class PluginExU2 extends PluginTEBase {

	public static final String MOD_ID = "extrautils2";
	public static final String MOD_NAME = "Extra Utilities 2";

	public PluginExU2() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

		ItemStack[] cobblestone = new ItemStack[8];
		ItemStack[] dirt = new ItemStack[4];
		ItemStack[] sand = new ItemStack[2];
		ItemStack[] gravel = new ItemStack[2];
		ItemStack[] netherrack = new ItemStack[6];

		for (int i = 0; i < cobblestone.length; i++) {
			cobblestone[i] = getItemStack("compressedcobblestone", 1, i);
		}
		for (int i = 0; i < dirt.length; i++) {
			dirt[i] = getItemStack("compresseddirt", 1, i);
		}
		for (int i = 0; i < sand.length; i++) {
			sand[i] = getItemStack("compressedsand", 1, i);
		}
		for (int i = 0; i < gravel.length; i++) {
			gravel[i] = getItemStack("compressedgravel", 1, i);
		}
		for (int i = 0; i < netherrack.length; i++) {
			netherrack[i] = getItemStack("compressednetherrack", 1, i);
		}
		ItemStack enderLily = getItemStack("enderlilly", 1);
		ItemStack redOrchid = getItemStack("redorchid", 1);

		/* PULVERIZER */
		{

		}

		/* INSOLATOR */
		{
			InsolatorManager.addRecipe(120000, 4000, enderLily, ItemFertilizer.fertilizerFlux, new ItemStack(Items.ENDER_PEARL), enderLily, 100);
			InsolatorManager.addRecipe(120000, 4000, redOrchid, ItemFertilizer.fertilizerFlux, new ItemStack(Items.REDSTONE), redOrchid, 100);
		}

		/* COMPACTOR */
		{
			//			CompactorManager.addDefaultStorageRecipe(new ItemStack(Blocks.COBBLESTONE), cobblestone[0]);
			//			CompactorManager.addDefaultStorageRecipe(new ItemStack(Blocks.DIRT), dirt[0]);
			//			// CompactorManager.addDefaultStorageRecipe(new ItemStack(Blocks.SAND), sand[0]);
			//			CompactorManager.addDefaultStorageRecipe(new ItemStack(Blocks.GRAVEL), gravel[0]);
			//			CompactorManager.addDefaultStorageRecipe(new ItemStack(Blocks.NETHERRACK), netherrack[0]);
			//
			//			for (int i = 0; i < cobblestone.length - 1; i++) {
			//				CompactorManager.addDefaultStorageRecipe(cobblestone[i], cobblestone[i + 1]);
			//			}
			//			for (int i = 0; i < dirt.length - 1; i++) {
			//				CompactorManager.addDefaultStorageRecipe(dirt[i], dirt[i + 1]);
			//			}
			//			for (int i = 0; i < sand.length - 1; i++) {
			//				CompactorManager.addDefaultStorageRecipe(sand[i], sand[i + 1]);
			//			}
			//			for (int i = 0; i < gravel.length - 1; i++) {
			//				CompactorManager.addDefaultStorageRecipe(gravel[i], gravel[i + 1]);
			//			}
			//			for (int i = 0; i < netherrack.length - 1; i++) {
			//				CompactorManager.addDefaultStorageRecipe(netherrack[i], netherrack[i + 1]);
			//			}
		}
	}

}
