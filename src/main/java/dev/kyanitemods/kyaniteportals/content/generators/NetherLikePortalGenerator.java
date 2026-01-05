package dev.kyanitemods.kyaniteportals.content.generators;

import com.mojang.serialization.Codec;
//? if >=1.20.6
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.registry.PortalGenerators;
import dev.kyanitemods.kyaniteportals.content.registry.PortalTriggers;
import dev.kyanitemods.kyaniteportals.content.testers.PortalTestResult;
import dev.kyanitemods.kyaniteportals.content.triggers.PortalTriggerInstance;
import dev.kyanitemods.kyaniteportals.content.triggers.TriggerResult;
import dev.kyanitemods.kyaniteportals.util.*;
import dev.kyanitemods.kyaniteportals.util.BlockPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.List;
import java.util.Set;

public class NetherLikePortalGenerator extends PortalGenerator<NetherLikePortalGenerator> {
    //$ map_codec_swap NetherLikePortalGenerator
    public static final MapCodec<NetherLikePortalGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PortalTriggers.CODEC.listOf().fieldOf("triggers").forGetter(NetherLikePortalGenerator::getTriggers),
            CodecHelper.DIMENSION_SET_CODEC.optionalFieldOf("valid_in", Set.of()).forGetter(NetherLikePortalGenerator::getValidDimensions),
            BlockEntityPair.CODEC.fieldOf("portal_block").forGetter(NetherLikePortalGenerator::getPortalBlock)
    ).apply(instance, NetherLikePortalGenerator::new));

    private final Set<ResourceKey<LevelStem>> validDimensions;
    private final List<PortalTriggerInstance<?>> triggers;
    private final BlockEntityPair portalBlock;

    public NetherLikePortalGenerator(List<PortalTriggerInstance<?>> triggers, Set<ResourceKey<LevelStem>> validDimensions, BlockEntityPair portalBlock) {
        this.triggers = triggers;
        this.validDimensions = validDimensions;
        this.portalBlock = portalBlock;
    }

    @Override
    public PortalGeneratorType<NetherLikePortalGenerator> getType() {
        return PortalGenerators.NETHER_LIKE;
    }

    @Override
    public List<PortalTriggerInstance<?>> getTriggers() {
        return triggers;
    }

    public Set<ResourceKey<LevelStem>> getValidDimensions() {
        return validDimensions;
    }

    public BlockEntityPair getPortalBlock() {
        return portalBlock;
    }

    @Override
    public TriggerResult run(GeneratorContext context) {
        if (context.tester() == null) return TriggerResult.FAIL;
        PortalTestResult result = context.tester().test(context.level(), context.pos());
        if (result.isSuccess()) {
            if (!result.isEmpty()) return TriggerResult.FAIL;
            if (!getValidDimensions().contains(Registries.levelToLevelStem(context.level().dimension()))) {
                return TriggerResult.FAIL;
            }
            BlockEntityPair pair = getPortalBlock();
            if (pair.state().hasProperty(BlockStateProperties.AXIS)) {
                pair = pair.with(pair.state().setValue(BlockStateProperties.AXIS, result.getAxis()));
            } else if (result.getAxis().isHorizontal() && pair.state().hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
                pair = pair.with(pair.state().setValue(BlockStateProperties.HORIZONTAL_AXIS, result.getAxis()));
            }
            final BlockEntityPair finalPair = pair;

            result.placePortalBlocks(context.level(), (level, pos) -> finalPair.set(level, pos, Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_CLIENTS));
            return TriggerResult.PASS;
        }
        return TriggerResult.FAIL;
    }
}
