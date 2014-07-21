package thermalexpansion.util;

import cofh.util.RedstoneControlHelper;
import cofh.util.StringHelper;

import java.util.List;

import net.minecraft.item.ItemStack;

public class RedprintHelper {

	private RedprintHelper() {

	}

	public static void addRedprintInformation(ItemStack stack, List<String> list) {

		if (stack.stackTagCompound == null) {
			list.add(StringHelper.getActivationText("info.thermalexpansion.diagram.1"));
			list.add(StringHelper.getInfoText("info.cofh.blank"));
			return;
		}
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		list.add(StringHelper.getDeactivationText("info.thermalexpansion.diagram.0"));
		list.add(StringHelper.getActivationText("info.thermalexpansion.diagram.2"));
		RedstoneControlHelper.addRSControlInformation(stack, list);
	}

	public static boolean hasName(ItemStack stack) {

		return stack.stackTagCompound == null ? false : stack.stackTagCompound.hasKey("Name");
	}

	public static String getName(ItemStack stack) {

		if (stack.stackTagCompound == null) {
			return "";
		}
		return ": " + StringHelper.localize(stack.stackTagCompound.getString("Name"));
	}

}
