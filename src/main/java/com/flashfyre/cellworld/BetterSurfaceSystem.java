package com.flashfyre.cellworld;

import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.*;

public class BetterSurfaceSystem extends SurfaceSystem {
    public BetterSurfaceSystem(RandomState randomState, BlockState defaultBlock, int seaLevel, PositionalRandomFactory noiseRandom) {
        super(randomState, defaultBlock, seaLevel, noiseRandom);
    }

    public void buildSurface(
            RandomState randomState,
            BiomeManager biomeManager,
            Registry<Biome> biomes,
            boolean useLegacyRandomSource,
            WorldGenerationContext context,
            final ChunkAccess chunk,
            NoiseChunk noiseChunk,
            SurfaceRules.RuleSource ruleSource
    ) {
    }
}
