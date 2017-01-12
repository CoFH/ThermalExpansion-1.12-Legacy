package cofh.thermalexpansion.block;

import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.render.particle.CustomParticleHandler;
import codechicken.lib.texture.IWorldBlockTextureProvider;
import cofh.api.tileentity.IRedstoneControl;
import cofh.api.tileentity.ISecurable;
import cofh.core.block.BlockCoFHBaseOld;
import cofh.core.block.TileCoFHBaseOld;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.*;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.Utils;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public abstract class BlockTEBase extends BlockCoFHBaseOld {

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
            if (WrenchHelper.isHoldingUsableWrench(player, traceResult)) {
                if (ServerHelper.isServerWorld(world)) {
                    dismantleBlock(world, pos, state, player, false);
                    WrenchHelper.usedWrench(player, traceResult);
                }
                return true;
            }
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

	/* Drop Helper */
	public ArrayList<ItemStack> dropDelegate(NBTTagCompound nbt, IBlockAccess world, BlockPos pos, int fortune) {
	    return dismantleDelegate(nbt, (World) world, pos, null, false, true);
    }

	/* Dismantle Helper */
	@Override
	public ArrayList<ItemStack> dismantleDelegate(NBTTagCompound nbt, World world, BlockPos pos, EntityPlayer player, boolean returnDrops, boolean simulate) {

		TileEntity tile = world.getTileEntity(pos);
        IBlockState state = world.getBlockState(pos);
		int bMeta = state.getBlock().getMetaFromState(state);

		ItemStack dropBlock = new ItemStack(this, 1, bMeta);

		if (nbt != null && !nbt.hasNoTags()) {
			dropBlock.setTagCompound(nbt);
		}
		if (!simulate) {
			if (tile instanceof TileCoFHBaseOld) {
				((TileCoFHBaseOld) tile).blockDismantled();
			}
			world.setBlockToAir(pos);

			if (!returnDrops) {
				float f = 0.3F;
				double x2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				double y2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				double z2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				EntityItem item = new EntityItem(world, pos.getX() + x2, pos.getY() + y2, pos.getZ() + z2, dropBlock);
				item.setPickupDelay(10);
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

    @Override
    @SideOnly(Side.CLIENT)//Because vanilla removed state and side based particle textures in 1.8..
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult trace, ParticleManager manager) {
        if (this instanceof IWorldBlockTextureProvider) {
            CustomParticleHandler.addHitEffects(state, world, trace, manager, ((IWorldBlockTextureProvider) this));
            return true;
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        if (this instanceof IWorldBlockTextureProvider) {
            CustomParticleHandler.addDestroyEffects(world, pos, manager, (IWorldBlockTextureProvider) this);
            return true;
        }
        return false;
    }
}
