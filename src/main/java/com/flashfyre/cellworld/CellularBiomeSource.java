package com.flashfyre.cellworld;

import com.flashfyre.cellworld.cells.Cell;
import com.flashfyre.cellworld.cells.CellMap;
import com.flashfyre.cellworld.cells.selector.CellSelectionSet;
import com.flashfyre.cellworld.cells.selector.WeightedRandomSelectionSet;
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


    private final Holder<CellMap> cellMap;

    public CellularBiomeSource(Holder<CellMap> cellMap) {
        this.cellMap = cellMap;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull Stream<Holder<Biome>> collectPossibleBiomes() {
        return flattenSelector(this.cellMap.value().cells());
    }

    public static Stream<Holder<Biome>> flattenSelector(CellSelectionSet selector) {
        if(selector instanceof WeightedRandomSelectionSet weightedRandomHolderSet) {
            weightedRandomHolderSet.buildList();
        }
        Either<Stream<Holder<Cell>>, Stream<CellSelectionSet>> all = selector.all();
        if(all.left().isPresent()) {
            return all.left().orElseThrow().map(e -> e.value().biome());
        } else {
            return all.right().orElseThrow().flatMap(CellularBiomeSource::flattenSelector);
        }
    }

    public static Stream<Holder<Biome>> flatten(Either<Holder<Cell>, CellSelectionSet> either) {
        if (either.left().isPresent()) {
            return Stream.of(either.left().orElseThrow().value().biome());
        } else {
            return flattenSelector(either.right().orElseThrow());
        }
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        return this.cellMap.value().getCell(x<<2, z<<2).biome(); // Here, x y and z are QuartPos so we need to divide by 4
    }
}
