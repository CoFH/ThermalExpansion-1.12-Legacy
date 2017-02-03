package cofh.thermalexpansion.item;

import cofh.api.core.IInitializer;
import cofh.core.item.ItemMulti;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileAugmentableSecure;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;
import static cofh.lib.util.helpers.ItemHelper.addRecipe;

public class ItemUpgrade extends ItemMulti implements IInitializer {

	public ItemUpgrade() {

		super("thermalexpansion");

		setUnlocalizedName("upgrade");
		setCreativeTab(ThermalExpansion.tabItems);
	}

	@SideOnly (Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {

	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {

		return true;
	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {

		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (!block.hasTileEntity(state)) {
			return EnumActionResult.PASS;
		}
		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileAugmentableSecure) {
			if (((TileAugmentableSecure) tile).changeLevel((byte) ItemHelper.getItemDamage(stack))) {
				if (!player.capabilities.isCreativeMode) {
					stack.stackSize--;
				}
			}
			return ServerHelper.isServerWorld(world) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
		}
		return EnumActionResult.PASS;
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		// upgradeCreative = addItem(0, "upgradeCreative");
		upgradeHardened = addItem(1, "upgradeHardened");
		upgradeReinforced = addItem(2, "upgradeReinforced");
		upgradeSignalum = addItem(3, "upgradeSignalum");
		upgradeResonant = addItem(4, "upgradeResonant");

		return true;
	}

	@Override
	public boolean initialize() {

		return true;
	}

	@Override
	public boolean postInit() {

		// @formatter:off
		addRecipe(ShapedRecipe(upgradeHardened,
				" I ",
				"IGI",
				"DID",
				'D', "dustRedstone",
				'G', "gearBronze",
				'I', "ingotInvar"
		));
		addRecipe(ShapedRecipe(upgradeReinforced,
				" I ",
				"IGI",
				"DID",
				'D', "dustGlowstone",
				'G', "gearSilver",
				'I', "glassHardened"
		));
		addRecipe(ShapedRecipe(upgradeSignalum,
				" I ",
				"IGI",
				"DID",
				'D', "dustCryotheum",
				'G', "gearElectrum",
				'I', "ingotSignalum"
		));
		addRecipe(ShapedRecipe(upgradeResonant,
				" I ",
				"IGI",
				"DID",
				'D', "dustPyrotheum",
				'G', "gearLumium",
				'I', "ingotEnderium"
		));
		// @formatter:on

		return true;
	}

	/* REFERENCES */
	// public static ItemStack upgradeCreative;
	public static ItemStack upgradeHardened;
	public static ItemStack upgradeReinforced;
	public static ItemStack upgradeSignalum;
	public static ItemStack upgradeResonant;

}
