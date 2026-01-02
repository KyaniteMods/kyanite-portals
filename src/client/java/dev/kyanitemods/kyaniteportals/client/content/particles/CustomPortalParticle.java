package dev.kyanitemods.kyaniteportals.client.content.particles;

import dev.kyanitemods.kyaniteportals.content.particles.CustomPortalParticleOptions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.SpriteSet;
//? if >=1.21.9
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class CustomPortalParticle extends PortalParticle {
    protected CustomPortalParticle(ClientLevel clientLevel, double x, double y, double z, double xd, double yd, double zd, CustomPortalParticleOptions options/*? if >=1.21.9 {*/, TextureAtlasSprite sprite/*? }*/) {
        super(clientLevel, x, y, z, xd, yd, zd/*? if >=1.21.9 {*/, sprite/*? }*/);
        float brightness = this.random.nextFloat() * 0.6F + 0.4F;
        rCol = brightness * options.getColor().x();
        gCol = brightness * options.getColor().y();
        bCol = brightness * options.getColor().z();
    }

    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleProvider<CustomPortalParticleOptions> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        //? if <1.21.9 {
        /*public Particle createParticle(CustomPortalParticleOptions options, ClientLevel clientLevel, double x, double y, double z, double xd, double yd, double zd) {
            PortalParticle portalParticle = new CustomPortalParticle(clientLevel, x, y, z, xd, yd, zd, options);
            portalParticle.pickSprite(this.sprite);
            return portalParticle;
        }*/
        //? } else {
        @Override
        public @Nullable Particle createParticle(CustomPortalParticleOptions options, ClientLevel clientLevel, double x, double y, double z, double xd, double yd, double zd, RandomSource random) {
            return new CustomPortalParticle(clientLevel, x, y, z, xd, yd, zd, options, sprite.get(random));
        }
        //? }
    }
}
