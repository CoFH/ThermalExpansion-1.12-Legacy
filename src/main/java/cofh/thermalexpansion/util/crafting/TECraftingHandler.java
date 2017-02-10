package cofh.thermalexpansion.util.crafting;

import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

import static cofh.lib.util.helpers.ItemHelper.ShapelessRecipe;

public class TECraftingHandler {

	public static TECraftingHandler instance = new TECraftingHandler();

	private TECraftingHandler() {

	}

	public static void initialize() {

		FMLCommonHandler.instance().bus().register(instance);
	}

	public static void addMachineRecipes(ItemStack stack, ItemStack augments, Object[] recipe) {

	}

	public static void addMachineUpgradeRecipes(ItemStack stack) {

		//		GameRegistry.addRecipe(new RecipeMachineUpgrade(1, RecipeMachineUpgrade.getMachineLevel(stack, 1), new Object[] { "IGI", " X ", "I I", 'I', "ingotInvar", 'G', "gearElectrum", 'X', RecipeMachineUpgrade.getMachineLevel(stack, 0) }));
		//		GameRegistry.addRecipe(new RecipeMachineUpgrade(2, RecipeMachineUpgrade.getMachineLevel(stack, 2), new Object[] { "IGI", " X ", "I I", 'I', "blockGlassHardened", 'G', "gearSignalum", 'X', RecipeMachineUpgrade.getMachineLevel(stack, 1) }));
		//		GameRegistry.addRecipe(new RecipeMachineUpgrade(3, RecipeMachineUpgrade.getMachineLevel(stack, 3), new Object[] { "IGI", " X ", "I I", 'I', "ingotSilver", 'G', "gearEnderium", 'X', RecipeMachineUpgrade.getMachineLevel(stack, 2) }));
	}

	public static void addSecureRecipe(ItemStack stack) {

		//GameRegistry.addRecipe(new RecipeSecure(stack, new Object[] { " L ", "SXS", " S ", 'L', TEItemsOld.lock, 'S', "nuggetSignalum", 'X', stack }));
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
		// Crafting Steps
//		if (stack.isItemEqual(BlockFrame.frameMachineBasic)) {
		//			player.addStat(TEAchievements.machineFrame, 1);
		//		}
		// Machine Achievements
		//		else if (stack.isItemEqual(BlockMachine.furnace)) {
		//			player.addStat(TEAchievements.furnace, 1);
		//		} else if (stack.isItemEqual(BlockMachine.pulverizer)) {
		//			player.addStat(TEAchievements.pulverizer, 1);
		//		} else if (stack.isItemEqual(BlockMachine.sawmill)) {
		//			player.addStat(TEAchievements.sawmill, 1);
		//		} else if (stack.isItemEqual(BlockMachine.smelter)) {
		//			player.addStat(TEAchievements.smelter, 1);
		//		} else if (stack.isItemEqual(BlockMachine.crucible)) {
		//			player.addStat(TEAchievements.crucible, 1);
		//		} else if (stack.isItemEqual(BlockMachine.transposer)) {
		//			player.addStat(TEAchievements.transposer, 1);
		//		} else if (stack.isItemEqual(BlockMachine.precipitator)) {
		//			player.addStat(TEAchievements.precipitator, 1);
		//		} else if (stack.isItemEqual(BlockMachine.extruder)) {
		//			player.addStat(TEAchievements.extruder, 1);
		//		} else if (stack.isItemEqual(BlockMachine.accumulator)) {
		//			player.addStat(TEAchievements.accumulator, 1);
		//		} else if (stack.isItemEqual(BlockMachine.assembler)) {
		//			player.addStat(TEAchievements.assembler, 1);
		//		} else if (stack.isItemEqual(BlockMachine.charger)) {
		//			player.addStat(TEAchievements.charger, 1);
		//		} else if (stack.isItemEqual(BlockMachine.insolator)) {
		//			player.addStat(TEAchievements.insolator, 1);
		//		}
		// Resonant Achievements
		//		else if (stack.isItemEqual(BlockCell.cellResonant)) {
		//			player.addStat(TEAchievements.resonantCell, 1);
		//		} else if (stack.isItemEqual(BlockTank.tankResonant)) {
		//			player.addStat(TEAchievements.resonantTank, 1);
		//		} else if (stack.isItemEqual(BlockCache.cacheResonant)) {
		//			player.addStat(TEAchievements.resonantCache, 1);
		//		} else if (stack.isItemEqual(BlockStrongbox.strongboxResonant)) {
		//			player.addStat(TEAchievements.resonantStrongbox, 1);
		//		}
	}

	public static void loadRecipes() {

		String[] oreNameList = OreDictionary.getOreNames();
		String oreType = "";

		for (int i = 0; i < oreNameList.length; i++) {
			if (oreNameList[i].startsWith("ore")) {
				oreType = oreNameList[i].substring(3, oreNameList[i].length());

				if (oreType.isEmpty()) {
					continue;
				}
				String oreName = "ore" + StringHelper.titleCase(oreType);
				String ingotName = "ingot" + StringHelper.titleCase(oreType);

				List<ItemStack> registeredOre = OreDictionary.getOres(oreName);
				List<ItemStack> registeredIngot = OreDictionary.getOres(ingotName);

				if (registeredOre.size() <= 0 || registeredIngot.size() <= 0) {
					continue;
				}
				ItemStack ingot = ItemHelper.cloneStack(registeredIngot.get(0), 1);
				GameRegistry.addRecipe(ShapelessRecipe(ingot, oreName, "dustPyrotheum"));
			}
		}
	}

}
