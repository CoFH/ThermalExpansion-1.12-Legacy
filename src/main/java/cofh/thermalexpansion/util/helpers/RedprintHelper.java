package cofh.thermalexpansion.util.helpers;

import cofh.lib.util.helpers.RedstoneControlHelper;
import cofh.lib.util.helpers.StringHelper;

import java.util.List;

import net.minecraft.item.ItemStack;

public class RedprintHelper {

	private RedprintHelper() {

	}

	public static void addRedprintInformation(ItemStack stack, List<String> list) {

		if (stack.getTagCompound() == null) {
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

		return stack.getTagCompound() != null && stack.getTagCompound().hasKey("Type");
	}

	public static String getName(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			return "";
		}
		if (stack.getTagCompound().hasKey("DisplayType")) {
			return ": " + StringHelper.localize(stack.getTagCompound().getString("Type")) + " ("
					+ StringHelper.localize(stack.getTagCompound().getString("DisplayType")) + ")";
		}
		return ": " + StringHelper.localize(stack.getTagCompound().getString("Type"));
	}

}
