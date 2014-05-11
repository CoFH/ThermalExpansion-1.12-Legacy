package thermalexpansion.item;

import cofh.item.ItemBase;
import cofh.util.ItemHelper;
import cofh.util.UpgradeRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.item.tool.ItemCapacitor;
import thermalexpansion.item.tool.ItemMultimeter;
import thermalexpansion.item.tool.ItemWrench;
import thermalfoundation.item.TFItems;

public class TEItems {

	public static void preInit() {

		TEEquipment.preInit();
	}

	public static void initialize() {

		itemWrench = (ItemWrench) new ItemWrench().setUnlocalizedName("tool");
		itemMultimeter = (ItemMultimeter) new ItemMultimeter().setUnlocalizedName("meter");
		itemCapacitor = (ItemCapacitor) new ItemCapacitor().setUnlocalizedName("capacitor");
		itemDiagram = (ItemDiagram) new ItemDiagram().setUnlocalizedName("diagram");
		itemComponent = (ItemBase) new ItemBase("thermalexpansion").setHasTextures(false).setUnlocalizedName("component");
		itemMaterial = (ItemBase) new ItemBase("thermalexpansion").setUnlocalizedName("material");

		loadItems();
		TEEquipment.initialize();
	}

	private static void loadItems() {

		/* Tools */
		toolWrench = itemWrench.addItem(0, "wrench");
		toolMultimeter = itemMultimeter.addItem(0, "multimeter");
		toolDebugger = itemMultimeter.addItem(1, "debugger");

		/* Capacitor */
		capacitorCreative = itemCapacitor.addItem(ItemCapacitor.Types.CREATIVE.ordinal(), "capacitorCreative", 3);
		capacitorPotato = ItemCapacitor.setDefaultTag(itemCapacitor.addItem(ItemCapacitor.Types.POTATO.ordinal(), "capacitorPotato", 0),
				ItemCapacitor.STORAGE[ItemCapacitor.Types.POTATO.ordinal()]);
		capacitorBasic = itemCapacitor.addItem(ItemCapacitor.Types.BASIC.ordinal(), "capacitorBasic", 0);
		capacitorHardened = itemCapacitor.addItem(ItemCapacitor.Types.HARDENED.ordinal(), "capacitorHardened", 0);
		capacitorReinforced = itemCapacitor.addItem(ItemCapacitor.Types.REINFORCED.ordinal(), "capacitorReinforced", 1);
		capacitorResonant = itemCapacitor.addItem(ItemCapacitor.Types.RESONANT.ordinal(), "capacitorResonant", 2);

		/* Diagram */
		diagramSchematic = itemDiagram.addItem(SCHEMATIC_ID, "schematic");

		/* Minerals */
		crystalCinnabar = itemMaterial.addItem(130, "crystalCinnabar");

		/* Process Items */
		woodchips = itemMaterial.addItem(160, "woodchips");
		sawdust = itemMaterial.addItem(161, "sawdust");
		sawdustCompressed = itemMaterial.addItem(162, "sawdustCompressed");
		slag = itemMaterial.addItem(163, "slag");
		slagRich = itemMaterial.addItem(164, "slagRich");

		/* Parts */
		pneumaticServo = itemMaterial.addItem(192, "pneumaticServo");
		powerCoilGold = itemMaterial.addItem(193, "powerCoilGold");
		powerCoilSilver = itemMaterial.addItem(194, "powerCoilSilver");
		powerCoilElectrum = itemMaterial.addItem(195, "powerCoilElectrum");

		OreDictionary.registerOre("crystalCinnabar", crystalCinnabar);
	}

	public static void postInit() {

		/* Tools */
		GameRegistry.addRecipe(new ShapedOreRecipe(toolWrench, new Object[] { "I I", " T ", " I ", 'I', Items.iron_ingot, 'T', "ingotTin" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(toolMultimeter, new Object[] { "C C", "LPL", " G ", 'C', "ingotCopper", 'L', "ingotLead", 'P',
				powerCoilElectrum, 'G', "gearElectrum" }));

		GameRegistry.addRecipe(new ShapelessOreRecipe(capacitorPotato, new Object[] { Items.potato, Items.redstone, "nuggetLead" }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(capacitorPotato, new Object[] { Items.poisonous_potato, Items.redstone, "nuggetLead" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(capacitorBasic, new Object[] { " R ", "IXI", "RYR", 'I', "ingotLead", 'R', Items.redstone, 'X',
				"ingotCopper", 'Y', "dustSulfur" }));
		GameRegistry.addRecipe(new UpgradeRecipe(capacitorHardened, new Object[] { " R ", "IXI", "RYR", 'I', "ingotInvar", 'R', Items.redstone, 'X',
				capacitorBasic, 'Y', "ingotTin" }));
		GameRegistry.addRecipe(new UpgradeRecipe(capacitorReinforced, new Object[] { " R ", "IXI", "RYR", 'I', "ingotElectrum", 'R', Items.redstone, 'X',
				capacitorHardened, 'Y', Items.diamond }));
		GameRegistry.addRecipe(new UpgradeRecipe(capacitorResonant, new Object[] { " R ", "IXI", "RYR", 'I', "ingotEnderium", 'R', Items.redstone, 'X',
				capacitorReinforced, 'Y', TFItems.dustPyrotheum }));

		/* Diagrams */
		GameRegistry.addRecipe(new ShapelessOreRecipe(diagramSchematic, new Object[] { Items.paper, Items.paper, "dyeBlue" }));

		/* Parts */
		String category = "tweak.recipe";
		boolean servosAllowSilver = ThermalExpansion.config.get(category, "PneumaticServo.AllowSilver", false);
		boolean servosAllowInvar = ThermalExpansion.config.get(category, "PneumaticServo.AllowInvar", false);
		boolean servosAllowBronze = ThermalExpansion.config.get(category, "PneumaticServo.AllowBronze", false);
		boolean servosAllowSteel = ThermalExpansion.config.get(category, "PneumaticServo.AllowSteel", false);

		GameRegistry.addRecipe(new ShapedOreRecipe(pneumaticServo, new Object[] { " I ", "GRG", " I ", 'R', Items.redstone, 'G', "glass", 'I', "ingotIron" }));

		if (servosAllowSilver) {
			GameRegistry.addRecipe(new ShapedOreRecipe(pneumaticServo, new Object[] { " I ", "GRG", " I ", 'R', Items.redstone, 'G', "glass", 'I',
					"ingotSilver" }));
		}
		if (servosAllowInvar) {
			GameRegistry.addRecipe(new ShapedOreRecipe(pneumaticServo,
					new Object[] { " I ", "GRG", " I ", 'R', Items.redstone, 'G', "glass", 'I', "ingotInvar" }));
		}
		if (servosAllowBronze) {
			GameRegistry.addRecipe(new ShapedOreRecipe(pneumaticServo, new Object[] { " I ", "GRG", " I ", 'R', Items.redstone, 'G', "glass", 'I',
					"ingotBronze" }));
		}
		if (servosAllowSteel) {
			GameRegistry.addRecipe(new ShapedOreRecipe(pneumaticServo,
					new Object[] { " I ", "GRG", " I ", 'R', Items.redstone, 'G', "glass", 'I', "ingotSteel" }));
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(powerCoilGold, new Object[] { "  R", " G ", "R  ", 'R', Items.redstone, 'G', "ingotGold" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(powerCoilSilver, new Object[] { "  R", " G ", "R  ", 'R', Items.redstone, 'G', "ingotSilver" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(powerCoilElectrum, new Object[] { "  R", " G ", "R  ", 'R', Items.redstone, 'G', "ingotElectrum" }));

		/* Misc Items */
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), new Object[] { "dustSaltpeter", "dustSaltpeter", "dustSulfur",
				"dustCoal" }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(Items.gunpowder, new Object[] { "dustSaltpeter", "dustSulfur", Items.coal }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(Items.gunpowder, new Object[] { "dustSaltpeter", "dustSulfur", new ItemStack(Items.coal, 1, 1) }));

		GameRegistry.addRecipe(new ItemStack(Items.paper, 3), new Object[] { "###", '#', woodchips });
		GameRegistry.addRecipe(sawdustCompressed, new Object[] { "###", "# #", "###", '#', sawdust });
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.clay_ball, 2), new Object[] { slag, slag, Blocks.dirt, Items.water_bucket }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(ItemHelper.cloneStack(TFItems.dustPyrotheum, 2), new Object[] { "dustCoal", "dustSulfur", Items.redstone,
				Items.blaze_powder }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(ItemHelper.cloneStack(TFItems.dustCryotheum, 2), new Object[] { Items.snowball, "dustSaltpeter",
				Items.redstone, TFItems.dustBlizz }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(ItemHelper.cloneStack(TFItems.dustBlizz, 2), new Object[] { TFItems.rodBlizz }));

		FurnaceRecipes.smelting().func_151394_a(sawdustCompressed, new ItemStack(Items.coal, 1, 1), 0.15F);

		/* Alloy Recipes */
		GameRegistry.addRecipe(new ShapelessOreRecipe(ItemHelper.cloneStack(TFItems.dustElectrum, 2), new Object[] { "dustGold", "dustSilver" }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(ItemHelper.cloneStack(TFItems.dustInvar, 3), new Object[] { "dustIron", "dustIron", "dustNickel" }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(ItemHelper.cloneStack(TFItems.dustBronze, 4), new Object[] { "dustCopper", "dustCopper", "dustCopper",
				"dustTin" }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(ItemHelper.cloneStack(TFItems.dustEnderium, 4), new Object[] { "dustTin", "dustTin", "dustTin",
				"dustPlatinum", TFItems.bucketEnder }));

		TEEquipment.postInit();
	}

	public static final int SCHEMATIC_ID = 0;

	public static ItemWrench itemWrench;
	public static ItemMultimeter itemMultimeter;
	public static ItemDiagram itemDiagram;
	public static ItemBase itemComponent;
	public static ItemBase itemMaterial;

	public static ItemCapacitor itemCapacitor;

	public static ItemStack toolWrench;
	public static ItemStack toolMultimeter;
	public static ItemStack toolDebugger;
	public static ItemStack diagramSchematic;

	public static ItemStack capacitorPotato;
	public static ItemStack capacitorBasic;
	public static ItemStack capacitorHardened;
	public static ItemStack capacitorReinforced;
	public static ItemStack capacitorResonant;
	public static ItemStack capacitorCreative;

	public static ItemStack crystalCinnabar;

	public static ItemStack woodchips;
	public static ItemStack sawdust;
	public static ItemStack sawdustCompressed;
	public static ItemStack slag;
	public static ItemStack slagRich;

	public static ItemStack pneumaticServo;
	public static ItemStack powerCoilGold;
	public static ItemStack powerCoilSilver;
	public static ItemStack powerCoilElectrum;

}
