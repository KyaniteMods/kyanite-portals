package dev.kyanitemods.kyaniteportals.client.content.particles;

import dev.kyanitemods.kyaniteportals.content.particles.CustomPortalParticleOptions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.SpriteSet;

@Environment(EnvType.CLIENT)
public class CustomPortalParticle extends PortalParticle {
    protected CustomPortalParticle(ClientLevel clientLevel, double d, double e, double f, double g, double h, double i, CustomPortalParticleOptions options) {
        super(clientLevel, d, e, f, g, h, i);
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

        public Particle createParticle(CustomPortalParticleOptions options, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            PortalParticle portalParticle = new CustomPortalParticle(clientLevel, d, e, f, g, h, i, options);
            portalParticle.pickSprite(this.sprite);
            return portalParticle;
        }
    }
}
