package cofh.thermalexpansion.plugins.jei.gui;


import cofh.thermalexpansion.plugins.jei.JeiPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * the object representing the power bar in jei recipes.
 */
public class PowerBar implements IGuiResource {
    private static final int WIDTH = 16;
    private static final int Height = 42;
    private int requiredEnergy;
    private int x;
    private int y;

    public PowerBar(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public List<String> getTooltips(int perTick) {
        List<String> list = new ArrayList<String>();

        list.add("using: " + requiredEnergy + "rf");
        list.add("at " + perTick + "rf/t");

        return list;
    }

    @Override
    public void setRequiredResource(int requiredResource) {
        requiredEnergy = requiredResource;
    }

    @Override
    public void draw(int level) {
        int fillLevel = level * 7;
        ResourceUtils.setAxisMode(true);
        ResourceUtils.drawMixedTextures(x, y, 0, 96, WIDTH, Height, fillLevel, JeiPlugin.JEI_HANDLER_LOCATION, JeiPlugin.JEI_HANDLER_LOCATION, false);
    }


    @Override
    public boolean inBounds(int x, int y) {
        return this.x + 1 <= x && WIDTH + this.x >= x
                && this.y <= y && Height + this.y >= y;
    }
}
