package cofh.thermalexpansion.item;

import cofh.core.item.ItemArmorAdv;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.item.tool.ItemWrenchBattle;
import cofh.thermalfoundation.item.TFEquipment;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class TEEquipment {

	private TEEquipment() {

	}

	public static void preInit() {

		itemHelmetInvar = TFEquipment.Invar.itemHelmet;
		itemPlateInvar = TFEquipment.Invar.itemPlate;
		itemLegsInvar = TFEquipment.Invar.itemLegs;
		itemBootsInvar = TFEquipment.Invar.itemBoots;

		itemBattleWrenchInvar = new ItemWrenchBattle(TFEquipment.Invar.TOOL_MATERIAL).setRepairIngot("ingotInvar");
		itemBattleWrenchInvar.setUnlocalizedName("thermalexpansion.tool.invarBattleWrench");
		itemBattleWrenchInvar.setTextureName("thermalexpansion:tool/InvarBattleWrench");
		itemBattleWrenchInvar.setCreativeTab(ThermalExpansion.tabTools);
		itemSwordInvar = TFEquipment.Invar.itemSword;
		itemShovelInvar = TFEquipment.Invar.itemShovel;
		itemPickaxeInvar = TFEquipment.Invar.itemPickaxe;
		itemAxeInvar = TFEquipment.Invar.itemAxe;
		itemHoeInvar = TFEquipment.Invar.itemHoe;
		itemShearsInvar = TFEquipment.Invar.itemShears;
		itemFishingRodInvar = TFEquipment.Invar.itemFishingRod;
		itemSickleInvar = TFEquipment.Invar.itemSickle;
		itemBowInvar = TFEquipment.Invar.itemBow;

		GameRegistry.registerItem(itemBattleWrenchInvar, "tool.battleWrenchInvar");

	}

	public static void initialize() {

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
		toolInvarBattleWrench = new ItemStack(itemBattleWrenchInvar);
		toolInvarSword = new ItemStack(itemSwordInvar);
		toolInvarShovel = new ItemStack(itemShovelInvar);
		toolInvarPickaxe = new ItemStack(itemPickaxeInvar);
		toolInvarAxe = new ItemStack(itemAxeInvar);
		toolInvarHoe = new ItemStack(itemHoeInvar);
		toolInvarShears = new ItemStack(itemShearsInvar);
		toolInvarFishingRod = new ItemStack(itemFishingRodInvar);
		toolInvarSickle = new ItemStack(itemSickleInvar);
		toolInvarBow = new ItemStack(itemBowInvar);

		GameRegistry.registerCustomItemStack("toolInvarBattleWrench", toolInvarBattleWrench);
		GameRegistry.registerCustomItemStack("toolInvarSword", toolInvarSword);
		GameRegistry.registerCustomItemStack("toolInvarShovel", toolInvarShovel);
		GameRegistry.registerCustomItemStack("toolInvarPickaxe", toolInvarPickaxe);
		GameRegistry.registerCustomItemStack("toolInvarAxe", toolInvarAxe);
		GameRegistry.registerCustomItemStack("toolInvarHoe", toolInvarHoe);
		GameRegistry.registerCustomItemStack("toolInvarShears", toolInvarShears);
		GameRegistry.registerCustomItemStack("toolInvarFishingRod", toolInvarFishingRod);
		GameRegistry.registerCustomItemStack("toolInvarSickle", toolInvarSickle);
		GameRegistry.registerCustomItemStack("toolInvarBow", toolInvarBow);
	}

	public static void postInit() {

		/* Tools */
		if (enableBattleWrench) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolInvarBattleWrench, new Object[] { "I I", " G ", " W ", 'I', "ingotInvar", 'G', "gearInvar", 'W',
					TEItems.toolWrench }));
		}
	}

	public static boolean enableBattleWrench = true;

	static {
		String category = "item.feature";
		enableBattleWrench = ThermalExpansion.config.get(category, "Tool.Invar.BattleWrench", true);
	}

	public static ItemArmorAdv itemHelmetInvar;
	public static ItemArmorAdv itemPlateInvar;
	public static ItemArmorAdv itemLegsInvar;
	public static ItemArmorAdv itemBootsInvar;

	public static Item itemBattleWrenchInvar;
	public static Item itemSwordInvar;
	public static Item itemShovelInvar;
	public static Item itemPickaxeInvar;
	public static Item itemAxeInvar;
	public static Item itemHoeInvar;
	public static Item itemShearsInvar;
	public static Item itemFishingRodInvar;
	public static Item itemSickleInvar;
	public static Item itemBowInvar;

	public static ItemStack armorInvarHelmet;
	public static ItemStack armorInvarPlate;
	public static ItemStack armorInvarLegs;
	public static ItemStack armorInvarBoots;

	public static ItemStack toolInvarBattleWrench;
	public static ItemStack toolInvarSword;
	public static ItemStack toolInvarShovel;
	public static ItemStack toolInvarPickaxe;
	public static ItemStack toolInvarAxe;
	public static ItemStack toolInvarHoe;
	public static ItemStack toolInvarShears;
	public static ItemStack toolInvarFishingRod;
	public static ItemStack toolInvarSickle;
	public static ItemStack toolInvarBow;

}
