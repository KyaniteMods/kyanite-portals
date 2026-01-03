//~ vec3f

package dev.kyanitemods.kyaniteportals.content.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.registry.KyanitePortalsParticleTypes;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
//? if >=1.20.5 {

import org.joml.Vector3fc;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

//? }
import java.util.Locale;

public class CustomPortalParticleOptions implements ParticleOptions {
    //$ map_codec_swap CustomPortalParticleOptions
    public static final MapCodec<CustomPortalParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(CustomPortalParticleOptions::getColor)
    ).apply(instance, CustomPortalParticleOptions::new));

    //? if >=1.20.5
    public static final StreamCodec<RegistryFriendlyByteBuf, CustomPortalParticleOptions> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VECTOR3F, options -> options.color, CustomPortalParticleOptions::new);

    //? if <1.20.5 {
    /*public static final ParticleOptions.Deserializer<CustomPortalParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        public CustomPortalParticleOptions fromCommand(ParticleType<CustomPortalParticleOptions> particleType, StringReader stringReader) throws CommandSyntaxException {
            Vector3f vector3f = readVector3f(stringReader);
            return new CustomPortalParticleOptions(vector3f);
        }

        public CustomPortalParticleOptions fromNetwork(ParticleType<CustomPortalParticleOptions> particleType, FriendlyByteBuf friendlyByteBuf) {
            return new CustomPortalParticleOptions(readVector3f(friendlyByteBuf));
        }
    };
    *///? }

    protected final Vector3fc color;

    public CustomPortalParticleOptions(Vector3fc vector3f) {
        this.color = vector3f;
    }

    public static Vector3fc readVector3f(StringReader stringReader) throws CommandSyntaxException {
        stringReader.expect(' ');
        float f = stringReader.readFloat();
        stringReader.expect(' ');
        float g = stringReader.readFloat();
        stringReader.expect(' ');
        float h = stringReader.readFloat();
        return new Vector3f(f, g, h);
    }

    public static Vector3fc readVector3f(FriendlyByteBuf friendlyByteBuf) {
        return new Vector3f(friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat());
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return KyanitePortalsParticleTypes.PORTAL;
    }

    public void writeToNetwork(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeFloat(this.color.x());
        friendlyByteBuf.writeFloat(this.color.y());
        friendlyByteBuf.writeFloat(this.color.z());
    }

    public @NotNull String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.color.x(), this.color.y(), this.color.z());
    }

    public Vector3fc getColor() {
        return this.color;
    }
}