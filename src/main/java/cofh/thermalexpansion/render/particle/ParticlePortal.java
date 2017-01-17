package cofh.thermalexpansion.render.particle;

import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class ParticlePortal extends Particle {

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
		//noClip = true;
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
	public void renderParticle(VertexBuffer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {

		particleAlpha = (1.0F - (particleAge + partialTicks) / particleMaxAge);

		Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
		double interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
		double interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
		double interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;

		float _x = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
		float _y = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
		float _z = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);
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

		int i = this.getBrightnessForRender(partialTicks);
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);

		CCRenderState ccrs = CCRenderState.instance();

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableTexture2D();

		ccrs.reset();
		ccrs.startDrawing(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
		ccrs.setColour(new ColourRGBA(particleRed, particleGreen, particleBlue, particleAlpha));
		ccrs.pullLightmap();
		model.generateBlock(0, cuboid.set(sx, sy, sz, _x, _y, _z).expand(vector.set(xScale, yScale, zScale).multiply(particleScale))).render(ccrs);
		// tessellator.addVertex(sx - rX * hScale - rYZ * hScale, sy - rXZ * vScale, sz - rZ * hScale - rXY * hScale);
		// tessellator.addVertex(_x - rX * hScale + rYZ * hScale, _y + rXZ * vScale, _z - rZ * hScale + rXY * hScale);
		// tessellator.addVertex(_x + rX * hScale + rYZ * hScale, _y + rXZ * vScale, _z + rZ * hScale + rXY * hScale);
		// tessellator.addVertex(sx + rX * hScale - rYZ * hScale, sy - rXZ * vScale, sz + rZ * hScale - rXY * hScale);
		ccrs.draw();

		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
	}

	@Override
	public int getFXLayer() {

		return 3;
	}
}
