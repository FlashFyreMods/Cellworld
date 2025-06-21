package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.registry.CellworldCells;
import com.flashfyre.cellworld.registry.CellworldRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;

public record WeightedCell(Holder<Cell> cell, int weight) {
    public static final Codec<WeightedCell> DIRECT_CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                            Cell.CODEC.fieldOf("cell").forGetter(w -> w.cell),
                            ExtraCodecs.POSITIVE_INT.fieldOf("weight").forGetter(entry -> entry.weight)
                    )
                    .apply(inst, WeightedCell::new)
    );

    public static final Codec<Holder<WeightedCell>> CODEC = RegistryFileCodec.create(CellworldRegistries.WEIGHTED_CELL_ENTRY_REGISTRY_KEY, DIRECT_CODEC);

    public static final TagKey<WeightedCell> NETHER = create("nether");

    private static TagKey<WeightedCell> create(String name) {
        return TagKey.create(CellworldRegistries.WEIGHTED_CELL_ENTRY_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }

    public static final ResourceKey<WeightedCell> NETHER_WASTES = createKey("nether_wastes");
    public static final ResourceKey<WeightedCell> SOUL_SAND_VALLEY = createKey("soul_sand_valley");
    public static final ResourceKey<WeightedCell> WARPED_FOREST = createKey("warped_forest");
    public static final ResourceKey<WeightedCell> CRIMSON_FOREST = createKey("crimson_forest");
    public static final ResourceKey<WeightedCell> BASALT_DELTAS = createKey("basalt_deltas");
    public static final ResourceKey<WeightedCell> GILDED_DEPTHS = createKey("gilded_depths");

    public static void bootstrap(BootstrapContext<WeightedCell> ctx) {
        HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
        HolderGetter<Cell> cells = ctx.lookup(CellworldRegistries.CELL_REGISTRY_KEY);
        ctx.register(NETHER_WASTES, new WeightedCell(cells.getOrThrow(CellworldCells.NETHER_WASTES), 100));
        ctx.register(SOUL_SAND_VALLEY, new WeightedCell(cells.getOrThrow(CellworldCells.SOUL_SAND_VALLEY), 90));
        ctx.register(WARPED_FOREST, new WeightedCell(cells.getOrThrow(CellworldCells.WARPED_FOREST), 80));
        ctx.register(CRIMSON_FOREST, new WeightedCell(cells.getOrThrow(CellworldCells.CRIMSON_FOREST), 90));
        ctx.register(BASALT_DELTAS, new WeightedCell(cells.getOrThrow(CellworldCells.BASALT_DELTAS), 90));
        ctx.register(GILDED_DEPTHS, new WeightedCell(cells.getOrThrow(CellworldCells.GILDED_DEPTHS), 80));
    }

    private static ResourceKey<WeightedCell> createKey(String name) {
        return ResourceKey.create(CellworldRegistries.WEIGHTED_CELL_ENTRY_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }
}
