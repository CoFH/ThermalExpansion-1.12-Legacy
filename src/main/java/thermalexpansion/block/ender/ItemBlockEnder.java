package thermalexpansion.block.ender;

import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.item.ItemBlockBase;
import cofh.util.RSControlHelper;
import cofh.util.SecurityHelper;
import cofh.util.StringHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemBlockEnder extends ItemBlockBase {

	public static ItemStack setDefaultTag(ItemStack container) {

		RSControlHelper.setControl(container, ControlMode.LOW);
		container.stackTagCompound.setInteger("Frequency", -1);
		container.stackTagCompound.setByte("ModeItems", (byte) 1);
		container.stackTagCompound.setByte("ModeFluid", (byte) 1);
		container.stackTagCompound.setByte("ModeEnergy", (byte) 1);

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

		return "tile.thermalexpansion.tesseract.name";
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		return EnumRarity.rare;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		if (stack.stackTagCompound == null) {
			setDefaultTag(stack);
		}
		SecurityHelper.addOwnerInformation(stack, list);

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForInfo());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		SecurityHelper.addAccessInformation(stack, list);

		if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("Frequency")) {
			int frequency = stack.stackTagCompound.getInteger("Frequency");
			byte modeItem = stack.stackTagCompound.getByte("ModeItems");
			byte modeFluid = stack.stackTagCompound.getByte("ModeFluid");
			byte modeEnergy = stack.stackTagCompound.getByte("ModeEnergy");

			if (frequency < 0) {
				list.add(StringHelper.localize("info.cofh.frequency") + ": None");
			} else {
				list.add(StringHelper.localize("info.cofh.frequency") + ": " + frequency);
			}
			list.add(StringHelper.localize("info.cofh.items") + ": " + MODES[modeItem]);
			list.add(StringHelper.localize("info.cofh.fluid") + ": " + MODES[modeFluid]);
			list.add(StringHelper.localize("info.cofh.energy") + ": " + MODES[modeEnergy]);
		}
		RSControlHelper.addRSControlInformation(stack, list);
	}

	public static final String[] MODES = { StringHelper.localize("info.thermalexpansion.modeSend"), StringHelper.localize("info.thermalexpansion.modeRecv"),
			StringHelper.localize("info.thermalexpansion.modeSendRecv"), StringHelper.localize("info.thermalexpansion.modeBlocked") };

}
