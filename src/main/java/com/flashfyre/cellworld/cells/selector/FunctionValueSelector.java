package com.flashfyre.cellworld.cells.selector;

import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.CellworldNoiseWiringHelper;
import com.flashfyre.cellworld.cells.SurfacedBiome;
import com.flashfyre.cellworld.cells.CellSelectionTree;
import com.flashfyre.cellworld.cells.CellTreeElement;
import com.flashfyre.cellworld.levelgen.densityfunction.SquareInput;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.List;
import java.util.stream.Stream;

/**
 * Selector that tries to select elements in order if the density function weight is lower than their listed float weight, before choosing the default_element`
 */
public class FunctionValueSelector implements CellSelector {
    /*public static final MapCodec<LevelParameterValueSelector> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                    LevelParameter.CODEC.fieldOf("level_parameter").forGetter(s -> s.parameter),
                    (Codec.mapPair(Codec.FLOAT.fieldOf("parameter_value"), Codec.either(Cell.CODEC, CellSelector.CODEC).fieldOf("cell"))).codec().listOf().fieldOf("list").forGetter(s -> s.entries),
                    Codec.either(Cell.CODEC, CellSelector.CODEC).fieldOf("default_cell").forGetter(s -> s.defaultCell)
            ).apply(inst, LevelParameterValueSelector::new));*/

    public static final MapCodec<FunctionValueSelector> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                    DensityFunction.DIRECT_CODEC.fieldOf("density_function").forGetter(s -> s.function),
                    (Codec.mapPair(Codec.FLOAT.fieldOf("parameter_value"), CellTreeElement.CODEC)).codec().listOf().fieldOf("entries").forGetter(s -> s.entries),
                    CellTreeElement.CODEC.fieldOf("else").forGetter(s -> s.elseEntry)
            ).apply(inst, FunctionValueSelector::new));

    private DensityFunction function;
    private final List<Pair<Float, CellTreeElement>> entries;
    CellTreeElement elseEntry;

    public FunctionValueSelector(DensityFunction function, List<Pair<Float, CellTreeElement>> entries, CellTreeElement elseEntry) {
        this.function = function;
        this.entries = entries;
        this.elseEntry = elseEntry;
    }

    public FunctionValueSelector(DensityFunction function, CellTreeElement entry, float value, CellTreeElement elseEntry) {
        this(function, List.of(new Pair<>(value, entry)), elseEntry);
    }

    public DensityFunction function() {
        return this.function;
    }

    public void wireNoise(CellworldNoiseWiringHelper noiseWirer) {
        this.function = this.function.mapAll(noiseWirer);
    }

    @Override
    public CellTreeElement get(CellSelectionTree.PositionalContext ctx) {
        for(Pair<Float, CellTreeElement> pair : this.entries) {
            float valueToTest = pair.getFirst();
            if(this.function() instanceof SquareInput) valueToTest *= valueToTest;
            if(this.function().compute(new DensityFunction.SinglePointContext(ctx.nucleusBlockX(), ctx.nucleusBlockY(), ctx.nucleusBlockZ())) < valueToTest) return pair.getSecond();
        }
        return this.elseEntry;
    }

    @Override
    public MapCodec<? extends CellSelector> type() {
        return Cellworld.FUNCTION_VALUE.get();
    }

    @Override
    public Stream<Holder<SurfacedBiome>> streamCells() {

        Stream<Holder<SurfacedBiome>> defaultStream = this.elseEntry.stream();
        /*if(this.defaultCell.getCell().isPresent()) {
            defaultStream = Stream.of(this.defaultCell.getCell().orElseThrow());
        } else {
            defaultStream = this.defaultCell.getSelector().orElseThrow().streamCells();
        }*/

        return Stream.concat(defaultStream, this.entries.stream().flatMap(p -> {
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
        return Stream.concat(this.entries.stream().map(Pair::getSecond), Stream.of(this.elseEntry)).toList();
    }
}
