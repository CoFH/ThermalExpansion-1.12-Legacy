package cofh.thermalexpansion.item;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.bakery.generation.IBakery;
import cofh.core.item.ItemMulti;
import cofh.core.util.CoreUtils;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.entity.projectile.EntityFlorb;
import cofh.thermalexpansion.init.TEFlorbs;
import cofh.thermalexpansion.render.item.ModelFlorb;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemFlorb extends ItemMulti implements IBakeryProvider {

	public static ItemStack setTag(ItemStack container, Fluid fluid) {

		if (fluid != null && fluid.canBePlacedInWorld()) {
			container.setTagCompound(new NBTTagCompound());
			container.getTagCompound().setString("Fluid", fluid.getName());
		}
		return container;
	}

	public static void dropFlorb(Fluid fluid, World worldObj, BlockPos pos) {

		if (fluid != null) {
			CoreUtils.dropItemStackIntoWorldWithVelocity(TEFlorbs.getFlorb(fluid), worldObj, pos);
		}
	}

	public ItemFlorb() {

		super("thermalexpansion");
		setCreativeTab(ThermalExpansion.tabFlorbs);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

		if (stack.getTagCompound() != null) {
			Fluid fluid = FluidRegistry.getFluid(stack.getTagCompound().getString("Fluid"));

			if (fluid == null || fluid.getDensity() >= 0) {
				return;
			}
		}
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			tooltip.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		if (stack.getTagCompound() == null) {
			if (ItemHelper.getItemDamage(stack) == 0) {
				tooltip.add(StringHelper.localize("info.thermalexpansion.florb.a.0"));
			} else {
				tooltip.add(StringHelper.localize("info.thermalexpansion.florb.b.0"));
			}
			tooltip.add(StringHelper.localize("info.thermalexpansion.florb.c.0"));
			tooltip.add(StringHelper.localize("info.thermalexpansion.florb.c.1"));
		} else {
			tooltip.add(StringHelper.localize("info.thermalexpansion.florb.d.0"));
			tooltip.add(StringHelper.localize("info.thermalexpansion.florb.d.1"));
		}
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (isInCreativeTab(tab)) {
			items.add(new ItemStack(this, 1, 0));
			items.add(new ItemStack(this, 1, 1));

			for (int i = 0; i < TEFlorbs.florbList.size(); i++) {
				items.add(TEFlorbs.florbList.get(i));
			}
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
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		ItemStack stack = player.getHeldItem(hand);
		if (stack.getTagCompound() == null) {
			return new ActionResult<>(EnumActionResult.PASS, stack);
		}
		if (!player.capabilities.isCreativeMode) {
			stack.shrink(1);
		}
		Fluid fluid = FluidRegistry.getFluid(stack.getTagCompound().getString("Fluid"));

		if (fluid != null) {
			world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if (ServerHelper.isServerWorld(world)) {
				EntityFlorb florb = new EntityFlorb(world, player, fluid);
				florb.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
				world.spawnEntity(florb);
			}
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		final ModelResourceLocation location = new ModelResourceLocation("thermalexpansion:florb", "type=florb");
		ModelLoader.setCustomMeshDefinition(this, stack -> location);
		ModelLoader.setCustomModelResourceLocation(this, 0, location);
		ModelRegistryHelper.register(location, new CCBakeryModel(""));
		ModelBakery.registerItemKeyGenerator(this, stack -> {

			String fluid = "";
			if (stack.getTagCompound() != null) {
				fluid = "," + stack.getTagCompound().getString("Fluid");
			}
			return ModelBakery.defaultItemKeyGenerator.generateKey(stack) + fluid;
		});
	}

	@Override
	@SideOnly (Side.CLIENT)
	public IBakery getBakery() {

		return ModelFlorb.INSTANCE;
	}

}
