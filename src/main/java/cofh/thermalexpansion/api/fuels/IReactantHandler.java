package cofh.thermalexpansion.api.fuels;

import net.minecraft.item.ItemStack;

public interface IReactantHandler {

	boolean addFuel(String name, int energy);

	boolean addReactant(ItemStack input, int energy);

	boolean removeFuel(String name);

	boolean removeReactant(ItemStack input);

}
