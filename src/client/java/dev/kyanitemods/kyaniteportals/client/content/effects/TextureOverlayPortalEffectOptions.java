package dev.kyanitemods.kyaniteportals.client.content.effects;

import com.mojang.serialization.Codec;
//? if >=1.20.6
//import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public class TextureOverlayPortalEffectOptions extends PortalEffectOptions<TextureOverlayPortalEffectOptions> {
    //$ map_codec_swap TextureOverlayPortalEffectOptions
    public static final Codec<TextureOverlayPortalEffectOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("atlas").forGetter(TextureOverlayPortalEffectOptions::getAtlas),
            ResourceLocation.CODEC.fieldOf("texture").forGetter(TextureOverlayPortalEffectOptions::getTexture),
            Codec.intRange(0, 16777215).optionalFieldOf("tint", 0xFFFFFF).forGetter(TextureOverlayPortalEffectOptions::getTint)
    ).apply(instance, TextureOverlayPortalEffectOptions::new));

    private final ResourceLocation atlas;
    private final ResourceLocation texture;
    private final int tint;

    public TextureOverlayPortalEffectOptions(ResourceLocation atlas, ResourceLocation texture, int tint) {
        this.atlas = atlas;
        this.texture = texture;
        this.tint = tint;
    }

    public ResourceLocation getAtlas() {
        return atlas;
    }

    public ResourceLocation getTexture() {
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
