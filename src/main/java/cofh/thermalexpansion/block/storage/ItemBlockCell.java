package cofh.thermalexpansion.block.storage;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.core.block.ItemBlockCore;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.RedstoneControlHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.util.ReconfigurableHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockCell extends ItemBlockCore implements IEnergyContainerItem {

	public static ItemStack setDefaultTag(ItemStack stack) {

		return setDefaultTag(stack, (byte) 0);
	}

	public static ItemStack setDefaultTag(ItemStack stack, byte level) {

		ReconfigurableHelper.setFacing(stack, 3);
		// ReconfigurableHelper.setSideCache(stack, TileCell.defaultSideConfig.defaultSides);
		RedstoneControlHelper.setControl(stack, ControlMode.DISABLED);
		EnergyHelper.setDefaultEnergyTag(stack, 0);
		stack.getTagCompound().setByte("Level", level);

		return stack;
	}

	public static byte getLevel(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			setDefaultTag(stack);
		}
		return stack.getTagCompound().getByte("Level");
	}

	public ItemBlockCell(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(1);
		setNoRepair();
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.storage.cell.name";
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {

		SecurityHelper.addOwnerInformation(stack, tooltip);
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			tooltip.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		SecurityHelper.addAccessInformation(stack, tooltip);
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			return 1;
		}
		return 0;
		// return 1D - ((double) stack.getTagCompound().getInteger("Energy") / (double) TileCell.CAPACITY[ItemHelper.getItemDamage(stack)]);
	}

	@Override
	public boolean isDamaged(ItemStack stack) {

		return true;
	}

	/* IEnergyContainerItem */
	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {

		//		if (container.getTagCompound() == null) {
		//			setDefaultTag(container, (byte) 0);
		//		}
		//		int metadata = ItemHelper.getItemDamage(container);
		//
		//		if (metadata == BlockCell.Types.CREATIVE.ordinal()) {
		//			return 0;
		//		}
		//		int stored = container.getTagCompound().getInteger("Energy");
		//		int receive = Math.min(maxReceive, Math.min(TileCell.CAPACITY[metadata] - stored, TileCell.MAX_RECEIVE[metadata]));
		//
		//		if (!simulate) {
		//			stored += receive;
		//			container.getTagCompound().setInteger("Energy", stored);
		//		}
		//		return receive;
		return 0;
	}

	@Override
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {

		//		if (container.getTagCompound() == null) {
		//			setDefaultTag(container, 0);
		//		}
		//		int metadata = ItemHelper.getItemDamage(container);
		//
		//		if (metadata == BlockCell.Types.CREATIVE.ordinal()) {
		//			return maxExtract;
		//		}
		//		int stored = container.getTagCompound().getInteger("Energy");
		//		int extract = Math.min(maxExtract, Math.min(stored, TileCell.MAX_SEND[metadata]));
		//
		//		if (!simulate) {
		//			stored -= extract;
		//			container.getTagCompound().setInteger("Energy", stored);
		//		}
		//		return extract;
		return 0;
	}

	@Override
	public int getEnergyStored(ItemStack container) {

		if (container.getTagCompound() == null) {
			setDefaultTag(container, (byte) 0);
		}
		return container.getTagCompound().getInteger("Energy");
	}

	@Override
	public int getMaxEnergyStored(ItemStack container) {

		// return TileCell.CAPACITY[ItemHelper.getItemDamage(container)];
		return 32000;
	}

}
