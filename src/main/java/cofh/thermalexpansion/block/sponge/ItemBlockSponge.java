package cofh.thermalexpansion.block.sponge;

import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.tank.BlockTank;
import cofh.thermalexpansion.block.tank.TileTank;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

public class ItemBlockSponge extends ItemBlock implements IFluidContainerItem {

	public ItemBlockSponge(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(1);
		setNoRepair();
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {

		String dispName = "";

		if (stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("Fluid")) {
			dispName += StringHelper.localize("info.cofh.dry") + " ";
		} else {
			dispName += StringHelper.localize("info.cofh.soaked") + " ";
		}
		return dispName + StringHelper.localize(getUnlocalizedName(stack));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.sponge." + BlockSponge.NAMES[ItemHelper.getItemDamage(stack)] + ".name";
	}

	@Override
	public int getMetadata(int i) {

		return i;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		switch (BlockSponge.Types.values()[ItemHelper.getItemDamage(stack)]) {
		case CREATIVE:
			return EnumRarity.epic;
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
			list.add(StringHelper.localize("info.cofh.empty"));
			return;
		}
		FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.stackTagCompound.getCompoundTag("Fluid"));

		if (fluid == null) {
			return;
		}
		String color = StringHelper.LIGHT_GRAY;

		if (fluid.getFluid().getRarity() == EnumRarity.uncommon) {
			color = StringHelper.YELLOW;
		} else if (fluid.getFluid().getRarity() == EnumRarity.rare) {
			color = StringHelper.BRIGHT_BLUE;
		} else if (fluid.getFluid().getRarity() == EnumRarity.epic) {
			color = StringHelper.PINK;
		}
		list.add(StringHelper.localize("info.cofh.fluid") + ": " + color + fluid.getFluid().getLocalizedName(fluid) + StringHelper.LIGHT_GRAY);
		list.add(StringHelper.localize("info.cofh.amount") + ": " + fluid.amount);
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

		return 0;
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
