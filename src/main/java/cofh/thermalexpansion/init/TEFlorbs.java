package cofh.thermalexpansion.init;

import cofh.core.init.CoreProps;
import cofh.core.util.ConfigHandler;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.item.ItemFlorb;
import cofh.thermalexpansion.util.crafting.TransposerManager;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;
import java.util.ArrayList;

import static cofh.lib.util.helpers.ItemHelper.ShapelessRecipe;

public class TEFlorbs {

	private TEFlorbs() {

	}

	public static void preInit() {

		CONFIG_FLORBS.setConfiguration(new Configuration(new File(CoreProps.configDir, "cofh/" + ThermalExpansion.MOD_ID + "/florbs.cfg"), true));

		String category = "General";
		String comment = "If TRUE, the recipe for Florbs is enabled. Setting this to FALSE means that you actively dislike fun things.";
		enable = CONFIG_FLORBS.getConfiguration().getBoolean("EnableRecipe", category, enable, comment);

		itemFlorb = (ItemFlorb) new ItemFlorb().setUnlocalizedName("florb");
	}

	public static void initialize() {

		florb = itemFlorb.addItem(0, "florb");
		florbMagmatic = itemFlorb.addItem(1, "florbMagmatic");
	}

	public static void postInit() {

		parseFlorbs();

		CONFIG_FLORBS.cleanUp(false, false);
	}

	public static void parseFlorbs() {

		ItemStack florbStack = ItemHelper.cloneStack(florb, 4);
		ItemStack florbMagmaticStack = ItemHelper.cloneStack(florbMagmatic, 4);

		for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
			if (fluid.canBePlacedInWorld()) {
				if (fluid.getTemperature() < TEProps.MAGMATIC_TEMPERATURE) {
					florbList.add(ItemFlorb.setTag(new ItemStack(itemFlorb, 1, 0), fluid));
				} else {
					florbList.add(ItemFlorb.setTag(new ItemStack(itemFlorb, 1, 1), fluid));
				}
				if (!enable) {
					continue;
				}
				if (CONFIG_FLORBS.get("Whitelist", fluid.getName(), true)) {
					if (fluid.getTemperature() < TEProps.MAGMATIC_TEMPERATURE) {
						TransposerManager.addFillRecipe(1600, florb, florbList.get(florbList.size() - 1), new FluidStack(fluid, 1000), false);
					} else {
						TransposerManager.addFillRecipe(1600, florbMagmatic, florbList.get(florbList.size() - 1), new FluidStack(fluid, 1000), false);
					}
				}
			}
		}
		if (!enable) {
			return;
		}
		GameRegistry.addRecipe(ShapelessRecipe(florbStack, "dustWood", ItemMaterial.crystalSlag, "slimeball"));
		GameRegistry.addRecipe(ShapelessRecipe(florbMagmaticStack, "dustWood", ItemMaterial.crystalSlag, "slimeball", Items.BLAZE_POWDER));
		GameRegistry.addRecipe(ShapelessRecipe(florbMagmaticStack, "dustWood", ItemMaterial.crystalSlag, Items.MAGMA_CREAM));

		GameRegistry.addRecipe(ShapelessRecipe(florbStack, "dustWood", ItemMaterial.crystalSlag, ItemMaterial.rosin));
		GameRegistry.addRecipe(ShapelessRecipe(florbMagmaticStack, "dustWood", ItemMaterial.crystalSlag, ItemMaterial.rosin, Items.BLAZE_POWDER));
	}

	public static boolean enable = true;
	public static ArrayList<ItemStack> florbList = new ArrayList<ItemStack>();

	public static final ConfigHandler CONFIG_FLORBS = new ConfigHandler(ThermalExpansion.VERSION);

	/* REFERENCES */
	public static ItemFlorb itemFlorb;

	public static ItemStack florb;
	public static ItemStack florbMagmatic;

}
