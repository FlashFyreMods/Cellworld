package com.flashfyre.cellworld;

import com.flashfyre.cellworld.cells.CellSelectionTree;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.*;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class CellularBiomeSource extends BiomeSource {
    public static final MapCodec<CellularBiomeSource> CODEC = CellSelectionTree.CODEC
            .fieldOf("cell_map")
            .xmap(CellularBiomeSource::new, s -> s.cellMap);


    private final Holder<CellSelectionTree> cellMap;

    public CellularBiomeSource(Holder<CellSelectionTree> cellMap) {
        this.cellMap = cellMap;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull Stream<Holder<Biome>> collectPossibleBiomes() {
        return this.cellMap.value().cellSelector().streamCells().map(e -> e.value().biome());
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        return this.cellMap.value().getCell(x<<2, z<<2).biome(); // Here, x y and z are QuartPos so we need to divide by 4
    }
}
