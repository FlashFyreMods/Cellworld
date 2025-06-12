package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.Cellworld;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;

import java.util.stream.Stream;

public class RandomFromWeightedListHolderSet extends CellSelector {
    public static final MapCodec<RandomFromWeightedListHolderSet> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                            RegistryCodecs.homogeneousList(Cellworld.WEIGHTED_CELL_ENTRY_REGISTRY_KEY).fieldOf("cells").forGetter(e -> e.cells)
                    )
                    .apply(inst, RandomFromWeightedListHolderSet::new)
    );

    private final HolderSet<WeightedCell> cells;
    private SimpleWeightedRandomList<CellEntry> list;

    public RandomFromWeightedListHolderSet(HolderSet<WeightedCell> cells) {
        this.cells = cells;
    }

    @Override
    public CellEntry get(RandomSource r) {
        return this.list.getRandomValue(r).orElseThrow();
    }

    @Override
    public MapCodec<? extends CellSelector> type() {
        return Cellworld.RANDOM_FROM_WEIGHTED_LIST_HOLDERSET.get();
    }

    @Override
    public Stream<CellEntry> all() {
        return this.cells.stream().map(e -> e.value().cell());
    }

    public void buildList() {
        SimpleWeightedRandomList.Builder<CellEntry> builder = new SimpleWeightedRandomList.Builder<>();
        this.cells.forEach(h -> {
            builder.add(h.value().cell(), h.value().weight());
        });
        this.list = builder.build();
    }
}
