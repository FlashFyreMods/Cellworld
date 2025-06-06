package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.Cellworld;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Random;

public class RandomFromList extends CellSelector {
    public static final MapCodec<RandomFromList> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                            Codec.list(CellEntry.CODEC).fieldOf("cells").forGetter(e -> e.cells)
                    )
                    .apply(inst, RandomFromList::new)
    );
    private final List<CellEntry> cells;

    public RandomFromList(List<CellEntry> cells) {
        this.cells = cells;
    }

    @Override
    public CellEntry get(Random r) {
        return null;
    }

    @Override
    public SelectorType<?> getType() {
        return Cellworld.RANDOM_FROM_LIST.get();
    }


}
