package cofh.thermalexpansion.block;

import codechicken.lib.render.particle.CustomParticleHandler;
import cofh.api.block.IConfigGui;
import cofh.core.block.BlockCoreTile;
import cofh.core.block.TileAugmentableSecure;
import cofh.core.block.TileNameable;
import cofh.core.util.CoreUtils;
import cofh.core.util.RayTracer;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.core.util.helpers.WrenchHelper;
import cofh.thermalexpansion.ThermalExpansion;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockTEBase extends BlockCoreTile implements IConfigGui {

	protected boolean standardGui = true;
	protected boolean configGui = false;

	protected BlockTEBase(Material material) {

		super(material, "thermalexpansion");
		setSoundType(SoundType.STONE);
		setCreativeTab(ThermalExpansion.tabCommon);
	}

	/* BLOCK METHODS */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {

		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileNameable) {
			((TileNameable) tile).setCustomName(ItemHelper.getNameFromItemStack(stack));
		}
		super.onBlockPlacedBy(world, pos, state, living, stack);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

		RayTraceResult traceResult = RayTracer.retrace(player);

		if (traceResult == null) {
			return false;
		}
		PlayerInteractEvent event = new PlayerInteractEvent.RightClickBlock(player, hand, pos, side, traceResult.hitVec);
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
		TileNameable tile = (TileNameable) world.getTileEntity(pos);

		if (tile == null || tile.isInvalid()) {
			return false;
		}
		if (WrenchHelper.isHoldingUsableWrench(player, traceResult)) {
			if (tile.canPlayerAccess(player)) {
				if (ServerHelper.isServerWorld(world)) {
					tile.onWrench(player, side);
				}
				WrenchHelper.usedWrench(player, traceResult);
			}
			return true;
		}
		if (onBlockActivatedDelegate(world, pos, state, player, hand, side, hitX, hitY, hitZ)) {
			return true;
		}
		if (standardGui && ServerHelper.isServerWorld(world)) {
			return tile.openGui(player);
		}
		return standardGui;
	}

	@Override
	public float getBlockHardness(IBlockState state, World world, BlockPos pos) {

		TileNameable tile = (TileNameable) world.getTileEntity(pos);

		if (tile instanceof TileAugmentableSecure) {
			return ((TileAugmentableSecure) tile).isCreative ? HARDNESS_CREATIVE : HARDNESS[(((TileAugmentableSecure) tile).getLevel()) % HARDNESS.length];
		}
		return blockHardness;
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {

		TileNameable tile = (TileNameable) world.getTileEntity(pos);

		if (tile instanceof TileAugmentableSecure) {
			return ((TileAugmentableSecure) tile).isCreative ? RESISTANCE_CREATIVE : RESISTANCE[(((TileAugmentableSecure) tile).getLevel()) % RESISTANCE.length];
		}
		return blockResistance / 5.0F;
	}

	public boolean onBlockActivatedDelegate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

		return false;
	}

	@Override
	public boolean openConfigGui(World world, BlockPos pos, EnumFacing side, EntityPlayer player) {

		TileNameable tile = (TileNameable) world.getTileEntity(pos);

		if (tile == null || tile.isInvalid()) {
			return false;
		}
		if (configGui && ServerHelper.isServerWorld(world)) {
			return tile.openConfigGui(player);
		}
		return configGui;
	}

	/* RENDERING METHODS */
	@Override
	public boolean addLandingEffects(IBlockState state, WorldServer world, BlockPos pos, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles) {

		return CustomParticleHandler.handleLandingEffects(world, pos, entity, numberOfParticles);
	}

	@Override
	@SideOnly (Side.CLIENT)
	public boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity) {

		return CustomParticleHandler.handleRunningEffects(world, pos, state, entity);
	}

	@Override
	@SideOnly (Side.CLIENT) // Because vanilla removed state and side based particle textures in 1.8..
	public boolean addHitEffects(IBlockState state, World world, RayTraceResult trace, ParticleManager manager) {

		return CustomParticleHandler.handleHitEffects(state, world, trace, manager);
	}

	@Override
	@SideOnly (Side.CLIENT)
	public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {

		return CustomParticleHandler.handleDestroyEffects(world, pos, manager);
	}

	/* IDismantleable */
	@Override
	public boolean canDismantle(World world, BlockPos pos, IBlockState state, EntityPlayer player) {

		TileNameable tile = (TileNameable) world.getTileEntity(pos);

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
