package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.util.managers.device.TapperManager;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Locale;

public class PluginTConstruct extends PluginTEBase {

	public static final String MOD_ID = "tconstruct";
	public static final String MOD_NAME = "Tinkers' Construct";

	public PluginTConstruct() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

		ItemStack slimeCongealed = getItemStack("slime_congealed", 1, 0);
		ItemStack slimeCongealedMagma = getItemStack("slime_congealed", 1, 4);

		ItemStack saplingSlimeBlue = getItemStack("slime_sapling", 1, 0);
		ItemStack saplingSlimePurple = getItemStack("slime_sapling", 1, 1);
		ItemStack saplingSlimeMagma = getItemStack("slime_sapling", 1, 2);

		Block log = getBlock("slime_congealed");

		Block leaves = getBlock("slime_leaves");

		Fluid blueslime = FluidRegistry.getFluid("blueslime");
		Fluid emerald = FluidRegistry.getFluid("emerald");

		/* CRUCIBLE */
		{
			addRecipeSet("iron");
			addRecipeSet("gold");

			if (emerald != null) {
				CrucibleManager.addRecipe(4000, new ItemStack(Items.EMERALD), new FluidStack(emerald, 666));
				CrucibleManager.addRecipe(4000 * 2, new ItemStack(Blocks.EMERALD_ORE), new FluidStack(emerald, 666 * 2));
				CrucibleManager.addRecipe(4000 * 8, new ItemStack(Blocks.EMERALD_BLOCK), new FluidStack(emerald, 666 * 9));
			}

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

			addRecipeSet("ardite");
			addRecipeSet("cobalt");
			addRecipeSet("manyullyn");
		}

		/* INSOLATOR */
		{
			InsolatorManager.addDefaultTreeRecipe(saplingSlimeBlue, ItemHelper.cloneStack(slimeCongealed, 6), saplingSlimeBlue);
			InsolatorManager.addDefaultTreeRecipe(saplingSlimePurple, ItemHelper.cloneStack(slimeCongealed, 6), saplingSlimePurple);
			InsolatorManager.addDefaultTreeRecipe(saplingSlimeMagma, ItemHelper.cloneStack(slimeCongealedMagma, 6), saplingSlimeMagma);
		}

		/* TAPPER */
		{
			TapperManager.addStandardMapping(slimeCongealed, new FluidStack(blueslime, 25));
			TapperManager.addStandardMapping(slimeCongealedMagma, new FluidStack(blueslime, 25));

			addLeafMapping(log, 0, leaves, 0);
			addLeafMapping(log, 0, leaves, 1);
			addLeafMapping(log, 4, leaves, 2);
		}
	}

	/* HELPERS */
	protected boolean addRecipeSet(String oreType) {

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
		if (!ingot.isEmpty()) {
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

}
