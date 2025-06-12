package com.flashfyre.cellworld;

import com.flashfyre.cellworld.cells.RandomFromWeightedList;
import com.flashfyre.cellworld.cells.WeightedCell;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class WeightedCellEntryTagProvider extends TagsProvider<WeightedCell>{
    public WeightedCellEntryTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, Cellworld.WEIGHTED_CELL_ENTRY_REGISTRY_KEY, lookupProvider, Cellworld.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(WeightedCell.NETHER).add(WeightedCell.BASALT_DELTAS, WeightedCell.CRIMSON_FOREST);
    }

}