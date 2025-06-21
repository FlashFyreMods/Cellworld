package com.flashfyre.cellworld.cells.selector;

import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.cells.Cell;
import com.flashfyre.cellworld.cells.CellEntry;
import com.flashfyre.cellworld.cells.WeightedCell;
import com.flashfyre.cellworld.registry.CellworldRegistries;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;

import java.util.stream.Stream;

public class WeightedRandomSelectionSet extends CellSelectionSet {
    public static final MapCodec<WeightedRandomSelectionSet> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(Codec.either(
                        RegistryCodecs.homogeneousList(CellworldRegistries.WEIGHTED_CELL_ENTRY_REGISTRY_KEY),
                        SimpleWeightedRandomList.wrappedCodec(CellSelectionSet.CODEC)
                    ).fieldOf("cells").forGetter(e -> e.cells)
    ).apply(inst, WeightedRandomSelectionSet::new));

    private final Either<HolderSet<WeightedCell>, SimpleWeightedRandomList<CellSelectionSet>> cells;
    private SimpleWeightedRandomList<Holder<Cell>> list;

    public WeightedRandomSelectionSet(Either<HolderSet<WeightedCell>, SimpleWeightedRandomList<CellSelectionSet>> cells) {
        this.cells = cells;
    }

    public WeightedRandomSelectionSet(HolderSet<WeightedCell> cells) {
        this(Either.left(cells));
    }

    public WeightedRandomSelectionSet(SimpleWeightedRandomList<CellSelectionSet> cells) {
        this(Either.right(cells));
    }

    @Override
    public Either<Holder<Cell>, CellSelectionSet> get(RandomSource r) {
        return this.cells.left().isPresent() ?
                Either.left(this.list.getRandomValue(r).orElseThrow())
                : Either.right(this.cells.right().orElseThrow().getRandomValue(r).orElseThrow());
    }

    @Override
    public MapCodec<? extends CellSelectionSet> type() {
        return Cellworld.WEIGHTED_RANDOM.get();
    }

    @Override
    public Either<Stream<Holder<Cell>>, Stream<CellSelectionSet>> all() {
        if(this.cells.left().isPresent()) {
            return Either.left(this.cells.left().orElseThrow().stream().map(e -> e.value().cell()));
        } else {
            return Either.right(this.cells.right().orElseThrow().unwrap().stream().map(WeightedEntry.Wrapper::data));
        }
    }

    public void buildList() {
        if(this.cells.left().isPresent()) {
            SimpleWeightedRandomList.Builder<Holder<Cell>> builder = new SimpleWeightedRandomList.Builder<>();
            this.cells.left().orElseThrow().forEach(h -> {
                builder.add(h.value().cell(), h.value().weight());
            });
            this.list = builder.build();
        }
    }
}
