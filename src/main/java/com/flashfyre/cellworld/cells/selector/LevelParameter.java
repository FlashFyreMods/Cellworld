package com.flashfyre.cellworld.cells.selector;

import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.registry.CellworldRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;

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

        @Override public float get(CellContext ctx) { return ctx.nucleusBlockX*ctx.nucleusBlockX + ctx.nucleusBlockZ*ctx.nucleusBlockZ; }

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

    record CellContext(RandomSource rand, int nucleusBlockX, int nucleusBlockY, int nucleusBlockZ) { }
}
