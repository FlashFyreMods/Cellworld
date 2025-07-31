package com.flashfyre.cellworld.cells.selector;

import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.cells.Cell;
import com.flashfyre.cellworld.cells.CellTreeElement;
import com.flashfyre.cellworld.cells.ICellTreeElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record TestRandomSelector(List<ICellTreeElement> cells) {//implements CellSelector {

    // RegistryCodecs.homogeneousList(CellworldRegistries.CELL_REGISTRY_KEY)

    /*public static final MapCodec<TestRandomSelector> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(CellTreeElement.CODEC.codec().listOf().fieldOf("elements").forGetter(e -> e.cells)
            ).apply(inst, TestRandomSelector::new));

    @Override
    public ICellTreeElement get(LevelParameter.CellContext ctx) {
        return this.cells.get(ctx.rand().nextInt(this.cells.size()));
    }

    @Override
    public MapCodec<? extends CellSelector> type() {
        return Cellworld.RANDOM.get();
    }

    @Override
    public Stream<CellHolder> streamCells() {
        return cells.stream().flatMap(e -> {
            if(e instanceof Cell cell) {
                return Stream.of(cell);
            } else if (e instanceof CellSelector selector) {
                return selector.streamCells();
            } return e;
        });
    }

    @Override
    public List<CellTreeElement> elements() {
        return this.cells;
    }*/
}
