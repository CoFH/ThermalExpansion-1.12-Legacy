package cofh.thermalexpansion.block.plate;

import cofh.repack.codechicken.lib.vec.Vector3;


public class TilePlateExcursion extends TilePlatePoweredBase {

	public TilePlateExcursion() {

		super(BlockPlate.Types.POWERED_IMPULSE, 200000);
	}

	int bindX, bindY = -1, bindZ;
	int ticksElapsed = 0;

	@Override
	public void updateEntity() {
		if(bindY > -1) {
			ticksElapsed++;

			Vector3 vec = getMovementVector();

			double dist = 0.1;
			int size = (int) (vec.mag() / dist);
			int count = 10;
			int start = ticksElapsed % size;

			Vector3 vecMag = vec.copy().normalize().multiply(dist);
			Vector3 vecTip = vecMag.copy().multiply(start).add(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);

			double radPer = Math.PI / 16.0;
			float mul = 0.5F;
			float mulPer = 0.4F;
			float maxMul = 2;
			for(int i = start; i < start + count; i++) { // <- Replace to i = 0; i < size
				mul = Math.min(maxMul, mul + mulPer);
				double rad = radPer * (i + ticksElapsed * 0.4);
				Vector3 vecRot = vecMag.copy().crossProduct(Vector3.one).multiply(mul).rotate(rad, vecMag).add(vecTip);
				//Botania.proxy.wispFX(worldObj, vecRot.x, vecRot.y, vecRot.z, 0.4F, 0.4F, 1F, 0.1F, (float) -vecMag.x, (float) -vecMag.y, (float) -vecMag.z, 1F);
				vecTip.add(vecMag);
			}
		}
	}

	public Vector3 getMovementVector() {
		return new Vector3(bindX - xCoord, bindY - yCoord, bindZ - zCoord);
	}

}
