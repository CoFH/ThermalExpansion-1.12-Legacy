package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager.Type;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
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
	public void preInitDelegate() {

		String category = "Plugins." + MOD_NAME;
		String comment;

		comment = "Secondary chance for seeds when using Phyto-Gro.";
		secondaryChanceBase = ThermalExpansion.CONFIG.getConfiguration().getInt("BaseSecondaryChance", category, secondaryChanceBase, 0, 150, comment);

		comment = "Secondary chance for seeds when using Rich Phyto-Gro.";
		secondaryChanceRich = ThermalExpansion.CONFIG.getConfiguration().getInt("RichSecondaryChance", category, secondaryChanceRich, 0, 150, comment);

		comment = "Secondary chance for seeds when using Fluxed Phyto-Gro.";
		secondaryChanceFlux = ThermalExpansion.CONFIG.getConfiguration().getInt("FluxedSecondaryChance", category, secondaryChanceFlux, 0, 150, comment);
	}

	@Override
	public void initializeDelegate() {

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

				"grains_of_infinity",
				"electrical_steel",
				"redstone_alloy",
				"conductive_iron",
				"soularium",
				"dark_steel",
				"pulsating_iron",
				"energetic_alloy",
				"vibrant_alloy",
				"end_steel",

				"mystical_flower",
				"manasteel",
				"elementium",
				"terrasteel",

				"quicksilver",
				"thaumium",
				"void_metal",

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

				"electrotine",

				"alumite",

				"steeleaf",
				"ironwood",
				"knightmetal",
				"fiery_ingot",

				"meteoric_iron",
				"desh",

				"coralium",
				"abyssalnite",
				"dreadium",

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

				"dark_gem",

				"compressed_iron",

				"ender_amethyst",

				"draconium",

				"yellorium",

				"sky_stone",
				"certus_quartz",
				"fluix",

				"quartz_enriched_iron"
		};
		// @formatter:on

		ItemStack essenceInferium = getItemStack("crafting", 1, 0);
		ItemStack shardProsperity = getItemStack("crafting", 1, 5);

		/* PULVERIZER */
		{
			int energy = PulverizerManager.DEFAULT_ENERGY;

			PulverizerManager.addRecipe(energy, ItemHelper.getOre("oreInferium"), ItemHelper.cloneStack(essenceInferium, 4));
			PulverizerManager.addRecipe(energy, ItemHelper.getOre("oreNetherInferium"), ItemHelper.cloneStack(essenceInferium, 5));
			PulverizerManager.addRecipe(energy, ItemHelper.getOre("oreEndInferium"), ItemHelper.cloneStack(essenceInferium, 6));

			PulverizerManager.addRecipe(energy, ItemHelper.getOre("oreProsperity"), ItemHelper.cloneStack(shardProsperity, 4));
			PulverizerManager.addRecipe(energy, ItemHelper.getOre("oreNetherProsperity"), ItemHelper.cloneStack(shardProsperity, 5));
			PulverizerManager.addRecipe(energy, ItemHelper.getOre("oreEndProsperity"), ItemHelper.cloneStack(shardProsperity, 6));
		}

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
