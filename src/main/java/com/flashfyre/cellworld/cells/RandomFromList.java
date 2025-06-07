package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.Cellworld;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class RandomFromList extends CellSelector {
    public static final MapCodec<RandomFromList> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                            Codec.list(CellEntry.CODEC).fieldOf("cells").forGetter(e -> e.cells)
                    )
                    .apply(inst, RandomFromList::new)
    );
    private final List<CellEntry> cells;

    protected RandomFromList(List<CellEntry> cells) {
        this.cells = cells;
    }

    @Override
    public CellEntry get(RandomSource r) { return this.cells.get(r.nextInt(this.cells.size())); }

    @Override
    public MapCodec<? extends CellSelector> type() {
        return Cellworld.RANDOM_FROM_LIST.get();
    }

    @Override
    public Stream<CellEntry> all() {
        return this.cells.stream();
    }
}
