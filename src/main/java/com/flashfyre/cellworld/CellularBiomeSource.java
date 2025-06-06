package com.flashfyre.cellworld;

import com.flashfyre.cellworld.cells.CellEntry;
import com.flashfyre.cellworld.cells.CellMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.*;

import java.util.List;
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
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return flatten(this.cellMap.cells().value());
    }

    public static Stream<Holder<Biome>> flatten(Either<Cell, List<CellEntry>> either) {
        if (either.left().isPresent()) {
            return Stream.of(either.left().orElseThrow().biome());
        } else {
            return either.right().orElseThrow().stream().flatMap(entry -> flatten(entry.value()));
        }
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        return this.cellMap.getCell(x, z).biome();
    }
}
