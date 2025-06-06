package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.Cellworld;
import com.mojang.serialization.Codec;

import java.util.Random;

public abstract class CellSelector {
    public static final Codec<CellSelector> CODEC = Cellworld.SELECTOR_TYPE_REGISTRY
            .byNameCodec()
            .dispatch("selector_type", CellSelector::getType, SelectorType::codec);

    public abstract CellEntry get(Random r);

    public abstract SelectorType<?> getType();
}
