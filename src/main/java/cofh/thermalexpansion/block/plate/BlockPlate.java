package cofh.thermalexpansion.block.plate;

import cofh.api.block.IBlockConfigGui;
import cofh.core.render.IconRegistry;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.util.crafting.TECraftingHandler;
import cofh.thermalexpansion.util.crafting.TransposerManager;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import thermalfoundation.fluid.TFFluids;

public class BlockPlate extends BlockTEBase implements IBlockConfigGui {

	private static class PlateMaterial extends Material {

		public PlateMaterial(MapColor color) {

			super(color);
			this.setRequiresTool();
		}

		@Override
		public boolean isSolid() {

			return false;
		}
	}

	public static final Material material = new PlateMaterial(MapColor.ironColor);

	public BlockPlate() {

		super(material);
		setBlockBounds(0, 0, 0, 1, 0.0625F, 1);
		setHardness(15.0F);
		setResistance(25.0F);
		setBlockName("thermalexpansion.plate");
		basicGui = false;
	}

	@Override
	public boolean openConfigGui(IBlockAccess world, int x, int y, int z, ForgeDirection side, EntityPlayer player) {

		return ((TilePlateBase) world.getTileEntity(x, y, z)).openGui(player);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		if (metadata >= Types.values().length) {
			return null;
		}
		switch (Types.values()[metadata]) {
		case FRAME:
			return new TilePlateBase();
		case SIGNAL:
			return new TilePlateSignal();
		case IMPULSE:
			return new TilePlateImpulse();
		case TRANSLOCATE:
			return new TilePlateTranslocate();
		default:
			return null;
		}
	}

	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis_fd) {

		boolean r = false;
		l: {
			if (world.isRemote) {
				break l;
			}
			TilePlateBase tile = (TilePlateBase) world.getTileEntity(x, y, z);
			if (tile == null) {
				break l;
			}
			tile.rotated();
			int axis = axis_fd.ordinal();
			if ((axis >> 1) == (tile.alignment >> 1)) {
				tile.direction = (byte) ((tile.direction + 1) % 6);
				r = true;
			} else {
				// TODO: rotate to next valid alignment
			}
		}
		if (r) {
			world.markBlockForUpdate(x, y, z);
		}
		return r;
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {

		for (int i = 0; i < Types.values().length; i++) {
			if (enable[i]) {
				list.add(new ItemStack(item, 1, i));
			}
		}
	}

	@Override
	public void onEntityWalking(World world, int x, int y, int z, Entity entity) {

		// onEntityCollidedWithBlock(world, x, y, z, entity);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {

		TilePlateBase tile = (TilePlateBase) world.getTileEntity(x, y, z);
		if (tile == null) {
			return;
		}

		AxisAlignedBB bb = entity.boundingBox;
		if (!bb.intersectsWith(getCollisionBlockBounds(tile, x, y, z))) {
			return;
		}
		tile.onEntityCollidedWithBlock(entity);
	}

	public AxisAlignedBB getCollisionBlockBounds(TilePlateBase theTile, int x, int y, int z) {

		float A = 1 / 16f;
		float B = 15 / 16f;
		AxisAlignedBB bb = null;
		switch (theTile.alignment) {
		case 0:
			bb = AxisAlignedBB.getBoundingBox(A, 0, A, B, 2 / 16f, B).offset(x, y, z);
			break;
		case 1:
			bb = AxisAlignedBB.getBoundingBox(A, 14 / 16f, A, B, 1, B).offset(x, y, z);
			break;
		case 2:
			bb = AxisAlignedBB.getBoundingBox(A, A, 0, B, B, 2 / 16f).offset(x, y, z);
			break;
		case 3:
			bb = AxisAlignedBB.getBoundingBox(A, A, 14 / 16f, B, B, 1).offset(x, y, z);
			break;
		case 4:
			bb = AxisAlignedBB.getBoundingBox(0, A, A, 2 / 16f, B, B).offset(x, y, z);
			break;
		case 5:
			bb = AxisAlignedBB.getBoundingBox(14 / 16f, A, A, 1, B, B).offset(x, y, z);
			break;
		default:
		}
		return bb;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {

		TilePlateBase theTile = (TilePlateBase) world.getTileEntity(x, y, z);
		if (theTile != null) {
			switch (theTile.alignment) {
			case 0:
				setBlockBounds(0, 0, 0, 1, 1 / 16f, 1);
				return;
			case 1:
				setBlockBounds(0, 15 / 16f, 0, 1, 1, 1);
				return;
			case 2:
				setBlockBounds(0, 0, 0, 1, 1, 1 / 16f);
				return;
			case 3:
				setBlockBounds(0, 0, 15 / 16f, 1, 1, 1);
				return;
			case 4:
				setBlockBounds(0, 0, 0, 1 / 16f, 1, 1);
				return;
			case 5:
				setBlockBounds(15 / 16f, 0, 0, 1, 1, 1);
				return;
			default:
			}
		}
	}

	@Override
	public int getRenderType() {

		return TEProps.renderIdPlate;
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 vec3d, Vec3 vec3d1) {

		setBlockBoundsBasedOnState(world, x, y, z);
		return super.collisionRayTrace(world, x, y, z, vec3d, vec3d1);
	}

	@Override
	protected AxisAlignedBB getStatelessBoundingBox(World world, int x, int y, int z) {

		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		IconRegistry.addIcon("PlateBottom", "thermalexpansion:plate/Plate_Bottom", ir);
		IconRegistry.addIcon("PlateTop0", "thermalexpansion:plate/Plate_Top_Down", ir);
		IconRegistry.addIcon("PlateTop1", "thermalexpansion:plate/Plate_Top_Up", ir);
		IconRegistry.addIcon("PlateTop2", "thermalexpansion:plate/Plate_Top_North", ir);
		IconRegistry.addIcon("PlateTop3", "thermalexpansion:plate/Plate_Top_South", ir);
		IconRegistry.addIcon("PlateTop4", "thermalexpansion:plate/Plate_Top_West", ir);
		IconRegistry.addIcon("PlateTop5", "thermalexpansion:plate/Plate_Top_East", ir);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		TilePlateBase.initialize();
		TilePlateSignal.initialize();
		TilePlateImpulse.initialize();
		TilePlateTranslocate.initialize();

		plateFrame = new ItemStack(this, 1, Types.FRAME.ordinal());
		plateSignal = new ItemStack(this, 1, Types.SIGNAL.ordinal());
		plateImpulse = new ItemStack(this, 1, Types.IMPULSE.ordinal());
		plateTranslocate = new ItemStack(this, 1, Types.TRANSLOCATE.ordinal());

		GameRegistry.registerCustomItemStack("plateFrame", plateFrame);
		GameRegistry.registerCustomItemStack("plateSignal", plateSignal);
		GameRegistry.registerCustomItemStack("plateImpulse", plateImpulse);
		GameRegistry.registerCustomItemStack("plateTranslocate", plateTranslocate);

		return true;
	}

	@Override
	public boolean postInit() {

		ItemHelper.addRecipe(new ShapedOreRecipe(plateFrame, new Object[] { "SGS", "I I", "SIS", 'S', "ingotSignalum", 'G', "blockGlassHardened", 'I',
				"ingotInvar", }));

		if (enable[Types.SIGNAL.ordinal()]) {
			TransposerManager.addTEFillRecipe(2000, plateFrame, plateSignal, new FluidStack(TFFluids.fluidRedstone, 1000), false);
		}

		if (enable[Types.IMPULSE.ordinal()]) {
			TransposerManager.addTEFillRecipe(2000, plateFrame, plateImpulse, new FluidStack(TFFluids.fluidGlowstone, 1000), false);
		}

		if (enable[Types.TRANSLOCATE.ordinal()]) {
			TransposerManager.addTEFillRecipe(2000, plateFrame, plateTranslocate, new FluidStack(TFFluids.fluidEnder, 1000), false);
		}

		TECraftingHandler.addSecureRecipe(plateSignal);
		TECraftingHandler.addSecureRecipe(plateImpulse);
		TECraftingHandler.addSecureRecipe(plateTranslocate);

		return true;
	}

	public static enum Types {
		FRAME, SIGNAL, IMPULSE, TRANSLOCATE
	}

	public static final String[] NAMES = { "frame", "signal", "impulse", "translocate" };
	public static boolean[] enable = new boolean[Types.values().length];

	static {
		String category = "block.feature";
		enable[Types.FRAME.ordinal()] = true;
		enable[Types.SIGNAL.ordinal()] = ThermalExpansion.config.get(category, "Plate.Signal", true);
		enable[Types.IMPULSE.ordinal()] = ThermalExpansion.config.get(category, "Plate.Impulse", true);
		enable[Types.TRANSLOCATE.ordinal()] = ThermalExpansion.config.get(category, "Plate.Translocate", true);
	}

	public static ItemStack plateFrame;
	public static ItemStack plateSignal;
	public static ItemStack plateImpulse;
	public static ItemStack plateTranslocate;

}
