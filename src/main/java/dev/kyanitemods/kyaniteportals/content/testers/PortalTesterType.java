package dev.kyanitemods.kyaniteportals.content.testers;

//? if <1.20.6 {
/*import com.mojang.serialization.Codec;
 *///? } else
import com.mojang.serialization.MapCodec;

public interface PortalTesterType<T extends PortalTester<T>> {
    /*? if <1.20.6 {*//*Codec<T>*//*? } else {*/MapCodec<T>/*? }*/ codec();
}
