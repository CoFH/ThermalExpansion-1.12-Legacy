package cofh.thermalexpansion.plugins;

import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.ExtruderManager;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import static cofh.thermalexpansion.util.managers.machine.ExtruderManager.DEFAULT_ENERGY;

public class PluginUndergroundBiomes extends PluginTEBase {

	public static final String MOD_ID = "undergroundbiomes";
	public static final String MOD_NAME = "Underground Biomes";

	public int igneousEnergy = DEFAULT_ENERGY * 2;
	public int igneousLava = 0;
	public int igneousWater = 1000;

	public int metamorphicEnergy = DEFAULT_ENERGY * 4;
	public int metamorphicLava = 0;
	public int metamorphicWater = 2000;

	public int sedimentaryEnergy = DEFAULT_ENERGY * 2;
	public int sedimentaryLava = 0;
	public int sedimentaryWater = 1500;

	public PluginUndergroundBiomes() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void preInitDelegate() {

		String category = "Plugins." + MOD_NAME;
		String comment;

		comment = "RF required to make Igneous stone.";
		igneousEnergy = ThermalExpansion.CONFIG.getConfiguration().getInt("IgneousEnergy", category, igneousEnergy, DEFAULT_ENERGY, DEFAULT_ENERGY * 100, comment);
		comment = "Lava required to make Igneous stone.";
		igneousLava = ThermalExpansion.CONFIG.getConfiguration().getInt("IgneousLava", category, igneousLava, 0, Fluid.BUCKET_VOLUME * 4, comment);
		comment = "Water required to make Igneous stone.";
		igneousWater = ThermalExpansion.CONFIG.getConfiguration().getInt("IgneousWater", category, igneousWater, 0, Fluid.BUCKET_VOLUME * 4, comment);

		comment = "RF required to make Metamorphic stone.";
		metamorphicEnergy = ThermalExpansion.CONFIG.getConfiguration().getInt("MetamorphicEnergy", category, metamorphicEnergy, DEFAULT_ENERGY, DEFAULT_ENERGY * 100, comment);
		comment = "Lava required to make Metamorphic stone.";
		metamorphicLava = ThermalExpansion.CONFIG.getConfiguration().getInt("MetamorphicLava", category, metamorphicLava, 0, Fluid.BUCKET_VOLUME * 4, comment);
		comment = "Water required to make Metamorphic stone.";
		metamorphicWater = ThermalExpansion.CONFIG.getConfiguration().getInt("MetamorphicWater", category, metamorphicWater, 0, Fluid.BUCKET_VOLUME * 4, comment);

		comment = "RF required to make Sedimentary stone.";
		sedimentaryEnergy = ThermalExpansion.CONFIG.getConfiguration().getInt("SedimentaryEnergy", category, sedimentaryEnergy, DEFAULT_ENERGY, DEFAULT_ENERGY * 100, comment);
		comment = "Lava required to make Sedimentary stone.";
		sedimentaryLava = ThermalExpansion.CONFIG.getConfiguration().getInt("SedimentaryLava", category, sedimentaryLava, 0, Fluid.BUCKET_VOLUME * 4, comment);
		comment = "Water required to make Sedimentary stone.";
		sedimentaryWater = ThermalExpansion.CONFIG.getConfiguration().getInt("SedimentaryWater", category, sedimentaryWater, 0, Fluid.BUCKET_VOLUME * 4, comment);

	}

	@Override
	public void initializeDelegate() {

		/* EXTRUDER */
		{
			for (int i = 0; i < 8; i++) {
				ExtruderManager.addRecipeIgneous(igneousEnergy, getItemStack("igneous_stone", 1, i), new FluidStack(FluidRegistry.LAVA, igneousLava), new FluidStack(FluidRegistry.WATER, igneousWater));
				ExtruderManager.addRecipeIgneous(metamorphicEnergy, getItemStack("metamorphic_stone", 1, i), new FluidStack(FluidRegistry.LAVA, metamorphicLava), new FluidStack(FluidRegistry.WATER, metamorphicWater));
				ExtruderManager.addRecipeSedimentary(sedimentaryEnergy, getItemStack("sedimentary_stone", 1, i), new FluidStack(FluidRegistry.LAVA, sedimentaryLava), new FluidStack(FluidRegistry.WATER, sedimentaryWater));
			}
		}
	}

}
