package cofh.thermalexpansion.block.storage;

import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.core.block.ItemBlockCore;
import cofh.lib.util.helpers.RedstoneControlHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import java.util.List;

public class ItemBlockTank extends ItemBlockCore implements IFluidContainerItem {

	public static ItemStack setDefaultTag(ItemStack stack) {

		return setDefaultTag(stack, 0);
	}

	public static ItemStack setDefaultTag(ItemStack stack, int level) {

		RedstoneControlHelper.setControl(stack, ControlMode.DISABLED);
		stack.getTagCompound().setByte("Level", (byte) level);

		return stack;
	}

	public static byte getLevel(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			setDefaultTag(stack);
		}
		return stack.getTagCompound().getByte("Level");
	}

	public ItemBlockTank(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(1);
		setNoRepair();
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.storage.tank.name";
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

		// tooltip.add(StringHelper.localize("info.thermalexpansion.dynamo.0"));
		// tooltip.add(StringHelper.getInfoText("info.thermalexpansion.dynamo." + BlockDynamo.Type.byMetadata(ItemHelper.getItemDamage(stack)).getName()));

		RedstoneControlHelper.addRSControlInformation(stack, tooltip);

		FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag("Fluid"));

		//		if (fluid != null) {
		//			String color = StringHelper.LIGHT_GRAY;
		//
		//			if (fluid.getFluid().getRarity() == EnumRarity.UNCOMMON) {
		//				color = StringHelper.YELLOW;
		//			} else if (fluid.getFluid().getRarity() == EnumRarity.RARE) {
		//				color = StringHelper.BRIGHT_BLUE;
		//			} else if (fluid.getFluid().getRarity() == EnumRarity.EPIC) {
		//				color = StringHelper.PINK;
		//			}
		//			tooltip.add(StringHelper.localize("info.cofh.fluid") + ": " + color + fluid.getFluid().getLocalizedName(fluid) + StringHelper.LIGHT_GRAY);
		//
		//			if (ItemHelper.getItemDamage(stack) == BlockTank.Types.CREATIVE.ordinal()) {
		//				tooltip.add(StringHelper.localize("info.cofh.infinite") + " " + StringHelper.localize("info.cofh.source"));
		//			} else {
		//				tooltip.add(StringHelper.localize("info.cofh.level") + ": " + fluid.amount + " / " + TileTank.CAPACITY[ItemHelper.getItemDamage(stack)] + " mB");
		//			}
		//		} else {
		//			tooltip.add(StringHelper.localize("info.cofh.fluid") + ": " + StringHelper.localize("info.cofh.empty"));
		//
		//			if (ItemHelper.getItemDamage(stack) == BlockTank.Types.CREATIVE.ordinal()) {
		//				tooltip.add(StringHelper.localize("info.cofh.infinite") + " " + StringHelper.localize("info.cofh.source"));
		//			} else {
		//				tooltip.add(StringHelper.localize("info.cofh.level") + ": 0 / " + TileTank.CAPACITY[ItemHelper.getItemDamage(stack)] + " mB");
		//			}
		//		}
	}

	/* IFluidContainerItem */
	@Override
	public FluidStack getFluid(ItemStack container) {

		if (!container.getTagCompound().hasKey("Fluid")) {
			return null;
		}
		return FluidStack.loadFluidStackFromNBT(container.getTagCompound().getCompoundTag("Fluid"));
	}

	@Override
	public int getCapacity(ItemStack container) {

		return TileTank.getCapacity(getLevel(container));
	}

	@Override
	public int fill(ItemStack container, FluidStack resource, boolean doFill) {

		if (resource == null || container.stackSize > 1) {
			return 0;
		}
		int capacity = getCapacity(container);

		if (!doFill) {
			if (!container.getTagCompound().hasKey("Fluid")) {
				return Math.min(capacity, resource.amount);
			}
			FluidStack stack = FluidStack.loadFluidStackFromNBT(container.getTagCompound().getCompoundTag("Fluid"));

			if (stack == null) {
				return Math.min(capacity, resource.amount);
			}
			if (!stack.isFluidEqual(resource)) {
				return 0;
			}
			return Math.min(capacity - stack.amount, resource.amount);
		}
		if (!container.getTagCompound().hasKey("Fluid")) {
			NBTTagCompound fluidTag = resource.writeToNBT(new NBTTagCompound());

			if (capacity < resource.amount) {
				fluidTag.setInteger("Amount", capacity);
				container.getTagCompound().setTag("Fluid", fluidTag);
				return capacity;
			}
			fluidTag.setInteger("Amount", resource.amount);
			container.getTagCompound().setTag("Fluid", fluidTag);
			return resource.amount;
		}
		NBTTagCompound fluidTag = container.getTagCompound().getCompoundTag("Fluid");
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
		container.getTagCompound().setTag("Fluid", stack.writeToNBT(fluidTag));
		return filled;
	}

	@Override
	public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {

		if (!container.getTagCompound().hasKey("Fluid") || maxDrain == 0 || container.stackSize > 1) {
			return null;
		}
		FluidStack stack = FluidStack.loadFluidStackFromNBT(container.getTagCompound().getCompoundTag("Fluid"));

		if (stack == null) {
			return null;
		}
		int drained = Math.min(stack.amount, maxDrain);

		if (doDrain) {
			if (maxDrain >= stack.amount) {
				container.getTagCompound().removeTag("Fluid");
				return stack;
			}
			NBTTagCompound fluidTag = container.getTagCompound().getCompoundTag("Fluid");
			fluidTag.setInteger("Amount", fluidTag.getInteger("Amount") - drained);
			container.getTagCompound().setTag("Fluid", fluidTag);
		}
		stack.amount = drained;
		return stack;
	}

}
