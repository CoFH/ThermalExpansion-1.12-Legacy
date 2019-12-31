package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import java.util.Collections;

import static java.util.Arrays.asList;

public class PluginGTC extends PluginTEBase {

    public static final String MOD_ID = "gtclassic";
    public static final String MOD_NAME = "Gregtech Classic";

    public PluginGTC() {

        super(MOD_ID, MOD_NAME);
    }

    @Override
    public void initializeDelegate() {
        /* CENTRIFUGE */
        {
            int energy = CentrifugeManager.DEFAULT_ENERGY;

            CentrifugeManager.addRecipe(energy * 50, new ItemStack(Blocks.DIRT, 64, 0), asList(new ItemStack(Blocks.SAND, 32, 0), getItemStack(PluginIC2.MOD_ID,"itemmisc", 2, 351), getItemStack(PluginIC2.MOD_ID,"itemmisc", 2, 350), new ItemStack(Items.CLAY_BALL, 2)), null);
            CentrifugeManager.addRecipe(energy * 50, new ItemStack(Blocks.GRASS, 64, 0), asList(new ItemStack(Blocks.SAND, 32, 0), getItemStack(PluginIC2.MOD_ID,"itemmisc", 2, 351), getItemStack(PluginIC2.MOD_ID,"itemmisc", 4, 350), new ItemStack(Items.CLAY_BALL, 2)), null);
            CentrifugeManager.addRecipe(energy * 62, new ItemStack(Blocks.MYCELIUM, 64, 0), asList(new ItemStack(Blocks.SAND, 32, 0), new ItemStack(Blocks.BROWN_MUSHROOM, 16), new ItemStack(Blocks.RED_MUSHROOM, 16), new ItemStack(Items.CLAY_BALL, 8)), null);
            CentrifugeManager.addRecipe(energy * 35, ItemHelper.getOre("dustEnderEye", 16), asList(ItemHelper.getOre("dustEnderpearl", 8), new ItemStack(Items.BLAZE_POWDER, 8)), null);
            CentrifugeManager.addRecipe(energy * 125, new ItemStack(Items.DYE, 64, 4), asList(ItemHelper.getOre("dustLazurite", 48), ItemHelper.getOre("dustSodalite", 8), ItemHelper.getOre("dustPyrite", 4), ItemHelper.getOre("dustCalcite", 4)), null);
            CentrifugeManager.addRecipe((int)(energy * 12.5F), getItemStack(PluginIC2.MOD_ID, "itemharz", 8, 0), asList(getItemStack(PluginIC2.MOD_ID,"itemmisc", 28, 450), getItemStack(PluginIC2.MOD_ID,"itemmisc", 2, 351), getItemStack(PluginIC2.MOD_ID,"itemmisc", 2, 350)), null);
            CentrifugeManager.addRecipe(energy * 24, new ItemStack(Blocks.STONE, 4, 1), asList(ItemHelper.getOre("dustAluminium", 2), ItemHelper.getOre("dustFlint", 1), ItemHelper.getOre("dustClay")), null);
            CentrifugeManager.addRecipe(energy * 36, new ItemStack(Blocks.STONE, 16, 3), Collections.singletonList(ItemHelper.getOre("dustNickel", 2)), null);
            if (!Loader.isModLoaded(PluginGTCX.MOD_ID) && !Loader.isModLoaded(PluginTechReborn.MOD_ID))
            CentrifugeManager.addRecipe(energy * 24, ItemHelper.getOre("dustBasalt", 16), asList(ItemHelper.getOre("dustFlint", 8), ItemHelper.getOre("dustCalcite", 3), ItemHelper.getOre("dustCarbon", 1), ItemHelper.getOre("dustEmerald")), null);
        }
    }

}
