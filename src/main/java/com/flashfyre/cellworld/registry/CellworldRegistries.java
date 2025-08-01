package com.flashfyre.cellworld.registry;

import com.flashfyre.cellworld.cells.*;
import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.cells.selector.CellSelector;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class CellworldRegistries {
    // Data registries
    public static final ResourceKey<Registry<SurfacedBiome>> SURFACED_BIOME_REG_KEY = createKey("surfaced_biome");
    public static final ResourceKey<Registry<WeightedSurfacedBiome>> WEIGHTED_SURFACED_BIOME_REG_KEY = createKey("weighted_surfaced_biome");
    public static final ResourceKey<Registry<CellSelectionTree>> CELL_SELECTION_TREE_REG_KEY = createKey("cell_selection_tree");

    public static final ResourceKey<Registry<MapCodec<? extends CellSelector>>> SELECTOR_TYPE_REGISTRY_KEY = createKey("selector_type");

    public static final Registry<MapCodec<? extends CellSelector>> SELECTOR_TYPE_REGISTRY = new RegistryBuilder<>(SELECTOR_TYPE_REGISTRY_KEY).create();

    private static <T> ResourceKey<Registry<T>> createKey(String id) {
        return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, id));
    }
}
