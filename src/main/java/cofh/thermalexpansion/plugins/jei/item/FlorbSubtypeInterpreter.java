package cofh.thermalexpansion.plugins.jei.item;

import mezz.jei.api.ISubtypeRegistry;
import net.minecraft.item.ItemStack;

public class FlorbSubtypeInterpreter implements ISubtypeRegistry.ISubtypeInterpreter {

	public static final FlorbSubtypeInterpreter INSTANCE = new FlorbSubtypeInterpreter();

	private FlorbSubtypeInterpreter() {

	}

	@Override
	public String apply(ItemStack stack) {

		if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("Fluid")) {
			return ISubtypeRegistry.ISubtypeInterpreter.NONE;
		}
		return stack.getTagCompound().getString("Fluid");
	}
}
