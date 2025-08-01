package com.flashfyre.cellworld.cells.selector;

import com.flashfyre.cellworld.cells.SurfacedBiome;
import com.flashfyre.cellworld.cells.CellSelectionTree;
import com.flashfyre.cellworld.cells.CellTreeElement;
import com.flashfyre.cellworld.registry.CellworldRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public interface CellSelector {
    Codec<CellSelector> CODEC = CellworldRegistries.SELECTOR_TYPE_REGISTRY
            .byNameCodec()
            .dispatch("type", CellSelector::type, Function.identity());

    CellTreeElement get(CellSelectionTree.PositionalContext ctx);

    MapCodec<? extends CellSelector> type();

    Stream<Holder<SurfacedBiome>> streamCells();

    List<CellTreeElement> elements();
}
