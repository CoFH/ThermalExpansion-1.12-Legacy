package thermalexpansion.item;

import cofh.core.CoFHProps;
import cofh.util.ConfigHandler;
import cofh.util.ItemHelper;
import cpw.mods.fml.common.registry.GameRegistry;

import java.io.File;
import java.util.ArrayList;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.core.TEProps;
import thermalexpansion.item.tool.ItemFlorb;
import thermalexpansion.util.crafting.TransposerManager;

public class TEFlorbs {

	private TEFlorbs() {

	}

	public static void preInit() {

		configFlorbs.setConfiguration(new Configuration(new File(CoFHProps.configDir, "cofh/ThermalExpansion-Florbs.cfg")));

		String category = "tweak";
		String comment = null;

		category = "item.feature";
		comment = "This allows you to disable Florbs entirely. It also means that you actively dislike fun things.";
		enableFlorbs = configFlorbs.get(category, "Florb.Enable", true, comment);

		itemFlorb = (ItemFlorb) new ItemFlorb().setUnlocalizedName("florb");
	}

	public static void initialize() {

		florb = itemFlorb.addItem(0, "florb");
		florbMagmatic = itemFlorb.addItem(1, "florbMagmatic");
	}

	public static void postInit() {

		parseFlorbs();

		configFlorbs.cleanUp(true, false);

	}

	public static void parseFlorbs() {

		ItemStack florbStack = ItemHelper.cloneStack(florb, 4);
		ItemStack florbMagmaticStack = ItemHelper.cloneStack(florbMagmatic, 4);

		if (!enableFlorbs) {
			return;
		}
		GameRegistry.addRecipe(new ShapelessOreRecipe(florbStack, new Object[] { TEItems.sawdust, TEItems.slag, "slimeball" }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(florbMagmaticStack, new Object[] { TEItems.sawdust, TEItems.slag, "slimeball", Items.blaze_powder }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(florbMagmaticStack, new Object[] { TEItems.sawdust, TEItems.slag, Items.magma_cream }));

		for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
			if (fluid.canBePlacedInWorld() && configFlorbs.get("whitelist", fluid.getName(), true)) {
				if (fluid.getTemperature() < TEProps.MAGMATIC_TEMPERATURE) {
					florbList.add(ItemFlorb.setTag(new ItemStack(itemFlorb, 1, 0), fluid));
					TransposerManager.addFillRecipe(1600, florb, florbList.get(florbList.size() - 1), new FluidStack(fluid, 1000), false);
				} else {
					florbList.add(ItemFlorb.setTag(new ItemStack(itemFlorb, 1, 1), fluid));
					TransposerManager.addFillRecipe(1600, florbMagmatic, florbList.get(florbList.size() - 1), new FluidStack(fluid, 1000), false);
				}
			}
		}
	}

	public static ItemFlorb itemFlorb;

	public static ItemStack florb;
	public static ItemStack florbMagmatic;
	public static ArrayList<ItemStack> florbList = new ArrayList();

	public static boolean enableFlorbs = true;
	public static ConfigHandler configFlorbs = new ConfigHandler(ThermalExpansion.version);

}
