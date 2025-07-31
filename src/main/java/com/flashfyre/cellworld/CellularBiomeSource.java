package com.flashfyre.cellworld;

import com.flashfyre.cellworld.cells.CellSelectionTree;
import com.flashfyre.cellworld.cells.CellSelectionTreeOld;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.*;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class CellularBiomeSource extends BiomeSource {
    public static final MapCodec<CellularBiomeSource> CODEC = CellSelectionTree.CODEC
            .fieldOf("cell_selection_tree")
            .xmap(CellularBiomeSource::new, s -> s.cellSelectionTree);


    private final Holder<CellSelectionTree> cellSelectionTree;

    public CellularBiomeSource(Holder<CellSelectionTree> cellSelectionTree) {
        this.cellSelectionTree = cellSelectionTree;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull Stream<Holder<Biome>> collectPossibleBiomes() {
        return this.cellSelectionTree.value().streamCells().map(e -> e.value().biome());
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        return this.cellSelectionTree.value().getCell(QuartPos.toBlock(x), QuartPos.toBlock(z)).biome(); // Here, x y and z are QuartPos so we need to divide by 4
    }
}
