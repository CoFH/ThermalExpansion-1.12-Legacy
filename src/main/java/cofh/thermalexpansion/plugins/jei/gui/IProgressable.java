package cofh.thermalexpansion.plugins.jei.gui;

import java.util.List;

/**
 * this class represents any progressBar element in the recipe gui.
 */
public interface IProgressable {

	void setDuration(int duration);

	void draw(int progressTicks);

	List<String> getTooltip();

	boolean inBounds(int x, int y);

}
