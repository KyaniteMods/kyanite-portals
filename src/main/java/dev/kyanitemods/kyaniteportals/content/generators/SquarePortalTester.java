package dev.kyanitemods.kyaniteportals.content.generators;

import dev.kyanitemods.kyaniteportals.util.Range;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class SquarePortalTester {
    private final Range.Int width;
    private final Range.Int height;
    private final BiFunction<Level, BlockPos, Boolean> frame;
    private final BiFunction<Level, BlockPos, Boolean> replaceable;
    private final boolean cornersRequired;

    public SquarePortalTester(Range.Int width, Range.Int height, BiFunction<Level, BlockPos, Boolean> frame, BiFunction<Level, BlockPos, Boolean> replaceable, boolean cornersRequired) {
        this.width = width;
        this.height = height;
        this.frame = frame;
        this.replaceable = replaceable;
        this.cornersRequired = cornersRequired;
    }

    public SquarePortalTester(Range.Int width, Range.Int height, BiFunction<Level, BlockPos, Boolean> frame, BiFunction<Level, BlockPos, Boolean> replaceable) {
        this(width, height, frame, replaceable, false);
    }

    public Result test(Level level, BlockPos pos, Direction.Axis axis, Direction.Axis... axes) {
        Result result = test(level, pos, axis);
        if (result.isSuccess()) return result;
        for (Direction.Axis axis1 : axes) {
            result = test(level, pos, axis1);
            if (result.isSuccess()) return result;
        }
        return FailResult.INSTANCE;
    }

    public Result test(Level level, BlockPos pos, Direction.Axis axis) {
        Direction right = switch (axis) {
            case X, Y -> Direction.WEST;
            case Z -> Direction.SOUTH;
        };
        Direction up = switch (axis) {
            case X, Z -> Direction.UP;
            case Y -> Direction.SOUTH;
        };

        BlockPos bottom = pos;

        int offsetY = 0;
        int minOffsetY = -(height.getMax().orElse(Integer.MAX_VALUE) - 3);
        while (offsetY > minOffsetY && replaceable.apply(level, bottom.relative(up.getOpposite()))) {
            if (!level.isInWorldBounds(bottom)) return FailResult.INSTANCE;
            bottom = bottom.relative(up.getOpposite());
            offsetY--;
        }

        BlockPos bottomLeft = null;

        for (int i = 0; i < width.getMax().orElse(Integer.MAX_VALUE) - 2; i++) {
            BlockPos left = bottom.relative(right.getOpposite(), i);
            BlockPos leftNeighbor = bottom.relative(right.getOpposite(), i + 1);
            if (replaceable.apply(level, left) && frame.apply(level, leftNeighbor)) {
                bottomLeft = left;
                break;
            }
        }
        if (bottomLeft == null) return FailResult.INSTANCE;

        int portalWidth = 0;
        for (int x = 0; x < width.getMax().orElse(Integer.MAX_VALUE) - 2; x++) {
            BlockPos inner = bottomLeft.relative(right, x);
            BlockPos below = inner.relative(up.getOpposite(), 1);
            if (replaceable.apply(level, inner)) {
                if (!frame.apply(level, below)) return FailResult.INSTANCE;
                portalWidth++;
                continue;
            }
            break;
        }

        int portalHeight = 0;
        for (int y = 0; y < height.getMax().orElse(Integer.MAX_VALUE) - 2; y++) {
            BlockPos leftCorner = bottomLeft.relative(up, y).relative(right.getOpposite(), 1);
            if (!frame.apply(level, leftCorner)) {
                portalHeight = y;
                break;
            }

            BlockPos rightCorner = bottomLeft.relative(up, y).relative(right, portalWidth);
            if (!frame.apply(level, rightCorner)) {
                portalHeight = y;
                break;
            }

            boolean obstructed = false;
            for (int x = 0; x < portalWidth; x++) {
                BlockPos inner = bottomLeft.relative(up, y).relative(right, x);
                if (!replaceable.apply(level, inner)) {
                    obstructed = true;
                    break;
                }
            }

            if (obstructed) {
                portalHeight = y;
                break;
            }
        }

        for (int x = 0; x < portalWidth; x++) {
            BlockPos top = bottomLeft.relative(right, x).relative(up, portalHeight);
            if (!frame.apply(level, top)) {
                return FailResult.INSTANCE;
            }
        }

        if (!width.matches(portalWidth + 2) || !height.matches(portalHeight + 2)) return FailResult.INSTANCE;
        if (cornersRequired) {
            BlockPos frameBottomLeft = bottomLeft.relative(right.getOpposite(), 1).relative(up.getOpposite(), 1);
            if (!frame.apply(level, frameBottomLeft)) return FailResult.INSTANCE;
            BlockPos frameBottomRight = bottomLeft.relative(right, portalWidth).relative(up.getOpposite(), 1);
            if (!frame.apply(level, frameBottomRight)) return FailResult.INSTANCE;
            BlockPos frameTopLeft = bottomLeft.relative(right.getOpposite(), 1).relative(up, portalHeight);
            if (!frame.apply(level, frameTopLeft)) return FailResult.INSTANCE;
            BlockPos frameTopRight = bottomLeft.relative(right, portalWidth).relative(up, portalHeight);
            if (!frame.apply(level, frameTopRight)) return FailResult.INSTANCE;
        }

        return new SuccessResult(level, bottomLeft, portalWidth, portalHeight, up, right, axis);
    }

    public interface Result {
        boolean isSuccess();
        void placePortalBlocks(BiConsumer<LevelAccessor, BlockPos> placer);
        Direction.Axis getAxis();
    }

    public static class FailResult implements Result {
        public static final FailResult INSTANCE = new FailResult();

        private FailResult() {}

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public void placePortalBlocks(BiConsumer<LevelAccessor, BlockPos> placer) {}

        @Override
        public Direction.Axis getAxis() {
            return null;
        }
    }

    public static class SuccessResult implements Result {
        private final LevelAccessor level;
        private final BlockPos bottomLeft;
        private final int width;
        private final int height;
        private final Direction up;
        private final Direction right;
        private final Direction.Axis axis;

        public SuccessResult(LevelAccessor level, BlockPos bottomLeft, int width, int height, Direction up, Direction right, Direction.Axis axis) {
            this.level = level;
            this.bottomLeft = bottomLeft;
            this.width = width;
            this.height = height;
            this.up = up;
            this.right = right;
            this.axis = axis;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public void placePortalBlocks(BiConsumer<LevelAccessor, BlockPos> placer) {
            BlockPos.betweenClosed(bottomLeft, bottomLeft.relative(up, height - 1).relative(right, width - 1))
                    .forEach(pos -> placer.accept(level, pos));
        }

        @Override
        public Direction.Axis getAxis() {
            return axis;
        }
    }
}
