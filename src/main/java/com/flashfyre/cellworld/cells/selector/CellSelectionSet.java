package com.flashfyre.cellworld.cells.selector;

import com.flashfyre.cellworld.cells.Cell;
import com.flashfyre.cellworld.cells.CellEntry;
import com.flashfyre.cellworld.registry.CellworldRegistries;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;

import java.util.function.Function;
import java.util.stream.Stream;

public abstract class CellSelectionSet {
    public static final Codec<CellSelectionSet> CODEC = CellworldRegistries.SELECTOR_TYPE_REGISTRY
            .byNameCodec()
            .dispatch("picker_type", com.flashfyre.cellworld.cells.selector.CellSelectionSet::type, Function.identity());

    public abstract Either<Holder<Cell>, CellSelectionSet> get(RandomSource r);

    public abstract MapCodec<? extends CellSelectionSet> type();

    public abstract Either<Stream<Holder<Cell>>, Stream<CellSelectionSet>> all();
}
