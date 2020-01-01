package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;

import java.util.Collections;

import static java.util.Arrays.asList;

public class PluginGTCX extends PluginTEBase {

    public static final String MOD_ID = "gtc_expansion";
    public static final String MOD_NAME = "Gregtech Classic Expansion";

    public PluginGTCX() {

        super(MOD_ID, MOD_NAME);
    }

    @Override
    public void initializeDelegate() {

        /* TRANSPOSER */
        {
            int energy = 2000;
            if (!Loader.isModLoaded(PluginTechReborn.MOD_ID))
                TransposerManager.addFillRecipe(energy, ItemHelper.getOre("ingotHotTungstensteel"), ItemHelper.getOre("ingotTungstensteel"), new FluidStack(TFFluids.fluidCryotheum, 200), false);
            TransposerManager.addFillRecipe(energy, ItemHelper.getOre("ingotHotTungsten"), ItemHelper.getOre("ingotTungsten"), new FluidStack(TFFluids.fluidCryotheum, 200), false);
            TransposerManager.addFillRecipe(energy, ItemHelper.getOre("ingotHotIridium"), ItemHelper.getOre("ingotIridium"), new FluidStack(TFFluids.fluidCryotheum, 200), false);
            TransposerManager.addFillRecipe(energy, ItemHelper.getOre("ingotHotKanthal"), ItemHelper.getOre("ingotKanthal"), new FluidStack(TFFluids.fluidCryotheum, 200), false);
            TransposerManager.addFillRecipe(energy, ItemHelper.getOre("ingotHotOsmium"), ItemHelper.getOre("ingotOsmium"), new FluidStack(TFFluids.fluidCryotheum, 200), false);
        }

        /* CENTRIFUGE */
        {
            int energy = CentrifugeManager.DEFAULT_ENERGY;

            if (!Loader.isModLoaded(PluginTechReborn.MOD_ID)){
                CentrifugeManager.addRecipe(energy * 15, ItemHelper.getOre("dustRedGarnet", 16), asList(ItemHelper.getOre("dustSpessartine", 8), ItemHelper.getOre("dustAlmandine", 5), ItemHelper.getOre("dustPyrope", 3)), null);
                CentrifugeManager.addRecipe(energy * 15, ItemHelper.getOre("dustYellowGarnet", 16), asList(ItemHelper.getOre("dustGrossular", 8), ItemHelper.getOre("dustAndradite", 5), ItemHelper.getOre("dustUvarovite", 3)), null);
                CentrifugeManager.addRecipe((int)(energy * 1.2F), ItemHelper.getOre("dustDarkAshes", 2), asList(ItemHelper.getOre("dustAshes"), ItemHelper.getOre("dustSlag")), null);
                CentrifugeManager.addRecipe((int)(energy * 5.3F), ItemHelper.getOre("dustMarble", 8), asList(ItemHelper.getOre("dustCalcite", 7), ItemHelper.getOre("dustMagnesium")), null);
                CentrifugeManager.addRecipe(energy * 24, ItemHelper.getOre("dustBasalt", 16), asList(ItemHelper.getOre("dustFlint", 8), ItemHelper.getOre("dustDarkAshes", 4), ItemHelper.getOre("dustCalcite", 3), ItemHelper.getOre("dustOlivine")), null);
            }
            CentrifugeManager.addRecipe(energy * 2, ItemHelper.getOre("dustRedrock", 16), asList(ItemHelper.getOre("dustCalcite", 8), ItemHelper.getOre("dustFlint", 4), ItemHelper.getOre("dustClay", 4)), null);
            CentrifugeManager.addRecipe((int)(energy * 1.2F), ItemHelper.getOre("dustAshes", 2), Collections.singletonList(ItemHelper.getOre("dustCarbon")), null);
            CentrifugeManager.addRecipe(energy * 4, ItemHelper.getOre("dustSlag", 16), asList(ItemHelper.getOre("dustSulfur", 4), ItemHelper.getOre("dustPhosphorus", 4), new ItemStack(Items.IRON_NUGGET, 3), new ItemStack(Items.GOLD_NUGGET)), null);
        }
    }
}
