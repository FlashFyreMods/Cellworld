package com.flashfyre.cellworld.registry;

import com.flashfyre.cellworld.cells.Cell;
import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.levelgen.CellworldSurfaceRules;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.apache.http.cookie.SM;

public class CellworldCells {
    public static final ResourceKey<Cell> NETHER_WASTES = createKey("nether_wastes");
    public static final ResourceKey<Cell> SOUL_SAND_VALLEY = createKey("soul_sand_valley");
    public static final ResourceKey<Cell> WARPED_FOREST = createKey("warped_forest");
    public static final ResourceKey<Cell> CRIMSON_FOREST = createKey("crimson_forest");
    public static final ResourceKey<Cell> BASALT_DELTAS = createKey("basalt_deltas");
    public static final ResourceKey<Cell> GILDED_DEPTHS = createKey("gilded_depths");

    public static final ResourceKey<Cell> THE_END = createKey("the_end");
    public static final ResourceKey<Cell> SMALL_END_ISLANDS = createKey("small_end_islands");
    public static final ResourceKey<Cell> END_HIGHLANDS = createKey("end_highlands");
    public static final ResourceKey<Cell> OBSIDIAN_SPIRES = createKey("obsidian_spires");
    public static final ResourceKey<Cell> AMETHYST_FIELDS = createKey("amethyst_fields");

    public static void bootstrap(BootstrapContext<Cell> ctx) {
        HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
        ctx.register(NETHER_WASTES, Cell.withSurfaceRules(biomes, Biomes.NETHER_WASTES, CellworldSurfaceRules.netherWastes()));
        ctx.register(SOUL_SAND_VALLEY, Cell.withSurfaceRules(biomes, Biomes.SOUL_SAND_VALLEY, CellworldSurfaceRules.soulSandValley()));
        ctx.register(WARPED_FOREST, Cell.withSurfaceRules(biomes, Biomes.WARPED_FOREST, CellworldSurfaceRules.warpedForest()));
        ctx.register(CRIMSON_FOREST, Cell.withSurfaceRules(biomes, Biomes.CRIMSON_FOREST, CellworldSurfaceRules.crimsonForest()));
        ctx.register(BASALT_DELTAS, Cell.withSurfaceRules(biomes, Biomes.BASALT_DELTAS, CellworldSurfaceRules.basaltDeltas()));
        ctx.register(GILDED_DEPTHS, Cell.withSurfaceRules(biomes, CellworldBiomes.GILDED_DEPTHS, CellworldSurfaceRules.gildedDepths()));

        ctx.register(THE_END, Cell.withSurfaceRules(biomes, Biomes.THE_END, CellworldSurfaceRules.END_STONE));
        ctx.register(SMALL_END_ISLANDS, Cell.withSurfaceRules(biomes, Biomes.SMALL_END_ISLANDS, CellworldSurfaceRules.END_STONE));
        ctx.register(END_HIGHLANDS, Cell.withSurfaceRules(biomes, Biomes.END_HIGHLANDS, CellworldSurfaceRules.END_STONE));
        ctx.register(OBSIDIAN_SPIRES, Cell.withSurfaceRules(biomes, CellworldBiomes.OBSIDIAN_SPIRES, CellworldSurfaceRules.obsidianSpires()));
        ctx.register(AMETHYST_FIELDS, Cell.withSurfaceRules(biomes, CellworldBiomes.AMETHYST_FIELDS, CellworldSurfaceRules.amethystFields()));
    }

    private static ResourceKey<Cell> createKey(String name) {
        return ResourceKey.create(CellworldRegistries.CELL_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }
}
