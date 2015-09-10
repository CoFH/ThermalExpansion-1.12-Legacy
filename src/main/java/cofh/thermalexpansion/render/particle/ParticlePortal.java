package cofh.thermalexpansion.render.particle;

import cofh.repack.codechicken.lib.render.CCModel;
import cofh.repack.codechicken.lib.render.CCRenderState;
import cofh.repack.codechicken.lib.vec.Cuboid6;
import cofh.repack.codechicken.lib.vec.Vector3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class ParticlePortal extends EntityFX {

	private static final CCModel model = CCModel.newModel(7, 24);
	private static final Cuboid6 cuboid = new Cuboid6(0, 0, 0, 0, 0, 0);
	private static final Vector3 vector = new Vector3();

	public double startX, startY, startZ;
	private float xScale = 0.1f, yScale = 0.1f, zScale = 0.1f;

	public ParticlePortal(World world, double x, double y, double z, float r, float g, float b) {

		super(world, x, y, z);
		startX = x;
		startY = y;
		startZ = z;
		noClip = true;
		particleRed = r;
		particleGreen = g;
		particleBlue = b;
		particleScale = ((float) (0.2 + 0.2 * Math.random()));
		motionY = (0.2 * (1.0D + Math.random()) / 4.75D);
		// particleIcon =
		particleMaxAge = ((int) (80.0D / (Math.random() * 0.6D + 0.4D)));
		particleGravity = 0;
	}

	@Override
	public void moveEntity(double x, double y, double z) {

		super.moveEntity(x, y, z);

		final double f = .3;
		startX += x * f;
		startY += y * f;
		startZ += z * f;
	}

	public void setScale(double[] d) {

		xScale = (float) d[0];
		yScale = (float) d[1];
		zScale = (float) d[2];
	}

	@Override
	public void renderParticle(Tessellator tessellator, float subTick, float rX, float rXZ, float rZ, float rYZ, float rXY) {

		particleAlpha = (1.0F - (particleAge + subTick) / particleMaxAge);

		EntityLivingBase entity = Minecraft.getMinecraft().renderViewEntity;
		double interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * subTick;
		double interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * subTick;
		double interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * subTick;

		float _x = (float) (prevPosX + (posX - prevPosX) * subTick - interpPosX);
		float _y = (float) (prevPosY + (posY - prevPosY) * subTick - interpPosY);
		float _z = (float) (prevPosZ + (posZ - prevPosZ) * subTick - interpPosZ);
		float sx = (float) (startX - interpPosX);
		float sy = (float) (startY - interpPosY);
		float sz = (float) (startZ - interpPosZ);

		{
			float _t;
			if (_x < sx) {
				_t = sx;
				sx = _x;
				_x = _t;
			}
			if (_y < sy) {
				_t = sy;
				sy = _y;
				_y = _t;
			}
			if (_z < sz) {
				_t = sz;
				sz = _z;
				_z = _t;
			}
		}

		int i = this.getBrightnessForRender(subTick);
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(particleRed, particleGreen, particleBlue, particleAlpha);
		CCRenderState.reset();
		model.generateBlock(0, cuboid.set(sx, sy, sz, _x, _y, _z).expand(vector.set(xScale, yScale, zScale).multiply(particleScale))).render();
		// tessellator.addVertex(sx - rX * hScale - rYZ * hScale, sy - rXZ * vScale, sz - rZ * hScale - rXY * hScale);
		// tessellator.addVertex(_x - rX * hScale + rYZ * hScale, _y + rXZ * vScale, _z - rZ * hScale + rXY * hScale);
		// tessellator.addVertex(_x + rX * hScale + rYZ * hScale, _y + rXZ * vScale, _z + rZ * hScale + rXY * hScale);
		// tessellator.addVertex(sx + rX * hScale - rYZ * hScale, sy - rXZ * vScale, sz + rZ * hScale - rXY * hScale);
		tessellator.draw();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true);
	}

	@Override
	public int getFXLayer() {

		return 3;
	}
}
