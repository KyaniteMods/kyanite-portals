package dev.kyanitemods.kyaniteportals.util;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

import java.util.function.BiPredicate;

public class AgnosticPredicate implements BiPredicate<Level, BlockPos> {
    public static final AgnosticPredicate ALWAYS_TRUE = AgnosticPredicate.create(BlockPredicate.alwaysTrue());
    public static final Codec<AgnosticPredicate> CODEC = BlockPredicate.CODEC.xmap(AgnosticPredicate::new, agnosticPredicate -> agnosticPredicate.predicate);

    private final BlockPredicate predicate;

    protected AgnosticPredicate(BlockPredicate predicate) {
        this.predicate = predicate;
    }

    private static AgnosticPredicate create(BlockPredicate predicate) {
        return new AgnosticPredicate(predicate);
    }

    public static AgnosticPredicate of(BlockPredicate predicate) {
        if (predicate == BlockPredicate.alwaysTrue()) return ALWAYS_TRUE;
        return create(predicate);
    }

    @Override
    public boolean test(Level level, BlockPos pos) {
        try {
            WorldGenLevel worldGenLevel = level instanceof WorldGenLevel ? ((WorldGenLevel) level) : LevelWrapper.of(level);
            return predicate.test(worldGenLevel, pos);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean test(WorldGenLevel level, BlockPos pos) {
        return predicate.test(level, pos);
    }

    public boolean test(LevelReader reader, BlockPos pos) {
        try {
            WorldGenLevel worldGenLevel = reader instanceof WorldGenLevel ? ((WorldGenLevel) reader) : LevelWrapper.of(reader);
            return predicate.test(worldGenLevel, pos);
        } catch (Exception e) {
            return false;
        }
    }

    public BlockPredicate getBlockPredicate() {
        return predicate;
    }
}
