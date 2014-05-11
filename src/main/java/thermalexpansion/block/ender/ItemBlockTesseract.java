package thermalexpansion.block.ender;

import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.util.StringHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockTesseract extends ItemBlock {

	public ItemBlockTesseract(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(1);
		setNoRepair();
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {

		return StringHelper.localize(getUnlocalizedName(stack));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.tesseract.name";
	}

	@Override
	public int getMetadata(int i) {

		return i;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		return EnumRarity.rare;
	}

	@Override
	public boolean isItemTool(ItemStack stack) {

		return false;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		if (stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("Owner")) {
			list.add(StringHelper.localize("info.cofh.owner") + ": " + StringHelper.localize("info.cofh.none"));
			return;
		} else {
			list.add(StringHelper.localize("info.cofh.owner") + ": " + stack.stackTagCompound.getString("Owner"));
		}
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForInfo);
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		if (stack.stackTagCompound.hasKey("Frequency")) {
			int frequency = stack.stackTagCompound.getInteger("Frequency");
			byte modeItem = stack.stackTagCompound.getByte("ModeItems");
			byte modeFluid = stack.stackTagCompound.getByte("ModeFluid");
			byte modeEnergy = stack.stackTagCompound.getByte("ModeEnergy");
			byte access = stack.stackTagCompound.getByte("Access");

			ControlMode rsMode = ControlMode.values()[stack.stackTagCompound.getByte("rsMode")];

			String accessString = "";

			switch (access) {
			case 0:
				accessString = StringHelper.localize("info.cofh.accessPublic");
				break;
			case 1:
				accessString = StringHelper.localize("info.cofh.accessRestricted");
				break;
			case 2:
				accessString = StringHelper.localize("info.cofh.accessPrivate");
				break;
			}
			list.add(StringHelper.localize("info.cofh.access") + ": " + accessString);

			if (frequency < 0) {
				list.add(StringHelper.localize("info.cofh.frequency") + ": None");
			} else {
				list.add(StringHelper.localize("info.cofh.frequency") + ": " + frequency);
			}
			list.add(StringHelper.localize("info.cofh.items") + ": " + MODES[modeItem]);
			list.add(StringHelper.localize("info.cofh.fluid") + ": " + MODES[modeFluid]);
			list.add(StringHelper.localize("info.cofh.energy") + ": " + MODES[modeEnergy]);

			if (rsMode.isDisabled()) {
				list.add(StringHelper.localize("info.cofh.signal") + ": " + StringHelper.localize("info.cofh.redstoneControlOff"));
			} else if (rsMode.isLow()) {
				list.add(StringHelper.localize("info.cofh.signal") + ": " + StringHelper.localize("info.cofh.redstoneControlOn") + ", "
						+ StringHelper.localize("info.cofh.redstoneStateLow"));
			} else {
				list.add(StringHelper.localize("info.cofh.signal") + ": " + StringHelper.localize("info.cofh.redstoneControlOn") + ", "
						+ StringHelper.localize("info.cofh.redstoneStateHigh"));
			}
		}
	}

	public static final String[] MODES = { StringHelper.localize("info.thermalexpansion.modeSend"), StringHelper.localize("info.thermalexpansion.modeRecv"),
			StringHelper.localize("info.thermalexpansion.modeSendRecv"), StringHelper.localize("info.thermalexpansion.modeBlocked") };

}
