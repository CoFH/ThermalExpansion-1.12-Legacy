package cofh.thermalexpansion.item.tool;

import cofh.api.block.IBlockConfigGui;
import cofh.api.block.IBlockDebug;
import cofh.api.block.IBlockInfo;
import cofh.api.tileentity.ITileInfo;
import cofh.core.chat.ChatHelper;
import cofh.core.item.ItemBase;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemMultimeter extends ItemBase {

	public ItemMultimeter() {

		super("thermalexpansion");
		setMaxStackSize(1);
		setCreativeTab(ThermalExpansion.tabTools);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		if (ItemHelper.getItemDamage(stack) == 0) {
			list.add(StringHelper.getInfoText("info.thermalexpansion.tool.multimeter.0"));
			list.add(StringHelper.getNoticeText("info.thermalexpansion.tool.multimeter.1"));
		}
	}

	@Override
	public boolean hasEffect(ItemStack stack) {

		return ItemHelper.getItemDamage(stack) != 0;
	}

	@Override
	public boolean isFull3D() {

		return false;
	}

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return EnumActionResult.FAIL;
	}

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		boolean r = doItemUse(stack, player, world, pos, side, hitX, hitY, hitZ, hand);
		if (r) { // HACK: forge is fucking stupid with this method
			ServerHelper.sendItemUsePacket(world, pos, side, hand, hitX, hitY, hitZ);
		}
		return r ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
	}

	public boolean doItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing hitSide, float hitX, float hitY, float hitZ, EnumHand hand) {

		player.swingArm(hand);
        IBlockState state = world.getBlockState(pos);
		ArrayList<ITextComponent> info = new ArrayList<ITextComponent>();

		if (ItemHelper.getItemDamage(stack) == 0) { // Multimeter
			if (ServerHelper.isClientWorld(world)) {
				if (state.getBlock() instanceof IBlockConfigGui || state.getBlock() instanceof IBlockInfo) {
					return true;
				}
				TileEntity theTile = world.getTileEntity(pos);
				return theTile instanceof ITileInfo;
			}
			if (player.isSneaking() && state.getBlock() instanceof IBlockConfigGui) {
				if (((IBlockConfigGui) state.getBlock()).openConfigGui(world, pos, hitSide, player)) {
					return true;
				}
			}
			if (state.getBlock() instanceof IBlockInfo) {
				((IBlockInfo) (state.getBlock())).getBlockInfo(world, pos, hitSide, player, info, false);

				ChatHelper.sendIndexedChatMessagesToPlayer(player, info);
				info.clear();
				return true;
			} else {
				TileEntity theTile = world.getTileEntity(pos);
				if (theTile instanceof ITileInfo) {
					if (ServerHelper.isServerWorld(world)) {
						((ITileInfo) theTile).getTileInfo(info, null, player, false);
						ChatHelper.sendIndexedChatMessagesToPlayer(player, info);
					}
					info.clear();
					return true;
				}
			}
			info.clear();
			return false;
		} else { // Debugger
			if (player.isSneaking() && state.getBlock() instanceof IBlockDebug) {
				((IBlockDebug) (state.getBlock())).debugBlock(world, pos, hitSide, player);
				return true;
			} else if (state.getBlock() instanceof IBlockInfo) {
				if (ServerHelper.isClientWorld(world)) {
					info.add(new TextComponentString("-Client-"));
				} else {
					info.add(new TextComponentString("-Server-"));
				}
				((IBlockInfo) (state.getBlock())).getBlockInfo(world, pos, hitSide, player, info, true);
				ChatHelper.sendIndexedChatMessagesToPlayer(player, info);
				info.clear();
				return true;
			} else {
				TileEntity theTile = world.getTileEntity(pos);
				if (theTile instanceof ITileInfo) {
					if (ServerHelper.isServerWorld(world)) {
						((ITileInfo) theTile).getTileInfo(info, null, player, player.isSneaking());
						ChatHelper.sendIndexedChatMessagesToPlayer(player, info);
					}
					info.clear();
					return true;
				}
			}
		}
		info.clear();
		return false;
	}

}
