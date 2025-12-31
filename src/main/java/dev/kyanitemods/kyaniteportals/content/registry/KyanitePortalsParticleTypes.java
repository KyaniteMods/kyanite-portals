package dev.kyanitemods.kyaniteportals.content.registry;

import dev.kyanitemods.kyaniteportals.KyanitePortals;
import dev.kyanitemods.kyaniteportals.content.particles.CustomPortalParticleType;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public final class KyanitePortalsParticleTypes {
    private KyanitePortalsParticleTypes() {}

    public static final CustomPortalParticleType PORTAL = register("portal", new CustomPortalParticleType(false));

    private static <T extends ParticleOptions, U extends ParticleType<T>> U register(String id, U type) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, KyanitePortals.id(id), type);
    }

    public static void load() {
        KyanitePortals.LOGGER.debug("Loading particle types");
    }
}
