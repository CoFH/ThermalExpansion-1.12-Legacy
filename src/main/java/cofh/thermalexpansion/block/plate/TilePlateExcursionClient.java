package cofh.thermalexpansion.block.plate;

import codechicken.lib.vec.Vector3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;

public class TilePlateExcursionClient extends TilePlateExcursion implements ITickable {

	int ticksElapsed = 0;


	@Override
	public void update() {

		if (realDist > -1) {
			ticksElapsed++;

			Vector3 vec = new Vector3(fixVector(0, realDist + .65, 0));

			Vector3 vecMag = vec.copy().normalize().multiply(.1);
			final double dist = vec.mag() / vecMag.mag();

			double radPer = Math.PI / 6;
			float mul = 2.4F;
			double[] m = fixPosition(.5, .025 + (redstoneControlOrDisable() ? 0 : realDist + .65), .5);
			Vector3 vecTip = vecMag.copy().add(getPos()).add(m[0], m[1], m[2]);
			for (int i = 0; i < 2; ++i) {
				if (((ticksElapsed + i * 2) & 3) == 3) continue;
				double rad = radPer * (i * 6 + (ticksElapsed) * 0.4);
				Vector3 vecRot = vecMag.copy().crossProduct(Vector3.one).multiply(mul).rotate(rad, vecMag).add(vecTip);
				// vecTip.add(vecMag);
                ParticleFirework.Spark spark = new ParticleFirework.Spark(worldObj, vecRot.x, vecRot.y, vecRot.z, vecMag.x, vecMag.y, vecMag.z,
						Minecraft.getMinecraft().effectRenderer) {

					{
						particleMaxAge = MathHelper.ceiling_double_int(dist) + 1;
						particleAge = TilePlateExcursionClient.this.redstoneControlOrDisable() ? 0 : particleMaxAge - 1;
					}

					private float red, green, blue;

				    @Override
					public void setRBGColorF(float p_70538_1_, float p_70538_2_, float p_70538_3_) {
				        this.particleRed = red = p_70538_1_;
				        this.particleGreen = green = p_70538_2_;
				        this.particleBlue = blue = p_70538_3_;
				    }

				    //@Override
				    public void setEating(boolean a) {
						if (this.hasFadeColour) {
							this.particleRed = this.fadeColourRed;
							this.particleGreen = this.fadeColourGreen;
							this.particleBlue = this.fadeColourBlue;
						}
				    }

					@Override
					public void onUpdate() {

						this.prevPosX = this.posX;
						this.prevPosY = this.posY;
						this.prevPosZ = this.posZ;

						if (this.particleAge >= this.particleMaxAge || this.isCollided) {
							this.setExpired();
						}
						int mult = TilePlateExcursionClient.this.redstoneControlOrDisable() ? 1 : -1;
						this.particleAge += mult;

						if (this.particleAge > this.particleMaxAge / 1.5) {
							this.setAlphaF(1F - (this.particleAge - (this.particleMaxAge / 4)) / this.particleMaxAge);

							if (this.hasFadeColour) {
								this.particleRed += (this.fadeColourRed - this.particleRed) * 0.2F;
								this.particleGreen += (this.fadeColourGreen - this.particleGreen) * 0.2F;
								this.particleBlue += (this.fadeColourBlue - this.particleBlue) * 0.2F;
							}
						} else if (mult == -1 && this.particleAge <= this.particleMaxAge / 1.5) {
							this.setAlphaF(1F);

							if (this.hasFadeColour) {
								this.particleRed += (this.red - this.particleRed) * 0.2F;
								this.particleGreen += (this.green - this.particleGreen) * 0.2F;
								this.particleBlue += (this.blue - this.particleBlue) * 0.2F;
							}
						}

						this.setParticleTextureIndex(this.textureIdx + (7 - this.particleAge * 8 / this.particleMaxAge));
						this.moveEntity(mult * this.motionX, mult * this.motionY, mult * this.motionZ);
					}
				};
				spark.setColor(0xFF7700);
				spark.setColorFade(0xFFBB88);
				if (!redstoneControlOrDisable()) {
					//TODO spark.setEating(true);
				}
				Minecraft.getMinecraft().effectRenderer.addEffect(spark);
			}
		}
	}

}
