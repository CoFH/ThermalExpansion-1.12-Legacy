package cofh.thermalexpansion.api.fuels;

import net.minecraft.item.ItemStack;

public interface IReactantHandler {

	public boolean addFuel(String name, int energy);

	public boolean addReactant(ItemStack input, int energy);

	public boolean removeFuel(String name);

	public boolean removeReactant(ItemStack input);

}
