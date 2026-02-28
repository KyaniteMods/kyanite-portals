package dev.kyanitemods.kyaniteportals.content.blocks;

import dev.kyanitemods.kyaniteportals.content.Portal;
import dev.kyanitemods.kyaniteportals.content.blocks.entities.CustomPortalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class CustomNetherLikePortalBlock extends KyanitePortalBlock implements EntityBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

    private static final Map<Direction.Axis, VoxelShape> SHAPES = new EnumMap<>(Map.of(
            Direction.Axis.X, Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0),
            Direction.Axis.Y, Block.box(0.0, 6.0, 0.0, 16.0, 10.0, 16.0),
            Direction.Axis.Z, Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0)));

    public CustomNetherLikePortalBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    @Override
    //? if <1.21.3 {
    /*public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor level, BlockPos pos, BlockPos blockPos2) {
    *///? } else {
    protected BlockState updateShape(BlockState blockState, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos blockPos2, BlockState blockState2, net.minecraft.util.RandomSource randomSource) {
     //? }
        Direction.Axis axis = direction.getAxis();
        Direction.Axis axis2 = blockState.getValue(AXIS);
        boolean bl = axis2 != axis && ((axis2.isHorizontal() && axis.isHorizontal()) || (axis2.isVertical() && axis.isVertical()));
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
    public static final com.mojang.serialization.MapCodec<CustomNetherLikePortalBlock> CODEC = simpleCodec(CustomNetherLikePortalBlock::new);

    public com.mojang.serialization.MapCodec<CustomNetherLikePortalBlock> codec() {
        return CODEC;
    }
    //? }

    @Override
    public Optional<ResourceKey<Portal>> getPortalKey(LevelReader level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof CustomPortalBlockEntity blockEntity)) return Optional.empty();
        return Optional.of(blockEntity.getPortalKey());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CustomPortalBlockEntity(pos, state);
    }
}
