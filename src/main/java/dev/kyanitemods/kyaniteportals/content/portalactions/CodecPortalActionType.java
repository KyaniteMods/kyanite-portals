package dev.kyanitemods.kyaniteportals.content.portalactions;

//? if <1.20.6 {
import com.mojang.serialization.Codec;
//? } else
//import com.mojang.serialization.MapCodec;

import java.util.Objects;

public final class CodecPortalActionType<T extends PortalAction<T>> extends PortalActionType<T> {
    //? if <1.20.6 {
    private final Codec<T> codec;

    public CodecPortalActionType(Codec<T> codec) {
        this.codec = codec;
    }

    @Override
    public Codec<T> codec() {
        return codec;
    }
    //? } else {
    /*private final MapCodec<T> codec;

    public CodecPortalActionType(MapCodec<T> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<T> codec() {
        return codec;
    }*/
    //? }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CodecPortalActionType<?>) obj;
        return Objects.equals(this.codec, that.codec);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codec);
    }

    @Override
    public String toString() {
        return "CodecPortalActionType[" +
                "codec=" + codec + ']';
    }

}
