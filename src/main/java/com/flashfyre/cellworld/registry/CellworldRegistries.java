package com.flashfyre.cellworld.registry;

import com.flashfyre.cellworld.cells.*;
import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.cells.selector.CellSelector;
import com.flashfyre.cellworld.cells.selector.LevelParameter;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class CellworldRegistries {
    // Data registries
    public static final ResourceKey<Registry<Cell>> CELL_REGISTRY_KEY = createKey("cell");
    public static final ResourceKey<Registry<TerrainAugmentedCell>> TERRAIN_CONFIGURED_CELL_REGISTRY_KEY = createKey("terrain_configured_cell");
    public static final ResourceKey<Registry<SingleIntConfiguredCell>> SINGLE_INT_CONFIGURED_CELL = createKey("single_int_configured_cell");
    public static final ResourceKey<Registry<CellSelectionTreeOld>> CELL_MAP_REGISTRY_KEY = createKey("cell_map");
    public static final ResourceKey<Registry<CellSelectionTree>> CELL_SELECTION_TREE_REGISTRY_KEY = createKey("cell_selection_tree");

    public static final ResourceKey<Registry<MapCodec<? extends CellSelector>>> SELECTOR_TYPE_REGISTRY_KEY = createKey("selector_types");
    public static final ResourceKey<Registry<MapCodec<? extends LevelParameter>>> LEVEL_PARAMETER_TYPES_REGISTRY_KEY = createKey("value_selector_parameter_types");

    //public static final Registry<CellSelectionTree> CELL_SELECTION_TREE_REGISTRY = new RegistryBuilder<>(CELL_SELECTION_TREE_REGISTRY_KEY).create();
    public static final Registry<MapCodec<? extends CellSelector>> SELECTOR_TYPE_REGISTRY = new RegistryBuilder<>(SELECTOR_TYPE_REGISTRY_KEY).create();
    public static final Registry<MapCodec<? extends LevelParameter>> LEVEL_PARAMETER_TYPE_REGISTRY = new RegistryBuilder<>(LEVEL_PARAMETER_TYPES_REGISTRY_KEY).create();

    private static <T> ResourceKey<Registry<T>> createKey(String id) {
        return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, id));
    }
}
