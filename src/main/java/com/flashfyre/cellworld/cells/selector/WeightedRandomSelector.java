package com.flashfyre.cellworld.cells.selector;

import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.cells.Cell;
import com.flashfyre.cellworld.cells.CellTreeElement;
import com.flashfyre.cellworld.cells.SingleIntConfiguredCell;
import com.flashfyre.cellworld.registry.CellworldRegistries;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;

import java.util.stream.Stream;

public class WeightedRandomSelector implements CellSelector {
    public static final MapCodec<WeightedRandomSelector> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(Codec.either(
                        RegistryCodecs.homogeneousList(CellworldRegistries.SINGLE_INT_CONFIGURED_CELL),
                        SimpleWeightedRandomList.wrappedCodec(CellSelector.CODEC)
                    ).fieldOf("cellSelector").forGetter(e -> e.cells)
    ).apply(inst, WeightedRandomSelector::new));

    private final Either<HolderSet<SingleIntConfiguredCell>, SimpleWeightedRandomList<CellSelector>> cells;
    private SimpleWeightedRandomList<Holder<Cell>> list;

    public WeightedRandomSelector(Either<HolderSet<SingleIntConfiguredCell>, SimpleWeightedRandomList<CellSelector>> cells) {
        this.cells = cells;
    }

    public WeightedRandomSelector(HolderSet<SingleIntConfiguredCell> cells) {
        this(Either.left(cells));
    }

    public WeightedRandomSelector(SimpleWeightedRandomList<CellSelector> cells) {
        this(Either.right(cells));
    }

    @Override
    public CellTreeElement get(LevelParameter.CellContext ctx) {
        return this.cells.left().isPresent() ?
                CellTreeElement.cell(this.list.getRandomValue(ctx.rand()).orElseThrow())
                : CellTreeElement.selector(this.cells.right().orElseThrow().getRandomValue(ctx.rand()).orElseThrow());
    }

    @Override
    public MapCodec<? extends CellSelector> type() {
        return Cellworld.WEIGHTED_RANDOM.get();
    }

    public Stream<Holder<Cell>> streamCells() {
        this.buildList();
        if(this.cells.left().isPresent()) {
            return this.cells.left().orElseThrow().stream().map(e -> e.value().cell());
        } else {
            return this.cells.right().orElseThrow().unwrap().stream().flatMap(w -> w.data().streamCells());
        }
    }

    public void buildList() {
        if(this.cells.left().isPresent()) {
            SimpleWeightedRandomList.Builder<Holder<Cell>> builder = new SimpleWeightedRandomList.Builder<>();
            this.cells.left().orElseThrow().forEach(h -> {
                builder.add(h.value().cell(), h.value().value());
            });
            this.list = builder.build();
        }
    }
}
