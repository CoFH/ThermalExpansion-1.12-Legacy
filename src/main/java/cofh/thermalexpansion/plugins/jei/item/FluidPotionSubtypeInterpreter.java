//package cofh.thermalexpansion.plugins.jei.item;
//
//import mezz.jei.api.ISubtypeRegistry;
//import net.minecraft.potion.PotionEffect;
//import net.minecraft.potion.PotionType;
//import net.minecraft.potion.PotionUtils;
//import net.minecraftforge.fluids.FluidStack;
//
//import java.util.List;
//
//public class FluidPotionSubtypeInterpreter implements ISubtypeRegistry.ISubtypeInterpreter {
//
//	public static final FluidPotionSubtypeInterpreter INSTANCE = new FluidPotionSubtypeInterpreter();
//
//	private FluidPotionSubtypeInterpreter() {
//
//	}
//
//	@Override
//	public String apply(FluidStack stack) {
//
//		if (stack.tag == null) {
//			return ISubtypeRegistry.ISubtypeInterpreter.NONE;
//		}
//		PotionType potionType = PotionUtils.getPotionTypeFromNBT(stack.tag);
//		String potionTypeString = potionType.getNamePrefixed("");
//		StringBuilder stringBuilder = new StringBuilder(potionTypeString);
//		List<PotionEffect> effects = PotionUtils.getEffectsFromTag(stack.tag);
//		for (PotionEffect effect : effects) {
//			stringBuilder.append(";").append(effect);
//		}
//		return stringBuilder.toString();
//	}
//}
