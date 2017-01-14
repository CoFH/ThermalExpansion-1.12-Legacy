package cofh.thermalexpansion.item;

import codechicken.lib.item.ItemMultiType;
import codechicken.lib.item.ItemStackRegistry;
import cofh.core.util.crafting.RecipeUpgrade;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.item.tool.*;
import cofh.thermalexpansion.util.crafting.TECraftingHandler;
import cofh.thermalfoundation.item.Equipment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;
import static cofh.lib.util.helpers.ItemHelper.ShapelessRecipe;

public class TEItems {

    public static ItemWrench itemWrench;
    public static ItemWrenchBattle itemBattleWrench;
    public static ItemMultimeter itemMultimeter;
    public static ItemIgniter itemIgniter;
    public static ItemChiller itemChiller;
    public static ItemPump itemPump;
    public static ItemTransfuser itemTransfuser;
    public static ItemMiner itemMiner;
    public static ItemDiagram itemDiagram;
    public static ItemMultiType itemMaterial;

    public static ItemCapacitor itemCapacitor;
    public static ItemSatchel itemSatchel;

    public static ItemStack toolWrench;
    public static ItemStack toolBattleWrench;
    public static ItemStack toolMultimeter;
    public static ItemStack toolDebugger;
    public static ItemStack toolIgniter;
    public static ItemStack toolChiller;
    public static ItemStack toolPump;
    public static ItemStack toolTransfuser;
    public static ItemStack toolMiner;

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

    public static boolean enableIgniter = true;
    public static boolean enableChiller = true;
    public static boolean enablePump = true;
    public static boolean enableTransfuser = true;
    public static boolean enableBattleWrench = true;

    static {
        String category2 = "Item.Tool.";
        String category = category2 + "Igniter";
        enableIgniter = ThermalExpansion.config.get(category, "Recipe", true);

        category = category2 + "Chiller";
        enableChiller = ThermalExpansion.config.get(category, "Recipe", true);

        category = category2 + "Pump";
        enablePump = ThermalExpansion.config.get(category, "Recipe", true);

        category = category2 + "Transfuser";
        enableTransfuser = ThermalExpansion.config.get(category, "Recipe", true);

        category = category2 + "BattleWrench";
        enableBattleWrench = ThermalExpansion.config.get(category, "BattleWrench", true);
    }

    private TEItems() {

    }

    public static void preInit() {

        itemWrench = (ItemWrench) new ItemWrench().setUnlocalizedName("tool", "wrench");
        itemBattleWrench = (ItemWrenchBattle) new ItemWrenchBattle(Equipment.Invar.TOOL_MATERIAL).setRepairIngot("ingotInvar");
        GameRegistry.registerItem(itemBattleWrench, "tool.battleWrenchInvar");
        itemMultimeter = (ItemMultimeter) new ItemMultimeter().setUnlocalizedName("tool", "meter");
        itemIgniter = (ItemIgniter) new ItemIgniter().setUnlocalizedName("tool", "igniter");
        itemChiller = (ItemChiller) new ItemChiller().setUnlocalizedName("tool", "chiller");
        itemPump = (ItemPump) new ItemPump().setUnlocalizedName("tool", "pump");
        itemTransfuser = (ItemTransfuser) new ItemTransfuser().setUnlocalizedName("tool", "transfuser");
        itemCapacitor = new ItemCapacitor();
        itemSatchel = (ItemSatchel) new ItemSatchel().setUnlocalizedName("satchel");
        itemDiagram = (ItemDiagram) new ItemDiagram().setUnlocalizedName("diagram");
        itemMaterial = new ItemMultiType(ThermalExpansion.tabItems, "material").setUnlocalizedName("thermalexpansion.material.");

        //GameRegistry.register(itemWrench);
        //GameRegistry.register(itemBattleWrench);
        //GameRegistry.register(itemMultimeter);
        //GameRegistry.register(itemIgniter);
        //GameRegistry.register(itemChiller);
        //GameRegistry.register(itemPump);
        //GameRegistry.register(itemTransfuser);
        GameRegistry.register(itemCapacitor);
        //GameRegistry.register(itemSatchel);
        //GameRegistry.register(itemDiagram);
        GameRegistry.register(itemMaterial);

        		/* Tools */
        toolWrench = new ItemStack(itemWrench);
        toolBattleWrench = new ItemStack(itemBattleWrench);
        ItemStackRegistry.registerCustomItemStack("toolBattleWrench", toolBattleWrench);
        toolMultimeter = itemMultimeter.addItem(0, "multimeter");
        toolDebugger = itemMultimeter.addItem(1, "debugger");
        toolIgniter = new ItemStack(itemIgniter);
        toolChiller = new ItemStack(itemChiller);
        toolPump = new ItemStack(itemPump);
        toolTransfuser = new ItemStack(itemTransfuser);



        /* Capacitor */
        capacitorCreative = itemCapacitor.registerSubItem(ItemCapacitor.Types.CREATIVE.ordinal(), "capacitorCreative", EnumRarity.EPIC);
        capacitorPotato = EnergyHelper.setDefaultEnergyTag(itemCapacitor.registerSubItem(ItemCapacitor.Types.POTATO.ordinal(), "capacitorPotato", EnumRarity.COMMON), ItemCapacitor.CAPACITY[ItemCapacitor.Types.POTATO.ordinal()]);
        capacitorBasic = itemCapacitor.registerSubItem(ItemCapacitor.Types.BASIC.ordinal(), "capacitorBasic", EnumRarity.COMMON);
        capacitorHardened = itemCapacitor.registerSubItem(ItemCapacitor.Types.HARDENED.ordinal(), "capacitorHardened", EnumRarity.COMMON);
        capacitorReinforced = itemCapacitor.registerSubItem(ItemCapacitor.Types.REINFORCED.ordinal(), "capacitorReinforced", EnumRarity.UNCOMMON);
        capacitorResonant = itemCapacitor.registerSubItem(ItemCapacitor.Types.RESONANT.ordinal(), "capacitorResonant", EnumRarity.RARE);


        EnergyHelper.setDefaultEnergyTag(capacitorCreative, 0);
        EnergyHelper.setDefaultEnergyTag(capacitorBasic, 0);
        EnergyHelper.setDefaultEnergyTag(capacitorHardened, 0);
        EnergyHelper.setDefaultEnergyTag(capacitorReinforced, 0);
        EnergyHelper.setDefaultEnergyTag(capacitorResonant, 0);


        /* Parts */
        pneumaticServo = itemMaterial.registerSubItem(0, "pneumaticServo");
        powerCoilGold = itemMaterial.registerSubItem(1, "powerCoilGold");
        powerCoilSilver = itemMaterial.registerSubItem(2, "powerCoilSilver");
        powerCoilElectrum = itemMaterial.registerSubItem(3, "powerCoilElectrum");

        lock = itemMaterial.registerSubItem(16, "lock");

		/* Process Items */
        sawdust = itemMaterial.registerSubItemOreDict(512, "dustWood");
        sawdustCompressed = itemMaterial.registerSubItem(513, "dustWoodCompressed");
        slag = itemMaterial.registerSubItem(514, "slag");
        slagRich = itemMaterial.registerSubItem(515, "slagRich");
        fertilizer = itemMaterial.registerSubItemOreDict(516, "fertilizer");
        fertilizerRich = itemMaterial.registerSubItemOreDict(517, "fertilizerRich");
        OreDictionary.registerOre("fertilizer", fertilizerRich);

        TEAugments.preInit();
        TEEquipment.preInit();
        TEFlorbs.preInit();
    }

    public static void initialize() {

        ItemSatchel.configure();



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

        TEAugments.initialize();
        TEEquipment.initialize();
        TEFlorbs.initialize();
    }

    public static void postInit() {

		/* Tools */
        GameRegistry.addRecipe(ShapedRecipe(toolWrench, "I I", " T ", " I ", 'I', "ingotIron", 'T', "ingotTin"));
        GameRegistry.addRecipe(ShapedRecipe(toolMultimeter, "C C", "LPL", " G ", 'C', "ingotCopper", 'L', "ingotLead", 'P', powerCoilElectrum, 'G', "gearElectrum"));

        if (enableBattleWrench) {
            GameRegistry.addRecipe(ShapedRecipe(toolBattleWrench, "I I", " G ", " W ", 'I', "ingotInvar", 'G', "gearInvar", 'W', toolWrench));
        }
        if (enableIgniter) {
            GameRegistry.addRecipe(ShapedRecipe(toolIgniter, " R ", "IXI", " G ", 'I', "ingotIron", 'R', "dustRedstone", 'X', capacitorBasic, 'G', Items.FLINT));
        }
        if (enableChiller) {
            GameRegistry.addRecipe(ShapedRecipe(toolChiller, " R ", "IXI", " G ", 'I', "ingotIron", 'R', "dustRedstone", 'X', capacitorBasic, 'G', Items.SNOWBALL));
        }
        if (enablePump) {
            GameRegistry.addRecipe(ShapedRecipe(toolPump, "RR ", "RGI", " XY", 'I', "ingotInvar", 'R', "dustRedstone", 'X', capacitorBasic, 'Y', Items.BUCKET, 'G', "gearBronze"));
        }
        if (enableTransfuser) {
            GameRegistry.addRecipe(ShapedRecipe(toolTransfuser, "RR ", "RGI", " IY", 'I', "ingotInvar", 'R', "dustRedstone", 'X', "ingotSignalum", 'Y', powerCoilElectrum, 'G', "gearElectrum"));
        }

		/* Capacitors */
        if (ItemCapacitor.ENABLE[ItemCapacitor.Types.POTATO.ordinal()]) {
            GameRegistry.addRecipe(ShapelessRecipe(capacitorPotato, Items.POTATO, "dustRedstone", "nuggetLead"));
            GameRegistry.addRecipe(ShapelessRecipe(capacitorPotato, Items.POISONOUS_POTATO, "dustRedstone", "nuggetLead"));
        }
        if (ItemCapacitor.ENABLE[ItemCapacitor.Types.BASIC.ordinal()]) {
            GameRegistry.addRecipe(ShapedRecipe(capacitorBasic, " R ", "IXI", "RYR", 'I', "ingotLead", 'R', "dustRedstone", 'X', "ingotCopper", 'Y', "dustSulfur"));
        }
        if (ItemCapacitor.ENABLE[ItemCapacitor.Types.HARDENED.ordinal()]) {
            GameRegistry.addRecipe(new RecipeUpgrade(capacitorHardened, new Object[] { " R ", "IXI", "RYR", 'I', "ingotInvar", 'R', "dustRedstone", 'X', capacitorBasic, 'Y', "ingotTin" }));
        }
        if (ItemCapacitor.ENABLE[ItemCapacitor.Types.REINFORCED.ordinal()]) {
            GameRegistry.addRecipe(new RecipeUpgrade(capacitorReinforced, new Object[] { " R ", "IXI", "RYR", 'I', "ingotElectrum", 'R', "dustRedstone", 'X', capacitorHardened, 'Y', Items.DIAMOND }));
        }
        if (ItemCapacitor.ENABLE[ItemCapacitor.Types.RESONANT.ordinal()]) {
            GameRegistry.addRecipe(new RecipeUpgrade(capacitorResonant, new Object[] { " R ", "IXI", "RYR", 'I', "ingotEnderium", 'R', "dustRedstone", 'X', capacitorReinforced, 'Y', "dustPyrotheum" }));
        }

		/* Satchels */
        if (ItemSatchel.enable[ItemSatchel.Types.BASIC.ordinal()]) {
            GameRegistry.addRecipe(ShapedRecipe(satchelBasic, " Y ", "IXI", "Y Y", 'I', "ingotTin", 'X', "blockCloth", 'Y', Items.LEATHER));
            GameRegistry.addRecipe(ShapedRecipe(satchelBasic, " Y ", "IXI", "Y Y", 'I', "ingotTin", 'X', "blockCloth", 'Y', "blockClothRock"));
        }
        if (ItemSatchel.enable[ItemSatchel.Types.HARDENED.ordinal()]) {
            GameRegistry.addRecipe(new RecipeUpgrade(satchelHardened, new Object[] { " Y ", "IXI", "Y Y", 'I', "ingotInvar", 'X', satchelBasic, 'Y', "nuggetTin" }));
        }
        if (ItemSatchel.enable[ItemSatchel.Types.REINFORCED.ordinal()]) {
            GameRegistry.addRecipe(new RecipeUpgrade(satchelReinforced, new Object[] { " Y ", "IXI", "Y Y", 'I', "blockGlassHardened", 'X', satchelHardened, 'Y', "nuggetInvar" }));
        }
        if (ItemSatchel.enable[ItemSatchel.Types.RESONANT.ordinal()]) {
            GameRegistry.addRecipe(new RecipeUpgrade(satchelResonant, new Object[] { " Y ", "IXI", "Y Y", 'I', "ingotEnderium", 'X', satchelReinforced, 'Y', "nuggetElectrum" }));
        }

        TECraftingHandler.addSecureRecipe(satchelCreative);
        TECraftingHandler.addSecureRecipe(satchelBasic);
        TECraftingHandler.addSecureRecipe(satchelHardened);
        TECraftingHandler.addSecureRecipe(satchelReinforced);
        TECraftingHandler.addSecureRecipe(satchelResonant);

		/* Diagrams */
        GameRegistry.addRecipe(ShapelessRecipe(diagramSchematic, Items.PAPER, Items.PAPER, "dyeBlue"));
        GameRegistry.addRecipe(ShapelessRecipe(diagramRedprint, Items.PAPER, Items.PAPER, "dustRedstone"));

		/* Parts */
        String category = "General";
        boolean servosAllowSilver = ThermalExpansion.config.get(category, "PneumaticServo.AllowSilver", false);
        boolean servosAllowInvar = ThermalExpansion.config.get(category, "PneumaticServo.AllowInvar", false);
        boolean servosAllowBronze = ThermalExpansion.config.get(category, "PneumaticServo.AllowBronze", false);
        boolean servosAllowSteel = ThermalExpansion.config.get(category, "PneumaticServo.AllowSteel", false);

        GameRegistry.addRecipe(ShapedRecipe(pneumaticServo, " I ", "GRG", " I ", 'R', "dustRedstone", 'G', "blockGlass", 'I', "ingotIron"));

        if (servosAllowSilver) {
            GameRegistry.addRecipe(ShapedRecipe(pneumaticServo, " I ", "GRG", " I ", 'R', "dustRedstone", 'G', "blockGlass", 'I', "ingotSilver"));
        }
        if (servosAllowInvar) {
            GameRegistry.addRecipe(ShapedRecipe(pneumaticServo, " I ", "GRG", " I ", 'R', "dustRedstone", 'G', "blockGlass", 'I', "ingotInvar"));
        }
        if (servosAllowBronze) {
            GameRegistry.addRecipe(ShapedRecipe(pneumaticServo, " I ", "GRG", " I ", 'R', "dustRedstone", 'G', "blockGlass", 'I', "ingotBronze"));
        }
        if (servosAllowSteel) {
            GameRegistry.addRecipe(ShapedRecipe(pneumaticServo, " I ", "GRG", " I ", 'R', "dustRedstone", 'G', "blockGlass", 'I', "ingotSteel"));
        }
        GameRegistry.addRecipe(ShapedRecipe(powerCoilGold, "  R", " G ", "R  ", 'R', "dustRedstone", 'G', "ingotGold"));
        GameRegistry.addRecipe(ShapedRecipe(powerCoilSilver, "  R", " G ", "R  ", 'R', "dustRedstone", 'G', "ingotSilver"));
        GameRegistry.addRecipe(ShapedRecipe(powerCoilElectrum, "R  ", " G ", "  R", 'R', "dustRedstone", 'G', "ingotElectrum"));

        GameRegistry.addRecipe(ShapedRecipe(lock, " S ", "SBS", "SSS", 'B', "ingotBronze", 'S', "nuggetSignalum"));

		/* Misc Items */
        GameRegistry.addRecipe(ShapelessRecipe(new ItemStack(Items.GUNPOWDER, 2), "dustSaltpeter", "dustSaltpeter", "dustSulfur", "dustCoal"));
        GameRegistry.addRecipe(ShapelessRecipe(new ItemStack(Items.GUNPOWDER, 2), "dustSaltpeter", "dustSaltpeter", "dustSulfur", "dustCharcoal"));

        ItemHelper.addGearRecipe(new ItemStack(Items.PAPER, 2), "dustWood", new ItemStack(Items.WATER_BUCKET));
        GameRegistry.addRecipe(ShapedRecipe(sawdustCompressed, "###", "# #", "###", '#', "dustWood"));
        GameRegistry.addRecipe(ShapelessRecipe(new ItemStack(Items.CLAY_BALL, 4), slag, slag, Blocks.DIRT, Items.WATER_BUCKET));

        GameRegistry.addSmelting(sawdustCompressed, new ItemStack(Items.COAL, 1, 1), 0.15F);

        GameRegistry.addRecipe(ShapelessRecipe(ItemHelper.cloneStack(fertilizer, 8), "dustWood", "dustWood", "dustSaltpeter", slag));
        GameRegistry.addRecipe(ShapelessRecipe(ItemHelper.cloneStack(fertilizer, 32), "dustCharcoal", "dustSaltpeter", slag));
        GameRegistry.addRecipe(ShapelessRecipe(ItemHelper.cloneStack(fertilizerRich, 8), "dustWood", "dustWood", "dustSaltpeter", slagRich));
        GameRegistry.addRecipe(ShapelessRecipe(ItemHelper.cloneStack(fertilizerRich, 32), "dustCharcoal", "dustSaltpeter", slagRich));

        TEAugments.postInit();
        TEEquipment.postInit();
        TEFlorbs.postInit();
    }
}
