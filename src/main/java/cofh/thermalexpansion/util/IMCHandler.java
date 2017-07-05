package cofh.thermalexpansion.util;

import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.dynamo.CompressionManager;
import cofh.thermalexpansion.util.managers.dynamo.MagmaticManager;
import cofh.thermalexpansion.util.managers.machine.*;
import cofh.thermalexpansion.util.managers.machine.CompactorManager.Mode;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

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
						FurnaceManager.addRecipe(nbt.getInteger("energy"), new ItemStack(nbt.getCompoundTag("input")), new ItemStack(nbt.getCompoundTag("output")));
						continue;
					case ADD_PULVERIZER_RECIPE:
						if (nbt.hasKey("secondaryChance")) {
							PulverizerManager.addRecipe(nbt.getInteger("energy"), new ItemStack(nbt.getCompoundTag("input")), new ItemStack(nbt.getCompoundTag("primaryOutput")), new ItemStack(nbt.getCompoundTag("secondaryOutput")), nbt.getInteger("secondaryChance"));
						} else if (nbt.hasKey("secondaryOutput")) {
							PulverizerManager.addRecipe(nbt.getInteger("energy"), new ItemStack(nbt.getCompoundTag("input")), new ItemStack(nbt.getCompoundTag("primaryOutput")), new ItemStack(nbt.getCompoundTag("secondaryOutput")));
						} else {
							PulverizerManager.addRecipe(nbt.getInteger("energy"), new ItemStack(nbt.getCompoundTag("input")), new ItemStack(nbt.getCompoundTag("primaryOutput")));
						}
						continue;
					case ADD_SAWMILL_RECIPE:
						if (nbt.hasKey("secondaryChance")) {
							SawmillManager.addRecipe(nbt.getInteger("energy"), new ItemStack(nbt.getCompoundTag("input")), new ItemStack(nbt.getCompoundTag("primaryOutput")), new ItemStack(nbt.getCompoundTag("secondaryOutput")), nbt.getInteger("secondaryChance"));
						} else if (nbt.hasKey("secondaryOutput")) {
							SawmillManager.addRecipe(nbt.getInteger("energy"), new ItemStack(nbt.getCompoundTag("input")), new ItemStack(nbt.getCompoundTag("primaryOutput")), new ItemStack(nbt.getCompoundTag("secondaryOutput")));
						} else {
							SawmillManager.addRecipe(nbt.getInteger("energy"), new ItemStack(nbt.getCompoundTag("input")), new ItemStack(nbt.getCompoundTag("primaryOutput")));
						}
						continue;
					case ADD_SMELTER_RECIPE:
						if (nbt.hasKey("secondaryChance")) {
							SmelterManager.addRecipe(nbt.getInteger("energy"), new ItemStack(nbt.getCompoundTag("primaryInput")), new ItemStack(nbt.getCompoundTag("secondaryInput")), new ItemStack(nbt.getCompoundTag("primaryOutput")), new ItemStack(nbt.getCompoundTag("secondaryOutput")), nbt.getInteger("secondaryChance"));
						} else if (nbt.hasKey("secondaryOutput")) {
							SmelterManager.addRecipe(nbt.getInteger("energy"), new ItemStack(nbt.getCompoundTag("primaryInput")), new ItemStack(nbt.getCompoundTag("secondaryInput")), new ItemStack(nbt.getCompoundTag("primaryOutput")), new ItemStack(nbt.getCompoundTag("secondaryOutput")));
						} else {
							SmelterManager.addRecipe(nbt.getInteger("energy"), new ItemStack(nbt.getCompoundTag("primaryInput")), new ItemStack(nbt.getCompoundTag("secondaryInput")), new ItemStack(nbt.getCompoundTag("primaryOutput")));
						}
						continue;
					case ADD_INSOLATOR_RECIPE:
						if (nbt.hasKey("secondaryChance")) {
							InsolatorManager.addRecipe(nbt.getInteger("energy"), new ItemStack(nbt.getCompoundTag("primaryInput")), new ItemStack(nbt.getCompoundTag("secondaryInput")), new ItemStack(nbt.getCompoundTag("primaryOutput")), new ItemStack(nbt.getCompoundTag("secondaryOutput")), nbt.getInteger("secondaryChance"));
						} else if (nbt.hasKey("secondaryOutput")) {
							InsolatorManager.addRecipe(nbt.getInteger("energy"), new ItemStack(nbt.getCompoundTag("primaryInput")), new ItemStack(nbt.getCompoundTag("secondaryInput")), new ItemStack(nbt.getCompoundTag("primaryOutput")), new ItemStack(nbt.getCompoundTag("secondaryOutput")));
						} else {
							InsolatorManager.addRecipe(nbt.getInteger("energy"), new ItemStack(nbt.getCompoundTag("primaryInput")), new ItemStack(nbt.getCompoundTag("secondaryInput")), new ItemStack(nbt.getCompoundTag("primaryOutput")));
						}
						continue;
					case ADD_COMPACTOR_PRESS_RECIPE:
						CompactorManager.addRecipe(nbt.getInteger("energy"), new ItemStack(nbt.getCompoundTag("input")), new ItemStack(nbt.getCompoundTag("output")), Mode.PRESS);
						continue;
					case ADD_COMPACTOR_STORAGE_RECIPE:
						CompactorManager.addRecipe(nbt.getInteger("energy"), new ItemStack(nbt.getCompoundTag("input")), new ItemStack(nbt.getCompoundTag("output")), Mode.STORAGE);
						continue;
					case ADD_COMPACTOR_MINT_RECIPE:
						CompactorManager.addRecipe(nbt.getInteger("energy"), new ItemStack(nbt.getCompoundTag("input")), new ItemStack(nbt.getCompoundTag("output")), Mode.MINT);
						continue;
					case ADD_CRUCIBLE_RECIPE:
						CrucibleManager.addRecipe(nbt.getInteger("energy"), new ItemStack(nbt.getCompoundTag("input")), FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("output")));
						continue;
					case ADD_REFINERY_RECIPE:
						RefineryManager.addRecipe(nbt.getInteger("energy"), FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("input")), FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("output")), new ItemStack(nbt.getCompoundTag("outputItem")));
						continue;
					case ADD_TRANSPOSER_FILL_RECIPE:
						TransposerManager.addFillRecipe(nbt.getInteger("energy"), new ItemStack(nbt.getCompoundTag("input")), new ItemStack(nbt.getCompoundTag("output")), FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("fluid")), nbt.getBoolean("reversible"));
						continue;
					case ADD_TRANSPOSER_EXTRACT_RECIPE:
						TransposerManager.addExtractRecipe(nbt.getInteger("energy"), new ItemStack(nbt.getCompoundTag("input")), new ItemStack(nbt.getCompoundTag("output")), FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("fluid")), nbt.getInteger("chance"), nbt.getBoolean("reversible"));
						continue;
					case ADD_CHARGER_RECIPE:
						ChargerManager.addRecipe(nbt.getInteger("energy"), new ItemStack(nbt.getCompoundTag("input")), new ItemStack(nbt.getCompoundTag("output")));
						continue;
					case ADD_CENTRIFUGE_RECIPE:
						// TODO
						continue;

					/* REMOVE RECIPES */
					case REMOVE_FURNACE_RECIPE:
						FurnaceManager.removeRecipe(new ItemStack(nbt.getCompoundTag("input")));
						continue;
					case REMOVE_PULVERIZER_RECIPE:
						PulverizerManager.removeRecipe(new ItemStack(nbt.getCompoundTag("input")));
						continue;
					case REMOVE_SAWMILL_RECIPE:
						SawmillManager.removeRecipe(new ItemStack(nbt.getCompoundTag("input")));
						continue;
					case REMOVE_SMELTER_RECIPE:
						SmelterManager.removeRecipe(new ItemStack(nbt.getCompoundTag("primaryInput")), new ItemStack(nbt.getCompoundTag("secondaryInput")));
						continue;
					case REMOVE_INSOLATOR_RECIPE:
						InsolatorManager.removeRecipe(new ItemStack(nbt.getCompoundTag("primaryInput")), new ItemStack(nbt.getCompoundTag("secondaryInput")));
						continue;
					case REMOVE_COMPACTOR_PRESS_RECIPE:
						CompactorManager.removeRecipe(new ItemStack(nbt.getCompoundTag("input")), Mode.PRESS);
						continue;
					case REMOVE_COMPACTOR_STORAGE_RECIPE:
						CompactorManager.removeRecipe(new ItemStack(nbt.getCompoundTag("input")), Mode.STORAGE);
						continue;
					case REMOVE_COMPACTOR_MINT_RECIPE:
						CompactorManager.removeRecipe(new ItemStack(nbt.getCompoundTag("input")), Mode.MINT);
						continue;
					case REMOVE_CRUCIBLE_RECIPE:
						CrucibleManager.removeRecipe(new ItemStack(nbt.getCompoundTag("input")));
						continue;
					case REMOVE_REFINERY_RECIPE:
						RefineryManager.removeRecipe(FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("input")));
						continue;
					case REMOVE_TRANSPOSER_FILL_RECIPE:
						TransposerManager.removeFillRecipe(new ItemStack(nbt.getCompoundTag("input")), FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("fluid")));
						continue;
					case REMOVE_TRANSPOSER_EXTRACT_RECIPE:
						TransposerManager.removeExtractRecipe(new ItemStack(nbt.getCompoundTag("input")));
						continue;
					case REMOVE_CHARGER_RECIPE:
						ChargerManager.removeRecipe(new ItemStack(nbt.getCompoundTag("input")));
						continue;
					case REMOVE_CENTRIFUGE_RECIPE:
						CentrifugeManager.removeRecipe(new ItemStack(nbt.getCompoundTag("input")));
						continue;

					/* FUELS */
					case ADD_MAGMATIC_FUEL:
						MagmaticManager.addFuel(nbt.getString("fluidName").toLowerCase(Locale.ENGLISH), nbt.getInteger("energy"));
						continue;
					case ADD_COMPRESSION_FUEL:
						CompressionManager.addFuel(nbt.getString("fluidName").toLowerCase(Locale.ENGLISH), nbt.getInteger("energy"));
						continue;
				}
				ThermalExpansion.LOG.warn("Thermal Expansion received an invalid IMC from " + message.getSender() + "! Key was " + message.key);
			} catch (Exception e) {
				ThermalExpansion.LOG.warn("Thermal Expansion received a broken IMC from " + message.getSender() + "!");
				e.printStackTrace();
			}
		}
	}

	/* IMC STRINGS */
	public static final String ADD_FURNACE_RECIPE = "addfurnacerecipe";
	public static final String ADD_PULVERIZER_RECIPE = "addpulverizerrecipe";
	public static final String ADD_SAWMILL_RECIPE = "addsawmillrecipe";
	public static final String ADD_SMELTER_RECIPE = "addsmelterrecipe";
	public static final String ADD_INSOLATOR_RECIPE = "addinsolatorrecipe";

	public static final String ADD_COMPACTOR_PRESS_RECIPE = "addcompactorpressrecipe";
	public static final String ADD_COMPACTOR_STORAGE_RECIPE = "addcompactorstoragerecipe";
	public static final String ADD_COMPACTOR_MINT_RECIPE = "addcompactormintrecipe";

	public static final String ADD_CRUCIBLE_RECIPE = "addcruciblerecipe";
	public static final String ADD_REFINERY_RECIPE = "addrefineryrecipe";

	public static final String ADD_TRANSPOSER_FILL_RECIPE = "addtransposerfillrecipe";
	public static final String ADD_TRANSPOSER_EXTRACT_RECIPE = "addtransposerextractrecipe";

	public static final String ADD_CHARGER_RECIPE = "addchargerrecipe";

	public static final String ADD_CENTRIFUGE_RECIPE = "addcentrifugerecipe";

	public static final String REMOVE_FURNACE_RECIPE = "removefurnacerecipe";
	public static final String REMOVE_PULVERIZER_RECIPE = "removepulverizerrecipe";
	public static final String REMOVE_SAWMILL_RECIPE = "removesawmillrecipe";
	public static final String REMOVE_SMELTER_RECIPE = "removesmelterrecipe";
	public static final String REMOVE_INSOLATOR_RECIPE = "removeinsolatorrecipe";

	public static final String REMOVE_COMPACTOR_PRESS_RECIPE = "removecompactorpressrecipe";
	public static final String REMOVE_COMPACTOR_STORAGE_RECIPE = "removecompactorstoragerecipe";
	public static final String REMOVE_COMPACTOR_MINT_RECIPE = "removecompactormintrecipe";

	public static final String REMOVE_CRUCIBLE_RECIPE = "removecruciblerecipe";
	public static final String REMOVE_REFINERY_RECIPE = "removerefineryrecipe";

	public static final String REMOVE_TRANSPOSER_FILL_RECIPE = "removetransposerfillrecipe";
	public static final String REMOVE_TRANSPOSER_EXTRACT_RECIPE = "removetransposerextractrecipe";

	public static final String REMOVE_CHARGER_RECIPE = "removechargerrecipe";

	public static final String REMOVE_CENTRIFUGE_RECIPE = "removecentrifugerecipe";

	public static final String ADD_MAGMATIC_FUEL = "addmagmaticfuel";
	public static final String ADD_COMPRESSION_FUEL = "addcompressionfuel";
	public static final String ADD_REACTANT_FUEL = "addreactantfuel";

	public static final String ADD_COOLANT = "addcoolant";

}
