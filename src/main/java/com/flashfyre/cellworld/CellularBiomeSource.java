package com.flashfyre.cellworld;

import com.flashfyre.cellworld.cells.CellMap;
import com.flashfyre.cellworld.cells.CellSelector;
import com.flashfyre.cellworld.cells.RandomFromWeightedListHolderSet;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.*;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class CellularBiomeSource extends BiomeSource {
    public static final MapCodec<CellularBiomeSource> CODEC = CellMap.CODEC
            .fieldOf("cell_map")
            .xmap(CellularBiomeSource::new, s -> s.cellMap);


    private final CellMap cellMap;

    public CellularBiomeSource(CellMap cellMap) {
        this.cellMap = cellMap;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull Stream<Holder<Biome>> collectPossibleBiomes() {
        return flattenSelector(this.cellMap.cells());
    }

    public static Stream<Holder<Biome>> flattenSelector(CellSelector selector) {
        if(selector instanceof RandomFromWeightedListHolderSet weightedRandomHolderSet) {
            weightedRandomHolderSet.buildList();
        }
        return selector.all().flatMap(entry -> flatten(entry.value()));
    }

    public static Stream<Holder<Biome>> flatten(Either<Cell, CellSelector> either) {
        if (either.left().isPresent()) {
            return Stream.of(either.left().orElseThrow().biome());
        } else {
            return flattenSelector(either.right().orElseThrow());
        }
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        return this.cellMap.getCell(x, z).biome();
    }
}
