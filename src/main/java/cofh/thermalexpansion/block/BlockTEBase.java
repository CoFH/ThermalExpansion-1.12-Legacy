package cofh.thermalexpansion.block;

import codechicken.lib.render.particle.CustomParticleHandler;
import codechicken.lib.texture.IWorldBlockTextureProvider;
import cofh.api.core.IAugmentable;
import cofh.api.core.ISecurable;
import cofh.api.energy.IEnergyHandler;
import cofh.core.block.BlockCoreTile;
import cofh.core.block.TileCore;
import cofh.core.util.CoreUtils;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.RedstoneControlHelper;
import cofh.core.util.helpers.SecurityHelper;
import cofh.core.util.tileentity.IRedstoneControl;
import cofh.lib.util.RayTracer;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.WrenchHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.ReconfigurableHelper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;

public abstract class BlockTEBase extends BlockCoreTile {

	protected boolean basicGui = true;

	public BlockTEBase(Material material) {

		super(material);
		setSoundType(SoundType.STONE);
		setCreativeTab(ThermalExpansion.tabCommon);
	}

	/* BLOCK METHODS */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {

		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileTEBase) {
			((TileTEBase) tile).setName(ItemHelper.getNameFromItemStack(stack));
		}
		super.onBlockPlacedBy(world, pos, state, living, stack);
	}

	@Override
	public boolean addLandingEffects(IBlockState state, WorldServer worldObj, BlockPos blockPosition, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles) {

		return true;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

		RayTraceResult traceResult = RayTracer.retrace(player);
		PlayerInteractEvent event = new PlayerInteractEvent.RightClickBlock(player, hand, heldItem, pos, side, traceResult.hitVec);
		if (MinecraftForge.EVENT_BUS.post(event) || event.getResult() == Result.DENY) {
			return false;
		}
		if (player.isSneaking()) {
			if (WrenchHelper.isHoldingUsableWrench(player, traceResult)) {
				if (ServerHelper.isServerWorld(world) && canDismantle(world, pos, state, player)) {
					dismantleBlock(world, pos, state, player, false);
					WrenchHelper.usedWrench(player, traceResult);
				}
				return true;
			}
		}
		TileTEBase tile = (TileTEBase) world.getTileEntity(pos);

		if (tile == null || tile.isInvalid()) {
			return false;
		}
		if (WrenchHelper.isHoldingUsableWrench(player, traceResult)) {
			if (ServerHelper.isServerWorld(world)) {
				tile.onWrench(player, side);
			}
			WrenchHelper.usedWrench(player, traceResult);
			return true;
		}
		if (basicGui && ServerHelper.isServerWorld(world)) {
			return tile.openGui(player);
		}
		return basicGui;
	}

	@Override
	public float getBlockHardness(IBlockState state, World world, BlockPos pos) {

		TileTEBase tile = (TileTEBase) world.getTileEntity(pos);

		if (tile instanceof TileAugmentableSecure) {
			return ((TileAugmentableSecure) tile).isCreative ? HARDNESS_CREATIVE : HARDNESS[(((TileAugmentableSecure) tile).getLevel()) % HARDNESS.length];
		}
		return blockHardness;
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {

		TileTEBase tile = (TileTEBase) world.getTileEntity(pos);

		if (tile instanceof TileAugmentableSecure) {
			return ((TileAugmentableSecure) tile).isCreative ? RESISTANCE_CREATIVE : RESISTANCE[(((TileAugmentableSecure) tile).getLevel()) % RESISTANCE.length];
		}
		return blockResistance / 5.0F;
	}

	/* RENDERING METHODS */
	@Override
	@SideOnly (Side.CLIENT)
	public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {

		if (this instanceof IWorldBlockTextureProvider) {
			CustomParticleHandler.addDestroyEffects(world, pos, manager, (IWorldBlockTextureProvider) this);
			return true;
		}
		return false;
	}

	@Override
	@SideOnly (Side.CLIENT) // Because vanilla removed state and side based particle textures in 1.8..
	public boolean addHitEffects(IBlockState state, World world, RayTraceResult trace, ParticleManager manager) {

		if (this instanceof IWorldBlockTextureProvider) {
			CustomParticleHandler.addHitEffects(state, world, trace, manager, ((IWorldBlockTextureProvider) this));
			return true;
		}
		return false;
	}

	/* HELPERS */
	@Override
	public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

		TileEntity tile = world.getTileEntity(pos);
		NBTTagCompound retTag = new NBTTagCompound();

		if (tile instanceof TileTEBase && (!((TileTEBase) tile).tileName.isEmpty())) {
			retTag = ItemHelper.setItemStackTagName(retTag, ((TileTEBase) tile).tileName);
		}
		if (tile instanceof TileAugmentableSecure) {
			retTag.setBoolean("Creative", ((TileAugmentableSecure) tile).isCreative);
			retTag.setByte("Level", (byte) ((TileAugmentableSecure) tile).getLevel());
			if (((TileAugmentableSecure) tile).isSecured()) {
				retTag = SecurityHelper.setItemStackTagSecure(retTag, (ISecurable) tile);
			}
		}
		if (tile instanceof IAugmentable) {
			retTag = AugmentHelper.setItemStackTagAugments(retTag, (IAugmentable) tile);
		}
		if (tile instanceof IRedstoneControl) {
			retTag = RedstoneControlHelper.setItemStackTagRS(retTag, (IRedstoneControl) tile);
		}
		if (tile instanceof TileReconfigurable) {
			retTag = ReconfigurableHelper.setItemStackTagReconfig(retTag, (TileReconfigurable) tile);
		}
		if (tile instanceof IEnergyHandler) {
			retTag.setInteger("Energy", ((IEnergyHandler) tile).getEnergyStored(null));
		}
		return retTag;
	}

	@Override
	public ArrayList<ItemStack> dropDelegate(NBTTagCompound nbt, IBlockAccess world, BlockPos pos, int fortune) {

		IBlockState state = world.getBlockState(pos);
		int meta = state.getBlock().getMetaFromState(state);

		ItemStack dropBlock = new ItemStack(this, 1, meta);

		if (nbt != null) {
			dropBlock.setTagCompound(nbt);
		}
		ArrayList<ItemStack> ret = new ArrayList<>();
		ret.add(dropBlock);
		return ret;
	}

	@Override
	public ArrayList<ItemStack> dismantleDelegate(NBTTagCompound nbt, World world, BlockPos pos, EntityPlayer player, boolean returnDrops, boolean simulate) {

		TileEntity tile = world.getTileEntity(pos);
		IBlockState state = world.getBlockState(pos);
		int meta = state.getBlock().getMetaFromState(state);

		ItemStack dropBlock = new ItemStack(this, 1, meta);

		if (nbt != null) {
			dropBlock.setTagCompound(nbt);
		}
		if (!simulate) {
			if (tile instanceof TileCore) {
				((TileCore) tile).blockDismantled();
			}
			world.setBlockToAir(pos);

			if (!returnDrops) {
				float f = 0.3F;
				double x2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				double y2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				double z2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				EntityItem dropEntity = new EntityItem(world, pos.getX() + x2, pos.getY() + y2, pos.getZ() + z2, dropBlock);
				dropEntity.setPickupDelay(10);
				if (tile instanceof ISecurable && !((ISecurable) tile).getAccess().isPublic()) {
					dropEntity.setOwner(player.getName());
					// Set Owner - ensures dismantling player can pick it up first.
				}
				world.spawnEntityInWorld(dropEntity);

				if (player != null) {
					CoreUtils.dismantleLog(player.getName(), state.getBlock(), meta, pos);
				}
			}
		}
		ArrayList<ItemStack> ret = new ArrayList<>();
		ret.add(dropBlock);
		return ret;
	}

	/* IDismantleable */
	@Override
	public boolean canDismantle(World world, BlockPos pos, IBlockState state, EntityPlayer player) {

		TileTEBase tile = (TileTEBase) world.getTileEntity(pos);

		if (tile instanceof TileAugmentableSecure && ((TileAugmentableSecure) tile).isCreative && !CoreUtils.isOp(player)) {
			return false;
		}
		return super.canDismantle(world, pos, state, player);
	}

	public static final float[] HARDNESS = { 5.0F, 10.0F, 10.0F, 15.0F, 20.0F };
	public static final int[] RESISTANCE = { 15, 30, 30, 45, 120 };

	public static final float HARDNESS_CREATIVE = -1.0F;
	public static final float RESISTANCE_CREATIVE = 1200;

}
