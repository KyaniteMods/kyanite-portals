package dev.kyanitemods.kyaniteportals.client.content.effects;

//? if <1.20.6 {
import com.mojang.serialization.Codec;
//? } else
//import com.mojang.serialization.MapCodec;

public interface PortalEffectType<T extends PortalEffectOptions<T>> {
    /*? if <1.20.6 {*/Codec<T>/*? } else*//*MapCodec<T>*/ codec();
}
