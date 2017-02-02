package cofh.thermalexpansion.item.tool;

import codechicken.lib.raytracer.RayTracer;
import cofh.api.block.IDismantleable;
import cofh.api.item.IToolHammer;
import cofh.asm.relauncher.Implementable;
import cofh.core.item.tool.ItemSwordCore;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

import java.util.List;

@Implementable ("buildcraft.api.tools.IToolWrench")
public class ItemWrenchBattle extends ItemSwordCore implements IToolHammer {

	public ItemWrenchBattle(ToolMaterial toolMaterial) {

		super(toolMaterial);
		setHarvestLevel("wrench", 1);

		setUnlocalizedName("thermalexpansion.tool.battlewrench");
		//setCreativeTab(ThermalExpansion.tabTools);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean check) {

		list.add(StringHelper.getFlavorText("info.thermalexpansion.tool.battlewrench"));
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {

		return true;
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase entity, EntityLivingBase player) {

		stack.damageItem(1, player);
		entity.rotationYaw += 90;
		entity.rotationYaw %= 360;
		return true;
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		return ServerHelper.isClientWorld(world) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {

		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (world.isAirBlock(pos)) {
			return EnumActionResult.PASS;
		}
		RayTraceResult traceResult = RayTracer.retrace(player);
		PlayerInteractEvent event = new PlayerInteractEvent.RightClickBlock(player, hand, stack, pos, side, traceResult.hitVec);
		if (MinecraftForge.EVENT_BUS.post(event) || event.getResult() == Result.DENY) {
			return EnumActionResult.PASS;
		}
		if (ServerHelper.isServerWorld(world) && player.isSneaking() && block instanceof IDismantleable && ((IDismantleable) block).canDismantle(world, pos, state, player)) {
			((IDismantleable) block).dismantleBlock(world, pos, state, player, false);
			return EnumActionResult.SUCCESS;
		}
		if (BlockHelper.canRotate(block)) {
			if (player.isSneaking()) {
				world.setBlockState(pos, BlockHelper.rotateVanillaBlockAlt(world, state, pos), 3);
				world.playSound(null, pos, block.getSoundType(state, world, pos, player).getBreakSound(), SoundCategory.BLOCKS, 1.0F, 0.6F);
			} else {
				world.setBlockState(pos, BlockHelper.rotateVanillaBlock(world, state, pos), 3);
				world.playSound(null, pos, block.getSoundType(state, world, pos, player).getBreakSound(), SoundCategory.BLOCKS, 1.0F, 0.8F);
			}
			return ServerHelper.isServerWorld(world) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
		} else if (!player.isSneaking() && block.rotateBlock(world, pos, side)) {
			player.swingArm(hand);
			return ServerHelper.isServerWorld(world) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
		}
		return EnumActionResult.PASS;
	}

	/* IToolHammer */
	@Override
	public boolean isUsable(ItemStack item, EntityLivingBase user, BlockPos pos) {

		return true;
	}

	@Override
	public boolean isUsable(ItemStack item, EntityLivingBase user, Entity entity) {

		return true;
	}

	@Override
	public void toolUsed(ItemStack item, EntityLivingBase user, BlockPos pos) {

	}

	@Override
	public void toolUsed(ItemStack item, EntityLivingBase user, Entity entity) {

	}

	/* IToolWrench */
	public boolean canWrench(EntityPlayer player, int x, int y, int z) {

		return true;
	}

	public void wrenchUsed(EntityPlayer player, int x, int y, int z) {

	}

}
