package dev.kyanitemods.kyaniteportals.client.content.effects;

import com.mojang.serialization.Codec;
//? if >=1.20.6
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
//? if >=1.21.5
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;

public class NetherLikeLoadingBackgroundOptions extends LoadingBackgroundOptions<NetherLikeLoadingBackgroundOptions> {
    //$ map_codec_swap NetherLikeLoadingBackgroundOptions
    public static final MapCodec<NetherLikeLoadingBackgroundOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("atlas").forGetter(NetherLikeLoadingBackgroundOptions::getAtlas),
            Identifier.CODEC.fieldOf("texture").forGetter(NetherLikeLoadingBackgroundOptions::getTexture),
            Codec.intRange(0, 16777215).optionalFieldOf("tint", 0xFFFFFF).forGetter(NetherLikeLoadingBackgroundOptions::getTint)
    ).apply(instance, NetherLikeLoadingBackgroundOptions::new));

    private final Identifier atlas;
    private final Identifier texture;
    private TextureAtlasSprite cachedSprite = null;
    private final int tint;

    public NetherLikeLoadingBackgroundOptions(Identifier atlas, Identifier texture, int tint) {
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
    public boolean render(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
        //? if <1.20.2 {
        /*guiGraphics.setColor(((getTint() >> 16) & 0xFF) / 255.0f, ((getTint() >> 8) & 0xFF) / 255.0f, (getTint() & 0xFF) / 255.0f, 1.0f);
        guiGraphics.blit(0, 0, -90, guiGraphics.guiWidth(), guiGraphics.guiHeight(), getSprite());
        guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        *///? } else if <1.21.5 {
        /*guiGraphics.blitSprite(getSprite().atlasLocation(), 0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), getTint() | 0xFF000000);
        *///? } else {
        guiGraphics.blitSprite(RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND, getSprite(), 0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), getTint() | 0xFF000000);
        //? }
        return true;
    }

    private TextureAtlasSprite getSprite() {
        if (cachedSprite == null) {
            //? if <1.21.9 {
            /*cachedSprite = Minecraft.getInstance().getTextureAtlas(getAtlas()).apply(getTexture());
            *///? } else
            cachedSprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(getAtlas()).getSprite(getTexture());
        }
        return cachedSprite;
    }

    @Override
    public PortalEffectType<NetherLikeLoadingBackgroundOptions> getType() {
        return PortalEffects.NETHER_LIKE_LOADING_BACKGROUND;
    }
}
