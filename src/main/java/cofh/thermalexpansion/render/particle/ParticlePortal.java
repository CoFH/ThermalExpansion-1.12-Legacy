package cofh.thermalexpansion.render.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class ParticlePortal extends EntityFX {

	public double startY;

	public ParticlePortal(World world, double x, double y, double z, float r, float g, float b) {

		super(world, x, y, z);
		startY = y;
		noClip = true;
		particleRed = r;
		particleGreen = g;
		particleBlue = b;
		particleScale = ((float) (0.2 + 0.2  * Math.random()));
		motionY = (0.2 * (1.0D + Math.random()) / 4.75D);
		//particleIcon =
		particleMaxAge = ((int) (80.0D / (Math.random() * 0.6D + 0.4D)));
		particleGravity = 0;
	}

	@Override
	public void renderParticle(Tessellator tessellator, float subTick, float rX, float rXZ, float rZ, float rYZ, float rXY) {

		particleAlpha = (1.0F - (particleAge + subTick) / particleMaxAge);

		float hScale = 0.03F * particleScale;
		float vScale = 0.1F * particleScale;

		EntityLivingBase entity = Minecraft.getMinecraft().renderViewEntity;
        double interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * subTick;
        double interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * subTick;
        double interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * subTick;

		float x = (float) (prevPosX + (posX - prevPosX) * subTick - interpPosX);
		float y = (float) (prevPosY + (posY - prevPosY) * subTick - interpPosY);
		float z = (float) (prevPosZ + (posZ - prevPosZ) * subTick - interpPosZ);
		float sy = (float) (startY - interpPosY);

		startY += motionY * .25;

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
		tessellator.addVertex(x - rX * hScale - rYZ * hScale, sy - rXZ * vScale, z - rZ * hScale - rXY * hScale);
		tessellator.addVertex(x - rX * hScale + rYZ * hScale,  y + rXZ * vScale, z - rZ * hScale + rXY * hScale);
		tessellator.addVertex(x + rX * hScale + rYZ * hScale,  y + rXZ * vScale, z + rZ * hScale + rXY * hScale);
		tessellator.addVertex(x + rX * hScale - rYZ * hScale, sy - rXZ * vScale, z + rZ * hScale - rXY * hScale);
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
