package dev.kyanitemods.kyaniteportals.client.content.effects;

//? if <1.20.6 {
import com.mojang.serialization.Codec;
//? } else
//import com.mojang.serialization.MapCodec;

public record CodecPortalEffectType<T extends PortalEffectOptions<T>>(/*? if <1.20.6 {*/Codec<T>/*? } else*//*MapCodec<T>*/ codec) implements PortalEffectType<T> {
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        CodecPortalEffectType<?> that = (CodecPortalEffectType<?>) object;

        return codec.equals(that.codec);
    }

    @Override
    public int hashCode() {
        return codec.hashCode();
    }
}
