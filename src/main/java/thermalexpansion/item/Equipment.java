
/*
import cofh.core.item.ItemArmorAdv;
import cofh.core.item.tool.ItemAxeAdv;
import cofh.core.item.tool.ItemBowAdv;
import cofh.core.item.tool.ItemFishingRodAdv;
import cofh.core.item.tool.ItemHoeAdv;
import cofh.core.item.tool.ItemPickaxeAdv;
import cofh.core.item.tool.ItemShearsAdv;
import cofh.core.item.tool.ItemShovelAdv;
import cofh.core.item.tool.ItemSickleAdv;
import cofh.core.item.tool.ItemSwordAdv;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.ShapedOreRecipe;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.core.TEProps;
import thermalexpansion.item.tool.ItemWrenchBattle;

public enum Equipment {

	/* Name, Level, Uses, Speed, Damage, Ench, Dura, Absorption *//*
	Invar(       2,  450,    7F,      2,   16,   25, new int[] { 2, 7, 5, 2 });

	public final ToolMaterial TOOL_MATERIAL;
	public final ArmorMaterial ARMOR_MATERIAL;

	private final String ingot;

	private Equipment(int level, int uses, float speed, int damage, int enchant, int durability, int[] absorb) {

		TOOL_MATERIAL = EnumHelper.addToolMaterial("TE:"+name().toUpperCase(), level, uses, speed, damage, enchant);
		ARMOR_MATERIAL = EnumHelper.addArmorMaterial("TE:"+name().toUpperCase(), durability, absorb, enchant);
		ingot = "ingot" + name();
	}

	public void preInit() {

		String category = "item.feature." + name().toLowerCase();
		enableArmor = ThermalExpansion.config.get(category, "Armor", true);
		category += ".tool";
		enableTools[0] = ThermalExpansion.config.get(category, "BattleWrench", true);
		enableTools[1] = ThermalExpansion.config.get(category, "Sword", true);
		enableTools[2] = ThermalExpansion.config.get(category, "Shovel", true);
		enableTools[3] = ThermalExpansion.config.get(category, "Pickaxe", true);
		enableTools[4] = ThermalExpansion.config.get(category, "Axe", true);
		enableTools[5] = ThermalExpansion.config.get(category, "Hoe", true);
		enableTools[6] = ThermalExpansion.config.get(category, "Shears", true);
		enableTools[7] = ThermalExpansion.config.get(category, "FishingRod", true);
		enableTools[8] = ThermalExpansion.config.get(category, "Sickle", true);
		enableTools[9] = ThermalExpansion.config.get(category, "Bow", true);


		final String ARMOR = "thermalexpansion.armor."+name().toLowerCase();
		final String TOOL = "thermalexpansion.tool."+name().toLowerCase();
		final String[] TEXTURE = { TEProps.PATH_ARMOR + name() + "_1.png", TEProps.PATH_ARMOR + name() + "_2.png" };

		itemHelmet = (ItemArmorAdv) new ItemArmorAdv(ARMOR_MATERIAL, 0).setRepairIngot(ingot).setArmorTextures(TEXTURE)
				.setUnlocalizedName(ARMOR + "Helmet").setTextureName("thermalexpansion:armor/ArmorInvarHelmet").setCreativeTab(ThermalExpansion.tabItems);
		itemPlate = (ItemArmorAdv) new ItemArmorAdv(ARMOR_MATERIAL, 1).setRepairIngot(ingot).setArmorTextures(TEXTURE)
				.setUnlocalizedName(ARMOR + "Plate").setTextureName("thermalexpansion:armor/ArmorInvarChestplate")
				.setCreativeTab(ThermalExpansion.tabItems);
		itemLegs = (ItemArmorAdv) new ItemArmorAdv(ARMOR_MATERIAL, 2).setRepairIngot(ingot).setArmorTextures(TEXTURE)
				.setUnlocalizedName(ARMOR + "Legs").setTextureName("thermalexpansion:armor/ArmorInvarLegs").setCreativeTab(ThermalExpansion.tabItems);
		itemBoots = (ItemArmorAdv) new ItemArmorAdv(ARMOR_MATERIAL, 3).setRepairIngot(ingot).setArmorTextures(TEXTURE)
				.setUnlocalizedName(ARMOR + "Boots").setTextureName("thermalexpansion:armor/ArmorInvarBoots").setCreativeTab(ThermalExpansion.tabItems);

		itemBattleWrench = new ItemWrenchBattle(TOOL_MATERIAL).setRepairIngot(ingot).setUnlocalizedName(TOOL + "invarBattleWrench")
				.setTextureName("thermalexpansion:tool/InvarBattleWrench").setCreativeTab(ThermalExpansion.tabTools);
		itemSword = new ItemSwordAdv(TOOL_MATERIAL).setRepairIngot(ingot).setUnlocalizedName(TOOL + "invarSword")
				.setTextureName("thermalexpansion:tool/InvarSword").setCreativeTab(ThermalExpansion.tabTools);
		itemShovel = new ItemShovelAdv(TOOL_MATERIAL).setRepairIngot(ingot).setUnlocalizedName(TOOL + "invarShovel")
				.setTextureName("thermalexpansion:tool/InvarShovel").setCreativeTab(ThermalExpansion.tabTools);
		itemPickaxe = new ItemPickaxeAdv(TOOL_MATERIAL).setRepairIngot(ingot).setUnlocalizedName(TOOL + "invarPickaxe")
				.setTextureName("thermalexpansion:tool/InvarPickaxe").setCreativeTab(ThermalExpansion.tabTools);
		itemAxe = new ItemAxeAdv(TOOL_MATERIAL).setRepairIngot(ingot).setUnlocalizedName(TOOL + "invarAxe")
				.setTextureName("thermalexpansion:tool/InvarAxe").setCreativeTab(ThermalExpansion.tabTools);
		itemHoe = new ItemHoeAdv(TOOL_MATERIAL).setRepairIngot(ingot).setUnlocalizedName(TOOL + "invarHoe")
				.setTextureName("thermalexpansion:tool/InvarHoe").setCreativeTab(ThermalExpansion.tabTools);
		itemShears = new ItemShearsAdv(TOOL_MATERIAL).setRepairIngot(ingot).setUnlocalizedName(TOOL + "invarShears")
				.setTextureName("thermalexpansion:tool/InvarShears").setCreativeTab(ThermalExpansion.tabTools);
		itemFishingRod = new ItemFishingRodAdv(TOOL_MATERIAL).setRepairIngot(ingot).setUnlocalizedName(TOOL + "invarFishingRod")
				.setTextureName("thermalexpansion:tool/InvarFishingRod").setCreativeTab(ThermalExpansion.tabTools);
		itemSickle = new ItemSickleAdv(TOOL_MATERIAL).setRepairIngot(ingot).setUnlocalizedName(TOOL + "invarSickle")
				.setTextureName("thermalexpansion:tool/InvarSickle").setCreativeTab(ThermalExpansion.tabTools);
		itemBow = new ItemBowAdv(TOOL_MATERIAL).setRepairIngot(ingot).setArrowSpeed(2.5F).setArrowDamage(1.25F).setUnlocalizedName(TOOL + "invarBow")
				.setTextureName("thermalexpansion:tool/InvarBow").setCreativeTab(ThermalExpansion.tabTools);

		GameRegistry.registerItem(itemHelmet, "armor.helmetInvar");
		GameRegistry.registerItem(itemPlate, "armor.plateInvar");
		GameRegistry.registerItem(itemLegs, "armor.legsInvar");
		GameRegistry.registerItem(itemBoots, "armor.bootsInvar");

		GameRegistry.registerItem(itemBattleWrench, "tool.battleWrenchInvar");
		GameRegistry.registerItem(itemSword, "tool.swordInvar");
		GameRegistry.registerItem(itemShovel, "tool.shovelInvar");
		GameRegistry.registerItem(itemPickaxe, "tool.pickaxeInvar");
		GameRegistry.registerItem(itemAxe, "tool.axeInvar");
		GameRegistry.registerItem(itemHoe, "tool.hoeInvar");
		GameRegistry.registerItem(itemShears, "tool.shearsInvar");
		GameRegistry.registerItem(itemFishingRod, "tool.fishingRodInvar");
		GameRegistry.registerItem(itemSickle, "tool.sickleInvar");
		GameRegistry.registerItem(itemBow, "tool.bowInvar");

	}

	public void initialize() {

		// Armor
		armorHelmet = new ItemStack(itemHelmet);
		armorPlate = new ItemStack(itemPlate);
		armorLegs = new ItemStack(itemLegs);
		armorBoots = new ItemStack(itemBoots);

		GameRegistry.registerCustomItemStack("armorInvarHelmet", armorHelmet);
		GameRegistry.registerCustomItemStack("armorInvarPlate", armorPlate);
		GameRegistry.registerCustomItemStack("armorInvarLegs", armorLegs);
		GameRegistry.registerCustomItemStack("armorInvarBoots", armorBoots);

		// Tools
		toolBattleWrench = new ItemStack(itemBattleWrench);
		toolSword = new ItemStack(itemSword);
		toolShovel = new ItemStack(itemShovel);
		toolPickaxe = new ItemStack(itemPickaxe);
		toolAxe = new ItemStack(itemAxe);
		toolHoe = new ItemStack(itemHoe);
		toolShears = new ItemStack(itemShears);
		toolFishingRod = new ItemStack(itemFishingRod);
		toolSickle = new ItemStack(itemSickle);
		toolBow = new ItemStack(itemBow);

		GameRegistry.registerCustomItemStack("toolInvarBattleWrench", toolBattleWrench);
		GameRegistry.registerCustomItemStack("toolInvarSword", toolSword);
		GameRegistry.registerCustomItemStack("toolInvarShovel", toolShovel);
		GameRegistry.registerCustomItemStack("toolInvarPickaxe", toolPickaxe);
		GameRegistry.registerCustomItemStack("toolInvarAxe", toolAxe);
		GameRegistry.registerCustomItemStack("toolInvarHoe", toolHoe);
		GameRegistry.registerCustomItemStack("toolInvarShears", toolShears);
		GameRegistry.registerCustomItemStack("toolInvarFishingRod", toolFishingRod);
		GameRegistry.registerCustomItemStack("toolInvarSickle", toolSickle);
		GameRegistry.registerCustomItemStack("toolInvarBow", toolBow);
	}

	public void postInit() {

		// Armor
		if (enableArmor) {
			GameRegistry.addRecipe(new ShapedOreRecipe(armorHelmet, new Object[] { "III", "I I", 'I', ingot }));
			GameRegistry.addRecipe(new ShapedOreRecipe(armorPlate, new Object[] { "I I", "III", "III", 'I', ingot }));
			GameRegistry.addRecipe(new ShapedOreRecipe(armorLegs, new Object[] { "III", "I I", "I I", 'I', ingot }));
			GameRegistry.addRecipe(new ShapedOreRecipe(armorBoots, new Object[] { "I I", "I I", 'I', ingot }));
		}

		// Tools
		if (enableTools[0]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolBattleWrench, new Object[] { "I I", " G ", " W ", 'I', ingot, 'G', "gearInvar", 'W',
					TEItems.toolWrench }));
		}
		if (enableTools[1]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolSword, new Object[] { " I ", " I ", " W ", 'I', ingot, 'W', "stickWood" }));
		}
		if (enableTools[2]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolShovel, new Object[] { " I ", " W ", " W ", 'I', ingot, 'W', "stickWood" }));
		}
		if (enableTools[3]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolPickaxe, new Object[] { "III", " W ", " W ", 'I', ingot, 'W', "stickWood" }));
		}
		if (enableTools[4]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolAxe, new Object[] { "II ", "IW ", " W ", 'I', ingot, 'W', "stickWood" }));
		}
		if (enableTools[5]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolHoe, new Object[] { "II ", " W ", " W ", 'I', ingot, 'W', "stickWood" }));
		}
		if (enableTools[6]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolShears, new Object[] { " I", "I ", 'I', ingot }));
		}
		if (enableTools[7]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolFishingRod, new Object[] { "  I", " IS", "W S", 'I', ingot, 'W', "stickWood", 'S',
					Items.string }));
		}
		if (enableTools[8]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolSickle, new Object[] { " I ", "  I", "WI ", 'I', ingot, 'W', "stickWood" }));
		}
		if (enableTools[9]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolBow,
					new Object[] { " IW", "S W", " IW", 'I', ingot, 'S', "stickWood", 'W', Items.string }));
		}
	}

	public boolean enableArmor = true;
	public boolean[] enableTools = new boolean[10];

	public ItemArmorAdv itemHelmet;
	public ItemArmorAdv itemPlate;
	public ItemArmorAdv itemLegs;
	public ItemArmorAdv itemBoots;

	public Item itemBattleWrench;
	public Item itemSword;
	public Item itemShovel;
	public Item itemPickaxe;
	public Item itemAxe;
	public Item itemHoe;
	public Item itemShears;
	public Item itemFishingRod;
	public Item itemSickle;
	public Item itemBow;

	public ItemStack armorHelmet;
	public ItemStack armorPlate;
	public ItemStack armorLegs;
	public ItemStack armorBoots;

	public ItemStack toolBattleWrench;
	public ItemStack toolSword;
	public ItemStack toolShovel;
	public ItemStack toolPickaxe;
	public ItemStack toolAxe;
	public ItemStack toolHoe;
	public ItemStack toolShears;
	public ItemStack toolFishingRod;
	public ItemStack toolSickle;
	public ItemStack toolBow;

}
//*/