package com.flashfyre.cellworld.chunkgenerator;

import com.flashfyre.cellworld.cells.CellSelectionTreeOld;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class CellworldChunkGenerator extends NoiseBasedChunkGenerator {
    public CellworldChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings, CellSelectionTreeOld cellMap) {
        super(biomeSource, settings);
        this.cellMap = cellMap;
    }

    private final CellSelectionTreeOld cellMap;


}
