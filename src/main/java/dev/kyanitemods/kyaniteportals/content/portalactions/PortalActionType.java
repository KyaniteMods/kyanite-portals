package dev.kyanitemods.kyaniteportals.content.portalactions;

//? if <1.20.6 {
import com.mojang.serialization.Codec;
//? } else
//import com.mojang.serialization.MapCodec;

public abstract class PortalActionType<T extends PortalAction<T>> {
    public abstract /*? if <1.20.6 {*/Codec<T>/*? } else*//*MapCodec<T>*/ codec();
}
