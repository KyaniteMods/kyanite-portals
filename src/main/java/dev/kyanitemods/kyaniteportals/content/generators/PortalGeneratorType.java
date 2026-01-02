package dev.kyanitemods.kyaniteportals.content.generators;

//? if <1.20.6 {
/*import com.mojang.serialization.Codec;
*///? } else
import com.mojang.serialization.MapCodec;

public interface PortalGeneratorType<T extends PortalGenerator<T>> {
    /*? if <1.20.6 {*//*Codec<T>*//*? } else*/MapCodec<T> codec();
}
