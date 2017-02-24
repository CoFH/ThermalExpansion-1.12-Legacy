package cofh.thermalexpansion.plugins.jei.gui;

import java.util.List;

/**
 * this class represents any progressBar element in the recipe gui.
 */
public interface IPrograssable {
    void setDuration(int duration);

    void draw(int progressTicks);

    List<String> getTooltips();

    boolean inBounds(int x, int y);
}
