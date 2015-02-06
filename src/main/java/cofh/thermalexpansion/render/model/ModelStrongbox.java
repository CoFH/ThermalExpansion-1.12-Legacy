package cofh.thermalexpansion.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelStrongbox extends ModelBase {

	public ModelRenderer boxLid;
	public ModelRenderer boxBase;
	public ModelRenderer publicKnob;
	public ModelRenderer restrictedKnob;
	public ModelRenderer privateKnob;

	public ModelStrongbox() {

		boxLid = new ModelRenderer(this, 0, 0).setTextureSize(64, 64);
		boxLid.addBox(0.0F, -5F, -14F, 14, 5, 14, 0.0F);
		boxLid.rotationPointX = 1.0F;
		boxLid.rotationPointY = 7F;
		boxLid.rotationPointZ = 15F;

		boxBase = new ModelRenderer(this, 0, 19).setTextureSize(64, 64);
		boxBase.addBox(0.0F, 0.0F, 0.0F, 14, 10, 14, 0.0F);
		boxBase.rotationPointX = 1.0F;
		boxBase.rotationPointY = 6F;
		boxBase.rotationPointZ = 1.0F;

		publicKnob = new ModelRenderer(this, 0, 0).setTextureSize(64, 64);
		publicKnob.addBox(-1F, -2F, -15F, 2, 4, 1, 0.0F);
		publicKnob.rotationPointX = 8F;
		publicKnob.rotationPointY = 7F;
		publicKnob.rotationPointZ = 15F;

		restrictedKnob = new ModelRenderer(this, 6, 0).setTextureSize(64, 64);
		restrictedKnob.addBox(-1F, -2F, -15F, 2, 4, 1, 0.0F);
		restrictedKnob.rotationPointX = 8F;
		restrictedKnob.rotationPointY = 7F;
		restrictedKnob.rotationPointZ = 15F;

		privateKnob = new ModelRenderer(this, 0, 5).setTextureSize(64, 64);
		privateKnob.addBox(-1F, -2F, -15F, 2, 4, 1, 0.0F);
		privateKnob.rotationPointX = 8F;
		privateKnob.rotationPointY = 7F;
		privateKnob.rotationPointZ = 15F;
	}

	public void render(int access) {

		boxLid.render(0.0625F);
		boxBase.render(0.0625F);

		if (access == 0) {
			publicKnob.rotateAngleX = boxLid.rotateAngleX;
			publicKnob.render(0.0625F);
		} else if (access == 1) {
			restrictedKnob.rotateAngleX = boxLid.rotateAngleX;
			restrictedKnob.render(0.0625F);
		} else if (access == 2) {
			privateKnob.rotateAngleX = boxLid.rotateAngleX;
			privateKnob.render(0.0625F);
		}
	}

}
