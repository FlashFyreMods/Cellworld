package com.flashfyre.cellworld.cells.selector;

import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.CellworldNoiseWiringHelper;
import com.flashfyre.cellworld.registry.CellworldRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.function.Function;

public interface LevelParameter {
    Codec<LevelParameter> CODEC = CellworldRegistries.LEVEL_PARAMETER_TYPE_REGISTRY
            .byNameCodec()
            .dispatch("type", LevelParameter::type, Function.identity());

    MapCodec<? extends LevelParameter> type();
    float get(CellContext ctx);

    default boolean squareParameter() { return false; }

    record DistFromXZCoord(int x, int z) implements LevelParameter {

        public MapCodec<? extends LevelParameter> type() { return Cellworld.DIST_FROM_XZ_COORD.get(); }

        @Override public float get(CellContext ctx) { return Mth.square(ctx.nucleusBlockX - this.x) + Mth.square(ctx.nucleusBlockZ - this.z); }

        @Override
        public boolean squareParameter() {
            return true;
        }

        public static final MapCodec<DistFromXZCoord> CODEC = RecordCodecBuilder.mapCodec(
                inst -> inst.group(
                        Codec.INT.optionalFieldOf("x", 0).forGetter(DistFromXZCoord::x),
                        Codec.INT.optionalFieldOf("z", 0).forGetter(DistFromXZCoord::z)
                ).apply(inst, DistFromXZCoord::new));
    }

    record Height(int y) implements LevelParameter {
        public MapCodec<? extends LevelParameter> type() { return Cellworld.HEIGHT.get(); }
        @Override public float get(CellContext ctx) { return ctx.nucleusBlockY; }

        public static final MapCodec<Height> CODEC = RecordCodecBuilder.mapCodec(
                inst -> inst.group(
                        Codec.INT.fieldOf("y").forGetter(Height::y)
                ).apply(inst, Height::new));
    }

    record AngleFromXZCoord(int x, int z) implements LevelParameter {
        public MapCodec<? extends LevelParameter> type() { return Cellworld.ANGLE_FROM_XZ_COORD.get(); }

        @Override public float get(CellContext ctx) {
            return (((float) (Math.atan2(this.x - ctx.nucleusBlockX, this.z - ctx.nucleusBlockZ)) + Mth.PI) / Mth.TWO_PI) * 360f;
        }

        public static final MapCodec<AngleFromXZCoord> CODEC = RecordCodecBuilder.mapCodec(
                inst -> inst.group(
                        Codec.INT.optionalFieldOf("x", 0).forGetter(AngleFromXZCoord::x),
                        Codec.INT.optionalFieldOf("z", 0).forGetter(AngleFromXZCoord::z)
                ).apply(inst, AngleFromXZCoord::new));
    }

    record CellContext(RandomSource rand, int nucleusBlockX, int nucleusBlockY, int nucleusBlockZ) { }

    public class DensityFunctionInput implements LevelParameter {
        public MapCodec<? extends LevelParameter> type() { return Cellworld.DENSITY_FUNCTION_INPUT.get(); }

        private DensityFunction densityFunction;

        public DensityFunctionInput(DensityFunction function) {
            this.densityFunction = function;
        }

        @Override public float get(CellContext ctx) {
            /*int x = ctx.nucleusBlockX << 3;
            int z = ctx.nucleusBlockZ << 3;*/
            //int x = (SectionPos.blockToSectionCoord(ctx.nucleusBlockX) * 2 + 1) * 8;
            //int z = (SectionPos.blockToSectionCoord(ctx.nucleusBlockZ) * 2 + 1) * 8;
            return (float) this.densityFunction.compute(new DensityFunction.SinglePointContext((int) (ctx.nucleusBlockX), 0, (int) (ctx.nucleusBlockZ)));
        }

        public static final MapCodec<DensityFunctionInput> CODEC = RecordCodecBuilder.mapCodec(
                inst -> inst.group(
                        DensityFunction.DIRECT_CODEC.fieldOf("density_function").forGetter(d -> d.densityFunction)
                ).apply(inst, DensityFunctionInput::new));

        public void wireNoise(CellworldNoiseWiringHelper noiseWirer) {
            this.densityFunction = this.densityFunction.mapAll(noiseWirer);
        }
    }
}
