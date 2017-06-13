package cofh.thermalexpansion.plugins;

import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;

import java.util.Locale;

public class TinkersConstructPlugin {

	private TinkersConstructPlugin() {

	}

	public static final String MOD_ID = "tconstruct";
	public static final String MOD_NAME = "Tinkers' Construct";

	public static void initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";

		boolean enable = ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment);

		if (!enable || !Loader.isModLoaded(MOD_ID)) {
			return;
		}

		/* VANILLA */
		{
			addRecipeSet("iron");
			addRecipeSet("gold");

			Fluid emerald = FluidRegistry.getFluid("emerald");

			if (emerald != null) {
				CrucibleManager.addRecipe(4000, new ItemStack(Items.EMERALD), new FluidStack(emerald, 666));
				CrucibleManager.addRecipe(4000 * 2, new ItemStack(Blocks.EMERALD_ORE), new FluidStack(emerald, 666 * 2));
				CrucibleManager.addRecipe(4000 * 8, new ItemStack(Blocks.EMERALD_BLOCK), new FluidStack(emerald, 666 * 9));
			}
		}

		/* THERMAL FOUNDATION */
		{
			addRecipeSet("copper");
			addRecipeSet("tin");
			addRecipeSet("silver");
			addRecipeSet("lead");
			addRecipeSet("aluminum");
			addRecipeSet("nickel");
			addRecipeSet("platinum");
			addRecipeSet("iridium");

			addRecipeSet("steel");
			addRecipeSet("electrum");
			addRecipeSet("invar");
			addRecipeSet("bronze");
			addRecipeSet("constantan");
			addRecipeSet("signalum");
			addRecipeSet("lumium");
			addRecipeSet("enderium");
		}

		/* TINKERS' CONSTRUCT */
		{
			addRecipeSet("ardite");
			addRecipeSet("cobalt");
			addRecipeSet("manyullyn");
		}

		ThermalExpansion.LOG.info("Thermal Expansion: " + MOD_NAME + " Plugin Enabled.");
	}

	private static boolean addRecipeSet(String oreType) {

		if (oreType == null || oreType.isEmpty()) {
			return false;
		}
		Fluid fluid = FluidRegistry.getFluid(oreType.toLowerCase(Locale.ENGLISH));

		if (fluid == null) {
			return false;
		}
		oreType = StringHelper.titleCase(oreType);

		int energy = 4000;
		int fluidIngot = 144;

		String nuggetName = NUGGET + oreType;
		String ingotName = INGOT + oreType;
		String oreName = ORE + oreType;
		String blockName = BLOCK + oreType;
		String dustName = DUST + oreType;
		String plateName = PLATE + oreType;

		ItemStack nugget = ItemHelper.getOre(nuggetName);
		ItemStack ingot = ItemHelper.getOre(ingotName);
		ItemStack ore = ItemHelper.getOre(oreName);
		ItemStack block = ItemHelper.getOre(blockName);
		ItemStack dust = ItemHelper.getOre(dustName);
		ItemStack plate = ItemHelper.getOre(plateName);

		if (!nugget.isEmpty()) {
			CrucibleManager.addRecipe(energy / 8, nugget, new FluidStack(fluid, fluidIngot / 9));
		}
		if (ingot.isEmpty()) {
			CrucibleManager.addRecipe(energy, ingot, new FluidStack(fluid, fluidIngot));
		}
		if (!ore.isEmpty()) {
			CrucibleManager.addRecipe(energy * 2, ore, new FluidStack(fluid, fluidIngot * 2));
		}
		if (!block.isEmpty()) {
			CrucibleManager.addRecipe(energy * 8, block, new FluidStack(fluid, fluidIngot * 9));
		}
		if (!dust.isEmpty()) {
			CrucibleManager.addRecipe(energy / 2, dust, new FluidStack(fluid, fluidIngot));
		}
		if (!plate.isEmpty()) {
			CrucibleManager.addRecipe(energy, plate, new FluidStack(fluid, fluidIngot));
		}
		return true;
	}

	public static final String NUGGET = "nugget";
	public static final String INGOT = "ingot";
	public static final String ORE = "ore";
	public static final String BLOCK = "block";
	public static final String DUST = "dust";
	public static final String PLATE = "plate";

}
