package cofh.thermalexpansion.render.transformation;

import cofh.repack.codechicken.lib.vec.Matrix4;
import cofh.repack.codechicken.lib.vec.RedundantTransformation;
import cofh.repack.codechicken.lib.vec.Transformation;
import cofh.repack.codechicken.lib.vec.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TorchTransformation extends Transformation {

	public static Transformation[] sideTransformations = {
		new RedundantTransformation(),
		new RedundantTransformation(),
		new TorchTransformation(2),
		new TorchTransformation(3),
		new TorchTransformation(4),
		new TorchTransformation(5),
	};

	int alignment;

	public TorchTransformation(int alignment) {

		this.alignment = alignment;
	}

	@Override
	public void apply(Matrix4 mat) {

	}

	@Override
	public void applyN(Vector3 normal) {

		apply(normal);
	}

	@Override
	public void apply(Vector3 vec) {

		if (alignment > 1 && alignment < 6) {
			double amt = 0.25;
			if (vec.y <= 0.5) {
				amt += 0.25;
			}
			switch (alignment) {
			case 2:
				vec.z -= amt;
				break;
			case 3:
				vec.z += amt;
				break;
			case 4:
				vec.x -= amt;
				break;
			case 5:
				vec.x += amt;
				break;
			}
			vec.y += 3. / 16;
		}
	}

	@Override
	public Transformation merge(Transformation next) {

		if (next.getClass() == TorchTransformation.class) {
			TorchTransformation other = (TorchTransformation) next;
			if ((other.alignment ^ 1) == alignment) {
				return sideTransformations[0];
			}
		}
		return null;
	}

	@Override
	public Transformation inverse() {

		return new TorchTransformation(alignment ^ 1);
	}

	@Override
	public boolean isRedundant() {

		return alignment <= 1 || alignment >= 6;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void glApply() {

	}

}
