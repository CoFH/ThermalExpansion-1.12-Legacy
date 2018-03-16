package cofh.thermalexpansion.plugins;

import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager.Type;
import net.minecraft.item.ItemStack;

public class PluginMysticalAgriculture extends PluginTEBase {

	public static final String MOD_ID = "mysticalagriculture";
	public static final String MOD_NAME = "Mystical Agriculture";

	public int secondaryChanceBase = 100;
	public int secondaryChanceRich = 105;
	public int secondaryChanceFlux = 110;

	public PluginMysticalAgriculture() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

		String category = "Plugins";
		String comment;

		// TODO: Remove at some point.
		ThermalExpansion.CONFIG.renameProperty(category, "BaseSecondaryChance", "Plugins." + MOD_NAME, "BaseSecondaryChance", true);
		ThermalExpansion.CONFIG.renameProperty(category, "RichSecondaryChance", "Plugins." + MOD_NAME, "RichSecondaryChance", true);
		ThermalExpansion.CONFIG.renameProperty(category, "FluxedSecondaryChance", "Plugins." + MOD_NAME, "FluxedSecondaryChance", true);

		category = "Plugins." + MOD_NAME;

		comment = "Secondary chance for seeds when using Phyto-Gro.";
		secondaryChanceBase = ThermalExpansion.CONFIG.getConfiguration().getInt("BaseSecondaryChance", category, secondaryChanceBase, 0, 150, comment);

		comment = "Secondary chance for seeds when using Rich Phyto-Gro.";
		secondaryChanceRich = ThermalExpansion.CONFIG.getConfiguration().getInt("RichSecondaryChance", category, secondaryChanceRich, 0, 150, comment);

		comment = "Secondary chance for seeds when using Fluxed Phyto-Gro.";
		secondaryChanceFlux = ThermalExpansion.CONFIG.getConfiguration().getInt("FluxedSecondaryChance", category, secondaryChanceFlux, 0, 150, comment);
	}

	@Override
	public void registerDelegate() {

		// @formatter:off
		String[] names = {
				"stone",
				"dirt",
				"nature",
				"wood",
				"water",
				"ice",
				"fire",
				"dye",
				"nether",
				"coal",
				"iron",
				"nether_quartz",
				"glowstone",
				"redstone",
				"obsidian",
				"gold",
				"lapis_lazuli",
				"end",
				"experience",
				"diamond",
				"emerald",

				"zombie",
				"pig",
				"chicken",
				"cow",
				"sheep",
				"slime",
				"skeleton",
				"creeper",
				"spider",
				"rabbit",
				"guardian",
				"blaze",
				"ghast",
				"enderman",
				"wither_skeleton",

				"rubber",
				"silicon",
				"sulfur",
				"aluminum",
				"copper",
				"saltpeter",
				"tin",
				"bronze",
				"zinc",
				"brass",
				"silver",
				"lead",
				"steel",
				"nickel",
				"constantan",
				"electrum",
				"invar",
				"mithril",
				"tungsten",
				"titanium",
				"uranium",
				"chrome",
				"platinum",
				"iridium",

				"ruby",
				"sapphire",
				"peridot",
				"amber",
				"topaz",
				"malachite",
				"tanzanite",

				"blizz",
				"blitz",
				"basalz",
				"signalum",
				"lumium",
				"enderium",
				"fluxed_electrum",

				"aluminum_brass",
				"knightslime",
				"ardite",
				"cobalt",
				"manyullyn",

				"electrical_steel",
				"redstone_alloy",
				"conductive_iron",
				"soularium",
				"dark_steel",
				"pulsating_iron",
				"energetic_alloy",
				"vibrant_alloy",

				"mystical_flower",
				"manasteel",
				"elementium",
				"terrasteel",

				"dawnstone",

				"uranium_238",
				"iridium_ore",

				"osmium",
				"glowstone_ingot",
				"refined_obsidian",

				"aquarium",
				"cold_iron",
				"star_steel",
				"adamantine",

				"marble",
				"limestone",
				"basalt",

				"apatite",

				"steeleaf",
				"ironwood",
				"knightmetal",
				"fiery_ingot",

				"meteoric_iron",
				"desh",

				"black_quartz",

				"menril",

				"vinteum",
				"chimerite",
				"blue_topaz",
				"moonstone",
				"sunstone",

				"aquamarine",
				"starmetal",
				"rock_crystal",

				"ender_biotite",

				"slate",

				"ender_amethyst",

				"draconium",

				"yellorium",

				"sky_stone",
				"certus_quartz",
				"fluix",

				"quartz_enriched_iron"
		};
		// @formatter:on

		/* INSOLATOR */
		{
			for (String name : names) {
				ItemStack seeds = getSeeds(name);
				ItemStack essence = getEssence(name);
				InsolatorManager.addDefaultRecipe(InsolatorManager.DEFAULT_ENERGY * 2, InsolatorManager.DEFAULT_FLUID * 2, seeds, essence, seeds, secondaryChanceBase, secondaryChanceRich, secondaryChanceFlux, Type.STANDARD);
			}
			for (int i = 1; i <= 5; i++) {
				ItemStack seeds = getSeeds("tier" + i + "_inferium");
				InsolatorManager.addDefaultRecipe(InsolatorManager.DEFAULT_ENERGY * i, InsolatorManager.DEFAULT_FLUID * 4, seeds, getItemStack("crafting", i, 0), seeds, secondaryChanceBase, secondaryChanceRich, secondaryChanceFlux, Type.STANDARD);
			}
		}
	}

	/* HELPERS */
	protected ItemStack getSeeds(String name) {

		return getItemStack(modId, name + "_seeds", 1, 0);
	}

	protected ItemStack getEssence(String name) {

		return getItemStack(modId, name + "_essence", 1, 0);
	}

}
