package dev.kyanitemods.kyaniteportals.content.portalactions;

//? if <1.20.6 {
import com.mojang.serialization.Codec;
//? } else
//import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.portalactions.location.ActionLocation;
import dev.kyanitemods.kyaniteportals.content.registry.PortalActions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SpawnNetherLikePortalParticlesAction extends PortalAction<SpawnNetherLikePortalParticlesAction> {
    //$ map_codec_swap SpawnNetherLikePortalParticlesAction
    public static final Codec<SpawnNetherLikePortalParticlesAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Settings.optionalLocation(),
            IntProvider.POSITIVE_CODEC.fieldOf("amount").forGetter(SpawnNetherLikePortalParticlesAction::getAmount),
            ParticleTypes.CODEC.fieldOf("particle_options").forGetter(SpawnNetherLikePortalParticlesAction::getParticleOptions)
    ).apply(instance, SpawnNetherLikePortalParticlesAction::new));

    private final IntProvider amount;
    private final ParticleOptions particleOptions;

    public SpawnNetherLikePortalParticlesAction(Settings settings, IntProvider amount, ParticleOptions particleOptions) {
        super(settings);
        this.amount = amount;
        this.particleOptions = particleOptions;
    }

    @Override
    public PortalActionType<SpawnNetherLikePortalParticlesAction> getType() {
        return PortalActions.SPAWN_NETHER_LIKE_PORTAL_PARTICLES;
    }

    public IntProvider getAmount() {
        return amount;
    }

    public ParticleOptions getParticleOptions() {
        return particleOptions;
    }

    @Override
    public PortalActionResult execute(Level level, BlockPos pos, @Nullable Entity entity, ActionExecutionData data) {
        ActionLocation location = getSettings().locationOptions().get(level, pos, entity, level.getRandom(), data);
        BlockPos blockPos = BlockPos.containing(location.position());
        BlockState state = level.getBlockState(blockPos);
        int amount = getAmount().sample(level.getRandom());
        for (int i = 0; i < amount; i++) {
            double x = location.position().x() + level.getRandom().nextDouble();
            double y = location.position().y() + level.getRandom().nextDouble();
            double z = location.position().z() + level.getRandom().nextDouble();
            double xd = (level.getRandom().nextFloat() - 0.5) * 0.5;
            double yd = (level.getRandom().nextFloat() - 0.5) * 0.5;
            double zd = (level.getRandom().nextFloat() - 0.5) * 0.5;
            int k = level.getRandom().nextInt(2) * 2 - 1;
            // mad code from Mojang, this doesn't even work properly in the left and right edges of the portal
            if (!level.getBlockState(blockPos.west()).is(state.getBlock()) && !level.getBlockState(blockPos.east()).is(state.getBlock())) {
                x = location.position().x() + 0.5 + 0.25 * k;
                xd = level.getRandom().nextFloat() * 2.0F * k;
            } else {
                z = location.position().z() + 0.5 + 0.25 * k;
                zd = level.getRandom().nextFloat() * 2.0F * k;
            }

            if (!level.isClientSide()) {
                ((ServerLevel) level).sendParticles(getParticleOptions(), x, y, z, 1, xd, yd, zd, 1.0);
            } else {
                level.addParticle(getParticleOptions(), x, y, z, xd, yd, zd);
            }
        }
        return PortalActionResult.SUCCESS;
    }
}
