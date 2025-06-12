package com.flashfyre.cellworld;

import com.flashfyre.cellworld.cells.RandomFromWeightedList;
import com.flashfyre.cellworld.cells.WeightedCell;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class CellworldDataProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.WORLD_PRESET, CellworldWorldPresets::bootstrap)
            .add(Cellworld.WEIGHTED_CELL_ENTRY_REGISTRY_KEY, WeightedCell::bootstrap);;


    public CellworldDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Collections.singleton(Cellworld.MOD_ID));
    }
}
