package dev.kyanitemods.kyaniteportals.client.content.effects;

//? if <1.20.6 {
/*import com.mojang.serialization.Codec;
*///? } else
import com.mojang.serialization.MapCodec;
import dev.kyanitemods.kyaniteportals.util.CodecHelper;

public class SimplePortalEffect extends PortalEffectOptions<SimplePortalEffect> implements PortalEffectType<SimplePortalEffect> {
    private final /*? if <1.20.6 {*//*Codec<SimplePortalEffect>*//*? } else*/MapCodec<SimplePortalEffect> codec;

    public SimplePortalEffect() {
        //? if <1.20.6 {
        /*codec = CodecHelper.unitCodec(this);
        *///? } else
        codec = CodecHelper.unitMapCodec(this);
    }

    @Override
    public PortalEffectType<SimplePortalEffect> getType() {
        return this;
    }

    @Override
    public /*? if <1.20.6 {*//*Codec<SimplePortalEffect>*//*? } else*/MapCodec<SimplePortalEffect> codec() {
        return codec;
    }
}
