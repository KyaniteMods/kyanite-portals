package dev.kyanitemods.kyaniteportals.content.particles;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
//? if >=1.20.5 {

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

//? }
import org.jetbrains.annotations.NotNull;

public class CustomPortalParticleType extends ParticleType<CustomPortalParticleOptions> {
    public CustomPortalParticleType(boolean bl) {
        //? if <1.20.5 {
        /*super(bl, CustomPortalParticleOptions.DESERIALIZER);
        *///? } else
        super(bl);
    }

    @Override
    public @NotNull /*? if <1.20.5 { *//*Codec<CustomPortalParticleOptions>*//*? } else */MapCodec<CustomPortalParticleOptions> codec() {
        return CustomPortalParticleOptions.CODEC;
    }

    //? if >=1.20.5 {
    
    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, CustomPortalParticleOptions> streamCodec() {
        return CustomPortalParticleOptions.STREAM_CODEC;
    }
     
    //? }
}
