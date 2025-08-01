package com.flashfyre.cellworld.levelgen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;

public record AngleFromXZCoordFunction(int x, int z) implements DensityFunction.SimpleFunction {
    public static final MapCodec<AngleFromXZCoordFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                    Codec.INT.optionalFieldOf("x", 0).forGetter(AngleFromXZCoordFunction::x),
                    Codec.INT.optionalFieldOf("z", 0).forGetter(AngleFromXZCoordFunction::z)
            ).apply(inst, AngleFromXZCoordFunction::new));

    public static final KeyDispatchDataCodec<AngleFromXZCoordFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);

    @Override
    public double compute(FunctionContext ctx) {
        return (((float) (Math.atan2(this.x - ctx.blockX(), this.z - ctx.blockZ())) + Mth.PI) / Mth.TWO_PI);
    }

    @Override
    public double minValue() {
        return 0;
    }

    @Override
    public double maxValue() {
        return 1;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
