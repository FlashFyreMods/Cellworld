package com.flashfyre.cellworld.cells.selector;

import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.CellworldNoiseWiringHelper;
import com.flashfyre.cellworld.cells.Cell;
import com.flashfyre.cellworld.cells.CellTreeElement;
import com.flashfyre.cellworld.levelgen.SquareInput;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.DensityFunction;

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
                    DensityFunction.DIRECT_CODEC.fieldOf("density_function").forGetter(s -> s.function),
                    (Codec.mapPair(Codec.FLOAT.fieldOf("parameter_value"), CellTreeElement.CODEC)).codec().listOf().fieldOf("elements").forGetter(s -> s.cells),
                    CellTreeElement.CODEC.fieldOf("default_element").forGetter(s -> s.defaultCell)
            ).apply(inst, LevelParameterValueSelector::new));

    private DensityFunction function;
    private final List<Pair<Float, CellTreeElement>> cells;
    CellTreeElement defaultCell;

    public LevelParameterValueSelector(DensityFunction function, List<Pair<Float, CellTreeElement>> cells, CellTreeElement defaultCell) {
        this.function = function;
        this.cells = cells;
        this.defaultCell = defaultCell;
    }

    public LevelParameterValueSelector(DensityFunction function, CellTreeElement cell, float value, CellTreeElement defaultCell) {
        this(function, List.of(new Pair<>(value, cell)), defaultCell);
    }

    public DensityFunction function() {
        return this.function;
    }

    public void wireNoise(CellworldNoiseWiringHelper noiseWirer) {
        this.function = this.function.mapAll(noiseWirer);
    }

    @Override
    public CellTreeElement get(LevelParameter.CellContext ctx) {
        for(Pair<Float, CellTreeElement> pair : this.cells) {
            float valueToTest = pair.getFirst();
            if(this.function() instanceof SquareInput) valueToTest *= valueToTest;
            if(this.function().compute(new DensityFunction.SinglePointContext(ctx.nucleusBlockX(), ctx.nucleusBlockY(), ctx.nucleusBlockZ())) < valueToTest) return pair.getSecond();
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
