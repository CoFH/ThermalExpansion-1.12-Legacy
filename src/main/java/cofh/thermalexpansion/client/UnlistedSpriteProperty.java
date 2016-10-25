package cofh.thermalexpansion.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.Map;

/**
 * Created by covers1624 on 25/10/2016.
 */
public class UnlistedSpriteProperty implements IUnlistedProperty<TextureAtlasSprite> {

    private String name;

    public UnlistedSpriteProperty(String name){
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<TextureAtlasSprite> getType() {
        return TextureAtlasSprite.class;
    }

    @Override
    public String valueToString(TextureAtlasSprite value) {
        return value.toString();
    }

    @Override
    public boolean isValid(TextureAtlasSprite value) {
        return true;
    }

}
