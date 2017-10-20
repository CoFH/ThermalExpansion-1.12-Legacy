package cofh.thermalexpansion.plugins;

import cofh.core.util.ModPlugin;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public class PluginMysticalAgriculture extends ModPlugin {

	public static final String MOD_ID = "mysticalagriculture";
	public static final String MOD_NAME = "Mystical Agriculture";

	public PluginMysticalAgriculture() {

		super(MOD_ID, MOD_NAME);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";
		enable = Loader.isModLoaded(MOD_ID) && ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment);

		if (!enable) {
			return false;
		}
		return !error;
	}

	@Override
	public boolean register() {

		if (!enable) {
			return false;
		}
		try {
			// @formatter:off
			String[] names = { "adamantine",
					"aluminum_brass",
					"aluminum",
					"amber",
					"apatite",
					"aquarium",
					"ardite",
					"basalt",
					"basalz",
					"blaze",
					"blitz",
					"blizz",
					"blue_topaz",
					"brass",
					"bronze",
					"certus_quartz",
					"chicken",
					"chimerite",
					"chrome",
					"coal",
					"cobalt",
					"cold_iron",
					"conductive_iron",
					"constantan",
					"copper",
					"cow",
					"creeper",
					"dark_steel",
					"desh",
					"diamond",
					"dirt",
					"draconium",
					"dye",
					"electrical_steel",
					"electrum",
					"emerald",
					"end",
					"ender_amethyst",
					"ender_biotite",
					"enderium",
					"enderman",
					"energetic_alloy",
					"experience",
					"fire",
					"fluix",
					"ghast",
					"glowstone_ingot",
					"glowstone",
					"gold",
					"guardian",
					"ice",
					"invar",
					"iridium",
					"iron",
					"knightslime",
					"lapis_lazuli",
					"lead",
					"limestone",
					"lumium",
					"malachite",
					"manasteel",
					"manyullyn",
					"marble",
					"meteoric_iron",
					"mithril",
					"moonstone",
					"mystical_flower",
					"nature",
					"nether_quartz",
					"nether",
					"nickel",
					"obsidian",
					"osmium",
					"peridot",
					"pig",
					"platinum",
					"pulsating_iron",
					"quartz_enriched_iron",
					"rabbit",
					"redstone_alloy",
					"redstone",
					"refined_obsidian",
					"rubber",
					"ruby",
					"saltpeter",
					"sapphire",
					"sheep",
					"signalum",
					"silicon",
					"silver",
					"skeleton",
					"slime",
					"soularium",
					"spider",
					"star_steel",
					"steel",
					"stone",
					"sulfur",
					"sunstone",
					"tanzanite",
					"terrasteel",
					"tin",
					"titanium",
					"topaz",
					"tungsten",
					"uranium",
					"vibrant_alloy",
					"vinteum",
					"water",
					"wither_skeleton",
					"wood",
					"yellorium",
					"zinc",
					"zombie" };
			// @formatter:on

			/* INSOLATOR */
			{
				for (String name : names) {
					ItemStack seeds = getSeeds(name);
					ItemStack essence = getEssence(name);
					InsolatorManager.addDefaultRecipe(seeds, essence, seeds, 105);
				}
				for (int i = 1; i <= 5; i++) {
					ItemStack seeds = getSeeds("tier" + i + "_inferium");

					InsolatorManager.addDefaultRecipe(seeds, getItemStack("crafting", i, 0), seeds, 105);
				}
			}
		} catch (Throwable t) {
			ThermalExpansion.LOG.error("Thermal Expansion: " + MOD_NAME + " Plugin encountered an error:", t);
			error = true;
		}
		if (!error) {
			ThermalExpansion.LOG.info("Thermal Expansion: " + MOD_NAME + " Plugin Enabled.");
		}
		return !error;
	}

	/* HELPERS */
	protected ItemStack getSeeds(String name) {

		return getItemStack(modId, name + "_seeds", 1, 0);
	}

	protected ItemStack getEssence(String name) {

		return getItemStack(modId, name + "_essence", 1, 0);
	}

}
