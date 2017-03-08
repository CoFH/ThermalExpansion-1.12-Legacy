package cofh.thermalexpansion.plugins.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableStatic;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class Drawables {

	@Nullable
	private static Drawables instance;

	public static Drawables getDrawables(IGuiHelper guiHelper) {

		if (instance == null) {
			instance = new Drawables(guiHelper);
		}
		return instance;
	}

	public static final ResourceLocation JEI_TEXTURE = new ResourceLocation("thermalexpansion:textures/gui/jei_handler.png");

	private final IDrawableStatic[] slot = new IDrawableStatic[3];

	private final IDrawableStatic[] tank = new IDrawableStatic[3];
	private final IDrawableStatic[] tankOverlay = new IDrawableStatic[6];

	private final IDrawableStatic[] progressRight = new IDrawableStatic[3];
	private final IDrawableStatic[] progressRightFill = new IDrawableStatic[3];

	private final IDrawableStatic[] progressLeft = new IDrawableStatic[3];
	private final IDrawableStatic[] progressLeftFill = new IDrawableStatic[3];

	private final IDrawableStatic[] speed = new IDrawableStatic[8];
	private final IDrawableStatic[] speedFill = new IDrawableStatic[8];

	private final IDrawableStatic energyEmpty;
	private final IDrawableStatic energyFill;

	private Drawables(IGuiHelper guiHelper) {

		slot[0] = guiHelper.createDrawable(JEI_TEXTURE, 0, 0, 18, 18);
		slot[1] = guiHelper.createDrawable(JEI_TEXTURE, 32, 0, 26, 26);
		slot[2] = guiHelper.createDrawable(JEI_TEXTURE, 64, 0, 44, 26);

		for (int i = 0; i < 3; i++) {
			tank[i] = guiHelper.createDrawable(JEI_TEXTURE, 64 * i, 192, 18, 62);
			tankOverlay[2 * i] = guiHelper.createDrawable(JEI_TEXTURE, 32 + 64 * i, 193, 16, 60);
			tankOverlay[2 * i + 1] = guiHelper.createDrawable(JEI_TEXTURE, 64 + 64 * i, 193, 16, 60);

			progressLeft[i] = guiHelper.createDrawable(JEI_TEXTURE, 176, 32 * i, 24, 16);
			progressLeftFill[i] = guiHelper.createDrawable(JEI_TEXTURE, 200, 32 * i, 24, 16);

			progressRight[i] = guiHelper.createDrawable(JEI_TEXTURE, 176, 16 + 32 * i, 24, 16);
			progressRightFill[i] = guiHelper.createDrawable(JEI_TEXTURE, 200, 16 + 32 * i, 24, 16);
		}
		for (int i = 0; i < 8; i++) {
			speed[i] = guiHelper.createDrawable(JEI_TEXTURE, 224, i * 16, 16, 16);
			speedFill[i] = guiHelper.createDrawable(JEI_TEXTURE, 240, i * 16, 16, 16);
		}
		energyEmpty = guiHelper.createDrawable(JEI_TEXTURE, 192, 192, 14, 42);
		energyFill = guiHelper.createDrawable(JEI_TEXTURE, 208, 192, 14, 42);
	}

	public IDrawableStatic getSlot(int type) {

		return slot[type];
	}

	public IDrawableStatic getTank(int type) {

		return tank[type];
	}

	public IDrawableStatic getTankSmallOverlay(int type) {

		return tankOverlay[type * 2];
	}

	public IDrawableStatic getTankLargeOverlay(int type) {

		return tankOverlay[type * 2 + 1];
	}

	public IDrawableStatic getProgress(int type) {

		return progressRight[type];
	}

	public IDrawableStatic getProgressFill(int type) {

		return progressRightFill[type];
	}

	public IDrawableStatic getProgressLeft(int type) {

		return progressLeft[type];
	}

	public IDrawableStatic getProgressLeftFill(int type) {

		return progressLeftFill[type];
	}

	public IDrawableStatic getSpeed(int type) {

		return speed[type];
	}

	public IDrawableStatic getSpeedFill(int type) {

		return speedFill[type];
	}

	public IDrawableStatic getEnergyEmpty() {

		return energyEmpty;
	}

	public IDrawableStatic getEnergyFill() {

		return energyFill;
	}

}
