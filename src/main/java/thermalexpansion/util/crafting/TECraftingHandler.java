package thermalexpansion.util.crafting;

import cofh.util.ItemHelper;
import cofh.util.StringHelper;
import cpw.mods.fml.common.registry.GameRegistry;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import thermalexpansion.block.machine.BlockMachine;
import thermalexpansion.core.TEAchievements;
import thermalexpansion.core.TEProps;
import thermalfoundation.item.TFItems;

//TODO: ICraftingHandler was removed and does not catch crafting anymore. Need to find an alternative solution for this.
public class TECraftingHandler {

	public static TECraftingHandler instance = new TECraftingHandler();

	// public static Map<ItemStack, ItemStack> pyrotheumMap = new HashMap();

	public static void initialize() {

		// GameRegistry.registerCraftingHandler(instance);
	}

	// @Override
	// public void onCrafting(EntityPlayer player, ItemStack stack, IInventory craftMatrix) {
	//
	// if (stack == null) {
	// return;
	// }
	// checkAchievements(player, stack, craftMatrix);
	//
	// }
	//
	// @Override
	// public void onSmelting(EntityPlayer player, ItemStack stack) {
	//
	// }

	private void checkAchievements(EntityPlayer player, ItemStack stack, IInventory craftMatrix) {

		if (!TEProps.enableAchievements) {
			return;
		}
		if (stack.isItemEqual(BlockMachine.machineFrame)) {
			player.addStat(TEAchievements.machineFrame, 1);
		} else if (stack.isItemEqual(BlockMachine.furnace)) {
			player.addStat(TEAchievements.furnace, 1);
		} else if (stack.isItemEqual(BlockMachine.pulverizer)) {
			player.addStat(TEAchievements.pulverizer, 1);
		} else if (stack.isItemEqual(BlockMachine.sawmill)) {
			player.addStat(TEAchievements.sawmill, 1);
		} else if (stack.isItemEqual(BlockMachine.smelter)) {
			player.addStat(TEAchievements.smelter, 1);
		} else if (stack.isItemEqual(BlockMachine.crucible)) {
			player.addStat(TEAchievements.crucible, 1);
		} else if (stack.isItemEqual(BlockMachine.transposer)) {
			player.addStat(TEAchievements.transposer, 1);
		} else if (stack.isItemEqual(BlockMachine.iceGen)) {
			player.addStat(TEAchievements.iceGen, 1);
		} else if (stack.isItemEqual(BlockMachine.rockGen)) {
			player.addStat(TEAchievements.rockGen, 1);
		} else if (stack.isItemEqual(BlockMachine.waterGen)) {
			player.addStat(TEAchievements.waterGen, 1);
		} else if (stack.isItemEqual(BlockMachine.assembler)) {
			player.addStat(TEAchievements.assembler, 1);
		} else if (stack.isItemEqual(BlockMachine.charger)) {
			player.addStat(TEAchievements.charger, 1);
		}
	}

	public static void loadRecipes() {

		String[] oreNameList = OreDictionary.getOreNames();
		String oreType = "";

		for (int i = 0; i < oreNameList.length; i++) {
			if (oreNameList[i].startsWith("ore")) {
				oreType = oreNameList[i].substring(3, oreNameList[i].length());

				String oreName = "ore" + StringHelper.titleCase(oreType);
				String ingotName = "ingot" + StringHelper.titleCase(oreType);

				ArrayList<ItemStack> registeredOre = OreDictionary.getOres(oreName);
				ArrayList<ItemStack> registeredIngot = OreDictionary.getOres(ingotName);

				if (registeredOre.size() <= 0 || registeredIngot.size() <= 0) {
					continue;
				}
				ItemStack ingot = ItemHelper.cloneStack(registeredIngot.get(0), 1);
				// pyrotheumMap.put(registeredOre.get(0), ingot);
				GameRegistry.addRecipe(new ShapelessOreRecipe(ingot, new Object[] { oreName, TFItems.dustPyrotheum }));
			}
		}
	}

}
