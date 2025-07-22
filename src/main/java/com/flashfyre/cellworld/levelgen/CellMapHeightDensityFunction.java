package com.flashfyre.cellworld.levelgen;

import com.flashfyre.cellworld.cells.Cell;
import com.flashfyre.cellworld.cells.CellSelectionTree;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public record CellMapHeightDensityFunction(Holder<CellSelectionTree> cellMap) implements DensityFunction {

    public static final MapCodec<CellMapHeightDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(
            p_208597_ -> p_208597_.group(
                            CellSelectionTree.CODEC.fieldOf("input").forGetter(CellMapHeightDensityFunction::cellMap))
                    .apply(p_208597_, CellMapHeightDensityFunction::new)
    );
    public static final KeyDispatchDataCodec<CellMapHeightDensityFunction> CODEC = makeCodec(DATA_CODEC);

    static <O> KeyDispatchDataCodec<O> makeCodec(MapCodec<O> mapCodec) {
        return KeyDispatchDataCodec.of(mapCodec);
    }


    @Override
    public double compute(FunctionContext ctx) {
        Cell cell = this.cellMap.value().getCell(ctx.blockX(), ctx.blockZ());
        return 0;
    }

    @Override
    public void fillArray(double[] array, ContextProvider contextProvider) {
        contextProvider.fillAllDirectly(array, this);
    }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        return visitor.apply(new CellMapHeightDensityFunction(this.cellMap()));
    }

    @Override
    public double minValue() {
        return 0;
    }

    @Override
    public double maxValue() {
        return 0;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
