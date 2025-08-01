package com.flashfyre.cellworld.cells.selector;

import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.cells.SurfacedBiome;
import com.flashfyre.cellworld.cells.CellSelectionTree;
import com.flashfyre.cellworld.cells.CellTreeElement;
import com.flashfyre.cellworld.registry.CellworldRegistries;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;

import java.util.List;
import java.util.stream.Stream;

/**
 * Selector that picks entries from a SurfacedBiome HolderSet.
 * @param entries The HolderSet
 */
public record RandomSelector(HolderSet<SurfacedBiome> entries) implements CellSelector {

    /*public RandomSelector(CellTreeElement firstElement, CellTreeElement... elements) {
        this(Stream.concat(Stream.of(firstElement), Arrays.stream(elements)).collect(Collectors.toList()));
    }*/

    // RegistryCodecs.homogeneousList(CellworldRegistries.CELL_REGISTRY_KEY)

    public static final MapCodec<RandomSelector> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(RegistryCodecs.homogeneousList(CellworldRegistries.SURFACED_BIOME_REG_KEY).fieldOf("holder_set").forGetter(e -> e.entries)
            ).apply(inst, RandomSelector::new));

    @Override
    public CellTreeElement get(CellSelectionTree.PositionalContext ctx) {
        return CellTreeElement.cell(this.entries.getRandomElement(ctx.rand()).orElseThrow());
    }

    @Override
    public MapCodec<? extends CellSelector> type() {
        return Cellworld.RANDOM.get();
    }

    @Override
    public Stream<Holder<SurfacedBiome>> streamCells() {
        /*return entries.stream().flatMap(e -> {
            if(e.getCell().isPresent()) {
                return Stream.of(e.getCell().orElseThrow());
            } else {
                return e.getSelector().orElseThrow().streamCells();
            }
        });*/
        return this.entries.stream();
    }

    @Override
    public List<CellTreeElement> elements() {
        return this.entries.stream().map(CellTreeElement::cell).toList();
    }
}
