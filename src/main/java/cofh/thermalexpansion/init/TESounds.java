package cofh.thermalexpansion.init;

import cofh.thermalexpansion.ThermalExpansion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TESounds {

	private TESounds() {

	}

	static {
		MACHINE_FURNACE = getRegisteredSoundEvent("machine_furnace");
		MACHINE_PULVERIZER = getRegisteredSoundEvent("machine_pulverizer");
		MACHINE_SAWMILL = getRegisteredSoundEvent("machine_sawmill");
		MACHINE_SMELTER = getRegisteredSoundEvent("machine_smelter");
		MACHINE_CRUCIBLE = getRegisteredSoundEvent("machine_crucible");
		MACHINE_TRANSPOSER = getRegisteredSoundEvent("machine_transposer");

		DEVICE_WATER_GEN = getRegisteredSoundEvent("device_water_gen");
	}

	private static SoundEvent getRegisteredSoundEvent(String id) {

		SoundEvent sound = new SoundEvent(new ResourceLocation(ThermalExpansion.MOD_ID + ":" + id));
		sound.setRegistryName(id);
		GameRegistry.register(sound);
		return sound;
	}

	public static final SoundEvent MACHINE_FURNACE;
	public static final SoundEvent MACHINE_PULVERIZER;
	public static final SoundEvent MACHINE_SAWMILL;
	public static final SoundEvent MACHINE_SMELTER;
	public static final SoundEvent MACHINE_CRUCIBLE;
	public static final SoundEvent MACHINE_TRANSPOSER;

	public static final SoundEvent DEVICE_WATER_GEN;

}
