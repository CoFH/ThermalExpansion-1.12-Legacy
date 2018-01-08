package cofh.thermalexpansion.plugins.jei.item;

import mezz.jei.api.ISubtypeRegistry;
import net.minecraft.item.ItemStack;

public class MorbSubtypeInterpreter implements ISubtypeRegistry.ISubtypeInterpreter {

	public static final MorbSubtypeInterpreter INSTANCE = new MorbSubtypeInterpreter();

	private MorbSubtypeInterpreter() {

	}

	@Override
	public String apply(ItemStack stack) {

		if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("id")) {
			return ISubtypeRegistry.ISubtypeInterpreter.NONE;
		}
		return stack.getTagCompound().getString("id");
	}
}
