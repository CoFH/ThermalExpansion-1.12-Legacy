package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class PluginElementalDimensions extends PluginTEBase {

	public static final String MOD_ID = "elementaldimensions";
	public static final String MOD_NAME = "Elemental Dimensions";

	public PluginElementalDimensions() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

		/* PULVERIZER */
		{
			// TODO: Dust Ore support?
		}

		/* CENTRIFUGE */
		{
			CentrifugeManager.addDefaultMobRecipe(modId + ":ed_blaster", asList(new ItemStack(Items.BLAZE_ROD), ItemHelper.cloneStack(ItemMaterial.dustSulfur)), asList(50, 25), 10);
			CentrifugeManager.addDefaultMobRecipe(modId + ":ed_dirtzombie", asList(new ItemStack(Items.ROTTEN_FLESH, 2), getItemStack("waterrune_part1")), asList(50, 5), 5);
			CentrifugeManager.addDefaultMobRecipe(modId + ":ed_ghost", singletonList(getItemStack("spiritrune_part1")), singletonList(25), 5);
			CentrifugeManager.addDefaultMobRecipe(modId + ":ed_guard", singletonList(getItemStack("earthrune")), singletonList(5), 5);
			CentrifugeManager.addDefaultMobRecipe(modId + ":ed_spirit", asList(new ItemStack(Items.ROTTEN_FLESH, 2), getItemStack("firerune_part1"), getItemStack("firerune_part3")), asList(50, 5, 5), 5);
			CentrifugeManager.addDefaultMobRecipe(modId + ":ed_watercreep", singletonList(getItemStack("airrune_part1")), singletonList(5), 5);
		}
	}

}
