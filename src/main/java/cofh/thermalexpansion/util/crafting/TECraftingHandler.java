package cofh.thermalexpansion.util.crafting;

import cofh.core.util.crafting.RecipeSecure;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.core.TEAchievements;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.item.TEItems;
import cofh.thermalexpansion.plugins.nei.handlers.NEIRecipeWrapper;
import cofh.thermalfoundation.item.TFItems;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.registry.GameRegistry;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class TECraftingHandler {

	public static TECraftingHandler instance = new TECraftingHandler();

	public static void initialize() {

		FMLCommonHandler.instance().bus().register(instance);
	}

	public static void addMachineUpgradeRecipes(ItemStack stack) {

		GameRegistry.addRecipe(NEIRecipeWrapper.wrap(new RecipeMachineUpgrade(1, stack, new Object[] { "IGI", " X ", "I I", 'I', "ingotInvar", 'G',
				"gearElectrum", 'X', RecipeMachineUpgrade.getMachineLevel(stack, 0) })));
		GameRegistry.addRecipe(NEIRecipeWrapper.wrap(new RecipeMachineUpgrade(2, stack, new Object[] { "IGI", " X ", "I I", 'I', "blockGlassHardened", 'G',
				"gearSignalum", 'X', RecipeMachineUpgrade.getMachineLevel(stack, 1) })));
		GameRegistry.addRecipe(NEIRecipeWrapper.wrap(new RecipeMachineUpgrade(3, stack, new Object[] { "IGI", " X ", "I I", 'I', "ingotSilver", 'G',
				"gearEnderium", 'X', RecipeMachineUpgrade.getMachineLevel(stack, 2) })));
	}

	public static void addSecureRecipe(ItemStack stack) {

		GameRegistry.addRecipe(NEIRecipeWrapper.wrap(new RecipeSecure(stack, new Object[] { " L ", "SXS", " S ", 'L', TEItems.lock, 'S', "nuggetSignalum", 'X',
				stack })));
	}

	@SubscribeEvent
	public void handleOnItemCrafted(PlayerEvent.ItemCraftedEvent event) {

		if (event.crafting == null) {
			return;
		}
		checkAchievements(event.player, event.crafting, event.craftMatrix);
	}

	private void checkAchievements(EntityPlayer player, ItemStack stack, IInventory craftMatrix) {

		if (!TEProps.enableAchievements) {
			return;
		}
		if (stack.isItemEqual(BlockMachine.furnace)) {
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
		} else if (stack.isItemEqual(BlockMachine.precipitator)) {
			player.addStat(TEAchievements.precipitator, 1);
		} else if (stack.isItemEqual(BlockMachine.extruder)) {
			player.addStat(TEAchievements.extruder, 1);
		} else if (stack.isItemEqual(BlockMachine.accumulator)) {
			player.addStat(TEAchievements.accumulator, 1);
		} else if (stack.isItemEqual(BlockMachine.assembler)) {
			player.addStat(TEAchievements.assembler, 1);
		} else if (stack.isItemEqual(BlockMachine.charger)) {
			player.addStat(TEAchievements.charger, 1);
		} else if (stack.isItemEqual(BlockMachine.insolator)) {
			player.addStat(TEAchievements.insolator, 1);
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
				GameRegistry.addRecipe(new ShapelessOreRecipe(ingot, new Object[] { oreName, TFItems.dustPyrotheum }));
			}
		}
	}

}
