package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.Cell;
import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.CellworldBiomes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.List;
import java.util.stream.Stream;

public class RandomFromWeightedList extends CellSelector {
    public static final MapCodec<RandomFromWeightedList> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                            WeightedCell.CODEC.listOf().fieldOf("cells").forGetter(e -> e.cells)
                    )
                    .apply(inst, RandomFromWeightedList::new)
    );
    private final List<Holder<WeightedCell>> cells;
    private SimpleWeightedRandomList<CellEntry> simpleWeightedRandomList;

    protected RandomFromWeightedList(List<Holder<WeightedCell>> cells) {
        this.cells = cells;
    }

    private void build() {
        SimpleWeightedRandomList.Builder<CellEntry> builder = new SimpleWeightedRandomList.Builder<>();

        for(Holder<WeightedCell> weightedCellEntry : this.cells) {
            builder.add(weightedCellEntry.value().cell(), weightedCellEntry.value().weight());
        }
        this.simpleWeightedRandomList = builder.build();
    }

    @Override
    public MapCodec<? extends CellSelector> type() {
        return Cellworld.RANDOM_FROM_WEIGHTED_LIST.get();
    }

    @Override
    public CellEntry get(RandomSource r) { // Called
        if(this.simpleWeightedRandomList == null) {
            build(); // converts holderset of objects with weight to
        }
        return this.simpleWeightedRandomList.getRandomValue(r).orElseThrow();
    }

    @Override
    public Stream<CellEntry> all() {
        return this.cells.stream().map(e -> e.value().cell());
    }

    public static Holder<WeightedCell> entry(Cell cell, int weight) {
        return Holder.direct(new WeightedCell(new CellEntry(cell), weight));
    }



}
