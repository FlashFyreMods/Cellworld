package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.registry.CellworldRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class WeightedCellEntryTagProvider extends TagsProvider<WeightedSurfacedBiome>{
    public WeightedCellEntryTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, CellworldRegistries.WEIGHTED_SURFACED_BIOME_REG_KEY, lookupProvider, Cellworld.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(WeightedSurfacedBiome.NETHER).add(WeightedSurfacedBiome.BASALT_DELTAS, WeightedSurfacedBiome.CRIMSON_FOREST);
    }

}