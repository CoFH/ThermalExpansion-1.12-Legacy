package cofh.thermalexpansion.block.tank;

import cofh.core.item.ItemBlockBase;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

public class ItemBlockTank extends ItemBlockBase implements IFluidContainerItem {

	public ItemBlockTank(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
		// setMaxStackSize(1);
		setNoRepair();
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.tank." + BlockTank.NAMES[ItemHelper.getItemDamage(stack)] + ".name";
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		switch (BlockTank.Types.values()[ItemHelper.getItemDamage(stack)]) {
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

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		if (stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("Fluid")) {
			list.add(StringHelper.localize("info.cofh.fluid") + ": " + StringHelper.localize("info.cofh.empty"));

			if (ItemHelper.getItemDamage(stack) == BlockTank.Types.CREATIVE.ordinal()) {
				list.add(StringHelper.localize("info.cofh.infinite") + " " + StringHelper.localize("info.cofh.source"));
			} else {
				list.add(StringHelper.localize("info.cofh.level") + ": 0 / " + TileTank.CAPACITY[ItemHelper.getItemDamage(stack)] + " mB");
			}
			return;
		}
		FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.stackTagCompound.getCompoundTag("Fluid"));

		if (fluid != null) {
			String color = StringHelper.LIGHT_GRAY;

			if (fluid.getFluid().getRarity() == EnumRarity.uncommon) {
				color = StringHelper.YELLOW;
			} else if (fluid.getFluid().getRarity() == EnumRarity.rare) {
				color = StringHelper.BRIGHT_BLUE;
			} else if (fluid.getFluid().getRarity() == EnumRarity.epic) {
				color = StringHelper.PINK;
			}
			list.add(StringHelper.localize("info.cofh.fluid") + ": " + color + fluid.getFluid().getLocalizedName(fluid) + StringHelper.LIGHT_GRAY);

			if (ItemHelper.getItemDamage(stack) == BlockTank.Types.CREATIVE.ordinal()) {
				list.add(StringHelper.localize("info.cofh.infinite") + " " + StringHelper.localize("info.cofh.source"));
			} else {
				list.add(StringHelper.localize("info.cofh.level") + ": " + fluid.amount + " / " + TileTank.CAPACITY[ItemHelper.getItemDamage(stack)] + " mB");
			}
		} else {
			list.add(StringHelper.localize("info.cofh.fluid") + ": " + StringHelper.localize("info.cofh.empty"));

			if (ItemHelper.getItemDamage(stack) == BlockTank.Types.CREATIVE.ordinal()) {
				list.add(StringHelper.localize("info.cofh.infinite") + " " + StringHelper.localize("info.cofh.source"));
			} else {
				list.add(StringHelper.localize("info.cofh.level") + ": 0 / " + TileTank.CAPACITY[ItemHelper.getItemDamage(stack)] + " mB");
			}
		}
	}

	/* IFluidContainerItem */
	@Override
	public FluidStack getFluid(ItemStack container) {

		if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Fluid")) {
			return null;
		}
		return FluidStack.loadFluidStackFromNBT(container.stackTagCompound.getCompoundTag("Fluid"));
	}

	@Override
	public int getCapacity(ItemStack container) {

		return TileTank.CAPACITY[ItemHelper.getItemDamage(container)];
	}

	@Override
	public int fill(ItemStack container, FluidStack resource, boolean doFill) {

		if (resource == null || container.stackSize > 1) {
			return 0;
		}
		int capacity = getCapacity(container);

		if (!doFill) {
			if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Fluid")) {
				return Math.min(capacity, resource.amount);
			}
			FluidStack stack = FluidStack.loadFluidStackFromNBT(container.stackTagCompound.getCompoundTag("Fluid"));

			if (stack == null) {
				return Math.min(capacity, resource.amount);
			}
			if (!stack.isFluidEqual(resource)) {
				return 0;
			}
			return Math.min(capacity - stack.amount, resource.amount);
		}
		if (container.stackTagCompound == null) {
			container.stackTagCompound = new NBTTagCompound();
		}
		if (!container.stackTagCompound.hasKey("Fluid")) {
			NBTTagCompound fluidTag = resource.writeToNBT(new NBTTagCompound());

			if (capacity < resource.amount) {
				fluidTag.setInteger("Amount", capacity);
				container.stackTagCompound.setTag("Fluid", fluidTag);
				return capacity;
			}
			fluidTag.setInteger("Amount", resource.amount);
			container.stackTagCompound.setTag("Fluid", fluidTag);
			return resource.amount;
		}
		NBTTagCompound fluidTag = container.stackTagCompound.getCompoundTag("Fluid");
		FluidStack stack = FluidStack.loadFluidStackFromNBT(fluidTag);

		if (!stack.isFluidEqual(resource)) {
			return 0;
		}
		int filled = capacity - stack.amount;

		if (resource.amount < filled) {
			stack.amount += resource.amount;
			filled = resource.amount;
		} else {
			stack.amount = capacity;
		}
		container.stackTagCompound.setTag("Fluid", stack.writeToNBT(fluidTag));
		return filled;
	}

	@Override
	public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {

		if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Fluid") || maxDrain == 0 || container.stackSize > 1) {
			return null;
		}
		FluidStack stack = FluidStack.loadFluidStackFromNBT(container.stackTagCompound.getCompoundTag("Fluid"));

		if (stack == null) {
			return null;
		}
		int drained = Math.min(stack.amount, maxDrain);

		if (doDrain && ItemHelper.getItemDamage(container) != BlockTank.Types.CREATIVE.ordinal()) {
			if (maxDrain >= stack.amount) {
				container.stackTagCompound.removeTag("Fluid");

				if (container.stackTagCompound.hasNoTags()) {
					container.stackTagCompound = null;
				}
				return stack;
			}
			NBTTagCompound fluidTag = container.stackTagCompound.getCompoundTag("Fluid");
			fluidTag.setInteger("Amount", fluidTag.getInteger("Amount") - drained);
			container.stackTagCompound.setTag("Fluid", fluidTag);
		}
		stack.amount = drained;
		return stack;
	}

}
