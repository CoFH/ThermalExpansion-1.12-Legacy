package cofh.thermalexpansion.item.tool;

import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.util.SoundUtils;
import codechicken.lib.vec.Vector3;
import cofh.api.block.IDismantleable;
import cofh.api.item.IToolHammer;
import cofh.asm.relauncher.Implementable;
import cofh.core.item.tool.ItemSwordAdv;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

@Implementable("buildcraft.api.tools.IToolWrench")
public class ItemWrenchBattle extends ItemSwordAdv implements IToolHammer {

	public ItemWrenchBattle(ToolMaterial toolMaterial) {

		super(toolMaterial);

		setUnlocalizedName("thermalexpansion.tool.battleWrench");
		//setTextureName("thermalexpansion:tool/BattleWrench");
		setCreativeTab(ThermalExpansion.tabTools);
		setHarvestLevel("wrench", 1);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		list.add(StringHelper.getFlavorText("info.thermalexpansion.tool.battleWrench"));
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase entity, EntityLivingBase player) {

		stack.damageItem(1, player);
		entity.rotationYaw += 90;
		entity.rotationYaw %= 360;
		return true;
	}

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (state == null) {
			return EnumActionResult.PASS;
		}
        RayTraceResult traceResult = RayTracer.retrace(player);
		PlayerInteractEvent event = new PlayerInteractEvent.RightClickBlock(player, hand, stack, pos, side, traceResult.hitVec);
		if (MinecraftForge.EVENT_BUS.post(event) || event.getResult() == Result.DENY  ) {
			return EnumActionResult.FAIL;
		}
		if (ServerHelper.isServerWorld(world) && player.isSneaking() && block instanceof IDismantleable
				&& ((IDismantleable) block).canDismantle(player, world, pos)) {
			((IDismantleable) block).dismantleBlock(player, world, pos, false);
			return EnumActionResult.SUCCESS;
		}
		if (BlockHelper.canRotate(block)) {
			if (player.isSneaking()) {
				world.setBlockState(pos, BlockHelper.rotateVanillaBlockAlt(world, state, pos), 3);
                SoundUtils.playSoundAt(new Vector3(pos).add(0.5), world, SoundCategory.BLOCKS, block.getSoundType(state, world, pos, player).getBreakSound(), 1.0F, 0.6F);
			} else {
				world.setBlockState(pos, BlockHelper.rotateVanillaBlock(world, state, pos), 3);
                SoundUtils.playSoundAt(new Vector3(pos).add(0.5), world, SoundCategory.BLOCKS, block.getSoundType(state, world, pos, player).getBreakSound(), 1.0F, 0.8F);
			}
			return ServerHelper.isServerWorld(world) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
		} else if (!player.isSneaking() && block.rotateBlock(world, pos, side)) {
			player.swingArm(hand);
			return ServerHelper.isServerWorld(world) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
		}
		return EnumActionResult.PASS;
	}

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
		return true;
	}

	/* IToolHammer */
	@Override
	public boolean isUsable(ItemStack item, EntityLivingBase user, int x, int y, int z) {

		return true;
	}

	@Override
	public void toolUsed(ItemStack item, EntityLivingBase user, int x, int y, int z) {

	}

	/* IToolWrench */
	public boolean canWrench(EntityPlayer player, int x, int y, int z) {

		return true;
	}

	public void wrenchUsed(EntityPlayer player, int x, int y, int z) {

	}

}
