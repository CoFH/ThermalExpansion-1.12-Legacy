package cofh.thermalexpansion.block;

import codechicken.lib.render.particle.CustomParticleHandler;
import codechicken.lib.texture.IWorldBlockTextureProvider;
import cofh.api.energy.IEnergyHandler;
import cofh.api.tileentity.IAugmentable;
import cofh.api.tileentity.IRedstoneControl;
import cofh.api.tileentity.ISecurable;
import cofh.core.block.BlockCoreTile;
import cofh.core.block.TileCore;
import cofh.core.util.CoreUtils;
import cofh.lib.util.RayTracer;
import cofh.lib.util.helpers.*;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.ReconfigurableHelper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
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

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {

		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileTEBase) {
			((TileTEBase) tile).setName(ItemHelper.getNameFromItemStack(stack));
		}
		super.onBlockPlacedBy(world, pos, state, living, stack);
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

	/* HELPERS */
	@Override
	public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

		TileEntity tile = world.getTileEntity(pos);
		NBTTagCompound retTag = new NBTTagCompound();

		if (tile instanceof TileTEBase && (!((TileTEBase) tile).tileName.isEmpty())) {
			retTag = ItemHelper.setItemStackTagName(retTag, ((TileTEBase) tile).tileName);
		}
		if (tile instanceof TileAugmentableSecure) {
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
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
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
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		ret.add(dropBlock);
		return ret;
	}

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
	@SideOnly (Side.CLIENT)//Because vanilla removed state and side based particle textures in 1.8..
	public boolean addHitEffects(IBlockState state, World world, RayTraceResult trace, ParticleManager manager) {

		if (this instanceof IWorldBlockTextureProvider) {
			CustomParticleHandler.addHitEffects(state, world, trace, manager, ((IWorldBlockTextureProvider) this));
			return true;
		}
		return false;
	}

	/* SIDE CONFIG */
	public enum EnumSideConfig implements IStringSerializable {

		// @formatter:off
		NONE(0, "none"),
		BLUE(1, "blue"),
		RED(2, "red"),
		YELLOW(3, "yellow"),
		ORANGE(4, "orange"),
		GREEN(5, "green"),
		PURPLE(6, "purple"),
		OPEN(7, "open");
		// @formatter:on

		private final int index;
		private final String name;

		EnumSideConfig(int index, String name) {

			this.index = index;
			this.name = name;
		}

		public int getIndex() {

			return this.index;
		}

		@Override
		public String getName() {

			return this.name;
		}

		public static final BlockTEBase.EnumSideConfig[] VALUES = new BlockTEBase.EnumSideConfig[values().length];

		static {
			for (EnumSideConfig config : values()) {
				VALUES[config.getIndex()] = config;
			}
		}

	}

}
