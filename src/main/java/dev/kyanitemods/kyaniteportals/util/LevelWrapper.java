package dev.kyanitemods.kyaniteportals.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
//? if >=1.21.11 {
import net.minecraft.world.attribute.EnvironmentAttributeReader;
//? }
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.ScheduledTick;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public class LevelWrapper implements WorldGenLevel, LevelReader {
    private final LevelReader level;

    private static final LevelData defaultLevelData = new LevelData() {
        //? if <1.20.6 {
        /*@Override
        public int getXSpawn() {
            return 0;
        }

        @Override
        public int getYSpawn() {
            return 0;
        }

        @Override
        public int getZSpawn() {
            return 0;
        }

        @Override
        public float getSpawnAngle() {
            return 0;
        }
        *///? } else if <1.21.9 {
        /*@Override
        public BlockPos getSpawnPos() {
            return BlockPos.ZERO;
        }

        @Override
        public float getSpawnAngle() {
            return 0;
        }
        *///? } else {
        @Override
        public LevelData.RespawnData getRespawnData() {
            return LevelData.RespawnData.DEFAULT;
        }
        //? }

        @Override
        public long getGameTime() {
            return 0;
        }

        @Override
        public long getDayTime() {
            return 0;
        }

        @Override
        public boolean isThundering() {
            return false;
        }

        @Override
        public boolean isRaining() {
            return false;
        }

        @Override
        public void setRaining(boolean bl) {

        }

        @Override
        public boolean isHardcore() {
            return false;
        }

        //? if <1.21.3 {
        /*@Override
        public GameRules getGameRules() {
            return new GameRules();
        }
        *///? }

        @Override
        public Difficulty getDifficulty() {
            return Difficulty.NORMAL;
        }

        @Override
        public boolean isDifficultyLocked() {
            return false;
        }
    };

    private final ChunkSource defaultChunkSource = new ChunkSource() {
        @Override
        public @Nullable ChunkAccess getChunk(int i, int j, ChunkStatus chunkStatus, boolean bl) {
            return level.getChunk(i, j, chunkStatus, bl);
        }

        @Override
        public void tick(BooleanSupplier booleanSupplier, boolean bl) {

        }

        @Override
        public String gatherStats() {
            return getClass().getTypeName() + " has no stats";
        }

        @Override
        public int getLoadedChunksCount() {
            return 0;
        }

        @Override
        public LevelLightEngine getLightEngine() {
            return level.getLightEngine();
        }

        @Override
        public BlockGetter getLevel() {
            return level;
        }
    };
    private static final LevelTickAccess<Block> defaultBlockTickAccess = new LevelTickAccess<>() {
        @Override
        public boolean willTickThisTick(BlockPos blockPos, Block object) {
            return false;
        }

        @Override
        public void schedule(ScheduledTick<Block> scheduledTick) {

        }

        @Override
        public boolean hasScheduledTick(BlockPos blockPos, Block object) {
            return false;
        }

        @Override
        public int count() {
            return 0;
        }
    };
    private static final LevelTickAccess<Fluid> defaultFluidTickAccess = new LevelTickAccess<>() {
        @Override
        public boolean willTickThisTick(BlockPos blockPos, Fluid object) {
            return false;
        }

        @Override
        public void schedule(ScheduledTick<Fluid> scheduledTick) {

        }

        @Override
        public boolean hasScheduledTick(BlockPos blockPos, Fluid object) {
            return false;
        }

        @Override
        public int count() {
            return 0;
        }
    };

    protected LevelWrapper(LevelReader level) {
        this.level = level;
    }

    public static LevelWrapper of(LevelReader level) {
        return new LevelWrapper(level);
    }

    @Override
    public long getSeed() {
        return 0L;
    }

    @Override
    public ServerLevel getLevel() {
        return level instanceof ServerLevelAccessor serverLevelAccessor ? serverLevelAccessor.getLevel() : null;
    }

    @Override
    public DifficultyInstance getCurrentDifficultyAt(BlockPos blockPos) {
        //? if <1.21.11 {
        /*if (level instanceof LevelAccessor levelAccessor) return levelAccessor.getCurrentDifficultyAt(blockPos);
        *///? } else {
        if (level instanceof ServerLevelAccessor serverLevelAccessor) {
            return serverLevelAccessor.getCurrentDifficultyAt(blockPos);
        } else if (level instanceof Level level1) {
            return new DifficultyInstance(level1.getDifficulty(), level1.getDayTime(), 0L, 0.0f);
        } else if (level instanceof LevelAccessor levelAccessor) {
            return new DifficultyInstance(levelAccessor.getDifficulty(), 0L, 0L, 0.0f);
        }
        //? }
        return new DifficultyInstance(Difficulty.NORMAL, 0L, 0L, 0.0f);
    }

    @Override
    public long nextSubTickCount() {
        return level instanceof WorldGenLevel worldGenLevel ? worldGenLevel.nextSubTickCount() : 0L;
    }

    @Override
    public LevelData getLevelData() {
        return level instanceof Level level1 ? level1.getLevelData() : defaultLevelData;
    }

    @Override
    public @Nullable MinecraftServer getServer() {
        return level instanceof WorldGenLevel worldGenLevel ? worldGenLevel.getServer() : null;
    }

    @Override
    public ChunkSource getChunkSource() {
        return level instanceof LevelAccessor levelAccessor ? levelAccessor.getChunkSource() : defaultChunkSource;
    }

    @Override
    public RandomSource getRandom() {
        return level instanceof LevelAccessor levelAccessor ? levelAccessor.getRandom() : RandomSource.create(0L);
    }

    @Override
    public void addParticle(ParticleOptions particleOptions, double d, double e, double f, double g, double h, double i) {
        if (level instanceof LevelAccessor levelAccessor) levelAccessor.addParticle(particleOptions, d, e, f, g, h, i);
    }

    //? if <1.21.5 {
    /*@Override
    public void playSound(@Nullable Player player, BlockPos blockPos, SoundEvent soundEvent, SoundSource soundSource, float f, float g) {
        if (level instanceof LevelAccessor levelAccessor) levelAccessor.playSound(player, blockPos, soundEvent, soundSource, f, g);
    }

    @Override
    public void levelEvent(@Nullable Player player, int i, BlockPos blockPos, int j) {
        if (level instanceof LevelAccessor levelAccessor) levelAccessor.levelEvent(player, i, blockPos, j);
    }
    *///? } else {
    @Override
    public void playSound(@Nullable Entity entity, BlockPos blockPos, SoundEvent soundEvent, SoundSource soundSource, float f, float g) {
        if (level instanceof LevelAccessor levelAccessor) levelAccessor.playSound(entity, blockPos, soundEvent, soundSource, f, g);
    }

    @Override
    public void levelEvent(@Nullable Entity entity, int i, BlockPos blockPos, int j) {
        if (level instanceof LevelAccessor levelAccessor) levelAccessor.levelEvent(entity, i, blockPos, j);
    }
    //? }

    //? if <1.20.6 {
    /*@Override
    public void gameEvent(GameEvent gameEvent, Vec3 vec3, GameEvent.Context context) {
        if (level instanceof LevelAccessor levelAccessor) levelAccessor.gameEvent(gameEvent, vec3, context);
    }
    *///? } else {
    @Override
    public void gameEvent(Holder<GameEvent> holder, Vec3 vec3, GameEvent.Context context) {
        if (level instanceof LevelAccessor levelAccessor) levelAccessor.gameEvent(holder, vec3, context);
    }
    //? }

    @Override
    public List<Entity> getEntities(@Nullable Entity entity, AABB aABB, Predicate<? super Entity> predicate) {
        return level instanceof LevelAccessor levelAccessor ? levelAccessor.getEntities(entity, aABB, predicate) : List.of();
    }

    @Override
    public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> entityTypeTest, AABB aABB, Predicate<? super T> predicate) {
        return level instanceof LevelAccessor levelAccessor ? levelAccessor.getEntities(entityTypeTest, aABB, predicate) : List.of();
    }

    @Override
    public List<? extends Player> players() {
        return level instanceof LevelAccessor levelAccessor ? levelAccessor.players() : List.of();
    }

    @Override
    public @Nullable ChunkAccess getChunk(int i, int j, ChunkStatus chunkStatus, boolean bl) {
        return level.getChunk(i, j, chunkStatus, bl);
    }

    @Override
    public int getHeight(Heightmap.Types types, int i, int j) {
        return level.getHeight(types, i, j);
    }

    @Override
    public int getSkyDarken() {
        return level.getSkyDarken();
    }

    @Override
    public BiomeManager getBiomeManager() {
        return level.getBiomeManager();
    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int i, int j, int k) {
        return level.getUncachedNoiseBiome(i, j, k);
    }

    @Override
    public boolean isClientSide() {
        return level.isClientSide();
    }

    @Override
    public int getSeaLevel() {
        return level.getSeaLevel();
    }

    @Override
    public DimensionType dimensionType() {
        return level.dimensionType();
    }

    @Override
    public RegistryAccess registryAccess() {
        return level.registryAccess();
    }

    @Override
    public FeatureFlagSet enabledFeatures() {
        return level.enabledFeatures();
    }

    //? if >=1.21.11 {
    @Override
    public EnvironmentAttributeReader environmentAttributes() {
        return level.environmentAttributes();
    }
    //? }

    @Override
    public float getShade(Direction direction, boolean bl) {
        return level.getShade(direction, bl);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return level.getLightEngine();
    }

    @Override
    public WorldBorder getWorldBorder() {
        return level.getWorldBorder();
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos blockPos) {
        return level.getBlockEntity(blockPos);
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos) {
        return level.getBlockState(blockPos);
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        return level.getFluidState(blockPos);
    }

    @Override
    public boolean isStateAtPosition(BlockPos blockPos, Predicate<BlockState> predicate) {
        return level instanceof LevelSimulatedReader levelSimulatedReader ? levelSimulatedReader.isStateAtPosition(blockPos, predicate) : predicate.test(getBlockState(blockPos));
    }

    @Override
    public boolean isFluidAtPosition(BlockPos blockPos, Predicate<FluidState> predicate) {
        return level instanceof LevelSimulatedReader levelSimulatedReader ? levelSimulatedReader.isFluidAtPosition(blockPos, predicate) : predicate.test(getFluidState(blockPos));
    }

    @Override
    public boolean setBlock(BlockPos blockPos, BlockState blockState, int i, int j) {
        return level instanceof LevelWriter levelWriter && levelWriter.setBlock(blockPos, blockState, i, j);
    }

    @Override
    public boolean removeBlock(BlockPos blockPos, boolean bl) {
        return level instanceof LevelWriter levelWriter && levelWriter.removeBlock(blockPos, bl);
    }

    @Override
    public boolean destroyBlock(BlockPos blockPos, boolean bl, @Nullable Entity entity, int i) {
        return level instanceof LevelWriter levelWriter && levelWriter.destroyBlock(blockPos, bl, entity, i);
    }

    //~ if <1.21.3 'ScheduledTickAccess' -> 'LevelAccessor' {
    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return level instanceof ScheduledTickAccess scheduledTickAccess ? scheduledTickAccess.getBlockTicks() : defaultBlockTickAccess;
    }

    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return level instanceof ScheduledTickAccess scheduledTickAccess ? scheduledTickAccess.getFluidTicks() : defaultFluidTickAccess;
    }
    //~ }
}
