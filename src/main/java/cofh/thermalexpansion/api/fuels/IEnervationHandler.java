package cofh.thermalexpansion.api.fuels;

import net.minecraft.item.ItemStack;

public interface IEnervationHandler {

	boolean addFuel(ItemStack input, int energy);

	boolean removeFuel(ItemStack input);

}
