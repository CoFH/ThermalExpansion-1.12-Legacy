package thermalexpansion.item;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.ShapedOreRecipe;
import thermalexpansion.ThermalExpansion;
import thermalexpansion.core.TEProps;
import thermalexpansion.item.tool.ItemWrenchBattle;
import cofh.item.ItemArmorAdv;
import cofh.item.ItemAxeAdv;
import cofh.item.ItemFishingRodAdv;
import cofh.item.ItemHoeAdv;
import cofh.item.ItemPickaxeAdv;
import cofh.item.ItemShearsAdv;
import cofh.item.ItemShovelAdv;
import cofh.item.ItemSickleAdv;
import cofh.item.ItemSwordAdv;
import cpw.mods.fml.common.registry.GameRegistry;

public class TEEquipment {

	public static void preInit() {

	}

	public static void initialize() {

		itemHelmetInvar = (ItemArmorAdv) new ItemArmorAdv(ThermalExpansion.config.getItemId("Armor.Invar.Helmet"), ARMOR_INVAR, 0).setRepairIngot("ingotInvar")
				.setArmorTextures(TEXTURE_INVAR).setUnlocalizedName(ARMOR + "invarHelmet").setTextureName("thermalexpansion:armor/ArmorInvarHelmet");
		itemPlateInvar = (ItemArmorAdv) new ItemArmorAdv(ThermalExpansion.config.getItemId("Armor.Invar.Plate"), ARMOR_INVAR, 1).setRepairIngot("ingotInvar")
				.setArmorTextures(TEXTURE_INVAR).setUnlocalizedName(ARMOR + "invarPlate").setTextureName("thermalexpansion:armor/ArmorInvarChestplate");
		itemLegsInvar = (ItemArmorAdv) new ItemArmorAdv(ThermalExpansion.config.getItemId("Armor.Invar.Legs"), ARMOR_INVAR, 2).setRepairIngot("ingotInvar")
				.setArmorTextures(TEXTURE_INVAR).setUnlocalizedName(ARMOR + "invarLegs").setTextureName("thermalexpansion:armor/ArmorInvarLegs");
		itemBootsInvar = (ItemArmorAdv) new ItemArmorAdv(ThermalExpansion.config.getItemId("Armor.Invar.Boots"), ARMOR_INVAR, 3).setRepairIngot("ingotInvar")
				.setArmorTextures(TEXTURE_INVAR).setUnlocalizedName(ARMOR + "invarBoots").setTextureName("thermalexpansion:armor/ArmorInvarBoots");

		itemSwordInvar = new ItemSwordAdv(ThermalExpansion.config.getItemId("Tool.Invar.Sword"), TOOL_INVAR).setRepairIngot("ingotInvar")
				.setUnlocalizedName(TOOL + "invarSword").setTextureName("thermalexpansion:tool/InvarSword");
		itemShovelInvar = new ItemShovelAdv(ThermalExpansion.config.getItemId("Tool.Invar.Shovel"), TOOL_INVAR).setRepairIngot("ingotInvar")
				.setUnlocalizedName(TOOL + "invarShovel").setTextureName("thermalexpansion:tool/InvarShovel");
		itemPickaxeInvar = new ItemPickaxeAdv(ThermalExpansion.config.getItemId("Tool.Invar.Pickaxe"), TOOL_INVAR).setRepairIngot("ingotInvar")
				.setUnlocalizedName(TOOL + "invarPickaxe").setTextureName("thermalexpansion:tool/InvarPickaxe");
		itemAxeInvar = new ItemAxeAdv(ThermalExpansion.config.getItemId("Tool.Invar.Axe"), TOOL_INVAR).setRepairIngot("ingotInvar")
				.setUnlocalizedName(TOOL + "invarAxe").setTextureName("thermalexpansion:tool/InvarAxe");
		itemHoeInvar = new ItemHoeAdv(ThermalExpansion.config.getItemId("Tool.Invar.Hoe"), TOOL_INVAR).setRepairIngot("ingotInvar")
				.setUnlocalizedName(TOOL + "invarHoe").setTextureName("thermalexpansion:tool/InvarHoe");
		itemShearsInvar = new ItemShearsAdv(ThermalExpansion.config.getItemId("Tool.Invar.Shears"), TOOL_INVAR).setRepairIngot("ingotInvar")
				.setUnlocalizedName(TOOL + "invarShears").setTextureName("thermalexpansion:tool/InvarShears");
		itemFishingRodInvar = new ItemFishingRodAdv(ThermalExpansion.config.getItemId("Tool.Invar.FishingRod"), TOOL_INVAR).setRepairIngot("ingotInvar")
				.setUnlocalizedName(TOOL + "invarFishingRod").setTextureName("thermalexpansion:tool/InvarFishingRod");
		itemSickleInvar = new ItemSickleAdv(ThermalExpansion.config.getItemId("Tool.Invar.Sickle"), TOOL_INVAR).setRepairIngot("ingotInvar")
				.setUnlocalizedName(TOOL + "invarSickle").setTextureName("thermalexpansion:tool/InvarSickle");
		itemBattleWrenchInvar = new ItemWrenchBattle(ThermalExpansion.config.getItemId("Tool.Invar.BattleWrench"), TOOL_INVAR).setRepairIngot("ingotInvar")
				.setUnlocalizedName(TOOL + "invarBattleWrench").setTextureName("thermalexpansion:tool/InvarBattleWrench");

		loadItems();
	}

	private static void loadItems() {

		/* Armor */
		armorInvarHelmet = new ItemStack(itemHelmetInvar);
		armorInvarPlate = new ItemStack(itemPlateInvar);
		armorInvarLegs = new ItemStack(itemLegsInvar);
		armorInvarBoots = new ItemStack(itemBootsInvar);
		GameRegistry.registerCustomItemStack("armorInvarHelmet", armorInvarHelmet);
		GameRegistry.registerCustomItemStack("armorInvarPlate", armorInvarPlate);
		GameRegistry.registerCustomItemStack("armorInvarLegs", armorInvarLegs);
		GameRegistry.registerCustomItemStack("armorInvarBoots", armorInvarBoots);

		/* Tools */
		toolInvarSword = new ItemStack(itemSwordInvar);
		toolInvarShovel = new ItemStack(itemShovelInvar);
		toolInvarPickaxe = new ItemStack(itemPickaxeInvar);
		toolInvarAxe = new ItemStack(itemAxeInvar);
		toolInvarHoe = new ItemStack(itemHoeInvar);
		toolInvarShears = new ItemStack(itemShearsInvar);
		toolInvarFishingRod = new ItemStack(itemFishingRodInvar);
		toolInvarSickle = new ItemStack(itemSickleInvar);
		toolInvarBattleWrench = new ItemStack(itemBattleWrenchInvar);

		GameRegistry.registerCustomItemStack("toolInvarSword", toolInvarSword);
		GameRegistry.registerCustomItemStack("toolInvarShovel", toolInvarShovel);
		GameRegistry.registerCustomItemStack("toolInvarPickaxe", toolInvarPickaxe);
		GameRegistry.registerCustomItemStack("toolInvarAxe", toolInvarAxe);
		GameRegistry.registerCustomItemStack("toolInvarHoe", toolInvarHoe);
		GameRegistry.registerCustomItemStack("toolInvarShears", toolInvarShears);
		GameRegistry.registerCustomItemStack("toolInvarFishingRod", toolInvarFishingRod);
		GameRegistry.registerCustomItemStack("toolInvarSickle", toolInvarSickle);
		GameRegistry.registerCustomItemStack("toolInvarBattleWrench", toolInvarBattleWrench);
	}

	public static void postInit() {

		/* Armor */
		if (enableArmor) {
			GameRegistry.addRecipe(new ShapedOreRecipe(armorInvarHelmet, new Object[] { "III", "I I", 'I', "ingotInvar" }));
			GameRegistry.addRecipe(new ShapedOreRecipe(armorInvarPlate, new Object[] { "I I", "III", "III", 'I', "ingotInvar" }));
			GameRegistry.addRecipe(new ShapedOreRecipe(armorInvarLegs, new Object[] { "III", "I I", "I I", 'I', "ingotInvar" }));
			GameRegistry.addRecipe(new ShapedOreRecipe(armorInvarBoots, new Object[] { "I I", "I I", 'I', "ingotInvar" }));
		}

		/* Tools */
		if (enableTools[0]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolInvarSword, new Object[] { " I ", " I ", " S ", 'I', "ingotInvar", 'S', "stickWood" }));
		}
		if (enableTools[1]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolInvarShovel, new Object[] { " I ", " S ", " S ", 'I', "ingotInvar", 'S', "stickWood" }));
		}
		if (enableTools[2]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolInvarPickaxe, new Object[] { "III", " S ", " S ", 'I', "ingotInvar", 'S', "stickWood" }));
		}
		if (enableTools[3]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolInvarAxe, new Object[] { "II ", "IS ", " S ", 'I', "ingotInvar", 'S', "stickWood" }));
		}
		if (enableTools[4]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolInvarHoe, new Object[] { "II ", " S ", " S ", 'I', "ingotInvar", 'S', "stickWood" }));
		}
		if (enableTools[5]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolInvarShears, new Object[] { " I", "I ", 'I', "ingotInvar" }));
		}
		if (enableTools[6]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolInvarFishingRod, new Object[] { "  I", " IW", "S W", 'I', "ingotInvar", 'S', "stickWood", 'W',
					Items.string }));
		}
		if (enableTools[7]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolInvarSickle, new Object[] { " I ", "  I", "SI ", 'I', "ingotInvar", 'S', "stickWood" }));
		}
		if (enableTools[8]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolInvarBattleWrench, new Object[] { "I I", " G ", " W ", 'I', "ingotInvar", 'G', "gearInvar", 'W',
					TEItems.toolWrench }));
		}
	}

	public static boolean enableArmor = true;
	public static boolean[] enableTools = new boolean[9];

	static {
		String category = "item.feature";
		enableArmor = ThermalExpansion.config.get(category, "Armor.Invar", true);
		enableTools[0] = ThermalExpansion.config.get(category, "Tool.Invar.Sword", true);
		enableTools[1] = ThermalExpansion.config.get(category, "Tool.Invar.Shovel", true);
		enableTools[2] = ThermalExpansion.config.get(category, "Tool.Invar.Pickaxe", true);
		enableTools[3] = ThermalExpansion.config.get(category, "Tool.Invar.Axe", true);
		enableTools[4] = ThermalExpansion.config.get(category, "Tool.Invar.Hoe", true);
		enableTools[5] = ThermalExpansion.config.get(category, "Tool.Invar.Shears", true);
		enableTools[6] = ThermalExpansion.config.get(category, "Tool.Invar.FishingRod", true);
		enableTools[7] = ThermalExpansion.config.get(category, "Tool.Invar.Sickle", true);
		enableTools[8] = ThermalExpansion.config.get(category, "Tool.Invar.BattleWrench", true);
	}

	public static ItemArmorAdv itemHelmetInvar;
	public static ItemArmorAdv itemPlateInvar;
	public static ItemArmorAdv itemLegsInvar;
	public static ItemArmorAdv itemBootsInvar;

	public static Item itemSwordInvar;
	public static Item itemShovelInvar;
	public static Item itemPickaxeInvar;
	public static Item itemAxeInvar;
	public static Item itemHoeInvar;
	public static Item itemShearsInvar;
	public static Item itemFishingRodInvar;
	public static Item itemSickleInvar;
	public static Item itemBattleWrenchInvar;

	public static ItemStack armorInvarHelmet;
	public static ItemStack armorInvarPlate;
	public static ItemStack armorInvarLegs;
	public static ItemStack armorInvarBoots;

	public static ItemStack toolInvarSword;
	public static ItemStack toolInvarShovel;
	public static ItemStack toolInvarPickaxe;
	public static ItemStack toolInvarAxe;
	public static ItemStack toolInvarHoe;
	public static ItemStack toolInvarShears;
	public static ItemStack toolInvarFishingRod;
	public static ItemStack toolInvarSickle;
	public static ItemStack toolInvarBattleWrench;

	public static final Item.ToolMaterial TOOL_INVAR = EnumHelper.addToolMaterial("TE_INVAR", 2, 450, 7F, 2, 16);
	public static final ItemArmor.ArmorMaterial ARMOR_INVAR = EnumHelper.addArmorMaterial("TE_INVAR", 25, new int[] { 2, 7, 5, 2 }, 11);
	public static final String[] TEXTURE_INVAR = { TEProps.PATH_ARMOR + "Invar_1.png", TEProps.PATH_ARMOR + "Invar_2.png" };

	public static final String ARMOR = "thermalexpansion.armor.";
	public static final String TOOL = "thermalexpansion.tool.";
}
