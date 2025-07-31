package com.flashfyre.cellworld.levelgen.densityfunction;

import com.flashfyre.cellworld.cells.selector.LevelParameter;
import com.flashfyre.cellworld.levelgen.SquareInput;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;

public record DistToXZCoordFunction(int x, int z) implements DensityFunction.SimpleFunction, SquareInput {
    public static final MapCodec<DistToXZCoordFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                    Codec.INT.optionalFieldOf("x", 0).forGetter(DistToXZCoordFunction::x),
                    Codec.INT.optionalFieldOf("z", 0).forGetter(DistToXZCoordFunction::z)
            ).apply(inst, DistToXZCoordFunction::new));

    public static final KeyDispatchDataCodec<DistToXZCoordFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);


    @Override
    public double compute(FunctionContext ctx) {
        return Mth.square(ctx.blockX() - this.x) + Mth.square(ctx.blockZ() - this.z);
    }

    @Override
    public double minValue() {
        return 0;
    }

    @Override
    public double maxValue() {
        return Double.MAX_VALUE;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
