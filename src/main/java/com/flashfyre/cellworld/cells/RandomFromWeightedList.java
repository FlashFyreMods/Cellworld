package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.Cell;
import com.flashfyre.cellworld.Cellworld;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class RandomFromWeightedList extends CellSelector {
    public static final MapCodec<RandomFromWeightedList> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                            WeightedCellEntry.CODEC.listOf().fieldOf("cells").forGetter(e -> e.cells)
                    )
                    .apply(inst, RandomFromWeightedList::new)
    );
    private final List<Holder<WeightedCellEntry>> cells;
    private SimpleWeightedRandomList<CellEntry> simpleWeightedRandomList;

    protected RandomFromWeightedList(List<Holder<WeightedCellEntry>> cells) {
        this.cells = cells;
    }

    private void build() {
        SimpleWeightedRandomList.Builder<CellEntry> builder = new SimpleWeightedRandomList.Builder<>();

        for(Holder<WeightedCellEntry> weightedCellEntry : this.cells) {
            builder.add(weightedCellEntry.value().cell, weightedCellEntry.value().weight);
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
        return this.cells.stream().map(e -> e.value().cell);
    }

    public static Holder<WeightedCellEntry> entry(Cell cell, int weight) {
        return Holder.direct(new WeightedCellEntry(new CellEntry(cell), weight));
    }

    public static final ResourceKey<WeightedCellEntry> BASALT_DELTAS = createKey("basalt_deltas");
    public static final ResourceKey<WeightedCellEntry> CRIMSON_FOREST = createKey("crimson_forest");

    public static void bootstrap(BootstrapContext<WeightedCellEntry> ctx) {
        HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
        ctx.register(BASALT_DELTAS, new WeightedCellEntry(new CellEntry(Cell.of(biomes, Biomes.BASALT_DELTAS)), 100));
        ctx.register(CRIMSON_FOREST, new WeightedCellEntry(new CellEntry(Cell.of(biomes, Biomes.CRIMSON_FOREST)), 50));
    }

    private static ResourceKey<WeightedCellEntry> createKey(String name) {
        return ResourceKey.create(Cellworld.WEIGHTED_CELL_ENTRY_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }

    public record WeightedCellEntry(CellEntry cell, int weight) {
        public static final Codec<WeightedCellEntry> DIRECT_CODEC = RecordCodecBuilder.create(
                inst -> inst.group(
                                CellEntry.CODEC.fieldOf("cell").forGetter(entry -> entry.cell),
                                ExtraCodecs.POSITIVE_INT.fieldOf("weight").forGetter(entry -> entry.weight)
                        )
                        .apply(inst, WeightedCellEntry::new)
        );

        public static final Codec<Holder<WeightedCellEntry>> CODEC = RegistryFileCodec.create(Cellworld.WEIGHTED_CELL_ENTRY_REGISTRY_KEY, DIRECT_CODEC);

        public static final TagKey<WeightedCellEntry> NETHER = create("nether");
        private static TagKey<WeightedCellEntry> create(String name) {
            return TagKey.create(Cellworld.WEIGHTED_CELL_ENTRY_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
        }
    }
}
