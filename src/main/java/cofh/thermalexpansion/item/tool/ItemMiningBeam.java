package cofh.thermalexpansion.item.tool;

import cofh.api.energy.IEnergyContainerItem;
import cofh.core.item.ItemBase;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemMiningBeam extends ItemBase implements IEnergyContainerItem {

	public int maxEnergy = 80000;
	public int maxTransfer = 160;
	public int energyPerUse = 400;
	public int range = 32;

	public ItemMiningBeam() {

		super("thermalexpansion");
		setMaxDamage(1);
		setMaxStackSize(1);
		setCreativeTab(ThermalExpansion.tabTools);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {

		list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item, 1, 0), 0));
		list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item, 1, 0), maxEnergy));
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		if (stack.stackTagCompound == null) {
			EnergyHelper.setDefaultEnergyTag(stack, 0);
		}
		list.add(StringHelper.localize("info.cofh.charge") + ": " + stack.stackTagCompound.getInteger("Energy") + " / " + maxEnergy + " RF");
		list.add(StringHelper.ORANGE + energyPerUse + " RF " + StringHelper.localize("info.cofh.perUse") + StringHelper.END);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {

	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

		return stack;
	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public boolean isDamaged(ItemStack stack) {

		return stack.getItemDamage() != Short.MAX_VALUE;
	}

	@Override
	public int getDisplayDamage(ItemStack stack) {

		if (stack.stackTagCompound == null) {
			EnergyHelper.setDefaultEnergyTag(stack, 0);
		}
		return 1 + maxEnergy - stack.stackTagCompound.getInteger("Energy");
	}

	@Override
	public int getMaxDamage(ItemStack stack) {

		return 1 + maxEnergy;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {

		return EnumAction.bow;
	}

	/* IEnergyContainerItem */
	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {

		if (container.stackTagCompound == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		int stored = container.stackTagCompound.getInteger("Energy");
		int receive = Math.min(maxReceive, Math.min(maxEnergy - stored, maxTransfer));

		if (!simulate) {
			stored += receive;
			container.stackTagCompound.setInteger("Energy", stored);
		}
		return receive;
	}

	@Override
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {

		if (container.stackTagCompound == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		int stored = container.stackTagCompound.getInteger("Energy");
		int extract = Math.min(maxExtract, stored);

		if (!simulate) {
			stored -= extract;
			container.stackTagCompound.setInteger("Energy", stored);
		}
		return extract;
	}

	@Override
	public int getEnergyStored(ItemStack container) {

		if (container.stackTagCompound == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		return container.stackTagCompound.getInteger("Energy");
	}

	@Override
	public int getMaxEnergyStored(ItemStack container) {

		return maxEnergy;
	}

}
