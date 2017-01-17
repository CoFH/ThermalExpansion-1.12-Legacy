package cofh.thermalexpansion.item;

import codechicken.lib.util.SoundUtils;
import cofh.core.item.ItemCoFHBase;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.entity.projectile.EntityFlorb;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.List;

public class ItemFlorb extends ItemCoFHBase {

	public static ItemStack setTag(ItemStack container, Fluid fluid) {

		if (fluid != null && fluid.canBePlacedInWorld()) {
			container.setTagCompound(new NBTTagCompound());
			container.getTagCompound().setString("Fluid", fluid.getName());
		}
		return container;
	}

	public static void dropFlorb(Fluid fluid, World worldObj, BlockPos pos) {

		if (fluid != null) {
			if (fluid.getTemperature() < TEProps.MAGMATIC_TEMPERATURE) {
				CoreUtils.dropItemStackIntoWorldWithVelocity(ItemFlorb.setTag(new ItemStack(TEFlorbs.itemFlorb, 1, 0), fluid), worldObj, pos);
			} else {
				CoreUtils.dropItemStackIntoWorldWithVelocity(ItemFlorb.setTag(new ItemStack(TEFlorbs.itemFlorb, 1, 1), fluid), worldObj, pos);
			}
		}
	}

	public ItemFlorb() {

		super("thermalexpansion");
		setMaxStackSize(16);
		setCreativeTab(ThermalExpansion.tabFlorbs);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {

		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));

		for (int i = 0; i < TEFlorbs.florbList.size(); i++) {
			list.add(TEFlorbs.florbList.get(i));
		}
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean check) {

		if (stack.getTagCompound() != null) {
			Fluid fluid = FluidRegistry.getFluid(stack.getTagCompound().getString("Fluid"));

			if (fluid == null || fluid.getDensity() >= 0) {
				return;
			}
		}
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		if (stack.getTagCompound() == null) {
			if (ItemHelper.getItemDamage(stack) == 0) {
				list.add(StringHelper.localize("info.thermalexpansion.florb.0"));
			} else {
				list.add(StringHelper.localize("info.thermalexpansion.florb.1"));
			}
			list.add(StringHelper.localize("info.thermalexpansion.florb.2") + " " + StringHelper.YELLOW + StringHelper.ITALIC + StringHelper.localize("tile.thermalexpansion.machine.transposer.name") + StringHelper.END + StringHelper.LIGHT_GRAY + ".");
			list.add(StringHelper.localize("info.thermalexpansion.florb.3"));
		} else {
			list.add(StringHelper.localize("info.thermalexpansion.florb.4"));
			list.add(StringHelper.localize("info.thermalexpansion.florb.5"));
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {

		String fluidName = "info.cofh.empty";
		String openParen = " (";
		String closeParen = StringHelper.END + ")";

		if (stack.getTagCompound() != null) {
			Fluid fluid = FluidRegistry.getFluid(stack.getTagCompound().getString("Fluid"));

			if (fluid != null) {
				fluidName = fluid.getUnlocalizedName();

				if (fluid.getRarity() == EnumRarity.UNCOMMON) {
					openParen += StringHelper.YELLOW;
				} else if (fluid.getRarity() == EnumRarity.RARE) {
					openParen += StringHelper.BRIGHT_BLUE;
				} else if (fluid.getRarity() == EnumRarity.EPIC) {
					openParen += StringHelper.PINK;
				}
			}
		}
		return super.getItemStackDisplayName(stack) + openParen + StringHelper.localize(fluidName) + closeParen;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {

		if (stack.getTagCompound() == null) {
			return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
		}
		if (!player.capabilities.isCreativeMode) {
			--stack.stackSize;
		}
		Fluid fluid = FluidRegistry.getFluid(stack.getTagCompound().getString("Fluid"));

		if (fluid != null) {
			SoundUtils.playSoundAt(player, SoundCategory.NEUTRAL, SoundEvents.ENTITY_ARROW_SHOOT, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if (ServerHelper.isServerWorld(world)) {
				world.spawnEntityInWorld(new EntityFlorb(world, player, fluid));
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}
}
