package cofh.thermalexpansion.plugins.jei.gui;

import cofh.thermalexpansion.plugins.jei.JeiPluginTE;

import java.util.ArrayList;
import java.util.List;

/**
 * this class represents a progressable gui element Like Progress Arrows see@{@link #arrow(int, int, boolean)} .
 */
public class Progressable implements IProgressable {

	protected final int x;
	protected final int y;
	private final int u;
	private final int v;
	protected final int width;
	protected final int height;
	private final int ticksPerCycle;
	private final boolean reversed;
	private final boolean yAxis;
	private int duration = 0;

	public Progressable(int u, int v, int x, int y, int width, int height, int ticksPerCycle, boolean reversed, boolean yAxis) {

		this.x = x;
		this.y = y;
		this.u = u;
		this.v = v;
		this.width = width;
		this.height = height;
		this.ticksPerCycle = ticksPerCycle;
		this.reversed = reversed;
		this.yAxis = yAxis;
	}

	@Override
	public void setDuration(int duration) {

		this.duration = duration;
	}

	@Override
	public void draw(int progressTicks) {

		int progress = progressTicks / ticksPerCycle;
		int appliedV = reversed ? v - height : v;
		ResourceUtils.setAxisMode(yAxis);
		ResourceUtils.drawMixedTextures(x, y, u, appliedV, width, height, progress, JeiPluginTE.JEI_HANDLER_LOCATION, JeiPluginTE.JEI_HANDLER_LOCATION, reversed);
	}

	@Override
	public List<String> getTooltip() {

		List<String> tooltips = new ArrayList<String>();
		tooltips.add("duration: " + this.duration + " ticks");
		return tooltips;
	}

	@Override
	public boolean inBounds(int x, int y) {

		return this.x <= x && width + this.x >= x && this.y <= y && height + this.y >= y;
	}

	//new Instances for a predefined portion of the gui sprite.
	public static Progressable arrow(int x, int y, boolean reversed) {

		return new Progressable(176, 16, x, y, 23, 16, 5, reversed, false);
	}

	public static Progressable crushing(int x, int y, boolean reversed) {

		return new Progressable(225, 17, x, y, 15, 15, 8, reversed, true);
	}

	public static Progressable burning(int x, int y, boolean reversed) {

		return new Progressable(225, 33, x, y, 15, 15, 8, reversed, true);
	}

	public static IProgressable sawing(int x, int y, boolean reversed) {

		return new Progressable(225, 48, x, y, 15, 15, 8, reversed, true);
	}

	public static IProgressable infusing(int x, int y, boolean reversed) {

		return new Progressable(226, 1, x, y, 15, 15, 8, reversed, true);
	}

}
