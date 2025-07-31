package com.flashfyre.cellworld.cells.selector;

import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.cells.Cell;
import com.flashfyre.cellworld.cells.CellTreeElement;
import com.flashfyre.cellworld.registry.CellworldRegistries;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import org.lwjgl.stb.STBEasyFont;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record RandomSelector(HolderSet<Cell> cells) implements CellSelector {

    /*public RandomSelector(CellTreeElement firstElement, CellTreeElement... elements) {
        this(Stream.concat(Stream.of(firstElement), Arrays.stream(elements)).collect(Collectors.toList()));
    }*/

    // RegistryCodecs.homogeneousList(CellworldRegistries.CELL_REGISTRY_KEY)

    public static final MapCodec<RandomSelector> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(RegistryCodecs.homogeneousList(CellworldRegistries.CELL_REGISTRY_KEY).fieldOf("holder_set").forGetter(e -> e.cells)
            ).apply(inst, RandomSelector::new));

    @Override
    public CellTreeElement get(LevelParameter.CellContext ctx) {
        return CellTreeElement.cell(this.cells.getRandomElement(ctx.rand()).orElseThrow());
    }

    @Override
    public MapCodec<? extends CellSelector> type() {
        return Cellworld.RANDOM.get();
    }

    @Override
    public Stream<Holder<Cell>> streamCells() {
        /*return cells.stream().flatMap(e -> {
            if(e.getCell().isPresent()) {
                return Stream.of(e.getCell().orElseThrow());
            } else {
                return e.getSelector().orElseThrow().streamCells();
            }
        });*/
        return this.cells.stream();
    }

    @Override
    public List<CellTreeElement> elements() {
        return this.cells.stream().map(CellTreeElement::cell).toList();
    }
}
