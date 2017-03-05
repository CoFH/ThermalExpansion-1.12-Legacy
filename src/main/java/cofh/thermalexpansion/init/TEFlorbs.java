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

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
		ThermalExpansion.proxy.addIModelRegister(itemFlorb);
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

				ItemStack stack;
				if (fluid.getTemperature() < TEProps.MAGMATIC_TEMPERATURE) {
					stack = new ItemStack(itemFlorb, 1, 0);
				} else {
					stack = new ItemStack(itemFlorb, 1, 1);
				}
				addFlorb(stack, fluid);

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

		GameRegistry.addRecipe(ShapelessRecipe(florbStack, "dustWood", ItemMaterial.crystalSlag, ItemMaterial.globRosin));
		GameRegistry.addRecipe(ShapelessRecipe(florbMagmaticStack, "dustWood", ItemMaterial.crystalSlag, ItemMaterial.globRosin, Items.BLAZE_POWDER));
	}

	private static void addFlorb(ItemStack florb, Fluid fluid) {

		ItemFlorb.setTag(florb, fluid);
		florbList.add(florb);
		florbMap.put(fluid.getName(), florb);
	}

	/**
	 * Attempts to get a Florb ItemStack from the given fluid.
	 *
	 * @param fluid The fluid a Florb is being requested for.
	 * @return The ItemStack. Will be null if a florb doesn't exist.
	 */
	@Nullable
	public static ItemStack getFlorb(Fluid fluid) {

		return florbMap.get(fluid.getName());
	}

	public static boolean enable = true;
	public static ArrayList<ItemStack> florbList = new ArrayList<>();
	public static Map<String, ItemStack> florbMap = new HashMap<>();

	public static final ConfigHandler CONFIG_FLORBS = new ConfigHandler(ThermalExpansion.VERSION);

	/* REFERENCES */
	public static ItemFlorb itemFlorb;

	public static ItemStack florb;
	public static ItemStack florbMagmatic;

}
