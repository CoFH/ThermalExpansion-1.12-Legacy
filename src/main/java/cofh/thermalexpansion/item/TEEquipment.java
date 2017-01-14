package cofh.thermalexpansion.item;

import codechicken.lib.item.ItemStackRegistry;
import cofh.core.item.ItemArmorAdv;
import cofh.thermalfoundation.item.Equipment;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TEEquipment {

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
    public static Item itemBowInvar;

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
    public static ItemStack toolInvarBow;


	private TEEquipment() {

	}

	public static void preInit() {

		itemHelmetInvar = Equipment.Invar.itemHelmet;
		itemPlateInvar = Equipment.Invar.itemPlate;
		itemLegsInvar = Equipment.Invar.itemLegs;
		itemBootsInvar = Equipment.Invar.itemBoots;

		itemSwordInvar = Equipment.Invar.itemSword;
		itemShovelInvar = Equipment.Invar.itemShovel;
		itemPickaxeInvar = Equipment.Invar.itemPickaxe;
		itemAxeInvar = Equipment.Invar.itemAxe;
		itemHoeInvar = Equipment.Invar.itemHoe;
		itemShearsInvar = Equipment.Invar.itemShears;
		itemFishingRodInvar = Equipment.Invar.itemFishingRod;
		itemSickleInvar = Equipment.Invar.itemSickle;
		itemBowInvar = Equipment.Invar.itemBow;

	}

	public static void initialize() {

		/* Armor */
		armorInvarHelmet = new ItemStack(itemHelmetInvar);
		armorInvarPlate = new ItemStack(itemPlateInvar);
		armorInvarLegs = new ItemStack(itemLegsInvar);
		armorInvarBoots = new ItemStack(itemBootsInvar);

		ItemStackRegistry.registerCustomItemStack("armorInvarHelmet", armorInvarHelmet);
        ItemStackRegistry.registerCustomItemStack("armorInvarPlate", armorInvarPlate);
        ItemStackRegistry.registerCustomItemStack("armorInvarLegs", armorInvarLegs);
        ItemStackRegistry.registerCustomItemStack("armorInvarBoots", armorInvarBoots);

		/* Tools */
		toolInvarSword = new ItemStack(itemSwordInvar);
		toolInvarShovel = new ItemStack(itemShovelInvar);
		toolInvarPickaxe = new ItemStack(itemPickaxeInvar);
		toolInvarAxe = new ItemStack(itemAxeInvar);
		toolInvarHoe = new ItemStack(itemHoeInvar);
		toolInvarShears = new ItemStack(itemShearsInvar);
		toolInvarFishingRod = new ItemStack(itemFishingRodInvar);
		toolInvarSickle = new ItemStack(itemSickleInvar);
		toolInvarBow = new ItemStack(itemBowInvar);

        ItemStackRegistry.registerCustomItemStack("toolInvarSword", toolInvarSword);
        ItemStackRegistry.registerCustomItemStack("toolInvarShovel", toolInvarShovel);
        ItemStackRegistry.registerCustomItemStack("toolInvarPickaxe", toolInvarPickaxe);
        ItemStackRegistry.registerCustomItemStack("toolInvarAxe", toolInvarAxe);
        ItemStackRegistry.registerCustomItemStack("toolInvarHoe", toolInvarHoe);
        ItemStackRegistry.registerCustomItemStack("toolInvarShears", toolInvarShears);
        ItemStackRegistry.registerCustomItemStack("toolInvarFishingRod", toolInvarFishingRod);
        ItemStackRegistry.registerCustomItemStack("toolInvarSickle", toolInvarSickle);
        ItemStackRegistry.registerCustomItemStack("toolInvarBow", toolInvarBow);
	}

	public static void postInit() {

	}
}
