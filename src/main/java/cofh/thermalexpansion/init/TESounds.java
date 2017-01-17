package cofh.thermalexpansion.init;

import cofh.thermalexpansion.ThermalExpansion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class TESounds {

	private TESounds() {

	}

	static {
		MACHINE_FURNACE = getRegisteredSoundEvent("blockMachineFurnace");
		MACHINE_PULVERIZER = getRegisteredSoundEvent("blockMachinePulverizer");
		MACHINE_SAWMILL = getRegisteredSoundEvent("blockMachineSawmill");
		MACHINE_SMELTER = getRegisteredSoundEvent("blockMachineSmelter");
		MACHINE_CRUCIBLE = getRegisteredSoundEvent("blockMachineCrucible");
		MACHINE_TRANSPOSER = getRegisteredSoundEvent("blockMachineTransposer");
		MACHINE_ACCUMULATOR = getRegisteredSoundEvent("blockMachineAccumulator");
	}

	private static SoundEvent getRegisteredSoundEvent(String id) {

		return new SoundEvent(new ResourceLocation(ThermalExpansion.MOD_ID + ":" + id));
	}

	public static final SoundEvent MACHINE_FURNACE;
	public static final SoundEvent MACHINE_PULVERIZER;
	public static final SoundEvent MACHINE_SAWMILL;
	public static final SoundEvent MACHINE_SMELTER;
	public static final SoundEvent MACHINE_CRUCIBLE;
	public static final SoundEvent MACHINE_TRANSPOSER;
	public static final SoundEvent MACHINE_ACCUMULATOR;

}
