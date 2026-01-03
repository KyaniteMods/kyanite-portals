package dev.kyanitemods.kyaniteportals.content.blocks;

//? if >=1.20.4
import com.mojang.serialization.MapCodec;
import dev.kyanitemods.kyaniteportals.KyanitePortals;
import dev.kyanitemods.kyaniteportals.content.Portal;
import dev.kyanitemods.kyaniteportals.content.blocks.entities.CustomPortalBlockEntity;
import dev.kyanitemods.kyaniteportals.content.interfaces.EntityInPortal;
import dev.kyanitemods.kyaniteportals.content.portalactions.PortalAction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
//? if >=1.21.5
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
//? if >=1.21.5
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class CustomNetherLikePortalBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    //? if <1.21.5 {
    /*private static final Map<Direction.Axis, VoxelShape> SHAPES = Map.of(
            Direction.Axis.X, Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0),
            Direction.Axis.Z, Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0));
    *///? } else
    private static final Map<Direction.Axis, VoxelShape> SHAPES = Shapes.rotateHorizontalAxis(Block.column(4.0, 16.0, 0.0, 16.0));

    public CustomNetherLikePortalBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    //TODO: updateShape

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPES.get(blockState.getValue(AXIS));
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return switch (rotation) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> {
                switch (blockState.getValue(AXIS)) {
                    case X: {
                        yield blockState.setValue(AXIS, Direction.Axis.Z);
                    }
                    case Z: {
                        yield blockState.setValue(AXIS, Direction.Axis.X);
                    }
                }
                yield blockState;
            }
            default -> blockState;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    //? if >=1.20.4 {
    public static final MapCodec<CustomNetherLikePortalBlock> CODEC = simpleCodec(CustomNetherLikePortalBlock::new);

    public MapCodec<CustomNetherLikePortalBlock> codec() {
        return CODEC;
    }
    //? }
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

    protected void execute(Level level, BlockPos pos, @Nullable Entity entity, Function<Portal, List<PortalAction<?>>> actions) {
        if (!(level.getBlockEntity(pos) instanceof CustomPortalBlockEntity blockEntity)) return;
        Optional<? extends HolderLookup.RegistryLookup<Portal>> lookup = level.registryAccess().lookup(KyanitePortals.RESOURCE_KEY);
        if (lookup.isEmpty()) return;
        Optional<Holder.Reference<Portal>> portalReference = lookup.get().get(blockEntity.getPortalKey());
        if (portalReference.isEmpty()) return;
        Portal portal = portalReference.get().value();
        Portal.executeAll(level, pos, entity, actions.apply(portal));
    }

    @Override
    //? if <1.21.5 {
    /*public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
    *///? } else if <1.21.10 {
    //public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier) {
    //? } else
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier, boolean bl) {
        if (!(level.getBlockEntity(pos) instanceof CustomPortalBlockEntity blockEntity)) return;
        //? if <1.21 {
        //if (!entity.canChangeDimensions()) return;
        //? } else
        if (!entity.canUsePortal(false)) return;

        Optional<? extends HolderLookup.RegistryLookup<Portal>> lookup = level.registryAccess().lookup(KyanitePortals.RESOURCE_KEY);
        if (lookup.isEmpty()) return;
        Optional<Holder.Reference<Portal>> portalReference = lookup.get().get(blockEntity.getPortalKey());
        if (portalReference.isEmpty()) return;
        Portal portal = portalReference.get().value();
        if (portal.entityPredicate().isEmpty() || (!level.isClientSide() && portal.entityPredicate().get().matches((ServerLevel) level, pos.getCenter(), entity))) {
            ((EntityInPortal) entity).tick(level, pos, blockEntity.getPortalKey());
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CustomPortalBlockEntity(pos, state);
    }
}
