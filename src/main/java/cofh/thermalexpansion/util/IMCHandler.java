package cofh.thermalexpansion.util;

import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.crafting.ChargerManager;
import cofh.thermalexpansion.util.crafting.CrucibleManager;
import cofh.thermalexpansion.util.crafting.FurnaceManager;
import cofh.thermalexpansion.util.crafting.InsolatorManager;
import cofh.thermalexpansion.util.crafting.PulverizerManager;
import cofh.thermalexpansion.util.crafting.SawmillManager;
import cofh.thermalexpansion.util.crafting.SmelterManager;
import cofh.thermalexpansion.util.crafting.TransposerManager;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;

import java.util.List;
import java.util.Locale;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

public class IMCHandler {

	public static IMCHandler instance = new IMCHandler();

	public void handleIMC(List<IMCMessage> messages) {

		NBTTagCompound theNBT;
		for (IMCMessage theMessage : messages) {
			try {
				if (theMessage.isNBTMessage()) {
					theNBT = theMessage.getNBTValue();

					if (theMessage.key.equalsIgnoreCase("FurnaceRecipe")) {
						FurnaceManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")), ItemStack
								.loadItemStackFromNBT(theNBT.getCompoundTag("output")), theNBT.hasKey("overwrite") ? theNBT.getBoolean("overwrite") : false);
						continue;
					}

					else if (theMessage.key.equalsIgnoreCase("RemoveFurnaceRecipe")) {
						FurnaceManager.removeRecipe(ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")));
						continue;
					}

					else if (theMessage.key.equalsIgnoreCase("PulverizerRecipe")) {
						if (theNBT.hasKey("secondaryChance")) {
							PulverizerManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryOutput")), theNBT.getInteger("secondaryChance"),
									theNBT.hasKey("overwrite") ? theNBT.getBoolean("overwrite") : false);
						} else if (theNBT.hasKey("secondaryOutput")) {
							PulverizerManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryOutput")),
									theNBT.hasKey("overwrite") ? theNBT.getBoolean("overwrite") : false);
						} else {
							PulverizerManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")),
									theNBT.hasKey("overwrite") ? theNBT.getBoolean("overwrite") : false);
						}
						continue;
					}

					else if (theMessage.key.equalsIgnoreCase("RemovePulverizerRecipe")) {
						PulverizerManager.removeRecipe(ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")));
						continue;
					}

					else if (theMessage.key.equalsIgnoreCase("SawmillRecipe")) {
						if (theNBT.hasKey("secondaryChance")) {
							SawmillManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryOutput")), theNBT.getInteger("secondaryChance"),
									theNBT.hasKey("overwrite") ? theNBT.getBoolean("overwrite") : false);
						} else if (theNBT.hasKey("secondaryOutput")) {
							SawmillManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryOutput")),
									theNBT.hasKey("overwrite") ? theNBT.getBoolean("overwrite") : false);
						} else {
							SawmillManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")),
									theNBT.hasKey("overwrite") ? theNBT.getBoolean("overwrite") : false);
						}
						continue;
					}

					else if (theMessage.key.equalsIgnoreCase("RemoveSawmillRecipe")) {
						SawmillManager.removeRecipe(ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")));
						continue;
					}

					else if (theMessage.key.equalsIgnoreCase("SmelterRecipe")) {
						if (theNBT.hasKey("secondaryChance")) {
							SmelterManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryInput")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryInput")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryOutput")), theNBT.getInteger("secondaryChance"),
									theNBT.hasKey("overwrite") ? theNBT.getBoolean("overwrite") : false);
						} else if (theNBT.hasKey("secondaryOutput")) {
							SmelterManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryInput")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryInput")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryOutput")),
									theNBT.hasKey("overwrite") ? theNBT.getBoolean("overwrite") : false);
						} else {
							SmelterManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryInput")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryInput")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")),
									theNBT.hasKey("overwrite") ? theNBT.getBoolean("overwrite") : false);
						}
						continue;
					}

					else if (theMessage.key.equalsIgnoreCase("RemoveSmelterRecipe")) {
						SmelterManager.removeRecipe(ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryInput")),
								ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryInput")));
						continue;
					}

					else if (theMessage.key.equalsIgnoreCase("SmelterBlastOreType")) {
						if (theNBT.hasKey("oreType")) {
							SmelterManager.addBlastOreName(theNBT.getString("oreType"));
						}
						continue;
					}

					else if (theMessage.key.equalsIgnoreCase("CrucibleRecipe")) {
						CrucibleManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")), FluidStack
								.loadFluidStackFromNBT(theNBT.getCompoundTag("output")), theNBT.hasKey("overwrite") ? theNBT.getBoolean("overwrite") : false);
						continue;
					}

					else if (theMessage.key.equalsIgnoreCase("RemoveCrucibleRecipe")) {
						CrucibleManager.removeRecipe(ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")));
						continue;
					}

					else if (theMessage.key.equalsIgnoreCase("TransposerFillRecipe")) {
						TransposerManager.addFillRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")), ItemStack
								.loadItemStackFromNBT(theNBT.getCompoundTag("output")), FluidStack.loadFluidStackFromNBT(theNBT.getCompoundTag("fluid")),
								theNBT.getBoolean("reversible"), theNBT.hasKey("overwrite") ? theNBT.getBoolean("overwrite") : false);
						continue;
					}

					else if (theMessage.key.equalsIgnoreCase("RemoveTransposerFillRecipe")) {
						TransposerManager.removeFillRecipe(ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")),
								FluidStack.loadFluidStackFromNBT(theNBT.getCompoundTag("fluid")));
						continue;
					}

					else if (theMessage.key.equalsIgnoreCase("TransposerExtractRecipe")) {
						TransposerManager.addExtractionRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")),
								ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("output")),
								FluidStack.loadFluidStackFromNBT(theNBT.getCompoundTag("fluid")), theNBT.getInteger("chance"), theNBT.getBoolean("reversible"),
								theNBT.hasKey("overwrite") ? theNBT.getBoolean("overwrite") : false);
						continue;
					}

					else if (theMessage.key.equalsIgnoreCase("RemoveTransposerExtractRecipe")) {
						TransposerManager.removeExtractionRecipe(ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")));
						continue;
					}

					else if (theMessage.key.equalsIgnoreCase("ChargerRecipe")) {
						ChargerManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")), ItemStack
								.loadItemStackFromNBT(theNBT.getCompoundTag("output")), theNBT.hasKey("overwrite") ? theNBT.getBoolean("overwrite") : false);
						continue;
					}

					else if (theMessage.key.equalsIgnoreCase("RemoveChargerRecipe")) {
						ChargerManager.removeRecipe(ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")));
						continue;
					}

					else if (theMessage.key.equalsIgnoreCase("InsolatorRecipe")) {
						if (theNBT.hasKey("secondaryChance")) {
							InsolatorManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryInput")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryInput")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryOutput")), theNBT.getInteger("secondaryChance"),
									theNBT.hasKey("overwrite") ? theNBT.getBoolean("overwrite") : false);
						} else if (theNBT.hasKey("secondaryOutput")) {
							InsolatorManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryInput")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryInput")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryOutput")),
									theNBT.hasKey("overwrite") ? theNBT.getBoolean("overwrite") : false);
						} else {
							InsolatorManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryInput")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryInput")),
									ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")),
									theNBT.hasKey("overwrite") ? theNBT.getBoolean("overwrite") : false);
						}
						continue;
					}

					else if (theMessage.key.equalsIgnoreCase("RemoveInsolatorRecipe")) {
						InsolatorManager.removeRecipe(ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryInput")),
								ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryInput")));
						continue;
					}

					else if (theMessage.key.equalsIgnoreCase("MagmaticFuel")) {
						String fluidName = theNBT.getString("fluidName").toLowerCase(Locale.ENGLISH);
						int energy = theNBT.getInteger("energy");

						if (FuelHandler.registerMagmaticFuel(fluidName, energy)) {
							FuelHandler.configFuels.get("Fuels.Magmatic", fluidName, energy);
						}
						continue;
					}

					else if (theMessage.key.equalsIgnoreCase("CompressionFuel")) {
						String fluidName = theNBT.getString("fluidName").toLowerCase(Locale.ENGLISH);
						int energy = theNBT.getInteger("energy");

						if (FuelHandler.registerCompressionFuel(fluidName, energy)) {
							FuelHandler.configFuels.get("Fuels.Compression", fluidName, energy);
						}
						continue;
					}

					else if (theMessage.key.equalsIgnoreCase("ReactantFuel")) {
						String fluidName = theNBT.getString("fluidName").toLowerCase(Locale.ENGLISH);
						int energy = theNBT.getInteger("energy");

						if (FuelHandler.registerCompressionFuel(fluidName, energy)) {
							FuelHandler.configFuels.get("Fuels.Reactant", fluidName, energy);
						}
						continue;
					}

					else if (theMessage.key.equalsIgnoreCase("Coolant")) {
						String fluidName = theNBT.getString("fluidName").toLowerCase(Locale.ENGLISH);
						int energy = theNBT.getInteger("energy");

						if (FuelHandler.registerCoolant(fluidName, energy)) {
							FuelHandler.configFuels.get("Coolants", fluidName, energy);
						}
						continue;
					}
					ThermalExpansion.log.warn("ThermalExpansion received an invalid IMC from " + theMessage.getSender() + "! Key was " + theMessage.key);
				}
			} catch (Exception e) {
				ThermalExpansion.log.warn("ThermalExpansion received a broken IMC from " + theMessage.getSender() + "!");
				e.printStackTrace();
			}
		}
	}

}
