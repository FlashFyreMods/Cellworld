package com.flashfyre.cellworld.cells.selector;

import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.cells.Cell;
import com.flashfyre.cellworld.cells.CellTreeElement;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;

import java.util.List;
import java.util.stream.Stream;

public record RandomSelector(List<CellTreeElement> cells) implements CellSelector {
    public static final MapCodec<RandomSelector> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(CellTreeElement.CODEC.codec().listOf().fieldOf("elements").forGetter(e -> e.cells)
            ).apply(inst, RandomSelector::new));

    @Override
    public CellTreeElement get(LevelParameter.CellContext ctx) {
        return this.cells.get(ctx.rand().nextInt(this.cells.size()));
    }

    @Override
    public MapCodec<? extends CellSelector> type() {
        return Cellworld.RANDOM.get();
    }

    @Override
    public Stream<Holder<Cell>> streamCells() {
        return cells.stream().flatMap(e -> {
            if(e.left().isPresent()) {
                return Stream.of(e.left().orElseThrow());
            } else {
                return e.right().orElseThrow().streamCells();
            }
        });
    }
}
