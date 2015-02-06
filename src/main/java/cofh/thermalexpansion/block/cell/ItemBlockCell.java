package cofh.thermalexpansion.block.cell;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.core.item.ItemBlockBase;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.RedstoneControlHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.util.ReconfigurableHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;


public class ItemBlockCell extends ItemBlockBase implements IEnergyContainerItem {

	public static ItemStack setDefaultTag(ItemStack container, int energy) {

		ReconfigurableHelper.setFacing(container, 3);
		ReconfigurableHelper.setSideCache(container, ItemHelper.getItemDamage(container) == BlockCell.Types.CREATIVE.ordinal() ? TileCellCreative.DEFAULT_SIDES
				: TileCell.DEFAULT_SIDES);
		RedstoneControlHelper.setControl(container, ControlMode.LOW);
		EnergyHelper.setDefaultEnergyTag(container, energy);
		container.stackTagCompound.setInteger("Send", TileCell.MAX_SEND[container.getItemDamage()]);
		container.stackTagCompound.setInteger("Recv", TileCell.MAX_RECEIVE[container.getItemDamage()]);

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
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.cell." + BlockCell.NAMES[ItemHelper.getItemDamage(stack)] + ".name";
	}

	@Override
	public int getDisplayDamage(ItemStack stack) {

		if (stack.stackTagCompound == null) {
			return TileCell.STORAGE[ItemHelper.getItemDamage(stack)];
		}
		return TileCell.STORAGE[ItemHelper.getItemDamage(stack)] - stack.stackTagCompound.getInteger("Energy");
	}

	@Override
	public int getMaxDamage(ItemStack stack) {

		return TileCell.STORAGE[ItemHelper.getItemDamage(stack)];
	}

	@Override
	public boolean isDamaged(ItemStack stack) {

		return ItemHelper.getItemDamage(stack) != BlockCell.Types.CREATIVE.ordinal();
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		switch (BlockCell.Types.values()[ItemHelper.getItemDamage(stack)]) {
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
		SecurityHelper.addOwnerInformation(stack, list);
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		SecurityHelper.addAccessInformation(stack, list);

		if (ItemHelper.getItemDamage(stack) == BlockCell.Types.CREATIVE.ordinal()) {
			list.add(StringHelper.localize("info.cofh.charge") + ": 1.21G RF");
		} else {
			list.add(StringHelper.localize("info.cofh.charge") + ": " + StringHelper.getScaledNumber(stack.stackTagCompound.getInteger("Energy")) + " / "
					+ StringHelper.getScaledNumber(TileCell.STORAGE[ItemHelper.getItemDamage(stack)]) + " RF");
		}
		list.add(StringHelper.localize("info.cofh.send") + "/" + StringHelper.localize("info.cofh.receive") + ": " + stack.stackTagCompound.getInteger("Send")
				+ "/" + stack.stackTagCompound.getInteger("Recv") + " RF/t");

		RedstoneControlHelper.addRSControlInformation(stack, list);
	}

	/* IEnergyContainerItem */
	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {

		if (container.stackTagCompound == null) {
			setDefaultTag(container, 0);
		}
		int stored = container.stackTagCompound.getInteger("Energy");
		int metadata = ItemHelper.getItemDamage(container);
		int receive = Math.min(maxReceive, Math.min(TileCell.STORAGE[metadata] - stored, TileCell.MAX_RECEIVE[metadata]));

		if (!simulate && metadata != BlockCell.Types.CREATIVE.ordinal()) {
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
		int metadata = ItemHelper.getItemDamage(container);
		int extract = Math.min(maxExtract, Math.min(stored, TileCell.MAX_SEND[metadata]));

		if (!simulate && metadata != BlockCell.Types.CREATIVE.ordinal()) {
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

		return TileCell.STORAGE[ItemHelper.getItemDamage(container)];
	}

}
