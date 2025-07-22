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

public record TerrainAugmentedCell(Holder<Cell> cell, int height) {
    public static final Codec<TerrainAugmentedCell> DIRECT_CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                            Cell.CODEC.fieldOf("cell").forGetter(TerrainAugmentedCell::cell),
                            Codec.INT.fieldOf("height").forGetter(TerrainAugmentedCell::height)
                    )
                    .apply(inst, TerrainAugmentedCell::new)
    );

    public static final Codec<Holder<TerrainAugmentedCell>> CODEC = RegistryFileCodec.create(CellworldRegistries.TERRAIN_CONFIGURED_CELL_REGISTRY_KEY, DIRECT_CODEC);

    public static final ResourceKey<TerrainAugmentedCell> TEST1 = createKey("test1");
    public static final ResourceKey<TerrainAugmentedCell> TEST2 = createKey("test2");
    public static final ResourceKey<TerrainAugmentedCell> TEST3 = createKey("test3");

    public static void bootstrap(BootstrapContext<TerrainAugmentedCell> ctx) {
        HolderGetter<Cell> cells = ctx.lookup(CellworldRegistries.CELL_REGISTRY_KEY);
        ctx.register(TEST1, new TerrainAugmentedCell(cells.getOrThrow(CellworldCells.NETHER_WASTES), 80));
        ctx.register(TEST2, new TerrainAugmentedCell(cells.getOrThrow(CellworldCells.SOUL_SAND_VALLEY), 40));
        ctx.register(TEST3, new TerrainAugmentedCell(cells.getOrThrow(CellworldCells.BASALT_DELTAS), 60));
    }

    private static ResourceKey<TerrainAugmentedCell> createKey(String name) {
        return ResourceKey.create(CellworldRegistries.TERRAIN_CONFIGURED_CELL_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }
}
