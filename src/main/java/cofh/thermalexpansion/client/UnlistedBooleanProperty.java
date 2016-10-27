package cofh.thermalexpansion.client;

import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * Created by covers1624 on 28/10/2016.
 */
public class UnlistedBooleanProperty implements IUnlistedProperty<Boolean> {

    private final String name;

    public UnlistedBooleanProperty(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(Boolean value) {
        return true;
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    @Override
    public String valueToString(Boolean value) {
        return value.toString();
    }
}
