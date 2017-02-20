package cofh.thermalexpansion.plugins.top;

import mcjty.theoneprobe.api.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface IProbeInfoWrapper {
    ILayoutStyle defaultLayoutStyle();

    IProgressStyle defaultProgressStyle();

    ITextStyle defaultTextStyle() ;

    IItemStyle defaultItemStyle() ;

    IEntityStyle defaultEntityStyle() ;

    IIconStyle defaultIconStyle();

    IProbeInfoWrapper icon(ResourceLocation resourceLocation, int i, int i1, int i2, int i3);

    IProbeInfoWrapper entity(String s);

    IProbeInfoWrapper entity(Entity entity);

    IProbeInfoWrapper text(String s);

    IProbeInfoWrapper item(ItemStack itemStack);

    IProbeInfoWrapper progress(int i, int i1);

    IProbeInfoWrapper horizontal();

    IProbeInfoWrapper vertical();

    IProbeInfoWrapper element(IElement iElement);

    void setStyleObject(Object styleObject);
}
