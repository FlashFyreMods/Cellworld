package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.registry.CellworldSurfacedBiomes;
import com.flashfyre.cellworld.registry.CellworldRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public record WeightedSurfacedBiome(Holder<SurfacedBiome> cell, int weight) {
    public static final Codec<WeightedSurfacedBiome> DIRECT_CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                            SurfacedBiome.CODEC.fieldOf("cell").forGetter(w -> w.cell),
                            Codec.INT.fieldOf("weight").forGetter(entry -> entry.weight)
                    )
                    .apply(inst, WeightedSurfacedBiome::new)
    );

    public static WeightedSurfacedBiome of(HolderGetter<SurfacedBiome> surfacedBiomeGetter, ResourceKey<SurfacedBiome> surfacedBiomeKey, int weight) {
        return new WeightedSurfacedBiome(surfacedBiomeGetter.getOrThrow(surfacedBiomeKey), weight);
    }

    public static final Codec<Holder<WeightedSurfacedBiome>> CODEC = RegistryFileCodec.create(CellworldRegistries.WEIGHTED_SURFACED_BIOME_REG_KEY, DIRECT_CODEC);

    public static final TagKey<WeightedSurfacedBiome> NETHER = create("nether");
    public static final TagKey<WeightedSurfacedBiome> OUTER_END = create("outer_end");

    private static TagKey<WeightedSurfacedBiome> create(String name) {
        return TagKey.create(CellworldRegistries.WEIGHTED_SURFACED_BIOME_REG_KEY, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }

    public static final ResourceKey<WeightedSurfacedBiome> NETHER_WASTES = createKey("nether_wastes");
    public static final ResourceKey<WeightedSurfacedBiome> SOUL_SAND_VALLEY = createKey("soul_sand_valley");
    public static final ResourceKey<WeightedSurfacedBiome> WARPED_FOREST = createKey("warped_forest");
    public static final ResourceKey<WeightedSurfacedBiome> CRIMSON_FOREST = createKey("crimson_forest");
    public static final ResourceKey<WeightedSurfacedBiome> BASALT_DELTAS = createKey("basalt_deltas");
    public static final ResourceKey<WeightedSurfacedBiome> GILDED_DEPTHS = createKey("gilded_depths");

    public static final ResourceKey<WeightedSurfacedBiome> END_HIGHLANDS = createKey("end_highlands");
    public static final ResourceKey<WeightedSurfacedBiome> OBSIDIAN_SPIRES = createKey("obsidian_spires");
    public static final ResourceKey<WeightedSurfacedBiome> AMETHYST_FIELDS = createKey("amethyst_fields");

    public static void bootstrap(BootstrapContext<WeightedSurfacedBiome> ctx) {
        HolderGetter<SurfacedBiome> cells = ctx.lookup(CellworldRegistries.SURFACED_BIOME_REG_KEY);
        ctx.register(NETHER_WASTES, WeightedSurfacedBiome.of(cells, CellworldSurfacedBiomes.NETHER_WASTES, 100));
        ctx.register(SOUL_SAND_VALLEY, WeightedSurfacedBiome.of(cells, CellworldSurfacedBiomes.SOUL_SAND_VALLEY, 80));
        ctx.register(WARPED_FOREST, WeightedSurfacedBiome.of(cells, CellworldSurfacedBiomes.WARPED_FOREST, 75));
        ctx.register(CRIMSON_FOREST, WeightedSurfacedBiome.of(cells, CellworldSurfacedBiomes.CRIMSON_FOREST, 80));
        ctx.register(BASALT_DELTAS, WeightedSurfacedBiome.of(cells, CellworldSurfacedBiomes.BASALT_DELTAS, 80));
        ctx.register(GILDED_DEPTHS, WeightedSurfacedBiome.of(cells, CellworldSurfacedBiomes.GILDED_DEPTHS, 75));

        ctx.register(END_HIGHLANDS, WeightedSurfacedBiome.of(cells, CellworldSurfacedBiomes.END_HIGHLANDS, 100));
        ctx.register(OBSIDIAN_SPIRES, WeightedSurfacedBiome.of(cells, CellworldSurfacedBiomes.OBSIDIAN_SPIRES, 70));
        ctx.register(AMETHYST_FIELDS, WeightedSurfacedBiome.of(cells, CellworldSurfacedBiomes.AMETHYST_FIELDS, 70));
    }

    private static ResourceKey<WeightedSurfacedBiome> createKey(String name) {
        return ResourceKey.create(CellworldRegistries.WEIGHTED_SURFACED_BIOME_REG_KEY, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }
}
