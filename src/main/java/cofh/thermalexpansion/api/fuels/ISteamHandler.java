package cofh.thermalexpansion.api.fuels;

import net.minecraft.item.ItemStack;

public interface ISteamHandler {

	boolean addFuel(ItemStack input, int energy);

	boolean removeFuel(ItemStack input);

}
