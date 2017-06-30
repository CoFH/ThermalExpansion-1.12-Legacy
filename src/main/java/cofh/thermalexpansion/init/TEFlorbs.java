package cofh.thermalexpansion.init;

import cofh.core.init.CoreProps;
import cofh.core.util.ConfigHandler;
import cofh.lib.util.DefaultedHashMap;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.item.ItemFlorb;
import cofh.thermalexpansion.util.BehaviorFlorbDispense;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.block.BlockDispenser;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import static cofh.lib.util.helpers.RecipeHelper.addShapelessRecipe;

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

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(itemFlorb, new BehaviorFlorbDispense());

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
		addShapelessRecipe(florbStack, "dustWood", "crystalSlag", "slimeball");
		addShapelessRecipe(florbMagmaticStack, "dustWood", "crystalSlag", "slimeball", Items.BLAZE_POWDER);
		addShapelessRecipe(florbMagmaticStack, "dustWood", "crystalSlag", Items.MAGMA_CREAM);

		addShapelessRecipe(florbStack, "dustWood", "crystalSlag", ItemMaterial.globRosin);
		addShapelessRecipe(florbMagmaticStack, "dustWood", "crystalSlag", ItemMaterial.globRosin, Items.BLAZE_POWDER);
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
	 * @return The ItemStack.
	 */
	@Nonnull
	public static ItemStack getFlorb(Fluid fluid) {

		return florbMap.get(fluid.getName());
	}

	public static boolean enable = true;
	public static ArrayList<ItemStack> florbList = new ArrayList<>();
	public static Map<String, ItemStack> florbMap = new DefaultedHashMap<String, ItemStack>(ItemStack.EMPTY);

	public static final ConfigHandler CONFIG_FLORBS = new ConfigHandler(ThermalExpansion.VERSION);

	/* REFERENCES */
	public static ItemFlorb itemFlorb;

	public static ItemStack florb;
	public static ItemStack florbMagmatic;

}
