package cofh.thermalexpansion.item;

import cofh.core.item.ItemBase;
import cofh.core.util.crafting.RecipeUpgrade;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.item.tool.ItemChiller;
import cofh.thermalexpansion.item.tool.ItemIgniter;
import cofh.thermalexpansion.item.tool.ItemMiner;
import cofh.thermalexpansion.item.tool.ItemMultimeter;
import cofh.thermalexpansion.item.tool.ItemPump;
import cofh.thermalexpansion.item.tool.ItemTransfuser;
import cofh.thermalexpansion.item.tool.ItemWrench;
import cofh.thermalexpansion.item.tool.ItemWrenchBattle;
import cofh.thermalexpansion.util.crafting.TECraftingHandler;
import cofh.thermalfoundation.item.Equipment;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class TEItems {

	private TEItems() {

	}

	public static void preInit() {

		itemWrench = (ItemWrench) new ItemWrench().setUnlocalizedName("tool", "wrench");
		itemMultimeter = (ItemMultimeter) new ItemMultimeter().setUnlocalizedName("tool", "meter");
		itemIgniter = (ItemIgniter) new ItemIgniter().setUnlocalizedName("tool", "igniter");
		itemChiller = (ItemChiller) new ItemChiller().setUnlocalizedName("tool", "chiller");
		itemPump = (ItemPump) new ItemPump().setUnlocalizedName("tool", "pump");
		itemTransfuser = (ItemTransfuser) new ItemTransfuser().setUnlocalizedName("tool", "transfuser");
		itemMiner = (ItemMiner) new ItemMiner().setUnlocalizedName("tool", "miner");
		itemCapacitor = (ItemCapacitor) new ItemCapacitor().setUnlocalizedName("capacitor");
		itemSatchel = (ItemSatchel) new ItemSatchel().setUnlocalizedName("satchel");
		itemDiagram = (ItemDiagram) new ItemDiagram().setUnlocalizedName("diagram");
		itemMaterial = (ItemBase) new ItemBase("thermalexpansion").setUnlocalizedName("material").setCreativeTab(ThermalExpansion.tabItems);

		itemBattleWrenchInvar = new ItemWrenchBattle(Equipment.Invar.TOOL_MATERIAL).setRepairIngot("ingotInvar");
		itemBattleWrenchInvar.setUnlocalizedName("thermalexpansion.tool.invarBattleWrench");
		itemBattleWrenchInvar.setTextureName("thermalexpansion:tool/InvarBattleWrench");
		itemBattleWrenchInvar.setCreativeTab(ThermalExpansion.tabTools);
		GameRegistry.registerItem(itemBattleWrenchInvar, "tool.battleWrenchInvar");

		TEAugments.preInit();
		TEEquipment.preInit();
		TEFlorbs.preInit();
	}

	public static void initialize() {

		ItemSatchel.configure();

		/* Tools */
		toolWrench = itemWrench.addItem(0, "wrench");
		toolMultimeter = itemMultimeter.addItem(0, "multimeter");
		toolDebugger = itemMultimeter.addItem(1, "debugger");
		toolIgniter = new ItemStack(itemIgniter);
		toolChiller = new ItemStack(itemChiller);
		toolPump = new ItemStack(itemPump);
		toolTransfuser = new ItemStack(itemTransfuser);
		toolMiner = new ItemStack(itemMiner);

		toolInvarBattleWrench = new ItemStack(itemBattleWrenchInvar);
		GameRegistry.registerCustomItemStack("toolInvarBattleWrench", toolInvarBattleWrench);

		/* Capacitor */
		capacitorCreative = itemCapacitor.addItem(ItemCapacitor.Types.CREATIVE.ordinal(), "capacitorCreative", 3);
		capacitorPotato = EnergyHelper.setDefaultEnergyTag(itemCapacitor.addItem(ItemCapacitor.Types.POTATO.ordinal(), "capacitorPotato", 0),
				ItemCapacitor.CAPACITY[ItemCapacitor.Types.POTATO.ordinal()]);
		capacitorBasic = itemCapacitor.addItem(ItemCapacitor.Types.BASIC.ordinal(), "capacitorBasic", 0);
		capacitorHardened = itemCapacitor.addItem(ItemCapacitor.Types.HARDENED.ordinal(), "capacitorHardened", 0);
		capacitorReinforced = itemCapacitor.addItem(ItemCapacitor.Types.REINFORCED.ordinal(), "capacitorReinforced", 1);
		capacitorResonant = itemCapacitor.addItem(ItemCapacitor.Types.RESONANT.ordinal(), "capacitorResonant", 2);

		EnergyHelper.setDefaultEnergyTag(capacitorCreative, 0);
		EnergyHelper.setDefaultEnergyTag(capacitorBasic, 0);
		EnergyHelper.setDefaultEnergyTag(capacitorHardened, 0);
		EnergyHelper.setDefaultEnergyTag(capacitorReinforced, 0);
		EnergyHelper.setDefaultEnergyTag(capacitorResonant, 0);

		/* Satchel */
		satchelCreative = itemSatchel.addItem(ItemSatchel.Types.CREATIVE.ordinal(), "satchelCreative", 3);
		satchelBasic = itemSatchel.addItem(ItemSatchel.Types.BASIC.ordinal(), "satchelBasic", 0);
		satchelHardened = itemSatchel.addItem(ItemSatchel.Types.HARDENED.ordinal(), "satchelHardened", 0);
		satchelReinforced = itemSatchel.addItem(ItemSatchel.Types.REINFORCED.ordinal(), "satchelReinforced", 1);
		satchelResonant = itemSatchel.addItem(ItemSatchel.Types.RESONANT.ordinal(), "satchelResonant", 2);

		ItemSatchel.setDefaultInventoryTag(satchelCreative);
		ItemSatchel.setDefaultInventoryTag(satchelBasic);
		ItemSatchel.setDefaultInventoryTag(satchelHardened);
		ItemSatchel.setDefaultInventoryTag(satchelReinforced);
		ItemSatchel.setDefaultInventoryTag(satchelResonant);

		/* Diagram */
		diagramSchematic = itemDiagram.addItem(ItemDiagram.Types.SCHEMATIC.ordinal(), "schematic");
		diagramRedprint = itemDiagram.addItem(ItemDiagram.Types.REDPRINT.ordinal(), "redprint");

		/* Parts */
		pneumaticServo = itemMaterial.addItem(0, "pneumaticServo");
		powerCoilGold = itemMaterial.addItem(1, "powerCoilGold");
		powerCoilSilver = itemMaterial.addItem(2, "powerCoilSilver");
		powerCoilElectrum = itemMaterial.addItem(3, "powerCoilElectrum");

		lock = itemMaterial.addItem(16, "lock");

		/* Process Items */
		sawdust = itemMaterial.addOreDictItem(512, "dustWood");
		sawdustCompressed = itemMaterial.addItem(513, "dustWoodCompressed");
		slag = itemMaterial.addItem(514, "slag");
		slagRich = itemMaterial.addItem(515, "slagRich");
		fertilizer = itemMaterial.addOreDictItem(516, "fertilizer");
		fertilizerRich = itemMaterial.addOreDictItem(517, "fertilizerRich");
		OreDictionary.registerOre("fertilizer", fertilizerRich);

		TEAugments.initialize();
		TEEquipment.initialize();
		TEFlorbs.initialize();
	}

	public static void postInit() {

		/* Tools */
		GameRegistry.addRecipe(new ShapedOreRecipe(toolWrench, new Object[] { "I I", " T ", " I ", 'I', "ingotIron", 'T', "ingotTin" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(toolMultimeter, new Object[] { "C C", "LPL", " G ", 'C', "ingotCopper", 'L', "ingotLead", 'P',
				powerCoilElectrum, 'G', "gearElectrum" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(toolIgniter, new Object[] { " R ", "IXI", " G ", 'I', "ingotIron", 'R', "dustRedstone", 'X', capacitorBasic,
				'G', Items.flint }));

		if (enableBattleWrench) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolInvarBattleWrench, new Object[] { "I I", " G ", " W ", 'I', "ingotInvar", 'G', "gearInvar", 'W',
					toolWrench }));
		}

		/* Capacitors */
		GameRegistry.addRecipe(new ShapelessOreRecipe(capacitorPotato, new Object[] { Items.potato, "dustRedstone", "nuggetLead" }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(capacitorPotato, new Object[] { Items.poisonous_potato, "dustRedstone", "nuggetLead" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(capacitorBasic, new Object[] { " R ", "IXI", "RYR", 'I', "ingotLead", 'R', "dustRedstone", 'X',
				"ingotCopper", 'Y', "dustSulfur" }));
		GameRegistry.addRecipe(new RecipeUpgrade(capacitorHardened, new Object[] { " R ", "IXI", "RYR", 'I', "ingotInvar", 'R', "dustRedstone", 'X',
				capacitorBasic, 'Y', "ingotTin" }));
		GameRegistry.addRecipe(new RecipeUpgrade(capacitorReinforced, new Object[] { " R ", "IXI", "RYR", 'I', "ingotElectrum", 'R', "dustRedstone", 'X',
				capacitorHardened, 'Y', Items.diamond }));
		GameRegistry.addRecipe(new RecipeUpgrade(capacitorResonant, new Object[] { " R ", "IXI", "RYR", 'I', "ingotEnderium", 'R', "dustRedstone", 'X',
				capacitorReinforced, 'Y', "dustPyrotheum" }));

		/* Satchels */
		GameRegistry.addRecipe(new ShapedOreRecipe(satchelBasic, new Object[] { " Y ", "IXI", "Y Y", 'I', "ingotTin", 'X', "blockCloth", 'Y', Items.leather }));
		GameRegistry.addRecipe(new ShapedOreRecipe(satchelBasic,
				new Object[] { " Y ", "IXI", "Y Y", 'I', "ingotTin", 'X', "blockCloth", 'Y', "blockClothRock" }));
		GameRegistry
				.addRecipe(new RecipeUpgrade(satchelHardened, new Object[] { " Y ", "IXI", "Y Y", 'I', "ingotInvar", 'X', satchelBasic, 'Y', "nuggetTin" }));
		GameRegistry.addRecipe(new RecipeUpgrade(satchelReinforced, new Object[] { " Y ", "IXI", "Y Y", 'I', "blockGlassHardened", 'X', satchelHardened, 'Y',
				"nuggetInvar" }));
		GameRegistry.addRecipe(new RecipeUpgrade(satchelResonant, new Object[] { " Y ", "IXI", "Y Y", 'I', "ingotEnderium", 'X', satchelReinforced, 'Y',
				"nuggetElectrum" }));

		TECraftingHandler.addSecureRecipe(satchelCreative);
		TECraftingHandler.addSecureRecipe(satchelBasic);
		TECraftingHandler.addSecureRecipe(satchelHardened);
		TECraftingHandler.addSecureRecipe(satchelReinforced);
		TECraftingHandler.addSecureRecipe(satchelResonant);

		/* Diagrams */
		GameRegistry.addRecipe(new ShapelessOreRecipe(diagramSchematic, new Object[] { Items.paper, Items.paper, "dyeBlue" }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(diagramRedprint, new Object[] { Items.paper, Items.paper, "dustRedstone" }));

		/* Parts */
		String category = "General";
		boolean servosAllowSilver = ThermalExpansion.config.get(category, "PneumaticServo.AllowSilver", false);
		boolean servosAllowInvar = ThermalExpansion.config.get(category, "PneumaticServo.AllowInvar", false);
		boolean servosAllowBronze = ThermalExpansion.config.get(category, "PneumaticServo.AllowBronze", false);
		boolean servosAllowSteel = ThermalExpansion.config.get(category, "PneumaticServo.AllowSteel", false);

		GameRegistry.addRecipe(new ShapedOreRecipe(pneumaticServo,
				new Object[] { " I ", "GRG", " I ", 'R', "dustRedstone", 'G', "blockGlass", 'I', "ingotIron" }));

		if (servosAllowSilver) {
			GameRegistry.addRecipe(new ShapedOreRecipe(pneumaticServo, new Object[] { " I ", "GRG", " I ", 'R', "dustRedstone", 'G', "blockGlass", 'I',
					"ingotSilver" }));
		}
		if (servosAllowInvar) {
			GameRegistry.addRecipe(new ShapedOreRecipe(pneumaticServo, new Object[] { " I ", "GRG", " I ", 'R', "dustRedstone", 'G', "blockGlass", 'I',
					"ingotInvar" }));
		}
		if (servosAllowBronze) {
			GameRegistry.addRecipe(new ShapedOreRecipe(pneumaticServo, new Object[] { " I ", "GRG", " I ", 'R', "dustRedstone", 'G', "blockGlass", 'I',
					"ingotBronze" }));
		}
		if (servosAllowSteel) {
			GameRegistry.addRecipe(new ShapedOreRecipe(pneumaticServo, new Object[] { " I ", "GRG", " I ", 'R', "dustRedstone", 'G', "blockGlass", 'I',
					"ingotSteel" }));
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(powerCoilGold, new Object[] { "  R", " G ", "R  ", 'R', "dustRedstone", 'G', "ingotGold" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(powerCoilSilver, new Object[] { "  R", " G ", "R  ", 'R', "dustRedstone", 'G', "ingotSilver" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(powerCoilElectrum, new Object[] { "R  ", " G ", "  R", 'R', "dustRedstone", 'G', "ingotElectrum" }));

		GameRegistry.addRecipe(new ShapedOreRecipe(lock, new Object[] { " S ", "SBS", "SSS", 'B', "ingotBronze", 'S', "nuggetSignalum" }));

		/* Misc Items */
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), new Object[] { "dustSaltpeter", "dustSaltpeter", "dustSulfur",
				"dustCoal" }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), new Object[] { "dustSaltpeter", "dustSaltpeter", "dustSulfur",
				"dustCharcoal" }));

		ItemHelper.addGearRecipe(new ItemStack(Items.paper, 2), "dustWood", new ItemStack(Items.water_bucket));
		GameRegistry.addRecipe(new ShapedOreRecipe(sawdustCompressed, new Object[] { "###", "# #", "###", '#', "dustWood" }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.clay_ball, 4), new Object[] { slag, slag, Blocks.dirt, Items.water_bucket }));

		GameRegistry.addSmelting(sawdustCompressed, new ItemStack(Items.coal, 1, 1), 0.15F);

		GameRegistry.addRecipe(new ShapelessOreRecipe(ItemHelper.cloneStack(fertilizer, 8), new Object[] { "dustWood", "dustWood", "dustSaltpeter", slag }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(ItemHelper.cloneStack(fertilizer, 32), new Object[] { "dustCharcoal", "dustSaltpeter", slag }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(ItemHelper.cloneStack(fertilizerRich, 8), new Object[] { "dustWood", "dustWood", "dustSaltpeter",
				slagRich }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(ItemHelper.cloneStack(fertilizerRich, 32), new Object[] { "dustCharcoal", "dustSaltpeter", slagRich }));

		TEAugments.postInit();
		TEEquipment.postInit();
		TEFlorbs.postInit();
	}

	public static boolean enableBattleWrench = true;

	static {
		String category = "Equipment.Invar.Tool";
		enableBattleWrench = ThermalExpansion.config.get(category, "BattleWrench", true);
	}

	public static ItemWrench itemWrench;
	public static ItemMultimeter itemMultimeter;
	public static ItemIgniter itemIgniter;
	public static ItemChiller itemChiller;
	public static ItemPump itemPump;
	public static ItemTransfuser itemTransfuser;
	public static ItemMiner itemMiner;
	public static ItemDiagram itemDiagram;
	public static ItemBase itemMaterial;

	public static Item itemBattleWrenchInvar;

	public static ItemCapacitor itemCapacitor;
	public static ItemSatchel itemSatchel;

	public static ItemStack toolWrench;
	public static ItemStack toolMultimeter;
	public static ItemStack toolDebugger;
	public static ItemStack toolIgniter;
	public static ItemStack toolChiller;
	public static ItemStack toolPump;
	public static ItemStack toolTransfuser;
	public static ItemStack toolMiner;

	public static ItemStack toolInvarBattleWrench;

	public static ItemStack diagramSchematic;
	public static ItemStack diagramRedprint;
	public static ItemStack diagramPattern;

	public static ItemStack capacitorPotato;
	public static ItemStack capacitorBasic;
	public static ItemStack capacitorHardened;
	public static ItemStack capacitorReinforced;
	public static ItemStack capacitorResonant;
	public static ItemStack capacitorCreative;

	public static ItemStack satchelBasic;
	public static ItemStack satchelHardened;
	public static ItemStack satchelReinforced;
	public static ItemStack satchelResonant;
	public static ItemStack satchelCreative;

	public static ItemStack pneumaticServo;
	public static ItemStack powerCoilGold;
	public static ItemStack powerCoilSilver;
	public static ItemStack powerCoilElectrum;

	public static ItemStack lock;

	public static ItemStack sawdust;
	public static ItemStack sawdustCompressed;
	public static ItemStack slag;
	public static ItemStack slagRich;
	public static ItemStack fertilizer;
	public static ItemStack fertilizerRich;

}
