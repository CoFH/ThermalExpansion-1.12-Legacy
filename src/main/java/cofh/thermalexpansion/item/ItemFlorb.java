package cofh.thermalexpansion.item;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.bakery.generation.IBakery;
import cofh.core.init.CoreProps;
import cofh.core.item.ItemMulti;
import cofh.core.util.ConfigHandler;
import cofh.core.util.CoreUtils;
import cofh.core.util.DefaultedHashMap;
import cofh.core.util.core.IInitializer;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.entity.projectile.EntityFlorb;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.render.item.ModelFlorb;
import cofh.thermalexpansion.util.BehaviorFlorbDispense;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cofh.core.util.helpers.RecipeHelper.addShapelessRecipe;
import static java.util.Arrays.asList;

public class ItemFlorb extends ItemMulti implements IBakeryProvider, IInitializer {

	public static ItemStack setTag(ItemStack container, Fluid fluid) {

		if (fluid != null && fluid.canBePlacedInWorld()) {
			container.setTagCompound(new NBTTagCompound());
			container.getTagCompound().setString("Fluid", fluid.getName());
		}
		return container;
	}

	public ItemFlorb() {

		super("thermalexpansion");

		setUnlocalizedName("florb");
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

		if (TEProps.creativeTabHideFlorbs) {
			return;
		}
		if (isInCreativeTab(tab)) {
			items.add(new ItemStack(this, 1, 0));
			items.add(new ItemStack(this, 1, 1));
			items.addAll(florbList);
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
		if (!stack.hasTagCompound()) {
			return new ActionResult<>(EnumActionResult.PASS, stack);
		}
		Fluid fluid = FluidRegistry.getFluid(stack.getTagCompound().getString("Fluid"));
		if (fluid == null) {
			return new ActionResult<>(EnumActionResult.PASS, stack);
		}
		//if (!player.capabilities.isCreativeMode) {
		stack.shrink(1);
		//}
		world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F));

		if (ServerHelper.isServerWorld(world)) {
			EntityFlorb florb = new EntityFlorb(world, player, fluid);
			florb.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
			world.spawnEntity(florb);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	/* HELPERS */
	public static void addFlorb(ItemStack florb, Fluid fluid) {

		setTag(florb, fluid);
		florbList.add(florb);
		florbMap.put(fluid.getName(), florb);
	}

	public static void dropFlorb(Fluid fluid, World world, BlockPos pos) {

		CoreUtils.dropItemStackIntoWorldWithVelocity(getFlorb(fluid), world, pos);
	}

	/**
	 * Attempts to get a Florb ItemStack from the given fluid.
	 *
	 * @param fluid The fluid a Florb is being requested for.
	 * @return The ItemStack.
	 */
	@Nonnull
	public static ItemStack getFlorb(Fluid fluid) {

		if (!florbMap.containsKey(fluid.getName())) {
			return florbStandard.copy();
		}
		return florbMap.get(fluid.getName());
	}

	public static void parseFlorbs() {

		List<String> list = asList(blacklist);

		for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
			if (!fluid.canBePlacedInWorld()) {
				continue;
			}
			if (list.contains(fluid.getName())) {
				continue;
			}
			if (fluid.getTemperature() < CoreProps.MAGMATIC_TEMPERATURE) {
				addFlorb(ItemHelper.cloneStack(florbStandard), fluid);
				TransposerManager.addFillRecipe(1600, ItemFlorb.florbStandard, florbList.get(florbList.size() - 1), new FluidStack(fluid, Fluid.BUCKET_VOLUME), false);
			} else {
				addFlorb(ItemHelper.cloneStack(florbMagmatic), fluid);
				TransposerManager.addFillRecipe(1600, ItemFlorb.florbMagmatic, florbList.get(florbList.size() - 1), new FluidStack(fluid, Fluid.BUCKET_VOLUME), false);
			}
		}
		CONFIG_FLORBS.cleanUp(false, true);
	}

	/* IBakeryProvider */
	@Override
	@SideOnly (Side.CLIENT)
	public IBakery getBakery() {

		return ModelFlorb.INSTANCE;
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		final ModelResourceLocation location = new ModelResourceLocation("thermalexpansion:florb", "type=florb");
		ModelLoader.setCustomMeshDefinition(this, stack -> location);
		ModelLoader.setCustomModelResourceLocation(this, 0, location);
		ModelRegistryHelper.register(location, new CCBakeryModel());
		ModelBakery.registerItemKeyGenerator(this, stack -> {

			String fluid = "";
			if (stack.getTagCompound() != null) {
				fluid = "," + stack.getTagCompound().getString("Fluid");
			}
			return ModelBakery.defaultItemKeyGenerator.generateKey(stack) + fluid;
		});
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		config();

		florbStandard = addItem(0, "standard");
		florbMagmatic = addItem(1, "magmatic");

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, new BehaviorFlorbDispense());

		ThermalExpansion.proxy.addIModelRegister(this);

		return true;
	}

	@Override
	public boolean register() {

		if (!enable) {
			return false;
		}
		ItemStack florbStack = ItemHelper.cloneStack(florbStandard, 4);
		ItemStack florbMagmaticStack = ItemHelper.cloneStack(florbMagmatic, 4);

		addShapelessRecipe(florbStack, "dustWood", "crystalSlag", "slimeball");
		addShapelessRecipe(florbMagmaticStack, "dustWood", "crystalSlag", "slimeball", Items.BLAZE_POWDER);
		addShapelessRecipe(florbMagmaticStack, "dustWood", "crystalSlag", Items.MAGMA_CREAM);

		addShapelessRecipe(florbStack, "dustWood", "crystalSlag", ItemMaterial.globRosin);
		addShapelessRecipe(florbMagmaticStack, "dustWood", "crystalSlag", ItemMaterial.globRosin, Items.BLAZE_POWDER);

		return true;
	}

	private static void config() {

		CONFIG_FLORBS.setConfiguration(new Configuration(new File(CoreProps.configDir, "cofh/" + ThermalExpansion.MOD_ID + "/florbs.cfg"), true));

		String category = "General";
		String comment = "If TRUE, the recipes for Florbs are enabled. Setting this to FALSE means that you actively dislike fun things.";
		enable = CONFIG_FLORBS.getConfiguration().getBoolean("EnableRecipe", category, enable, comment);

		category = "Blacklist";
		comment = "List of fluids that are not allowed to be placed in Florbs.";
		blacklist = CONFIG_FLORBS.getConfiguration().getStringList("Blacklist", category, blacklist, comment);
	}

	public static final ConfigHandler CONFIG_FLORBS = new ConfigHandler(ThermalExpansion.VERSION);

	public static ArrayList<ItemStack> florbList = new ArrayList<>();
	public static Map<String, ItemStack> florbMap = new DefaultedHashMap<String, ItemStack>(ItemStack.EMPTY);

	public static String[] blacklist = new String[] {};
	public static boolean enable = true;

	/* REFERENCES */
	public static ItemStack florbStandard;
	public static ItemStack florbMagmatic;

}
