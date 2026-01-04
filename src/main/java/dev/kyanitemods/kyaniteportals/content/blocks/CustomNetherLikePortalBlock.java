package dev.kyanitemods.kyaniteportals.content.blocks;

//? if >=1.20.4
import com.mojang.serialization.MapCodec;
import dev.kyanitemods.kyaniteportals.KyanitePortals;
import dev.kyanitemods.kyaniteportals.content.Portal;
import dev.kyanitemods.kyaniteportals.content.blocks.entities.CustomPortalBlockEntity;
import dev.kyanitemods.kyaniteportals.content.interfaces.EntityInPortal;
import dev.kyanitemods.kyaniteportals.content.actions.PortalAction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
//? if >=1.21.5
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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

    @Override
    //? if <1.21.3 {
    /*public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor level, BlockPos pos, BlockPos blockPos2) {
    *///? } else {
    protected BlockState updateShape(BlockState blockState, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos blockPos2, BlockState blockState2, RandomSource randomSource) {
     //? }
        Direction.Axis axis = direction.getAxis();
        Direction.Axis axis2 = blockState.getValue(AXIS);
        boolean bl = axis2 != axis && axis.isHorizontal();
        Optional<Portal> portal = getPortal(level, pos);
        if (bl || portal.isEmpty() || !portal.get().testValidityAfterGeneration() || portal.get().tester().isEmpty() || portal.get().tester().get().test(level, pos).isComplete()) {
            //? if <1.21.3 {
            /*return super.updateShape(blockState, direction, blockState2, level, pos, blockPos2);
            *///? } else
            return super.updateShape(blockState, level, scheduledTickAccess, pos, direction, blockPos2, blockState2, randomSource);
        }
        return Blocks.AIR.defaultBlockState();
    }

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

    protected Optional<Portal> getPortal(LevelReader level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof CustomPortalBlockEntity blockEntity)) return Optional.empty();
        Optional<? extends HolderLookup.RegistryLookup<Portal>> lookup = level.registryAccess().lookup(KyanitePortals.RESOURCE_KEY);
        if (lookup.isEmpty()) return Optional.empty();
        Optional<Holder.Reference<Portal>> portalReference = lookup.get().get(blockEntity.getPortalKey());
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
    //public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier) {
    //? } else
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier, boolean bl) {
        if (!(level.getBlockEntity(pos) instanceof CustomPortalBlockEntity blockEntity)) return;
        //? if <1.21 {
        /*if (!entity.canChangeDimensions()) return;
        *///? } else
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
