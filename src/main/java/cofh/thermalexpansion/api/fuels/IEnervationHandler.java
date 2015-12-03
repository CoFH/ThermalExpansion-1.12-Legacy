package cofh.thermalexpansion.api.fuels;

import net.minecraft.item.ItemStack;

public interface IEnervationHandler {

	public boolean addFuel(ItemStack input, int energy);

	public boolean removeFuel(ItemStack input);

}
