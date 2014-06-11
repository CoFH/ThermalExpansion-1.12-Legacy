package thermalexpansion.item.tool;

import cofh.item.ItemBase;
import cofh.render.IconRegistry;
import cofh.util.CoreUtils;
import cofh.util.StringHelper;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.entity.projectile.EntityFlorb;
import thermalexpansion.item.TEFlorbs;

public class ItemFlorb extends ItemBase {

	public static ItemStack setTag(ItemStack container, Fluid fluid) {

		if (fluid != null && fluid.canBePlacedInWorld()) {
			container.setTagCompound(new NBTTagCompound());
			container.stackTagCompound.setString("Fluid", fluid.getName());
		}
		return container;
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
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		if (stack.stackTagCompound != null) {
			Fluid fluid = FluidRegistry.getFluid(stack.stackTagCompound.getString("Fluid"));

			if (fluid == null || fluid.getDensity() >= 0) {
				return;
			}
		}
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForInfo());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		if (stack.stackTagCompound == null) {
			if (stack.getItemDamage() == 0) {
				list.add(StringHelper.localize("info.thermalexpansion.florb1"));
			} else {
				list.add(StringHelper.localize("info.thermalexpansion.florb1a"));
			}
			list.add(StringHelper.localize("info.thermalexpansion.florb2") + " " + StringHelper.YELLOW + StringHelper.ITALIC
					+ StringHelper.localize("tile.thermalexpansion.machine.transposer.name") + StringHelper.END + StringHelper.LIGHT_GRAY + ".");
			list.add(StringHelper.localize("info.thermalexpansion.florb3"));
		} else {
			list.add(StringHelper.localize("info.thermalexpansion.florb4"));
			list.add(StringHelper.localize("info.thermalexpansion.florb5"));
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {

		String fluidName = "info.cofh.empty";
		String openParen = " (";
		String closeParen = StringHelper.END + ")";

		if (stack.stackTagCompound != null) {
			Fluid fluid = FluidRegistry.getFluid(stack.stackTagCompound.getString("Fluid"));

			if (fluid != null) {
				fluidName = fluid.getUnlocalizedName();

				if (fluid.getRarity() == EnumRarity.uncommon) {
					openParen += StringHelper.YELLOW;
				} else if (fluid.getRarity() == EnumRarity.rare) {
					openParen += StringHelper.BRIGHT_BLUE;
				} else if (fluid.getRarity() == EnumRarity.epic) {
					openParen += StringHelper.PINK;
				}
			}
		}
		return super.getItemStackDisplayName(stack) + openParen + StringHelper.localize(fluidName) + closeParen;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

		if (stack.stackTagCompound == null) {
			return stack;
		}
		if (!player.capabilities.isCreativeMode) {
			--stack.stackSize;
		}
		Fluid fluid = FluidRegistry.getFluid(stack.stackTagCompound.getString("Fluid"));

		if (fluid != null) {
			world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if (!world.isRemote) {
				world.spawnEntityInWorld(new EntityFlorb(world, player, fluid));
			}
		}
		return stack;
	}

	@Override
	public void registerIcons(IIconRegister ir) {

		IconRegistry.addIcon("Florb", "thermalexpansion:florb/Florb", ir);
		IconRegistry.addIcon("FlorbMagmatic", "thermalexpansion:florb/FlorbMagmatic", ir);
		IconRegistry.addIcon("FlorbMask", "thermalexpansion:florb/FlorbMask", ir);
		IconRegistry.addIcon("FlorbOutline", "thermalexpansion:florb/FlorbOutline", ir);
	}

	public static void dropFlorb(Fluid fluid, World worldObj, int x, int y, int z) {

		if (fluid != null) {
			if (fluid.getTemperature() < TEFlorbs.MAGMATIC_FLORB_TEMPERATURE) {
				CoreUtils.dropItemStackIntoWorldWithVelocity(ItemFlorb.setTag(new ItemStack(TEFlorbs.itemFlorb, 1, 0), fluid), worldObj, x, y, z);
			} else {
				CoreUtils.dropItemStackIntoWorldWithVelocity(ItemFlorb.setTag(new ItemStack(TEFlorbs.itemFlorb, 1, 1), fluid), worldObj, x, y, z);
			}
		}
	}

}
