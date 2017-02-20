package cofh.thermalexpansion.plugins.top;

import mcjty.theoneprobe.api.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ProbeInfoWrapper implements IProbeInfoWrapper {
    private final IProbeInfo info;
    private Object styleObject;

    public ProbeInfoWrapper(IProbeInfo info) {
        this.info = info;
    }

    @Override
    public ILayoutStyle defaultLayoutStyle() {
        return info.defaultLayoutStyle();
    }

    @Override
    public IProgressStyle defaultProgressStyle() {
        return info.defaultProgressStyle();
    }

    @Override
    public ITextStyle defaultTextStyle() {
        return info.defaultTextStyle();
    }

    @Override
    public IItemStyle defaultItemStyle() {
        return info.defaultItemStyle();
    }

    @Override
    public IEntityStyle defaultEntityStyle() {
        return info.defaultEntityStyle();
    }

    @Override
    public IIconStyle defaultIconStyle() {
        return info.defaultIconStyle();
    }

    @Override
    public IProbeInfoWrapper icon(ResourceLocation resourceLocation, int i, int i1, int i2, int i3) {
        if (styleObject instanceof IIconStyle)
            info.icon(resourceLocation, i, i1, i2, i3, (IIconStyle) styleObject);
        else
            info.icon(resourceLocation, i, i1, i2, i3);
        return this;
    }

    @Override
    public IProbeInfoWrapper entity(String s) {
        if (styleObject instanceof IEntityStyle)
            info.entity(s, (IEntityStyle) styleObject);
        else
            info.entity(s);
        return this;
    }

    @Override
    public IProbeInfoWrapper entity(Entity entity) {
        if (styleObject instanceof IEntityStyle)
            info.entity(entity, (IEntityStyle) styleObject);
        else
            info.entity(entity);
        return this;
    }

    @Override
    public IProbeInfoWrapper text(String s) {
        if (styleObject instanceof ITextStyle)
            info.text(s, (ITextStyle) styleObject);
        else
            info.text(s);
        return this;
    }

    @Override
    public IProbeInfoWrapper item(ItemStack itemStack) {
        if (styleObject instanceof IIconStyle)
            info.item(itemStack, (IItemStyle) styleObject);
        else
            info.item(itemStack);
        return this;
    }

    @Override
    public IProbeInfoWrapper progress(int i, int i1) {
        if (styleObject instanceof IProgressStyle)
            info.progress(i, i1, (IProgressStyle) styleObject);
        else
            info.progress(i, i1);
        return this;
    }

    @Override
    public IProbeInfoWrapper horizontal() {
        return new ProbeInfoWrapper(info.horizontal());
    }

    @Override
    public IProbeInfoWrapper vertical() {
        return new ProbeInfoWrapper(info.vertical());
    }

    @Override
    public IProbeInfoWrapper element(IElement iElement) {
        return new ProbeInfoWrapper(info.element(iElement));
    }

    @Override
    public void setStyleObject(Object styleObject) {
        this.styleObject = styleObject;
    }
}
