package com.flashfyre.cellworld;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.feature.configurations.PointedDripstoneConfiguration;

import java.util.Optional;

public class Cell {

    private final Holder<Biome> biome;
    private final Optional<SurfaceRules.RuleSource> surfaceRules;

    public static final Codec<Cell> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                            Biome.CODEC.fieldOf("biome").forGetter(cell -> cell.biome),
                            SurfaceRules.RuleSource.CODEC.optionalFieldOf("surface_rules").forGetter(cell -> cell.surfaceRules)
                    )
                    .apply(inst, Cell::new)
    );

    private Cell(Holder<Biome> biome, Optional<SurfaceRules.RuleSource> surfaceRules) {
        this.biome = biome;
        this.surfaceRules = surfaceRules;
    }

    private Cell(Holder<Biome> biome) {
        this(biome, Optional.empty());
    }

    public static Cell of(HolderGetter<Biome> biomeGetter, ResourceKey<Biome> biome) {
        return new Cell(biomeGetter.getOrThrow(biome), Optional.empty());
    }

    public Holder<Biome> biome() { return this.biome; }
}