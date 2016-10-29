package cofh.thermalexpansion.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

/**
 * Created by brandon3055 on 29/10/2016.
 *
 * I added this with the intention of using it to update the machine sounds but didn't end up using it.
 * But it may still come in handy for something else.
 */
public class TESounds {

    public static final SoundEvent machineFurnace;
    public static final SoundEvent machinePulverizer;
    public static final SoundEvent machineSawmill;
    public static final SoundEvent machineSmelter;
    public static final SoundEvent machineCrucible;
    public static final SoundEvent machineTransposer;
    public static final SoundEvent machineAccumulator;

    static {
        machineFurnace = getRegisteredSoundEvent("thermalexpansion:blockMachineFurnace");
        machinePulverizer = getRegisteredSoundEvent("thermalexpansion:blockMachinePulverizer");
        machineSawmill = getRegisteredSoundEvent("thermalexpansion:blockMachineSawmill");
        machineSmelter = getRegisteredSoundEvent("thermalexpansion:blockMachineSmelter");
        machineCrucible = getRegisteredSoundEvent("thermalexpansion:blockMachineCrucible");
        machineTransposer = getRegisteredSoundEvent("thermalexpansion:blockMachineTransposer");
        machineAccumulator = getRegisteredSoundEvent("thermalexpansion:blockMachineAccumulator");
    }

    private static SoundEvent getRegisteredSoundEvent(String id) {
        return new SoundEvent(new ResourceLocation(id));
    }
}
