package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.registry.CellworldCells;
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

public record SingleIntConfiguredCell(Holder<Cell> cell, int value) {
    public static final Codec<SingleIntConfiguredCell> DIRECT_CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                            Cell.CODEC.fieldOf("cell").forGetter(w -> w.cell),
                            Codec.INT.fieldOf("value").forGetter(entry -> entry.value)
                    )
                    .apply(inst, SingleIntConfiguredCell::new)
    );

    public static final Codec<Holder<SingleIntConfiguredCell>> CODEC = RegistryFileCodec.create(CellworldRegistries.SINGLE_INT_CONFIGURED_CELL, DIRECT_CODEC);

    public static final TagKey<SingleIntConfiguredCell> NETHER = create("nether");

    private static TagKey<SingleIntConfiguredCell> create(String name) {
        return TagKey.create(CellworldRegistries.SINGLE_INT_CONFIGURED_CELL, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }

    public static final ResourceKey<SingleIntConfiguredCell> NETHER_WASTES = createKey("nether_wastes_weighted");
    public static final ResourceKey<SingleIntConfiguredCell> SOUL_SAND_VALLEY = createKey("soul_sand_valley_weighted");
    public static final ResourceKey<SingleIntConfiguredCell> WARPED_FOREST = createKey("warped_forest_weighted");
    public static final ResourceKey<SingleIntConfiguredCell> CRIMSON_FOREST = createKey("crimson_forest_weighted");
    public static final ResourceKey<SingleIntConfiguredCell> BASALT_DELTAS = createKey("basalt_deltas_weighted");
    public static final ResourceKey<SingleIntConfiguredCell> GILDED_DEPTHS = createKey("gilded_depths_weighted");

    public static void bootstrap(BootstrapContext<SingleIntConfiguredCell> ctx) {
        HolderGetter<Cell> cells = ctx.lookup(CellworldRegistries.CELL_REGISTRY_KEY);
        ctx.register(NETHER_WASTES, new SingleIntConfiguredCell(cells.getOrThrow(CellworldCells.NETHER_WASTES), 100));
        ctx.register(SOUL_SAND_VALLEY, new SingleIntConfiguredCell(cells.getOrThrow(CellworldCells.SOUL_SAND_VALLEY), 90));
        ctx.register(WARPED_FOREST, new SingleIntConfiguredCell(cells.getOrThrow(CellworldCells.WARPED_FOREST), 80));
        ctx.register(CRIMSON_FOREST, new SingleIntConfiguredCell(cells.getOrThrow(CellworldCells.CRIMSON_FOREST), 90));
        ctx.register(BASALT_DELTAS, new SingleIntConfiguredCell(cells.getOrThrow(CellworldCells.BASALT_DELTAS), 90));
        ctx.register(GILDED_DEPTHS, new SingleIntConfiguredCell(cells.getOrThrow(CellworldCells.GILDED_DEPTHS), 80));
    }

    private static ResourceKey<SingleIntConfiguredCell> createKey(String name) {
        return ResourceKey.create(CellworldRegistries.SINGLE_INT_CONFIGURED_CELL, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }
}
