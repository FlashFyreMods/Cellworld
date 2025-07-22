package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.registry.CellworldRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class WeightedCellEntryTagProvider extends TagsProvider<SingleIntConfiguredCell>{
    public WeightedCellEntryTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, CellworldRegistries.SINGLE_INT_CONFIGURED_CELL, lookupProvider, Cellworld.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(SingleIntConfiguredCell.NETHER).add(SingleIntConfiguredCell.BASALT_DELTAS, SingleIntConfiguredCell.CRIMSON_FOREST);
    }

}