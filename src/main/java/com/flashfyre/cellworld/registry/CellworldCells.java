package com.flashfyre.cellworld.registry;

import com.flashfyre.cellworld.cells.Cell;
import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.levelgen.CellworldSurfaceRules;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class CellworldCells {
    public static final ResourceKey<Cell> NETHER_WASTES = createKey("nether_wastes");
    public static final ResourceKey<Cell> SOUL_SAND_VALLEY = createKey("soul_sand_valley");
    public static final ResourceKey<Cell> WARPED_FOREST = createKey("warped_forest");
    public static final ResourceKey<Cell> CRIMSON_FOREST = createKey("crimson_forest");
    public static final ResourceKey<Cell> BASALT_DELTAS = createKey("basalt_deltas");
    public static final ResourceKey<Cell> GILDED_DEPTHS = createKey("gilded_depths");

    public static void bootstrap(BootstrapContext<Cell> ctx) {
        HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
        ctx.register(NETHER_WASTES, Cell.of(biomes, Biomes.NETHER_WASTES, CellworldSurfaceRules.netherWastes()));
        ctx.register(SOUL_SAND_VALLEY, Cell.of(biomes, Biomes.SOUL_SAND_VALLEY, CellworldSurfaceRules.soulSandValley()));
        ctx.register(WARPED_FOREST, Cell.of(biomes, Biomes.WARPED_FOREST, CellworldSurfaceRules.warpedForest()));
        ctx.register(CRIMSON_FOREST, Cell.of(biomes, Biomes.CRIMSON_FOREST, CellworldSurfaceRules.crimsonForest()));
        ctx.register(BASALT_DELTAS, Cell.of(biomes, Biomes.BASALT_DELTAS, CellworldSurfaceRules.basaltDeltas()));
        ctx.register(GILDED_DEPTHS, Cell.of(biomes, CellworldBiomes.GILDED_DEPTHS, CellworldSurfaceRules.gildedDepths()));
    }

    private static ResourceKey<Cell> createKey(String name) {
        return ResourceKey.create(CellworldRegistries.CELL_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }
}
