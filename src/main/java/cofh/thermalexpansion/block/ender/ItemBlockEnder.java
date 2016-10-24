package cofh.thermalexpansion.block.ender;

import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.core.item.ItemBlockBase;
import cofh.lib.util.helpers.RedstoneControlHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemBlockEnder extends ItemBlockBase {

	public static ItemStack setDefaultTag(ItemStack container) {

		RedstoneControlHelper.setControl(container, ControlMode.LOW);
		container.getTagCompound().setInteger("Frequency", -1);
		container.getTagCompound().setByte("ModeItems", (byte) 1);
		container.getTagCompound().setByte("ModeFluid", (byte) 1);
		container.getTagCompound().setByte("ModeEnergy", (byte) 1);

		return container;
	}

	public ItemBlockEnder(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(1);
		setNoRepair();
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.ender.tesseract.name";
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		return EnumRarity.RARE;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		if (stack.getTagCompound() == null) {
			setDefaultTag(stack);
		}
		SecurityHelper.addOwnerInformation(stack, list);

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		SecurityHelper.addAccessInformation(stack, list);

		list.add(StringHelper.getInfoText("info.thermalexpansion.ender.tesseract.0"));
		list.add(StringHelper.getInfoText("info.thermalexpansion.ender.tesseract.1"));

		if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("Frequency")) {
			int frequency = stack.getTagCompound().getInteger("Frequency");
			byte modeItem = stack.getTagCompound().getByte("ModeItems");
			byte modeFluid = stack.getTagCompound().getByte("ModeFluid");
			byte modeEnergy = stack.getTagCompound().getByte("ModeEnergy");

			if (frequency < 0) {
				list.add(StringHelper.localize("info.cofh.frequency") + ": " + StringHelper.localize("info.cofh.none"));
			} else {
				list.add(StringHelper.localize("info.cofh.frequency") + ": " + frequency);
			}
			list.add(StringHelper.localize("info.cofh.items") + ": " + MODES[modeItem]);
			list.add(StringHelper.localize("info.cofh.fluid") + ": " + MODES[modeFluid]);
			list.add(StringHelper.localize("info.cofh.energy") + ": " + MODES[modeEnergy]);
		}
		RedstoneControlHelper.addRSControlInformation(stack, list);
	}

	public static final String[] MODES = { StringHelper.localize("info.thermalexpansion.modeSend"), StringHelper.localize("info.thermalexpansion.modeRecv"),
			StringHelper.localize("info.thermalexpansion.modeSendRecv"), StringHelper.localize("info.thermalexpansion.modeBlocked") };

}
