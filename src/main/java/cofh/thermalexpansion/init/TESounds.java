package cofh.thermalexpansion.init;

import cofh.thermalexpansion.ThermalExpansion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class TESounds {

	public static final TESounds INSTANCE = new TESounds();

	private TESounds() {

	}

	public static void preInit() {

		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}

	/* EVENT HANDLING */
	@SubscribeEvent
	public void registerSounds(RegistryEvent.Register<SoundEvent> event) {

		machineFurnace = registerSoundEvent("machine_furnace");
		machinePulverizer = registerSoundEvent("machine_pulverizer");
		machineSawmill = registerSoundEvent("machine_sawmill");
		machineSmelter = registerSoundEvent("machine_smelter");
		machineCrucible = registerSoundEvent("machine_crucible");
		machineTransposer = registerSoundEvent("machine_transposer");

		deviceWaterGen = registerSoundEvent("device_water_gen");
	}

	private static SoundEvent registerSoundEvent(String id) {

		SoundEvent sound = new SoundEvent(new ResourceLocation(ThermalExpansion.MOD_ID + ":" + id));
		sound.setRegistryName(id);
		ForgeRegistries.SOUND_EVENTS.register(sound);
		return sound;
	}

	public static SoundEvent machineFurnace;
	public static SoundEvent machinePulverizer;
	public static SoundEvent machineSawmill;
	public static SoundEvent machineSmelter;
	public static SoundEvent machineCrucible;
	public static SoundEvent machineTransposer;

	public static SoundEvent deviceWaterGen;

}
