package com.flashfyre.cellworld.registry;

import com.flashfyre.cellworld.cells.SurfacedBiome;
import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.levelgen.CellworldSurfaceRules;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class CellworldSurfacedBiomes {
    public static final ResourceKey<SurfacedBiome> NETHER_WASTES = createKey("nether_wastes");
    public static final ResourceKey<SurfacedBiome> SOUL_SAND_VALLEY = createKey("soul_sand_valley");
    public static final ResourceKey<SurfacedBiome> WARPED_FOREST = createKey("warped_forest");
    public static final ResourceKey<SurfacedBiome> CRIMSON_FOREST = createKey("crimson_forest");
    public static final ResourceKey<SurfacedBiome> BASALT_DELTAS = createKey("basalt_deltas");
    public static final ResourceKey<SurfacedBiome> GILDED_DEPTHS = createKey("gilded_depths");

    public static final ResourceKey<SurfacedBiome> THE_END = createKey("the_end");
    public static final ResourceKey<SurfacedBiome> SMALL_END_ISLANDS = createKey("small_end_islands");
    public static final ResourceKey<SurfacedBiome> END_BARRENS = createKey("end_barrens");
    public static final ResourceKey<SurfacedBiome> END_MIDLANDS = createKey("end_midlands");
    public static final ResourceKey<SurfacedBiome> END_HIGHLANDS = createKey("end_highlands");
    public static final ResourceKey<SurfacedBiome> OBSIDIAN_SPIRES = createKey("obsidian_spires");
    public static final ResourceKey<SurfacedBiome> AMETHYST_FIELDS = createKey("amethyst_fields");

    public static void bootstrap(BootstrapContext<SurfacedBiome> ctx) {
        HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
        ctx.register(NETHER_WASTES, SurfacedBiome.withSurfaceRules(biomes, Biomes.NETHER_WASTES, CellworldSurfaceRules.netherWastes()));
        ctx.register(SOUL_SAND_VALLEY, SurfacedBiome.withSurfaceRules(biomes, Biomes.SOUL_SAND_VALLEY, CellworldSurfaceRules.soulSandValley()));
        ctx.register(WARPED_FOREST, SurfacedBiome.withSurfaceRules(biomes, Biomes.WARPED_FOREST, CellworldSurfaceRules.warpedForest()));
        ctx.register(CRIMSON_FOREST, SurfacedBiome.withSurfaceRules(biomes, Biomes.CRIMSON_FOREST, CellworldSurfaceRules.crimsonForest()));
        ctx.register(BASALT_DELTAS, SurfacedBiome.withSurfaceRules(biomes, Biomes.BASALT_DELTAS, CellworldSurfaceRules.basaltDeltas()));
        ctx.register(GILDED_DEPTHS, SurfacedBiome.withSurfaceRules(biomes, CellworldBiomes.GILDED_DEPTHS, CellworldSurfaceRules.gildedDepths()));

        ctx.register(THE_END, SurfacedBiome.withSurfaceRules(biomes, Biomes.THE_END, CellworldSurfaceRules.END_STONE));
        ctx.register(SMALL_END_ISLANDS, SurfacedBiome.withSurfaceRules(biomes, Biomes.SMALL_END_ISLANDS, CellworldSurfaceRules.END_STONE));
        ctx.register(END_BARRENS, SurfacedBiome.withSurfaceRules(biomes, Biomes.END_BARRENS, CellworldSurfaceRules.END_STONE));
        ctx.register(END_MIDLANDS, SurfacedBiome.withSurfaceRules(biomes, Biomes.END_MIDLANDS, CellworldSurfaceRules.END_STONE));
        ctx.register(END_HIGHLANDS, SurfacedBiome.withSurfaceRules(biomes, Biomes.END_HIGHLANDS, CellworldSurfaceRules.END_STONE));
        ctx.register(OBSIDIAN_SPIRES, SurfacedBiome.withSurfaceRules(biomes, CellworldBiomes.OBSIDIAN_SPIRES, CellworldSurfaceRules.obsidianSpires()));
        ctx.register(AMETHYST_FIELDS, SurfacedBiome.withSurfaceRules(biomes, CellworldBiomes.AMETHYST_FIELDS, CellworldSurfaceRules.amethystFields()));
    }

    private static ResourceKey<SurfacedBiome> createKey(String name) {
        return ResourceKey.create(CellworldRegistries.SURFACED_BIOME_REG_KEY, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }
}
