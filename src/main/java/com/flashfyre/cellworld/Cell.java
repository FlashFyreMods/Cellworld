package com.flashfyre.cellworld;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.feature.configurations.PointedDripstoneConfiguration;

public record Cell(Holder<Biome> biome, float offset, float factor) {

    public static final Codec<Cell> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                            Biome.CODEC.fieldOf("biome").forGetter(cell -> cell.biome),
                            Codec.FLOAT.fieldOf("offset").orElse(0.0F).forGetter(cell -> cell.offset),
                            Codec.FLOAT.fieldOf("factor").orElse(0.0F).forGetter(cell -> cell.factor)
                    )
                    .apply(inst, Cell::new)
    );
}