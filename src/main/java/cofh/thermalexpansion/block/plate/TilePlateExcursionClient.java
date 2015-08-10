package cofh.thermalexpansion.block.plate;

import cofh.repack.codechicken.lib.vec.Vector3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFireworkSparkFX;
import net.minecraft.util.MathHelper;

public class TilePlateExcursionClient extends TilePlateExcursion {

	int ticksElapsed = 0;

	@Override
	public boolean canUpdate() {

		return true;
	}

	@Override
	public void updateEntity() {

		super.updateEntity();
		if (realDist > -1) {
			ticksElapsed++;// += bindY / Math.PI;

			Vector3 vec = getMovementVector();

			Vector3 vecMag = vec.copy().normalize().multiply(.1);
			final double dist = vec.mag() / vecMag.mag();

			double radPer = Math.PI / 6;
			float mul = 2.4F;
		    double[] m = fixPosition(.5, .025, .5);
			Vector3 vecTip = vecMag.copy().add(xCoord + m[0], yCoord + m[1], zCoord + m[2]);
			for (int i = 0; i < 2; ++i) {
				double rad = radPer * (i * 6 + ticksElapsed * 0.4);
				Vector3 vecRot = vecMag.copy().crossProduct(Vector3.one).multiply(mul).rotate(rad, vecMag).add(vecTip);
				//vecTip.add(vecMag);
				EntityFireworkSparkFX spark = new EntityFireworkSparkFX(worldObj, vecRot.x, vecRot.y, vecRot.z, vecMag.x, vecMag.y, vecMag.z,
						Minecraft.getMinecraft().effectRenderer) {

					{
						particleMaxAge = MathHelper.ceiling_double_int(dist) + 1;
					}

					@Override
					public void onUpdate() {

						this.prevPosX = this.posX;
						this.prevPosY = this.posY;
						this.prevPosZ = this.posZ;

						if (this.particleAge++ >= this.particleMaxAge || this.isCollided) {
							this.setDead();
						}

						if (this.particleAge > this.particleMaxAge / 1.5) {
							this.setAlphaF(1F - (this.particleAge - (this.particleMaxAge / 4)) / this.particleMaxAge);

							if (this.hasFadeColour) {
								this.particleRed += (this.fadeColourRed - this.particleRed) * 0.2F;
								this.particleGreen += (this.fadeColourGreen - this.particleGreen) * 0.2F;
								this.particleBlue += (this.fadeColourBlue - this.particleBlue) * 0.2F;
							}
						}

						this.setParticleTextureIndex(this.baseTextureIndex + (7 - this.particleAge * 8 / this.particleMaxAge));
						this.moveEntity(this.motionX, this.motionY, this.motionZ);
					}
				};
				spark.setColour(0x6666FF);
				spark.setFadeColour(0xAAAAFF);
				Minecraft.getMinecraft().effectRenderer.addEffect(spark);
			}
		}
	}

}
