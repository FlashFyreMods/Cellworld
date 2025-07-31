package com.flashfyre.cellworld;

import com.flashfyre.cellworld.levelgen.SeededEndIslandDensityFunction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.HashMap;
import java.util.Map;

public class CellworldNoiseWiringHelper implements DensityFunction.Visitor {
    private final Map<DensityFunction, DensityFunction> wrapped = new HashMap<>();

    private long levelSeed;
    private RandomState randomState;
    private boolean useLegacyRandomSource;

    public CellworldNoiseWiringHelper(long seed, RandomState randomState, boolean useLegacyRandomSource) {
        this.levelSeed = seed;
        this.randomState = randomState;
        this.useLegacyRandomSource = useLegacyRandomSource;
    }

    private RandomSource newLegacyInstance(long seed) {
        return new LegacyRandomSource(this.levelSeed + seed);
    }

    @Override
    public DensityFunction.NoiseHolder visitNoise(DensityFunction.NoiseHolder noiseHolder) {
        Holder<NormalNoise.NoiseParameters> holder = noiseHolder.noiseData();
        /*if (this.useLegacyRandomSource) {
            if (holder.is(Noises.TEMPERATURE)) {
                NormalNoise normalnoise3 = NormalNoise.createLegacyNetherBiome(
                        this.newLegacyInstance(0L), new NormalNoise.NoiseParameters(-7, 1.0, 1.0)
                );
                return new DensityFunction.NoiseHolder(holder, normalnoise3);
            }

            if (holder.is(Noises.VEGETATION)) {
                NormalNoise normalnoise2 = NormalNoise.createLegacyNetherBiome(
                        this.newLegacyInstance(1L), new NormalNoise.NoiseParameters(-7, 1.0, 1.0)
                );
                return new DensityFunction.NoiseHolder(holder, normalnoise2);
            }

            if (holder.is(Noises.SHIFT)) {
                NormalNoise normalnoise1 = NormalNoise.create(
                        this.randomState.random.fromHashOf(Noises.SHIFT.location()), new NormalNoise.NoiseParameters(0, 0.0)
                );
                return new DensityFunction.NoiseHolder(holder, normalnoise1);
            }
        }*/

        NormalNoise normalnoise = this.randomState.getOrCreateNoise(holder.unwrapKey().orElseThrow());
        return new DensityFunction.NoiseHolder(holder, normalnoise);
    }

    private DensityFunction wrapNew(DensityFunction densityFunction) {
        if (densityFunction instanceof BlendedNoise blendednoise) {
            RandomSource randomsource = this.useLegacyRandomSource
                    ? this.newLegacyInstance(0L)
                    : this.randomState.random.fromHashOf(ResourceLocation.withDefaultNamespace("terrain"));
            return blendednoise.withNewRandom(randomsource);
        } else {
            return (DensityFunction) (densityFunction instanceof DensityFunctions.EndIslandDensityFunction
                    ? new DensityFunctions.EndIslandDensityFunction(this.levelSeed)
                    : densityFunction instanceof SeededEndIslandDensityFunction ? new SeededEndIslandDensityFunction(this.levelSeed) : densityFunction);
        }
    }

    @Override
    public DensityFunction apply(DensityFunction densityFunction) {
        return this.wrapped.computeIfAbsent(densityFunction, this::wrapNew);
    }
}
