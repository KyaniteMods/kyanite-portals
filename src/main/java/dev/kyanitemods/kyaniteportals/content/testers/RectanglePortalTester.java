package dev.kyanitemods.kyaniteportals.content.testers;

import com.mojang.serialization.Codec;
//? if >=1.20.6
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.registry.PortalTesters;
import dev.kyanitemods.kyaniteportals.util.BlockPredicate;
import dev.kyanitemods.kyaniteportals.util.Range;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class RectanglePortalTester extends PortalTester<RectanglePortalTester> {
    //$ map_codec_swap RectanglePortalTester
    public static final MapCodec<RectanglePortalTester> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Range.Int.CODEC.fieldOf("width").forGetter(tester -> tester.width),
            Range.Int.CODEC.fieldOf("height").forGetter(tester -> tester.height),
            Direction.Axis.CODEC.listOf().xmap(Set::copyOf, List::copyOf).optionalFieldOf("axes", Set.of(Direction.Axis.X, Direction.Axis.Z)).forGetter(tester -> tester.axes),
            BlockPredicate.CODEC.fieldOf("frame").forGetter(tester -> tester.frame),
            BlockPredicate.CODEC.fieldOf("replaceable").forGetter(tester -> tester.replaceable),
            BlockPredicate.CODEC.fieldOf("portal").forGetter(tester -> tester.portal),
            Codec.BOOL.fieldOf("corners_required").forGetter(tester -> tester.cornersRequired)
    ).apply(instance, RectanglePortalTester::new));

    private final Range.Int width;
    private final Range.Int height;
    private final BlockPredicate frame;
    private final BlockPredicate replaceable;
    private final BlockPredicate portal;
    private final boolean cornersRequired;
    private final Set<Direction.Axis> axes;

    public RectanglePortalTester(Range.Int width, Range.Int height, Set<Direction.Axis> axes, BlockPredicate frame, BlockPredicate replaceable, BlockPredicate portal, boolean cornersRequired) {
        this.width = width;
        this.height = height;
        this.frame = frame;
        this.portal = portal;
        this.replaceable = replaceable;
        this.cornersRequired = cornersRequired;
        this.axes = axes;
    }

    public RectanglePortalTester(Range.Int width, Range.Int height, BlockPredicate frame, BlockPredicate replaceable, BlockPredicate portal) {
        this(width, height, Set.of(Direction.Axis.X, Direction.Axis.Z), frame, replaceable, portal, false);
    }

    public PortalTestResult test(LevelReader level, BlockPos pos) {
        for (Direction.Axis axis : axes) {
            PortalTestResult result = test(level, pos, axis);
            if (result.isSuccess()) return result;
        }
        return FailResult.INSTANCE;
    }

    // TODO: rotate 90° when axis is Y and test again, width and height are relative in Y axis
    public PortalTestResult test(LevelReader level, BlockPos pos, Direction.Axis axis) {
        int portalBlocks = 0;

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
        while (offsetY > minOffsetY && (portal.matches(level, bottom.relative(up.getOpposite())) || replaceable.matches(level, bottom.relative(up.getOpposite())))) {
            if (level.isOutsideBuildHeight(bottom)) return FailResult.INSTANCE;
            bottom = bottom.relative(up.getOpposite());
            offsetY--;
        }

        BlockPos bottomLeft = null;

        for (int x = 0; x < width.getMax().orElse(Integer.MAX_VALUE) - 2; x++) {
            BlockPos left = bottom.relative(right.getOpposite(), x);
            BlockPos leftNeighbor = bottom.relative(right.getOpposite(), x + 1);
            if (!frame.matches(level, leftNeighbor)) continue;
            if (portal.matches(level, left) || replaceable.matches(level, left)) {
                bottomLeft = left;
                break;
            }
        }
        if (bottomLeft == null) return FailResult.INSTANCE;

        int portalWidth = 0;
        for (int x = 0; x < width.getMax().orElse(Integer.MAX_VALUE) - 2; x++) {
            BlockPos inner = bottomLeft.relative(right, x);
            BlockPos below = inner.relative(up.getOpposite(), 1);
            if (portal.matches(level, inner) || replaceable.matches(level, inner)) {
                if (!frame.matches(level, below)) return FailResult.INSTANCE;
                portalWidth++;
                continue;
            }
            break;
        }

        int portalHeight = 0;
        for (int y = 0; y < height.getMax().orElse(Integer.MAX_VALUE) - 2; y++) {
            BlockPos leftCorner = bottomLeft.relative(up, y).relative(right.getOpposite(), 1);
            if (!frame.matches(level, leftCorner)) {
                portalHeight = y;
                break;
            }

            BlockPos rightCorner = bottomLeft.relative(up, y).relative(right, portalWidth);
            if (!frame.matches(level, rightCorner)) {
                portalHeight = y;
                break;
            }

            boolean obstructed = false;
            for (int x = 0; x < portalWidth; x++) {
                BlockPos inner = bottomLeft.relative(up, y).relative(right, x);

                if (portal.matches(level, inner)) {
                    portalBlocks++;
                    continue;
                }

                if (!replaceable.matches(level, inner)) {
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
            if (!frame.matches(level, top)) {
                return FailResult.INSTANCE;
            }
        }

        if (!width.matches(portalWidth + 2) || !height.matches(portalHeight + 2)) return FailResult.INSTANCE;
        if (cornersRequired) {
            BlockPos frameBottomLeft = bottomLeft.relative(right.getOpposite(), 1).relative(up.getOpposite(), 1);
            if (!frame.matches(level, frameBottomLeft)) return FailResult.INSTANCE;
            BlockPos frameBottomRight = bottomLeft.relative(right, portalWidth).relative(up.getOpposite(), 1);
            if (!frame.matches(level, frameBottomRight)) return FailResult.INSTANCE;
            BlockPos frameTopLeft = bottomLeft.relative(right.getOpposite(), 1).relative(up, portalHeight);
            if (!frame.matches(level, frameTopLeft)) return FailResult.INSTANCE;
            BlockPos frameTopRight = bottomLeft.relative(right, portalWidth).relative(up, portalHeight);
            if (!frame.matches(level, frameTopRight)) return FailResult.INSTANCE;
        }

        return new SuccessResult(bottomLeft, portalWidth, portalHeight, up, right, axis, portalBlocks);
    }

    @Override
    public PortalTesterType<RectanglePortalTester> getType() {
        return PortalTesters.RECTANGLE;
    }

    public static class FailResult implements PortalTestResult {
        public static final FailResult INSTANCE = new FailResult();

        private FailResult() {}

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public void placePortalBlocks(LevelAccessor level, BiConsumer<LevelAccessor, BlockPos> placer) {}

        @Override
        public Direction.Axis getAxis() {
            return null;
        }

        @Override
        public int getPortalBlocks() {
            return 0;
        }

        @Override
        public boolean isComplete() {
            return false;
        }
    }

    public static class SuccessResult implements PortalTestResult {
        private final BlockPos bottomLeft;
        private final int width;
        private final int height;
        private final Direction up;
        private final Direction right;
        private final Direction.Axis axis;
        private final int portalBlocks;

        public SuccessResult(BlockPos bottomLeft, int width, int height, Direction up, Direction right, Direction.Axis axis, int portalBlocks) {
            this.bottomLeft = bottomLeft;
            this.width = width;
            this.height = height;
            this.up = up;
            this.right = right;
            this.axis = axis;
            this.portalBlocks = portalBlocks;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public void placePortalBlocks(LevelAccessor level, BiConsumer<LevelAccessor, BlockPos> placer) {
            BlockPos.betweenClosed(bottomLeft, bottomLeft.relative(up, height - 1).relative(right, width - 1))
                    .forEach(pos -> placer.accept(level, pos));
        }

        @Override
        public Direction.Axis getAxis() {
            return axis;
        }

        @Override
        public int getPortalBlocks() {
            return portalBlocks;
        }

        @Override
        public boolean isComplete() {
            return getPortalBlocks() == width * height;
        }
    }
}
