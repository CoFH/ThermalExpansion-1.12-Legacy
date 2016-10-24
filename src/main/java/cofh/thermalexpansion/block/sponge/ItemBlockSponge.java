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
import net.minecraft.util.text.translation.I18n;
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

		String unloc = getUnlocalizedNameInefficiently(stack);
		String dispName, unloc2;

		if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("Fluid")) {
			unloc2 = ".dry";
			dispName = "info.cofh.dry";
		} else {
			unloc2 = ".wet";
			dispName = "info.cofh.soaked";
		}
		if (I18n.canTranslate(unloc + unloc2 + ".name")) {
			return StringHelper.localize(unloc + unloc2 + ".name");
		}
		return StringHelper.localize(dispName) + " " + StringHelper.localize(unloc + ".name");
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.sponge." + BlockSponge.NAMES[ItemHelper.getItemDamage(stack)];
	}

	@Override
	public int getMetadata(int i) {

		return i;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		switch (BlockSponge.Types.values()[ItemHelper.getItemDamage(stack)]) {
		case CREATIVE:
			return EnumRarity.EPIC;
		default:
			return EnumRarity.COMMON;
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
		if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("Fluid")) {
			list.add(StringHelper.localize("info.cofh.empty"));
			return;
		}
		FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag("Fluid"));

		if (fluid == null) {
			return;
		}
		String color = StringHelper.LIGHT_GRAY;

		if (fluid.getFluid().getRarity() == EnumRarity.UNCOMMON) {
			color = StringHelper.YELLOW;
		} else if (fluid.getFluid().getRarity() == EnumRarity.RARE) {
			color = StringHelper.BRIGHT_BLUE;
		} else if (fluid.getFluid().getRarity() == EnumRarity.EPIC) {
			color = StringHelper.PINK;
		}
		list.add(StringHelper.localize("info.cofh.fluid") + ": " + color + fluid.getFluid().getLocalizedName(fluid) + StringHelper.LIGHT_GRAY);
		list.add(StringHelper.localize("info.cofh.amount") + ": " + fluid.amount);
	}

	/* IFluidContainerItem */
	@Override
	public FluidStack getFluid(ItemStack container) {

		if (container.getTagCompound() == null || !container.getTagCompound().hasKey("Fluid")) {
			return null;
		}
		return FluidStack.loadFluidStackFromNBT(container.getTagCompound().getCompoundTag("Fluid"));
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

		if (container.getTagCompound() == null || !container.getTagCompound().hasKey("Fluid") || maxDrain == 0) {
			return null;
		}
		FluidStack stack = FluidStack.loadFluidStackFromNBT(container.getTagCompound().getCompoundTag("Fluid"));

		if (stack == null) {
			return null;
		}
		int drained = Math.min(stack.amount, maxDrain);

		if (doDrain && ItemHelper.getItemDamage(container) != BlockTank.Types.CREATIVE.ordinal()) {
			if (maxDrain >= stack.amount) {
				container.getTagCompound().removeTag("Fluid");

				if (container.getTagCompound().hasNoTags()) {
					container.setTagCompound(null);
				}
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
