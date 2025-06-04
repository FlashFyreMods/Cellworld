package com.flashfyre.cellworld;

import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;

public class CellNoiseChunk extends NoiseChunk {
    public CellNoiseChunk(int cellCountXZ, RandomState random, int firstNoiseX, int firstNoiseZ, NoiseSettings noiseSettings, DensityFunctions.BeardifierOrMarker beardifier, NoiseGeneratorSettings noiseGeneratorSettings, Aquifer.FluidPicker fluidPicker, Blender blendifier) {
        super(cellCountXZ, random, firstNoiseX, firstNoiseZ, noiseSettings, beardifier, noiseGeneratorSettings, fluidPicker, blendifier);
    }
}
