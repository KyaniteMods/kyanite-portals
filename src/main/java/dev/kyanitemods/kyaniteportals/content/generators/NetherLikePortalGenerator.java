package dev.kyanitemods.kyaniteportals.content.generators;

import com.mojang.serialization.Codec;
//? if >=1.20.6
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.registry.PortalGenerators;
import dev.kyanitemods.kyaniteportals.content.registry.PortalTriggers;
import dev.kyanitemods.kyaniteportals.content.triggers.PortalTriggerInstance;
import dev.kyanitemods.kyaniteportals.content.triggers.TriggerResult;
import dev.kyanitemods.kyaniteportals.util.*;
import dev.kyanitemods.kyaniteportals.util.BlockPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NetherLikePortalGenerator extends PortalGenerator<NetherLikePortalGenerator> {
    //$ map_codec_swap NetherLikePortalGenerator
    public static final MapCodec<NetherLikePortalGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PortalTriggers.CODEC.listOf().fieldOf("triggers").forGetter(NetherLikePortalGenerator::getTriggers),
            DimensionList.CODEC.optionalFieldOf("valid_in", DimensionList.EMPTY).forGetter(NetherLikePortalGenerator::getValidDimensions),
            BlockPredicate.CODEC.optionalFieldOf("frame_block", BlockPredicate.ANY).forGetter(NetherLikePortalGenerator::getFrame),
            BlockPredicate.CODEC.optionalFieldOf("replaceable", BlockPredicate.ANY).forGetter(NetherLikePortalGenerator::getReplaceable),
            BlockEntityPair.CODEC.fieldOf("portal_block").forGetter(NetherLikePortalGenerator::getPortalBlock),
            Range.Int.CODEC.optionalFieldOf("width", Range.Int.create(4, 23)).forGetter(NetherLikePortalGenerator::getWidth),
            Range.Int.CODEC.optionalFieldOf("height", Range.Int.create(5, 23)).forGetter(NetherLikePortalGenerator::getHeight),
            Codec.BOOL.optionalFieldOf("corners_required", false).forGetter(NetherLikePortalGenerator::areCornersRequired)
    ).apply(instance, NetherLikePortalGenerator::new));

    private final DimensionList validDimensions;
    private final List<PortalTriggerInstance<?>> triggers;
    private final BlockPredicate frame;
    private final BlockPredicate replaceable;
    private final BlockEntityPair portalBlock;
    private final Range.Int width;
    private final Range.Int height;
    private final boolean cornersRequired;

    public NetherLikePortalGenerator(List<PortalTriggerInstance<?>> triggers, DimensionList validDimensions, BlockPredicate frame, BlockPredicate replaceable, BlockEntityPair portalBlock, Range.Int width, Range.Int height, boolean cornersRequired) {
        this.triggers = triggers;
        this.validDimensions = validDimensions;
        this.frame = frame;
        this.replaceable = replaceable;
        this.portalBlock = portalBlock;
        this.width = width;
        this.height = height;
        this.cornersRequired = cornersRequired;
    }

    @Override
    public PortalGeneratorType<NetherLikePortalGenerator> getType() {
        return PortalGenerators.NETHER_LIKE;
    }

    @Override
    public List<PortalTriggerInstance<?>> getTriggers() {
        return triggers;
    }

    public DimensionList getValidDimensions() {
        return validDimensions;
    }

    public BlockPredicate getFrame() {
        return frame;
    }

    public BlockPredicate getReplaceable() {
        return replaceable;
    }

    public BlockEntityPair getPortalBlock() {
        return portalBlock;
    }

    public Range.Int getWidth() {
        return width;
    }

    public Range.Int getHeight() {
        return height;
    }

    public boolean areCornersRequired() {
        return cornersRequired;
    }

    @Override
    public <I extends PortalTriggerInstance<I>> TriggerResult run(I instance, Level level, BlockPos pos, @Nullable Player player) {
        SquarePortalTester tester = new SquarePortalTester(getWidth(), getHeight(), frame::matches, replaceable::matches, areCornersRequired());
        SquarePortalTester.Result result = tester.test(level, pos, Direction.Axis.X, Direction.Axis.Z);
        if (result.isSuccess()) {
            if (!getValidDimensions().matches(level)) {
                return TriggerResult.FAIL;
            }
            BlockEntityPair pair = getPortalBlock();
            if (pair.state().hasProperty(BlockStateProperties.AXIS)) {
                pair = pair.with(pair.state().setValue(BlockStateProperties.AXIS, result.getAxis()));
            } else if (result.getAxis().isHorizontal() && pair.state().hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
                pair = pair.with(pair.state().setValue(BlockStateProperties.HORIZONTAL_AXIS, result.getAxis()));
            }
            final BlockEntityPair pair1 = pair;

            result.placePortalBlocks((level1, pos1) -> pair1.set(level1, pos1, Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_CLIENTS));
            return TriggerResult.PASS;
        }
        return TriggerResult.FAIL;
    }
}
