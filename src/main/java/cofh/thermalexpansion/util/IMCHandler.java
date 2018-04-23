package cofh.thermalexpansion.util;

import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.device.CoolantManager;
import cofh.thermalexpansion.util.managers.dynamo.*;
import cofh.thermalexpansion.util.managers.machine.*;
import cofh.thermalexpansion.util.managers.machine.CompactorManager.Mode;
import cofh.thermalexpansion.util.managers.machine.EnchanterManager.Type;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IMCHandler {

	public static final IMCHandler INSTANCE = new IMCHandler();

	public void handleIMC(List<IMCMessage> messages) {

		NBTTagCompound nbt;
		for (IMCMessage message : messages) {
			try {
				if (!message.isNBTMessage()) {
					continue;
				}
				nbt = message.getNBTValue();
				String operation = message.key.toLowerCase(Locale.US);

				switch (operation) {
					/* ADD RECIPES */
					case ADD_FURNACE_RECIPE:
						FurnaceManager.addRecipe(nbt.getInteger(ENERGY), new ItemStack(nbt.getCompoundTag(INPUT)), new ItemStack(nbt.getCompoundTag(OUTPUT)));
						continue;
					case ADD_PULVERIZER_RECIPE:
						if (nbt.hasKey(CHANCE)) {
							PulverizerManager.addRecipe(nbt.getInteger(ENERGY), new ItemStack(nbt.getCompoundTag(INPUT)), new ItemStack(nbt.getCompoundTag(PRIMARY_OUTPUT)), new ItemStack(nbt.getCompoundTag(SECONDARY_OUTPUT)), nbt.getInteger(CHANCE));
						} else if (nbt.hasKey(SECONDARY_OUTPUT)) {
							PulverizerManager.addRecipe(nbt.getInteger(ENERGY), new ItemStack(nbt.getCompoundTag(INPUT)), new ItemStack(nbt.getCompoundTag(PRIMARY_OUTPUT)), new ItemStack(nbt.getCompoundTag(SECONDARY_OUTPUT)));
						} else {
							PulverizerManager.addRecipe(nbt.getInteger(ENERGY), new ItemStack(nbt.getCompoundTag(INPUT)), new ItemStack(nbt.getCompoundTag(PRIMARY_OUTPUT)));
						}
						continue;
					case ADD_SAWMILL_RECIPE:
						if (nbt.hasKey(CHANCE)) {
							SawmillManager.addRecipe(nbt.getInteger(ENERGY), new ItemStack(nbt.getCompoundTag(INPUT)), new ItemStack(nbt.getCompoundTag(PRIMARY_OUTPUT)), new ItemStack(nbt.getCompoundTag(SECONDARY_OUTPUT)), nbt.getInteger(CHANCE));
						} else if (nbt.hasKey(SECONDARY_OUTPUT)) {
							SawmillManager.addRecipe(nbt.getInteger(ENERGY), new ItemStack(nbt.getCompoundTag(INPUT)), new ItemStack(nbt.getCompoundTag(PRIMARY_OUTPUT)), new ItemStack(nbt.getCompoundTag(SECONDARY_OUTPUT)));
						} else {
							SawmillManager.addRecipe(nbt.getInteger(ENERGY), new ItemStack(nbt.getCompoundTag(INPUT)), new ItemStack(nbt.getCompoundTag(PRIMARY_OUTPUT)));
						}
						continue;
					case ADD_SMELTER_RECIPE:
						if (nbt.hasKey(CHANCE)) {
							SmelterManager.addRecipe(nbt.getInteger(ENERGY), new ItemStack(nbt.getCompoundTag(PRIMARY_INPUT)), new ItemStack(nbt.getCompoundTag(SECONDARY_INPUT)), new ItemStack(nbt.getCompoundTag(PRIMARY_OUTPUT)), new ItemStack(nbt.getCompoundTag(SECONDARY_OUTPUT)), nbt.getInteger(CHANCE));
						} else if (nbt.hasKey(SECONDARY_OUTPUT)) {
							SmelterManager.addRecipe(nbt.getInteger(ENERGY), new ItemStack(nbt.getCompoundTag(PRIMARY_INPUT)), new ItemStack(nbt.getCompoundTag(SECONDARY_INPUT)), new ItemStack(nbt.getCompoundTag(PRIMARY_OUTPUT)), new ItemStack(nbt.getCompoundTag(SECONDARY_OUTPUT)));
						} else {
							SmelterManager.addRecipe(nbt.getInteger(ENERGY), new ItemStack(nbt.getCompoundTag(PRIMARY_INPUT)), new ItemStack(nbt.getCompoundTag(SECONDARY_INPUT)), new ItemStack(nbt.getCompoundTag(PRIMARY_OUTPUT)));
						}
						continue;
					case ADD_INSOLATOR_RECIPE:
						if (nbt.hasKey(CHANCE)) {
							InsolatorManager.addRecipe(nbt.getInteger(ENERGY), new ItemStack(nbt.getCompoundTag(PRIMARY_INPUT)), new ItemStack(nbt.getCompoundTag(SECONDARY_INPUT)), new ItemStack(nbt.getCompoundTag(PRIMARY_OUTPUT)), new ItemStack(nbt.getCompoundTag(SECONDARY_OUTPUT)), nbt.getInteger(CHANCE));
						} else if (nbt.hasKey(SECONDARY_OUTPUT)) {
							InsolatorManager.addRecipe(nbt.getInteger(ENERGY), new ItemStack(nbt.getCompoundTag(PRIMARY_INPUT)), new ItemStack(nbt.getCompoundTag(SECONDARY_INPUT)), new ItemStack(nbt.getCompoundTag(PRIMARY_OUTPUT)), new ItemStack(nbt.getCompoundTag(SECONDARY_OUTPUT)));
						} else {
							InsolatorManager.addRecipe(nbt.getInteger(ENERGY), new ItemStack(nbt.getCompoundTag(PRIMARY_INPUT)), new ItemStack(nbt.getCompoundTag(SECONDARY_INPUT)), new ItemStack(nbt.getCompoundTag(PRIMARY_OUTPUT)));
						}
						continue;
					case ADD_COMPACTOR_PRESS_RECIPE:
						CompactorManager.addRecipe(nbt.getInteger(ENERGY), new ItemStack(nbt.getCompoundTag(INPUT)), new ItemStack(nbt.getCompoundTag(OUTPUT)), Mode.PLATE);
						continue;
					case ADD_COMPACTOR_COIN_RECIPE:
						CompactorManager.addRecipe(nbt.getInteger(ENERGY), new ItemStack(nbt.getCompoundTag(INPUT)), new ItemStack(nbt.getCompoundTag(OUTPUT)), Mode.COIN);
						continue;
					case ADD_CRUCIBLE_RECIPE:
						CrucibleManager.addRecipe(nbt.getInteger(ENERGY), new ItemStack(nbt.getCompoundTag(INPUT)), FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag(OUTPUT)));
						continue;
					case ADD_REFINERY_RECIPE:
						RefineryManager.addRecipe(nbt.getInteger(ENERGY), FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag(INPUT)), FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag(OUTPUT)), new ItemStack(nbt.getCompoundTag(SECONDARY_OUTPUT)));
						continue;
					case ADD_TRANSPOSER_FILL_RECIPE:
						TransposerManager.addFillRecipe(nbt.getInteger(ENERGY), new ItemStack(nbt.getCompoundTag(INPUT)), new ItemStack(nbt.getCompoundTag(OUTPUT)), FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag(FLUID)), nbt.getBoolean(REVERSIBLE));
						continue;
					case ADD_TRANSPOSER_EXTRACT_RECIPE:
						TransposerManager.addExtractRecipe(nbt.getInteger(ENERGY), new ItemStack(nbt.getCompoundTag(INPUT)), new ItemStack(nbt.getCompoundTag(OUTPUT)), FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag(FLUID)), nbt.getInteger(CHANCE), nbt.getBoolean(REVERSIBLE));
						continue;
					case ADD_CHARGER_RECIPE:
						ChargerManager.addRecipe(nbt.getInteger(ENERGY), new ItemStack(nbt.getCompoundTag(INPUT)), new ItemStack(nbt.getCompoundTag(OUTPUT)));
						continue;
					case ADD_CENTRIFUGE_RECIPE:
						ArrayList<ItemStack> output = new ArrayList<>();
						ArrayList<Integer> chance = new ArrayList<>();
						FluidStack fluid = null;

						if (nbt.hasKey(OUTPUT)) {
							NBTTagList list = nbt.getTagList(OUTPUT, 10);
							for (int i = 0; i < list.tagCount(); i++) {
								NBTTagCompound tag = list.getCompoundTagAt(i);
								output.add(new ItemStack(tag));
								if (tag.hasKey(CHANCE)) {
									chance.add(tag.getInteger(CHANCE));
								} else {
									chance.add(100);
								}
							}
						}
						if (nbt.hasKey(FLUID)) {
							fluid = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag(FLUID));
						}
						CentrifugeManager.addRecipe(nbt.getInteger(ENERGY), new ItemStack(nbt.getCompoundTag(INPUT)), output, chance, fluid);
						continue;
					case ADD_BREWER_RECIPE:
						BrewerManager.addRecipe(nbt.getInteger(ENERGY), new ItemStack(nbt.getCompoundTag(PRIMARY_INPUT)), FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag(SECONDARY_INPUT)), FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag(OUTPUT)));
						continue;
					case ADD_ENCHANTER_RECIPE:
						EnchanterManager.addRecipe(nbt.getInteger(ENERGY), new ItemStack(nbt.getCompoundTag(PRIMARY_INPUT)), new ItemStack(nbt.getCompoundTag(SECONDARY_INPUT)), new ItemStack(nbt.getCompoundTag(PRIMARY_OUTPUT)), nbt.getInteger(EXPERIENCE), Type.STANDARD);
						continue;

						/* REMOVE RECIPES */
					case REMOVE_FURNACE_RECIPE:
						FurnaceManager.removeRecipe(new ItemStack(nbt.getCompoundTag(INPUT)));
						continue;
					case REMOVE_PULVERIZER_RECIPE:
						PulverizerManager.removeRecipe(new ItemStack(nbt.getCompoundTag(INPUT)));
						continue;
					case REMOVE_SAWMILL_RECIPE:
						SawmillManager.removeRecipe(new ItemStack(nbt.getCompoundTag(INPUT)));
						continue;
					case REMOVE_SMELTER_RECIPE:
						SmelterManager.removeRecipe(new ItemStack(nbt.getCompoundTag(PRIMARY_INPUT)), new ItemStack(nbt.getCompoundTag(SECONDARY_INPUT)));
						continue;
					case REMOVE_INSOLATOR_RECIPE:
						InsolatorManager.removeRecipe(new ItemStack(nbt.getCompoundTag(PRIMARY_INPUT)), new ItemStack(nbt.getCompoundTag(SECONDARY_INPUT)));
						continue;
					case REMOVE_COMPACTOR_PRESS_RECIPE:
						CompactorManager.removeRecipe(new ItemStack(nbt.getCompoundTag(INPUT)), Mode.PLATE);
						continue;
					case REMOVE_COMPACTOR_MINT_RECIPE:
						CompactorManager.removeRecipe(new ItemStack(nbt.getCompoundTag(INPUT)), Mode.COIN);
						continue;
					case REMOVE_CRUCIBLE_RECIPE:
						CrucibleManager.removeRecipe(new ItemStack(nbt.getCompoundTag(INPUT)));
						continue;
					case REMOVE_REFINERY_RECIPE:
						RefineryManager.removeRecipe(FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag(INPUT)));
						continue;
					case REMOVE_TRANSPOSER_FILL_RECIPE:
						TransposerManager.removeFillRecipe(new ItemStack(nbt.getCompoundTag(INPUT)), FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag(FLUID)));
						continue;
					case REMOVE_TRANSPOSER_EXTRACT_RECIPE:
						TransposerManager.removeExtractRecipe(new ItemStack(nbt.getCompoundTag(INPUT)));
						continue;
					case REMOVE_CHARGER_RECIPE:
						ChargerManager.removeRecipe(new ItemStack(nbt.getCompoundTag(INPUT)));
						continue;
					case REMOVE_CENTRIFUGE_RECIPE:
						CentrifugeManager.removeRecipe(new ItemStack(nbt.getCompoundTag(INPUT)));
						continue;
					case REMOVE_BREWER_RECIPE:
						BrewerManager.removeRecipe(new ItemStack(nbt.getCompoundTag(INPUT)), FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag(FLUID)));
						continue;
					case REMOVE_ENCHANTER_RECIPE:
						EnchanterManager.removeRecipe(new ItemStack(nbt.getCompoundTag(PRIMARY_INPUT)), new ItemStack(nbt.getCompoundTag(SECONDARY_INPUT)));
						continue;

						/* FUELS */
					case ADD_STEAM_FUEL:
						SteamManager.addFuel(new ItemStack(nbt.getCompoundTag(INPUT)), nbt.getInteger(ENERGY));
						continue;
					case ADD_MAGMATIC_FUEL:
						MagmaticManager.addFuel(nbt.getString(FLUID_NAME).toLowerCase(Locale.ENGLISH), nbt.getInteger(ENERGY));
						continue;
					case ADD_COMPRESSION_FUEL:
						CompressionManager.addFuel(nbt.getString(FLUID_NAME).toLowerCase(Locale.ENGLISH), nbt.getInteger(ENERGY));
						continue;
					case ADD_REACTANT_FUEL:
						ReactantManager.addReaction(new ItemStack(nbt.getCompoundTag(INPUT)), FluidRegistry.getFluid(nbt.getString(FLUID_NAME).toLowerCase(Locale.ENGLISH)), nbt.getInteger(ENERGY));
					case ADD_ENERVATION_FUEL:
						EnervationManager.addFuel(new ItemStack(nbt.getCompoundTag(INPUT)), nbt.getInteger(ENERGY));
						continue;
					case ADD_NUMISMATIC_FUEL:
						NumismaticManager.addFuel(new ItemStack(nbt.getCompoundTag(INPUT)), nbt.getInteger(ENERGY));
						continue;

					case REMOVE_STEAM_FUEL:
						SteamManager.removeFuel(new ItemStack(nbt.getCompoundTag(INPUT)));
						continue;
					case REMOVE_MAGMATIC_FUEL:
						MagmaticManager.removeFuel(nbt.getString(FLUID_NAME).toLowerCase(Locale.ENGLISH));
						continue;
					case REMOVE_COMPRESSION_FUEL:
						CompressionManager.removeFuel(nbt.getString(FLUID_NAME).toLowerCase(Locale.ENGLISH));
						continue;
					case REMOVE_REACTANT_FUEL:
						ReactantManager.removeReaction(new ItemStack(nbt.getCompoundTag(INPUT)), FluidRegistry.getFluid(nbt.getString(FLUID_NAME).toLowerCase(Locale.ENGLISH)));
						continue;
					case REMOVE_ENERVATION_FUEL:
						EnervationManager.removeFuel(new ItemStack(nbt.getCompoundTag(INPUT)));
						continue;
					case REMOVE_NUMISMATIC_FUEL:
						NumismaticManager.removeFuel(new ItemStack(nbt.getCompoundTag(INPUT)));
						continue;

						/* COOLANT */
					case ADD_COOLANT:
						CoolantManager.addCoolant(nbt.getString(FLUID_NAME).toLowerCase(Locale.ENGLISH), nbt.getInteger(ENERGY), nbt.getInteger(FACTOR));
						continue;
					case REMOVE_COOLANT:
						CoolantManager.removeCoolant(nbt.getString(FLUID_NAME).toLowerCase(Locale.ENGLISH));
						continue;
				}
				ThermalExpansion.LOG.warn("Thermal Expansion received an invalid IMC from " + message.getSender() + "! Key was " + message.key);
			} catch (Exception e) {
				ThermalExpansion.LOG.warn("Thermal Expansion received a broken IMC from " + message.getSender() + "!", e);
			}
		}
	}

	/* IMC STRINGS */
	static final String ENERGY = "energy";
	static final String EXPERIENCE = "experience";
	static final String FLUID = "fluid";
	static final String FLUID_NAME = "fluidName";
	static final String REVERSIBLE = "reversible";
	static final String CHANCE = "chance";
	static final String FACTOR = "factor";

	static final String INPUT = "input";
	static final String OUTPUT = "output";
	static final String PRIMARY_INPUT = "primaryInput";
	static final String SECONDARY_INPUT = "secondaryInput";
	static final String PRIMARY_OUTPUT = "primaryOutput";
	static final String SECONDARY_OUTPUT = "secondaryOutput";

	public static final String ADD_FURNACE_RECIPE = "addfurnacerecipe";
	public static final String ADD_PULVERIZER_RECIPE = "addpulverizerrecipe";
	public static final String ADD_SAWMILL_RECIPE = "addsawmillrecipe";
	public static final String ADD_SMELTER_RECIPE = "addsmelterrecipe";
	public static final String ADD_INSOLATOR_RECIPE = "addinsolatorrecipe";
	public static final String ADD_COMPACTOR_PRESS_RECIPE = "addcompactorpressrecipe";
	public static final String ADD_COMPACTOR_COIN_RECIPE = "addcompactormintrecipe";
	public static final String ADD_CRUCIBLE_RECIPE = "addcruciblerecipe";
	public static final String ADD_REFINERY_RECIPE = "addrefineryrecipe";
	public static final String ADD_TRANSPOSER_FILL_RECIPE = "addtransposerfillrecipe";
	public static final String ADD_TRANSPOSER_EXTRACT_RECIPE = "addtransposerextractrecipe";
	public static final String ADD_CHARGER_RECIPE = "addchargerrecipe";
	public static final String ADD_CENTRIFUGE_RECIPE = "addcentrifugerecipe";
	public static final String ADD_BREWER_RECIPE = "addbrewerrecipe";
	public static final String ADD_ENCHANTER_RECIPE = "addenchanterrecipe";

	public static final String REMOVE_FURNACE_RECIPE = "removefurnacerecipe";
	public static final String REMOVE_PULVERIZER_RECIPE = "removepulverizerrecipe";
	public static final String REMOVE_SAWMILL_RECIPE = "removesawmillrecipe";
	public static final String REMOVE_SMELTER_RECIPE = "removesmelterrecipe";
	public static final String REMOVE_INSOLATOR_RECIPE = "removeinsolatorrecipe";
	public static final String REMOVE_COMPACTOR_PRESS_RECIPE = "removecompactorpressrecipe";
	public static final String REMOVE_COMPACTOR_MINT_RECIPE = "removecompactormintrecipe";
	public static final String REMOVE_CRUCIBLE_RECIPE = "removecruciblerecipe";
	public static final String REMOVE_REFINERY_RECIPE = "removerefineryrecipe";
	public static final String REMOVE_TRANSPOSER_FILL_RECIPE = "removetransposerfillrecipe";
	public static final String REMOVE_TRANSPOSER_EXTRACT_RECIPE = "removetransposerextractrecipe";
	public static final String REMOVE_CHARGER_RECIPE = "removechargerrecipe";
	public static final String REMOVE_CENTRIFUGE_RECIPE = "removecentrifugerecipe";
	public static final String REMOVE_BREWER_RECIPE = "removebrewerrecipe";
	public static final String REMOVE_ENCHANTER_RECIPE = "removeenchanterrecipe";

	public static final String ADD_STEAM_FUEL = "addsteamfuel";
	public static final String ADD_MAGMATIC_FUEL = "addmagmaticfuel";
	public static final String ADD_COMPRESSION_FUEL = "addcompressionfuel";
	public static final String ADD_REACTANT_FUEL = "addreactantfuel";
	public static final String ADD_ENERVATION_FUEL = "addenervationfuel";
	public static final String ADD_NUMISMATIC_FUEL = "addnumismaticfuel";

	public static final String REMOVE_STEAM_FUEL = "removesteamfuel";
	public static final String REMOVE_MAGMATIC_FUEL = "removemagmaticfuel";
	public static final String REMOVE_COMPRESSION_FUEL = "removecompressionfuel";
	public static final String REMOVE_REACTANT_FUEL = "removereactantfuel";
	public static final String REMOVE_ENERVATION_FUEL = "removeenervationfuel";
	public static final String REMOVE_NUMISMATIC_FUEL = "removenumismaticfuel";

	public static final String ADD_COOLANT = "addcoolant";
	public static final String REMOVE_COOLANT = "removecoolant";

}
