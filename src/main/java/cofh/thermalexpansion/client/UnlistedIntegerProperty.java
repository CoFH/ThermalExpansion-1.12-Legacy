package cofh.thermalexpansion.client;

import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * Created by covers1624 on 28/10/2016.
 */
public class UnlistedIntegerProperty implements IUnlistedProperty<Integer> {

    private final String name;

    public UnlistedIntegerProperty(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(Integer value) {
        return true;
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public String valueToString(Integer value) {
        return value.toString();
    }
}
