package dev.kyanitemods.kyaniteportals.content.testers;

//? if <1.20.6 {
/*import com.mojang.serialization.Codec;
*///? } else
import com.mojang.serialization.MapCodec;

import java.util.Objects;

public record CodecPortalTesterType<T extends PortalTester<T>>(/*? if <1.20.6 {*//*Codec<T>*//*? } else {*/MapCodec<T>/*? }*/ codec) implements PortalTesterType<T> {
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CodecPortalTesterType) obj;
        return Objects.equals(this.codec, that.codec);
    }

    @Override
    public String toString() {
        return "CodecPortalTesterType[" +
                "codec=" + codec + ']';
    }

}
