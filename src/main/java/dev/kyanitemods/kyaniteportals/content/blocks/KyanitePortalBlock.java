package dev.kyanitemods.kyaniteportals.content.blocks;

import dev.kyanitemods.kyaniteportals.KyanitePortals;
import dev.kyanitemods.kyaniteportals.content.Portal;
import dev.kyanitemods.kyaniteportals.content.actions.PortalAction;
import dev.kyanitemods.kyaniteportals.content.interfaces.EntityInPortal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class KyanitePortalBlock extends Block {
    public KyanitePortalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        execute(level, pos, null, Portal::animationTickActions);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState newState, boolean bl) {
        level.scheduleTick(pos, this, 1);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        execute(level, pos, null, Portal::tickActions);
        level.scheduleTick(pos, this, 1);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        execute(level, pos, null, Portal::randomTickActions);
    }

    public abstract Optional<ResourceKey<Portal>> getPortalKey(LevelReader level, BlockPos pos);

    protected Optional<Portal> getPortal(LevelReader level, BlockPos pos) {
        Optional<ResourceKey<Portal>> key = getPortalKey(level, pos);
        if (key.isEmpty()) return Optional.empty();
        Optional<? extends HolderLookup.RegistryLookup<Portal>> lookup = level.registryAccess().lookup(KyanitePortals.RESOURCE_KEY);
        if (lookup.isEmpty()) return Optional.empty();
        Optional<Holder.Reference<Portal>> portalReference = lookup.get().get(key.get());
        return portalReference.map(Holder.Reference::value);
    }

    protected void execute(Level level, BlockPos pos, @Nullable Entity entity, Function<Portal, List<PortalAction<?>>> actions) {
        Optional<Portal> portal = getPortal(level, pos);
        if (portal.isEmpty()) return;
        Portal.executeAll(level, pos, entity, actions.apply(portal.get()));
    }

    @Override
            //? if <1.21.5 {
    /*public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
     *///? } else if <1.21.10 {
    //public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, net.minecraft.world.entity.InsideBlockEffectApplier insideBlockEffectApplier) {
            //? } else
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, net.minecraft.world.entity.InsideBlockEffectApplier insideBlockEffectApplier, boolean bl) {
        //? if <1.21 {
        /*if (!entity.canChangeDimensions()) return;
         *///? } else
        if (!entity.canUsePortal(false)) return;

        Optional<ResourceKey<Portal>> portalKey = getPortalKey(level, pos);
        Optional<Portal> portal = getPortal(level, pos);
        if (portal.isEmpty() || portalKey.isEmpty()) return;

        if (portal.get().entityPredicate().isEmpty() || (!level.isClientSide() && portal.get().entityPredicate().get().matches((ServerLevel) level, pos.getCenter(), entity))) {
            ((EntityInPortal) entity).tick(level, pos, portalKey.get());
        }
    }
}
