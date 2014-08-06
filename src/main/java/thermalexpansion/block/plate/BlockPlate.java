package thermalexpansion.block.plate;

import cofh.core.render.IconRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.BlockTEBase;
import thermalexpansion.core.TEProps;

public class BlockPlate extends BlockTEBase {

	public BlockPlate() {

		super(Material.iron);
		setBlockBounds(0, 0, 0, 1, 0.0625F, 1);
		setHardness(15.0F);
		setResistance(25.0F);
		setBlockName("thermalexpansion.plate");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		if (metadata >= Types.values().length) {
			return null;
		}
		switch (Types.values()[metadata]) {
		case SIGNAL:
			// return new TilePlateSignal();
		case IMPULSE:
			// return new TilePlateImpulse();
		case TRANSLOCATE:
			// return new TilePlateTranslocate();
		default:
			return null;
		}
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
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {

		super.onBlockPlacedBy(world, x, y, z, living, stack);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {

		TilePlateBase tile = (TilePlateBase) world.getTileEntity(x, y, z);
		if (tile == null) {
			return;
		}
		tile.onEntityCollidedWithBlock(entity);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {

		TilePlateBase theTile = (TilePlateBase) world.getTileEntity(x, y, z);
		if (theTile != null) {
			switch (theTile.alignment) {
			case 0:
				setBlockBounds(0, 0, 0, 1, 0.0625F, 1);
				return;
			case 1:
				setBlockBounds(0, 0.9375F, 0, 1, 1, 1);
				return;
			case 2:
				setBlockBounds(0, 0, 0, 1, 1, 0.0625F);
				return;
			case 3:
				setBlockBounds(0, 0, 0.9375F, 1, 1, 1);
				return;
			case 4:
				setBlockBounds(0, 0, 0, 0.0625F, 1, 1);
				return;
			case 5:
				setBlockBounds(0.9375F, 0, 0, 1, 1, 1);
				return;
			default:
			}
		}
	}

	@Override
	public int getRenderType() {

		return TEProps.renderIdPlate;
	}

	// @Override
	// public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
	//
	// return AxisAlignedBB.getAABBPool().getAABB(x, y, z, x + 1, y + 0.0625, z + 1);
	// }

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 vec3d, Vec3 vec3d1) {

		setBlockBoundsBasedOnState(world, x, y, z);
		return super.collisionRayTrace(world, x, y, z, vec3d, vec3d1);
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

		// TilePlateSignal.initialize();
		// TilePlateImpulse.initialize();
		// TilePlateTranslocate.initialize();

		signalPlate = new ItemStack(this, 1, Types.SIGNAL.ordinal());
		impulsePlate = new ItemStack(this, 1, Types.IMPULSE.ordinal());
		translocatePlate = new ItemStack(this, 1, Types.TRANSLOCATE.ordinal());

		GameRegistry.registerCustomItemStack("plateSignal", signalPlate);
		GameRegistry.registerCustomItemStack("plateImpulse", impulsePlate);
		GameRegistry.registerCustomItemStack("plateTranslocate", translocatePlate);

		return true;
	}

	@Override
	public boolean postInit() {

		return true;
	}

	public static enum Types {
		SIGNAL, IMPULSE, TRANSLOCATE
	}

	public static final String[] NAMES = { "signal", "impulse", "translocate" };
	public static boolean[] enable = new boolean[Types.values().length];

	static {
		String category = "block.feature";
		enable[Types.SIGNAL.ordinal()] = ThermalExpansion.config.get(category, "Plate.Signal", true);
		enable[Types.IMPULSE.ordinal()] = ThermalExpansion.config.get(category, "Plate.Impulse", true);
		enable[Types.TRANSLOCATE.ordinal()] = ThermalExpansion.config.get(category, "Plate.Translocate", true);
	}

	public static ItemStack signalPlate;
	public static ItemStack impulsePlate;
	public static ItemStack translocatePlate;

}
