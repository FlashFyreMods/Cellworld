package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.registry.CellworldRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.Optional;

public class SurfacedBiome {
    private final Holder<Biome> biome;
    private final Optional<SurfaceRules.RuleSource> opt;

    public static final Codec<SurfacedBiome> DIRECT_CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                            Biome.CODEC.fieldOf("biome").forGetter(cell -> cell.biome),
                            SurfaceRules.RuleSource.CODEC.optionalFieldOf("surface_rules").forGetter(SurfacedBiome::ruleSource)
                    )
                    .apply(inst, SurfacedBiome::new)
    );

    public static final Codec<Holder<SurfacedBiome>> CODEC = RegistryFileCodec.create(CellworldRegistries.SURFACED_BIOME_REG_KEY, DIRECT_CODEC);

    protected SurfacedBiome(Holder<Biome> biome, Optional<SurfaceRules.RuleSource> ruleSource) {
        this.biome = biome;
        this.opt = ruleSource;
    }

    private SurfacedBiome(Holder<Biome> biome) {
        this(biome, Optional.empty());
    }

    public static SurfacedBiome withSurfaceRules(HolderGetter<Biome> biomeGetter, ResourceKey<Biome> biome, SurfaceRules.RuleSource ruleSource) {
        return new SurfacedBiome(biomeGetter.getOrThrow(biome), Optional.of(ruleSource));
    }

    public static SurfacedBiome simple(HolderGetter<Biome> biomeGetter, ResourceKey<Biome> biome) {
        return new SurfacedBiome(biomeGetter.getOrThrow(biome), Optional.empty());
    }

    public Holder<Biome> biome() { return this.biome; }

    public Optional<SurfaceRules.RuleSource> ruleSource() { return this.opt; }

    public static final TagKey<SurfacedBiome> OUTER_END = create("outer_end");
    public static final TagKey<SurfacedBiome> NETHER = create("nether");

    private static TagKey<SurfacedBiome> create(String name) {
        return TagKey.create(CellworldRegistries.SURFACED_BIOME_REG_KEY, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }
}