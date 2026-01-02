package dev.kyanitemods.kyaniteportals.client.content.effects;

import com.mojang.serialization.Codec;
//? if >=1.20.6
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

public class TextureOverlayPortalEffectOptions extends PortalEffectOptions<TextureOverlayPortalEffectOptions> {
    //$ map_codec_swap TextureOverlayPortalEffectOptions
    public static final MapCodec<TextureOverlayPortalEffectOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("atlas").forGetter(TextureOverlayPortalEffectOptions::getAtlas),
            Identifier.CODEC.fieldOf("texture").forGetter(TextureOverlayPortalEffectOptions::getTexture),
            Codec.intRange(0, 16777215).optionalFieldOf("tint", 0xFFFFFF).forGetter(TextureOverlayPortalEffectOptions::getTint)
    ).apply(instance, TextureOverlayPortalEffectOptions::new));

    private final Identifier atlas;
    private final Identifier texture;
    private final int tint;

    public TextureOverlayPortalEffectOptions(Identifier atlas, Identifier texture, int tint) {
        this.atlas = atlas;
        this.texture = texture;
        this.tint = tint;
    }

    public Identifier getAtlas() {
        return atlas;
    }

    public Identifier getTexture() {
        return texture;
    }

    public int getTint() {
        return tint;
    }

    @Override
    public PortalEffectType<TextureOverlayPortalEffectOptions> getType() {
        return PortalEffects.TEXTURE_OVERLAY;
    }
}
