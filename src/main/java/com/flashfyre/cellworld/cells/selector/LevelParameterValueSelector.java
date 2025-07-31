package com.flashfyre.cellworld.cells.selector;

import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.cells.Cell;
import com.flashfyre.cellworld.cells.CellTreeElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;

import java.util.List;
import java.util.stream.Stream;

public class LevelParameterValueSelector implements CellSelector {
    /*public static final MapCodec<LevelParameterValueSelector> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                    LevelParameter.CODEC.fieldOf("level_parameter").forGetter(s -> s.parameter),
                    (Codec.mapPair(Codec.FLOAT.fieldOf("parameter_value"), Codec.either(Cell.CODEC, CellSelector.CODEC).fieldOf("cell"))).codec().listOf().fieldOf("list").forGetter(s -> s.cells),
                    Codec.either(Cell.CODEC, CellSelector.CODEC).fieldOf("default_cell").forGetter(s -> s.defaultCell)
            ).apply(inst, LevelParameterValueSelector::new));*/

    public static final MapCodec<LevelParameterValueSelector> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                    LevelParameter.CODEC.fieldOf("level_parameter").forGetter(s -> s.parameter),
                    (Codec.mapPair(Codec.FLOAT.fieldOf("parameter_value"), CellTreeElement.CODEC)).codec().listOf().fieldOf("elements").forGetter(s -> s.cells),
                    CellTreeElement.CODEC.fieldOf("default_element").forGetter(s -> s.defaultCell)
            ).apply(inst, LevelParameterValueSelector::new));

    private final LevelParameter parameter;
    private final List<Pair<Float, CellTreeElement>> cells;
    CellTreeElement defaultCell;

    public LevelParameterValueSelector(LevelParameter parameter, List<Pair<Float, CellTreeElement>> cells, CellTreeElement defaultCell) {
        this.parameter = parameter;
        this.cells = cells;
        this.defaultCell = defaultCell;
    }

    public LevelParameterValueSelector(LevelParameter parameter, Pair<Float, CellTreeElement> cell, CellTreeElement defaultCell) {
        this(parameter, List.of(cell), defaultCell);
    }

    public LevelParameter getParameter() {
        return this.parameter;
    }

    @Override
    public CellTreeElement get(LevelParameter.CellContext ctx) {
        for(Pair<Float, CellTreeElement> pair : this.cells) {
            float valueToTest = pair.getFirst();
            if(this.parameter.squareParameter()) valueToTest *= valueToTest;
            if(this.parameter.get(ctx) < valueToTest) return pair.getSecond();
        }
        return this.defaultCell;
    }

    @Override
    public MapCodec<? extends CellSelector> type() {
        return Cellworld.LEVEL_PARAMETER_VALUE.get();
    }

    @Override
    public Stream<Holder<Cell>> streamCells() {

        Stream<Holder<Cell>> defaultStream = this.defaultCell.stream();
        /*if(this.defaultCell.getCell().isPresent()) {
            defaultStream = Stream.of(this.defaultCell.getCell().orElseThrow());
        } else {
            defaultStream = this.defaultCell.getSelector().orElseThrow().streamCells();
        }*/

        return Stream.concat(defaultStream, this.cells.stream().flatMap(p -> {
            CellTreeElement element = p.getSecond();
            if(element.getCell().isPresent()) {
                return Stream.of(element.getCell().orElseThrow());
            } else {
                return element.getSelector().orElseThrow().streamCells();
            }
        }));
    }

    @Override
    public List<CellTreeElement> elements() {
        return Stream.concat(this.cells.stream().map(Pair::getSecond), Stream.of(this.defaultCell)).toList();
    }
}
