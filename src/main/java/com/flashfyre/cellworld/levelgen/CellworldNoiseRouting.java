package com.flashfyre.cellworld.levelgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class CellworldNoiseRouting {
    public static final ResourceKey<DensityFunction> SHIFT_X = vanillaKey("shift_x");
    public static final ResourceKey<DensityFunction> SHIFT_Z = vanillaKey("shift_z");

    public static NoiseRouter flat(HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noises) {
        DensityFunction finalDensity = DensityFunctions.yClampedGradient(0, 64, 1.0D, -1.0D);

        return new NoiseRouter(
                DensityFunctions.zero(), // barrier noise
                DensityFunctions.zero(), // fluid level floodedness noise
                DensityFunctions.zero(), // fluid level spread noise
                DensityFunctions.zero(), // lava noise
                DensityFunctions.zero(), // temperature
                DensityFunctions.zero(), // vegetation
                DensityFunctions.zero(), // continentalness noise
                DensityFunctions.zero(), // erosion noise
                DensityFunctions.zero(), // depth
                DensityFunctions.zero(), // ridges
                finalDensity, // initial density without jaggedness
                finalDensity, // finaldensity
                DensityFunctions.zero(), // veinToggle
                DensityFunctions.zero(), // veinRidged
                DensityFunctions.zero()); // veinGap
    }

    static ResourceKey<DensityFunction> vanillaKey(String id) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, ResourceLocation.withDefaultNamespace(id));
    }

    public static DensityFunction getFunction(HolderGetter<DensityFunction> densityFunctions, ResourceKey<DensityFunction> key) {
        return new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(key));
    }
}
