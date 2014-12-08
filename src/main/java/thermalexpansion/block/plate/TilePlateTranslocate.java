package thermalexpansion.block.plate;

import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.Entity;

public class TilePlateTranslocate extends TilePlateBase {

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateTranslocate.class, "cofh.thermalexpansion.PlateTranslocate");
	}

	byte distance = 16;

	@Override
	public int getType() {

		return BlockPlate.Types.TRANSLOCATE.ordinal();
	}

	@Override
	public void onEntityCollidedWithBlock(Entity entity) {


		double[] v = getVector((double)distance);
		double x = entity.posX;//MathHelper.floor_double(entity.posX); // because minecraft is stupid on the client (where it matters)
		double y = entity.posY;//MathHelper.floor_double(entity.posY); // the player's x/y/z pos is their eyes, and attempting to move
		double z = entity.posZ;//MathHelper.floor_double(entity.posZ); // them to x/y/zCoord tries to put them in the ground. so round
		//entity.setPosition(x + v[0] + .5, y + v[1] + .125, z + v[2] + .5);
		entity.setPosition(x + v[0], y + v[1], z + v[2]);
		// theEntity.setPositionAndRotation(par1, par3, par5, par7, par8)
	}

}
