package cofh.thermalexpansion.block;

import codechicken.lib.raytracer.RayTracer;
import cofh.api.tileentity.IRedstoneControl;
import cofh.api.tileentity.ISecurable;
import cofh.core.block.BlockCoFHBase;
import cofh.core.block.TileCoFHBase;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.RedstoneControlHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.Utils;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

import java.util.ArrayList;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nullable;

public abstract class BlockTEBase extends BlockCoFHBase {

	protected boolean basicGui = true;

	public BlockTEBase(Material material) {

		super(material);
		setSoundType(SoundType.STONE);
		setCreativeTab(ThermalExpansion.tabBlocks);
	}

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileTEBase) {
			((TileTEBase) tile).setInvName(ItemHelper.getNameFromItemStack(stack));
		}
		super.onBlockPlacedBy(world, pos, state, living, stack);
	}

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        RayTraceResult traceResult = RayTracer.retrace(player);
		PlayerInteractEvent event = new PlayerInteractEvent.RightClickBlock(player, hand, heldItem, pos, side, traceResult.hitVec);
		if (MinecraftForge.EVENT_BUS.post(event) || event.getResult() == Result.DENY ) {
			return false;
		}
		if (player.isSneaking()) {
			if (Utils.isHoldingUsableWrench(player, traceResult)) {
				if (ServerHelper.isServerWorld(world) && canDismantle(player, world, pos)) {
					dismantleBlock(player, world, pos, false);
				}
				Utils.usedWrench(player, traceResult);
				return true;
			}
			return false;
		}
		TileTEBase tile = (TileTEBase) world.getTileEntity(pos);

		if (tile == null) {
			return false;
		}
		if (Utils.isHoldingUsableWrench(player, traceResult)) {
			if (ServerHelper.isServerWorld(world)) {
				tile.onWrench(player, side.ordinal());
			}
			Utils.usedWrench(player, traceResult);
			return true;
		}
		if (basicGui) {
			if (ServerHelper.isServerWorld(world)) {
				return tile.openGui(player);
			}
			return tile.hasGui();
		}
		return false;
	}

	//@Override
	//public IIcon getIcon(int side, int metadata) {
	//	return BlockGlass.TEXTURE[0];
	//}

	@Override
    public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

		TileEntity tile = world.getTileEntity(pos);

		NBTTagCompound retTag = null;

		if (tile instanceof TileTEBase && (!((TileTEBase) tile).tileName.isEmpty())) {
			retTag = ItemHelper.setItemStackTagName(retTag, ((TileTEBase) tile).tileName);
		}
		if (tile instanceof TileInventory && ((TileInventory) tile).isSecured()) {
			retTag = SecurityHelper.setItemStackTagSecure(retTag, (ISecurable) tile);
		}
		if (tile instanceof IRedstoneControl) {
			retTag = RedstoneControlHelper.setItemStackTagRS(retTag, (IRedstoneControl) tile);
		}
		return retTag;
	}

	/* Dismantle Helper */
	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, NBTTagCompound nbt, IBlockAccess blockAccess, BlockPos pos, boolean returnDrops, boolean simulate) {

		TileEntity tile = blockAccess.getTileEntity(pos);
        IBlockState state = blockAccess.getBlockState(pos);
		int bMeta = state.getBlock().getMetaFromState(state);

		ItemStack dropBlock = new ItemStack(this, 1, bMeta);

		if (nbt != null && !nbt.hasNoTags()) {
			dropBlock.setTagCompound(nbt);
		}
		if (!simulate) {
            World world = (World) blockAccess;
			if (tile instanceof TileCoFHBase) {
				((TileCoFHBase) tile).blockDismantled();
			}
			world.setBlockToAir(pos);

			if (!returnDrops) {
				float f = 0.3F;
				double x2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				double y2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				double z2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				EntityItem item = new EntityItem(world, pos.getX() + x2, pos.getY() + y2, pos.getZ() + z2, dropBlock);
				item.delayBeforeCanPickup = 10;
				if (tile instanceof ISecurable && !((ISecurable) tile).getAccess().isPublic()) {
					item.setOwner(player.getName());
					// set owner (not thrower) - ensures wrenching player can pick it up first
				}
				world.spawnEntityInWorld(item);

				if (player != null) {
					CoreUtils.dismantleLog(player.getName(), this, bMeta, pos);
				}
			}
		}
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		ret.add(dropBlock);
		return ret;
	}

}
