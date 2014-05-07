package thermalexpansion.block.tank;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import cofh.block.ItemBlockCoFHBase;
import cofh.util.StringHelper;

public class ItemBlockTank extends ItemBlockCoFHBase implements IFluidContainerItem {

	public ItemBlockTank(Block block) {

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

		return "tile.thermalexpansion.tank." + BlockTank.NAMES[stack.getItemDamage()] + ".name";
	}

	@Override
	public int getMetadata(int i) {

		return i;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		switch (BlockTank.Types.values()[stack.getItemDamage()]) {
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
	public boolean isItemTool(ItemStack stack) {

		return false;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForInfo);
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		if (stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("Fluid")) {
			list.add(StringHelper.localize("info.cofh.fluid") + ": " + StringHelper.localize("info.cofh.empty"));

			if (stack.getItemDamage() == BlockTank.Types.CREATIVE.ordinal()) {
				list.add(StringHelper.localize("info.cofh.infinite") + " " + StringHelper.localize("info.cofh.source"));
			} else {
				list.add(StringHelper.localize("info.cofh.level") + ": 0 / " + TileTank.CAPACITY[stack.getItemDamage()] + " mB");
			}
			return;
		}
		FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.stackTagCompound.getCompoundTag("Fluid"));

		if (fluid != null) {
			String color = StringHelper.GRAY;

			if (fluid.getFluid().getRarity() == EnumRarity.uncommon) {
				color = StringHelper.YELLOW;
			} else if (fluid.getFluid().getRarity() == EnumRarity.rare) {
				color = StringHelper.BRIGHT_BLUE;
			} else if (fluid.getFluid().getRarity() == EnumRarity.epic) {
				color = StringHelper.PINK;
			}
			list.add(StringHelper.localize("info.cofh.fluid") + ": " + color + fluid.getFluid().getLocalizedName() + StringHelper.GRAY);

			if (stack.getItemDamage() == BlockTank.Types.CREATIVE.ordinal()) {
				list.add(StringHelper.localize("info.cofh.infinite") + " " + StringHelper.localize("info.cofh.source"));
			} else {
				list.add(StringHelper.localize("info.cofh.level") + ": " + fluid.amount + " / " + TileTank.CAPACITY[stack.getItemDamage()] + " mB");
			}
		} else {
			list.add(StringHelper.localize("info.cofh.fluid") + ": " + StringHelper.localize("info.cofh.empty"));

			if (stack.getItemDamage() == BlockTank.Types.CREATIVE.ordinal()) {
				list.add(StringHelper.localize("info.cofh.infinite") + " " + StringHelper.localize("info.cofh.source"));
			} else {
				list.add(StringHelper.localize("info.cofh.level") + ": 0 / " + TileTank.CAPACITY[stack.getItemDamage()] + " mB");
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

		return TileTank.CAPACITY[container.getItemDamage()];
	}

	@Override
	public int fill(ItemStack container, FluidStack resource, boolean doFill) {

		if (resource == null) {
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

		if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Fluid") || maxDrain == 0) {
			return null;
		}
		FluidStack stack = FluidStack.loadFluidStackFromNBT(container.stackTagCompound.getCompoundTag("Fluid"));

		if (stack == null) {
			return null;
		}
		int drained = Math.min(stack.amount, maxDrain);

		if (doDrain && container.getItemDamage() != BlockTank.Types.CREATIVE.ordinal()) {
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
