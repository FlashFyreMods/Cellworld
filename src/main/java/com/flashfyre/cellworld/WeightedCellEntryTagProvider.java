package com.flashfyre.cellworld;

import com.flashfyre.cellworld.cells.RandomFromWeightedList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class WeightedCellEntryTagProvider extends TagsProvider<RandomFromWeightedList.WeightedCellEntry>{
    public WeightedCellEntryTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, Cellworld.WEIGHTED_CELL_ENTRY_REGISTRY_KEY, lookupProvider, Cellworld.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(RandomFromWeightedList.WeightedCellEntry.NETHER).add(RandomFromWeightedList.BASALT_DELTAS, RandomFromWeightedList.CRIMSON_FOREST);
    }

}