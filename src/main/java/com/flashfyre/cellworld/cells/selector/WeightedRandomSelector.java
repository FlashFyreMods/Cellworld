package com.flashfyre.cellworld.cells.selector;

import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.cells.SurfacedBiome;
import com.flashfyre.cellworld.cells.CellSelectionTree;
import com.flashfyre.cellworld.cells.CellTreeElement;
import com.flashfyre.cellworld.cells.WeightedSurfacedBiome;
import com.flashfyre.cellworld.registry.CellworldRegistries;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.util.random.SimpleWeightedRandomList;

import java.util.List;
import java.util.stream.Stream;

public class WeightedRandomSelector implements CellSelector {
    public static final MapCodec<WeightedRandomSelector> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(Codec.either(
                        RegistryCodecs.homogeneousList(CellworldRegistries.WEIGHTED_SURFACED_BIOME_REG_KEY),
                        SimpleWeightedRandomList.wrappedCodec(CellTreeElement.CODEC.codec())
                    ).fieldOf("entries").forGetter(e -> e.entries)
    ).apply(inst, WeightedRandomSelector::new));

    private final Either<HolderSet<WeightedSurfacedBiome>, SimpleWeightedRandomList<CellTreeElement>> entries;
    private SimpleWeightedRandomList<Holder<SurfacedBiome>> list;

    private WeightedRandomSelector(Either<HolderSet<WeightedSurfacedBiome>, SimpleWeightedRandomList<CellTreeElement>> entries) {
        this.entries = entries;
    }

    public static WeightedRandomSelector holderSet(HolderSet<WeightedSurfacedBiome> holderSet) {
        return new WeightedRandomSelector(Either.left(holderSet));
    }

    public static WeightedRandomSelector weightedList(SimpleWeightedRandomList<CellTreeElement> weightedList) {
        return new WeightedRandomSelector(Either.right(weightedList));
    }

    @Override
    public CellTreeElement get(CellSelectionTree.PositionalContext ctx) {
        return this.entries.left().isPresent() ?
                CellTreeElement.cell(this.list.getRandomValue(ctx.rand()).orElseThrow())
                : this.entries.right().orElseThrow().getRandomValue(ctx.rand()).orElseThrow();
    }

    @Override
    public MapCodec<? extends CellSelector> type() {
        return Cellworld.WEIGHTED_RANDOM.get();
    }

    public Stream<Holder<SurfacedBiome>> streamCells() {
        this.buildList();
        if(this.entries.left().isPresent()) {
            return this.entries.left().orElseThrow().stream().map(e -> e.value().cell());
        } else {
            return this.entries.right().orElseThrow().unwrap().stream().flatMap(w -> w.data().stream());
        }
    }

    @Override
    public List<CellTreeElement> elements() {
        if(this.entries.left().isPresent()) {
            return List.of();
        } else {
            return this.entries.right().orElseThrow().unwrap().stream().map(e -> e.data()).toList();
        }
    }

    public void buildList() {
        if(this.entries.left().isPresent()) {
            SimpleWeightedRandomList.Builder<Holder<SurfacedBiome>> builder = new SimpleWeightedRandomList.Builder<>();
            this.entries.left().orElseThrow().forEach(h -> {
                builder.add(h.value().cell(), h.value().weight());
            });
            this.list = builder.build();
        }
    }
}
