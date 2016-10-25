package cofh.thermalexpansion.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by covers1624 on 25/10/2016.
 */
public class UnlistedMapProperty implements IUnlistedProperty<Map> {

    private String name;

    public UnlistedMapProperty(String name){
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    @SuppressWarnings("unchecked")//Do this bullshit because reasons.
    public Class<Map> getType() {
        return Map.class;
    }

    @Override
    public String valueToString(Map value) {
        return value.toString();
    }

    @Override
    public boolean isValid(Map value) {
        return true;
    }

}
