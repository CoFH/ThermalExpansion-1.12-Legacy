package thermalexpansion.block.cell;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.util.StringHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemBlockCell extends ItemBlock implements IEnergyContainerItem {

	public static ItemStack setDefaultTag(ItemStack container, int energy) {

		container.setTagCompound(new NBTTagCompound());
		if (container.getItemDamage() == BlockCell.Types.CREATIVE.ordinal()) {
			container.stackTagCompound.setByteArray("SideCache", TileCellCreative.DEFAULT_SIDES);
		} else {
			container.stackTagCompound.setByteArray("SideCache", TileCell.DEFAULT_SIDES);
		}
		container.stackTagCompound.setByte("Facing", (byte) 3);

		container.stackTagCompound.setInteger("Energy", energy);
		container.stackTagCompound.setInteger("Send", TileCell.MAX_SEND[container.getItemDamage()]);
		container.stackTagCompound.setInteger("Receive", TileCell.MAX_RECEIVE[container.getItemDamage()]);

		container.stackTagCompound.setByte("rsMode", (byte) ControlMode.LOW.ordinal());

		return container;
	}

	public ItemBlockCell(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(1);
		setMaxStackSize(1);
		setNoRepair();
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {

		return StringHelper.localize(getUnlocalizedName(stack));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.cell." + BlockCell.NAMES[stack.getItemDamage()] + ".name";
	}

	@Override
	public int getMetadata(int i) {

		return i;
	}

	@Override
	public int getDisplayDamage(ItemStack stack) {

		if (stack.stackTagCompound == null) {
			return 1 + TileCell.STORAGE[stack.getItemDamage()];
		}
		return 1 + TileCell.STORAGE[stack.getItemDamage()] - stack.stackTagCompound.getInteger("Energy");
	}

	@Override
	public int getMaxDamage(ItemStack stack) {

		return 1 + TileCell.STORAGE[stack.getItemDamage()];
	}

	@Override
	public boolean isDamaged(ItemStack stack) {

		return stack.getItemDamage() != BlockCell.Types.CREATIVE.ordinal();
	}

	@Override
	public boolean isItemTool(ItemStack stack) {

		return false;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		switch (BlockCell.Types.values()[stack.getItemDamage()]) {
		case CREATIVE:
			return EnumRarity.epic;
		case RESONANT:
			return EnumRarity.rare;
		case REINFORCED:
			return EnumRarity.uncommon;
		default:
			return EnumRarity.common;
		}
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		if (stack.stackTagCompound == null) {
			setDefaultTag(stack, 0);
		}
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForInfo());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		int send = stack.stackTagCompound.getInteger("Send");

		byte rsMode = stack.stackTagCompound.getByte("rsMode");

		if (stack.getItemDamage() == BlockCell.Types.CREATIVE.ordinal()) {
			list.add(StringHelper.localize("info.cofh.charge") + ": " + StringHelper.localize("info.cofh.infinite"));
			list.add(StringHelper.localize("info.cofh.send") + ": " + send);
		} else {
			int energy = stack.stackTagCompound.getInteger("Energy");
			int receive = stack.stackTagCompound.getInteger("Receive");

			list.add(StringHelper.localize("info.cofh.charge") + ": " + StringHelper.getScaledNumber(energy) + " / "
					+ StringHelper.getScaledNumber(TileCell.STORAGE[stack.getItemDamage()]) + " RF");
			list.add(StringHelper.localize("info.cofh.send") + "/" + StringHelper.localize("info.cofh.receive") + ": " + send + "/" + receive + " RF/t");
		}
		switch (rsMode) {
		case 0:
			list.add(StringHelper.localize("info.cofh.signal") + ": " + StringHelper.localize("info.cofh.disabled"));
			break;
		case 1:
			list.add(StringHelper.localize("info.cofh.signal") + ": " + StringHelper.localize("info.cofh.enabled") + ", "
					+ StringHelper.localize("info.cofh.low"));
			break;
		case 2:
			list.add(StringHelper.localize("info.cofh.signal") + ": " + StringHelper.localize("info.cofh.enabled") + ", "
					+ StringHelper.localize("info.cofh.high"));
			break;
		}
	}

	/* IEnergyContainerItem */
	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {

		if (container.stackTagCompound == null) {
			setDefaultTag(container, 0);
		}
		int stored = container.stackTagCompound.getInteger("Energy");
		int receive = Math.min(maxReceive, Math.min(TileCell.STORAGE[container.getItemDamage()] - stored, TileCell.MAX_RECEIVE[container.getItemDamage()]));

		if (!simulate && container.getItemDamage() != BlockCell.Types.CREATIVE.ordinal()) {
			stored += receive;
			container.stackTagCompound.setInteger("Energy", stored);
		}
		return receive;
	}

	@Override
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {

		if (container.stackTagCompound == null) {
			setDefaultTag(container, 0);
		}
		int stored = container.stackTagCompound.getInteger("Energy");
		int extract = Math.min(maxExtract, Math.min(stored, TileCell.MAX_SEND[container.getItemDamage()]));

		if (!simulate && container.getItemDamage() != BlockCell.Types.CREATIVE.ordinal()) {
			stored -= extract;
			container.stackTagCompound.setInteger("Energy", stored);
		}
		return extract;
	}

	@Override
	public int getEnergyStored(ItemStack container) {

		if (container.stackTagCompound == null) {
			setDefaultTag(container, 0);
		}
		return container.stackTagCompound.getInteger("Energy");
	}

	@Override
	public int getMaxEnergyStored(ItemStack container) {

		return TileCell.STORAGE[container.getItemDamage()];
	}

}
