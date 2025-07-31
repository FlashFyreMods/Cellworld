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

public class Cell implements ICellTreeElement {
    private final Holder<Biome> biome;
    private final Optional<SurfaceRules.RuleSource> opt;

    public static final Codec<Cell> DIRECT_CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                            Biome.CODEC.fieldOf("biome").forGetter(cell -> cell.biome),
                            SurfaceRules.RuleSource.CODEC.optionalFieldOf("surface_rules").forGetter(Cell::ruleSource)
                    )
                    .apply(inst, Cell::new)
    );

    public static final Codec<Holder<Cell>> CODEC = RegistryFileCodec.create(CellworldRegistries.CELL_REGISTRY_KEY, DIRECT_CODEC);

    protected Cell(Holder<Biome> biome, Optional<SurfaceRules.RuleSource> ruleSource) {
        this.biome = biome;
        this.opt = ruleSource;
    }

    private Cell(Holder<Biome> biome) {
        this(biome, Optional.empty());
    }

    public static Cell withSurfaceRules(HolderGetter<Biome> biomeGetter, ResourceKey<Biome> biome, SurfaceRules.RuleSource ruleSource) {
        return new Cell(biomeGetter.getOrThrow(biome), Optional.of(ruleSource));
    }

    public static Cell simple(HolderGetter<Biome> biomeGetter, ResourceKey<Biome> biome) {
        return new Cell(biomeGetter.getOrThrow(biome), Optional.empty());
    }

    public Holder<Biome> biome() { return this.biome; }

    public Optional<SurfaceRules.RuleSource> ruleSource() { return this.opt; }

    public static final TagKey<Cell> OUTER_END = create("outer_end");

    private static TagKey<Cell> create(String name) {
        return TagKey.create(CellworldRegistries.CELL_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }
}