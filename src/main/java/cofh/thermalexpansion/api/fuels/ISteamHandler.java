package cofh.thermalexpansion.api.fuels;

import net.minecraft.item.ItemStack;

public interface ISteamHandler {

	public boolean addFuel(ItemStack input, int energy);

	public boolean removeFuel(ItemStack input);

}
