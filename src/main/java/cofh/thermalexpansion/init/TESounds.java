package cofh.thermalexpansion.init;

import cofh.thermalexpansion.ThermalExpansion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class TESounds {

	private TESounds() {

	}

	public static void initialize() {

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
		ForgeRegistries.SOUND_EVENTS.register(sound);
		return sound;
	}

	public static SoundEvent MACHINE_FURNACE;
	public static SoundEvent MACHINE_PULVERIZER;
	public static SoundEvent MACHINE_SAWMILL;
	public static SoundEvent MACHINE_SMELTER;
	public static SoundEvent MACHINE_CRUCIBLE;
	public static SoundEvent MACHINE_TRANSPOSER;

	public static SoundEvent DEVICE_WATER_GEN;

}
