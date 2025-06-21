package com.flashfyre.cellworld.registry;

import com.flashfyre.cellworld.cells.Cell;
import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.cells.CellMap;
import com.flashfyre.cellworld.cells.selector.CellSelectionSet;
import com.flashfyre.cellworld.cells.WeightedCell;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class CellworldRegistries {
    // Data registries
    public static final ResourceKey<Registry<Cell>> CELL_REGISTRY_KEY = createKey("cell");
    public static final ResourceKey<Registry<WeightedCell>> WEIGHTED_CELL_ENTRY_REGISTRY_KEY = createKey("weighted_cell_entry");
    public static final ResourceKey<Registry<CellMap>> CELL_MAP_REGISTRY_KEY = createKey("cell_map");

    public static final ResourceKey<Registry<MapCodec<? extends CellSelectionSet>>> SELECTOR_TYPE_REGISTRY_KEY = createKey("cell_entry_picker_types");

    public static final Registry<MapCodec<? extends CellSelectionSet>> SELECTOR_TYPE_REGISTRY = new RegistryBuilder<>(SELECTOR_TYPE_REGISTRY_KEY).create();
    private static <T> ResourceKey<Registry<T>> createKey(String id) {
        return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, id));
    }
}
