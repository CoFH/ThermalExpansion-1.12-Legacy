package cofh.thermalexpansion.item.tool;

import codechicken.lib.raytracer.RayTracer;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ServerHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ItemMiner extends ItemEnergyContainerBase {

	static final int MAX_DURATION = 72000;
	int energyPerUse = 1;
	int range = 128;

	public ItemMiner() {

		super("miningBeam");
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase entityBase, int count) {

		if (entityBase instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityBase;
			if (MAX_DURATION - count < 20) {
				return;
			}
			if (ServerHelper.isServerWorld(player.world)) {
				doBeam(stack, player.world, player);

				if (!player.capabilities.isCreativeMode) {
					extractEnergy(stack, energyPerUse, false);
				}
			}
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {

		return MAX_DURATION;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {

		return EnumAction.BOW;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		return EnumActionResult.PASS;
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {

		boolean r = doItemUse(player.getHeldItem(hand), world, player, hand);
		if (r) { // HACK: forge is fucking stupid with this method
			ServerHelper.sendItemUsePacket(world, pos, side, hand, hitX, hitY, hitZ);
		}
		return r ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		ItemStack stack = player.getHeldItem(hand);
		boolean success = doItemUse(stack, world, player, hand);
		return new ActionResult<>(success ? EnumActionResult.SUCCESS : EnumActionResult.PASS, stack);
	}

	public boolean doItemUse(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {

		if (!player.capabilities.isCreativeMode && extractEnergy(stack, energyPerUse, true) != energyPerUse) {
			return false;
		}
		player.setActiveHand(hand);
		return false;
	}

	public boolean doBeam(ItemStack stack, World world, EntityPlayer player) {

		RayTraceResult traceResult = RayTracer.retrace(player, range, true);

		if (!player.capabilities.isCreativeMode && extractEnergy(stack, energyPerUse, true) != energyPerUse) {
			player.stopActiveHand();
			return false;
		}
		if (traceResult != null) {
			BlockPos pos = traceResult.getBlockPos();

			int x = 0;
			int y = 0;
			int z = 0;
			BlockPos offsetPos;
			IBlockState offsetState;
			List<ItemStack> drops = new ArrayList<>();

			switch (traceResult.sideHit) {
				case DOWN:
				case UP:
					for (x = -1; x <= 1; x++) {
						for (z = -1; z <= 1; z++) {
							offsetPos = pos.add(x, y, z);
							offsetState = world.getBlockState(offsetPos);
							if (offsetState.getPlayerRelativeBlockHardness(player, world, offsetPos) > -1F) {
								drops.addAll(BlockHelper.breakBlock(world, player, offsetPos, offsetState, 0, true, false));
							}
						}
					}
					break;
				case NORTH:
				case SOUTH:
					for (x = -1; x <= 1; x++) {
						for (y = -1; y <= 1; y++) {
							offsetPos = pos.add(x, y, z);
							offsetState = world.getBlockState(offsetPos);
							if (offsetState.getPlayerRelativeBlockHardness(player, world, offsetPos) > -1F) {
								drops.addAll(BlockHelper.breakBlock(world, player, offsetPos, offsetState, 0, true, false));
							}
						}
					}
					break;
				default:
					for (y = -1; y <= 1; y++) {
						for (z = -1; z <= 1; z++) {
							offsetPos = pos.add(x, y, z);
							offsetState = world.getBlockState(offsetPos);
							if (offsetState.getPlayerRelativeBlockHardness(player, world, offsetPos) > -1F) {
								drops.addAll(BlockHelper.breakBlock(world, player, offsetPos, offsetState, 0, true, false));
							}
						}
					}
					break;
			}
			for (ItemStack drop : drops) {
				EntityItem item = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), drop);
				item.setPickupDelay(10);
				world.spawnEntity(item);
			}
		}
		return false;
	}
}
