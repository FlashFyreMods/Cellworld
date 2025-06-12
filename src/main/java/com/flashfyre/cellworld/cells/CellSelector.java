package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.Cellworld;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class CellSelector {
    public static final Codec<CellSelector> CODEC = Cellworld.SELECTOR_TYPE_REGISTRY
            .byNameCodec()
            .dispatch("selector_type", CellSelector::type, Function.identity());

    public abstract CellEntry get(RandomSource r);

    public abstract MapCodec<? extends CellSelector> type();

    public abstract Stream<CellEntry> all();

    public static RandomFromList randomFromList(CellEntry... cells) {
        return new RandomFromList(List.of(cells));
    }

    public static RandomFromWeightedList randomFromWeightedList(Holder<WeightedCell>... cells) {
        return new RandomFromWeightedList(List.of(cells));
    }
}
