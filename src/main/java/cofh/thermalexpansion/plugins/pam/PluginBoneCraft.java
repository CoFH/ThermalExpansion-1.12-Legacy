package cofh.thermalexpansion.plugins.pam;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.plugins.PluginTEBase;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class PluginBoneCraft extends PluginTEBase {

	public static final String MOD_ID = "bonecraft";
	public static final String MOD_NAME = "Pam's BoneCraft";

	public PluginBoneCraft() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

	}

	@Override
	public void registerDelegate() {

		ItemStack blockFossil = getItemStack("pamfossil");
		ItemStack bone = new ItemStack(Items.BONE);
		ItemStack bonemeal = new ItemStack(Items.DYE, 1, 15);

		ItemStack boneHelm = getItemStack("bonehelmitem");
		ItemStack boneChest = getItemStack("bonechestitem");
		ItemStack boneLegs = getItemStack("bonelegsitem");
		ItemStack boneBoots = getItemStack("bonebootsitem");

		ItemStack boneSword = getItemStack("bonesworditem");
		ItemStack boneShovel = getItemStack("boneshovelitem");
		ItemStack bonePickaxe = getItemStack("bonepickaxeitem");
		ItemStack boneAxe = getItemStack("boneaxeitem");
		ItemStack boneHoe = getItemStack("bonehoeitem");

		/* PULVERIZER */
		{
			int energy = PulverizerManager.DEFAULT_ENERGY;

			PulverizerManager.addRecipe(energy, blockFossil, ItemHelper.cloneStack(bone, 4), ItemHelper.cloneStack(bonemeal, 4));

			energy = PulverizerManager.DEFAULT_ENERGY * 3 / 2;

			PulverizerManager.addRecipe(energy, boneSword, ItemHelper.cloneStack(bone, 2), ItemHelper.cloneStack(bonemeal, 2));
			PulverizerManager.addRecipe(energy, bonePickaxe, ItemHelper.cloneStack(bone, 3), ItemHelper.cloneStack(bonemeal, 2));
			PulverizerManager.addRecipe(energy, boneAxe, ItemHelper.cloneStack(bone, 3), ItemHelper.cloneStack(bonemeal, 2));
			PulverizerManager.addRecipe(energy, boneShovel, ItemHelper.cloneStack(bone, 1), ItemHelper.cloneStack(bonemeal, 2));
			PulverizerManager.addRecipe(energy, boneHoe, ItemHelper.cloneStack(bone, 2), ItemHelper.cloneStack(bonemeal, 2));

			PulverizerManager.addRecipe(energy, boneHelm, ItemHelper.cloneStack(bone, 4), ItemHelper.cloneStack(bonemeal, 2));
			PulverizerManager.addRecipe(energy, boneChest, ItemHelper.cloneStack(bone, 7), ItemHelper.cloneStack(bonemeal, 2));
			PulverizerManager.addRecipe(energy, boneLegs, ItemHelper.cloneStack(bone, 6), ItemHelper.cloneStack(bonemeal, 2));
			PulverizerManager.addRecipe(energy, boneBoots, ItemHelper.cloneStack(bone, 3), ItemHelper.cloneStack(bonemeal, 2));
		}
	}

}
