package cofh.thermalexpansion.block.storage;

import cofh.api.fluid.IFluidContainerItem;
import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.core.block.BlockCore;
import cofh.core.init.CoreEnchantments;
import cofh.core.init.CoreProps;
import cofh.core.item.IEnchantableItem;
import cofh.core.util.capabilities.FluidContainerItemWrapper;
import cofh.core.util.helpers.RedstoneControlHelper;
import cofh.core.util.helpers.SecurityHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.ItemBlockTEBase;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockTank extends ItemBlockTEBase implements IFluidContainerItem, IEnchantableItem {

	public ItemBlockTank(BlockCore block) {

		super(block);
		setMaxStackSize(1);
	}

	public boolean isLocked(ItemStack stack) {

		return stack.getTagCompound().getBoolean("Lock");
	}

	@Override
	public ItemStack setDefaultTag(ItemStack stack, int level) {

		RedstoneControlHelper.setControl(stack, ControlMode.DISABLED);
		stack.getTagCompound().setByte("Level", (byte) level);

		return stack;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.storage.tank.name";
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

		SecurityHelper.addOwnerInformation(stack, tooltip);
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			tooltip.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		SecurityHelper.addAccessInformation(stack, tooltip);
		tooltip.add(StringHelper.getInfoText("info.thermalexpansion.storage.tank"));

		FluidStack fluid = getFluid(stack);
		if (fluid != null) {
			String color = fluid.getFluid().getRarity().rarityColor.toString();
			tooltip.add(StringHelper.localize("info.cofh.fluid") + StringHelper.localize("info.thermalexpansion.semicolon") + color + fluid.getFluid().getLocalizedName(fluid) + StringHelper.LIGHT_GRAY);
			if (isCreative(stack)) {
				tooltip.add(StringHelper.localize("info.cofh.infiniteSource"));
			} else {
				tooltip.add(StringHelper.localize("info.cofh.level") + StringHelper.localize("info.thermalexpansion.semicolon") + StringHelper.formatNumber(fluid.amount) + " / " + StringHelper.formatNumber(getCapacity(stack)) + " mB");
			}
			if (isLocked(stack)) {
				tooltip.add(StringHelper.YELLOW + StringHelper.localize("info.cofh.locked"));
			} else {
				tooltip.add(StringHelper.YELLOW + StringHelper.localize("info.cofh.unlocked"));
			}
		} else {
			tooltip.add(StringHelper.localize("info.cofh.fluid") + StringHelper.localize("info.thermalexpansion.semicolon") + StringHelper.localize("info.cofh.empty"));

			if (isCreative(stack)) {
				tooltip.add(StringHelper.localize("info.cofh.infiniteSource"));
			} else {
				tooltip.add(StringHelper.localize("info.cofh.level") + ": 0 / " + StringHelper.formatNumber(getCapacity(stack)) + " mB");
			}
		}
		RedstoneControlHelper.addRSControlInformation(stack, tooltip);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {

		return true;
	}

	@Override
	public int getItemEnchantability(ItemStack stack) {

		return 10;
	}

	/* IFluidContainerItem */
	@Override
	public FluidStack getFluid(ItemStack container) {

		if (container.getTagCompound() == null) {
			setDefaultTag(container);
		}
		if (!container.getTagCompound().hasKey(CoreProps.FLUID)) {
			return null;
		}
		return FluidStack.loadFluidStackFromNBT(container.getTagCompound().getCompoundTag(CoreProps.FLUID));
	}

	@Override
	public int getCapacity(ItemStack container) {

		return TileTank.getMaxCapacity(getLevel(container), EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.holding, container));
	}

	@Override
	public int fill(ItemStack container, FluidStack resource, boolean doFill) {

		if (container.getTagCompound() == null) {
			setDefaultTag(container);
		}
		if (resource == null || resource.amount <= 0 || isCreative(container)) {
			return 0;
		}
		int capacity = getCapacity(container);

		if (!doFill) {
			if (!container.getTagCompound().hasKey(CoreProps.FLUID)) {
				return Math.min(capacity, resource.amount);
			}
			FluidStack stack = FluidStack.loadFluidStackFromNBT(container.getTagCompound().getCompoundTag(CoreProps.FLUID));

			if (stack == null) {
				return Math.min(capacity, resource.amount);
			}
			if (!stack.isFluidEqual(resource)) {
				return 0;
			}
			return Math.min(capacity - stack.amount, resource.amount);
		}
		if (!container.getTagCompound().hasKey(CoreProps.FLUID)) {
			NBTTagCompound fluidTag = resource.writeToNBT(new NBTTagCompound());

			if (capacity < resource.amount) {
				fluidTag.setInteger(CoreProps.AMOUNT, capacity);
				container.getTagCompound().setTag(CoreProps.FLUID, fluidTag);
				return capacity;
			}
			fluidTag.setInteger(CoreProps.AMOUNT, resource.amount);
			container.getTagCompound().setTag(CoreProps.FLUID, fluidTag);
			return resource.amount;
		}
		NBTTagCompound fluidTag = container.getTagCompound().getCompoundTag(CoreProps.FLUID);
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
		container.getTagCompound().setTag(CoreProps.FLUID, stack.writeToNBT(fluidTag));
		return filled;
	}

	@Override
	public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {

		if (container.getTagCompound() == null) {
			setDefaultTag(container);
		}
		if (!container.getTagCompound().hasKey(CoreProps.FLUID) || maxDrain == 0) {
			return null;
		}
		FluidStack stack = FluidStack.loadFluidStackFromNBT(container.getTagCompound().getCompoundTag(CoreProps.FLUID));

		if (stack == null || stack.amount <= 0) {
			return null;
		}
		int drained = Math.min(stack.amount, maxDrain);

		if (doDrain && !isCreative(container)) {
			if (drained >= stack.amount) {
				if (isLocked(container)) {
					NBTTagCompound fluidTag = container.getTagCompound().getCompoundTag(CoreProps.FLUID);
					fluidTag.setInteger(CoreProps.AMOUNT, 0);
					container.getTagCompound().setTag(CoreProps.FLUID, fluidTag);
				} else {
					container.getTagCompound().removeTag(CoreProps.FLUID);
				}
				return stack;
			}
			NBTTagCompound fluidTag = container.getTagCompound().getCompoundTag(CoreProps.FLUID);
			fluidTag.setInteger(CoreProps.AMOUNT, fluidTag.getInteger(CoreProps.AMOUNT) - drained);
			container.getTagCompound().setTag(CoreProps.FLUID, fluidTag);
		}
		stack.amount = drained;
		return stack;
	}

	/* IEnchantableItem */
	@Override
	public boolean canEnchant(ItemStack stack, Enchantment enchantment) {

		return enchantment == CoreEnchantments.holding;
	}

	/* CAPABILITIES */
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {

		return new FluidContainerItemWrapper(stack, this);
	}

}
