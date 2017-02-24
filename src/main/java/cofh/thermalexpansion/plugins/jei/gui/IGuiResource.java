package cofh.thermalexpansion.plugins.jei.gui;

import java.util.List;

public interface IGuiResource {

	void setRequiredResource(int requiredResource);

	void draw(int level);

	List<String> getTooltip(int perTick);

	boolean inBounds(int x, int y);

}
