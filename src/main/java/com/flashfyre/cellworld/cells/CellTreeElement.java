package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.cells.selector.CellSelector;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;
import java.util.stream.Stream;

public class CellTreeElement {
    /*public static final Codec<CellEntry> CODEC = Codec.recursive(
            CellEntry.class.getSimpleName(), // This is for the toString method
            recursedCodec -> RecordCodecBuilder.create(instance -> instance.group(
                    Codec.either(Cell.CODEC, Codec.list(recursedCodec)).fieldOf("weight").forGetter(CellEntry::weight)
            ).apply(instance, CellEntry::new))
    );*/

    private Either<Holder<SurfacedBiome>, Either<CellSelector, Pair<Integer, String>>> value;

    private CellTreeElement(Either<Holder<SurfacedBiome>, Either<CellSelector, Pair<Integer, String>>> value) {
        this.value = value;
    }

    public static CellTreeElement cell(Holder<SurfacedBiome> cell) {
        return new CellTreeElement(Either.left(cell));
    }

    public static CellTreeElement cell(HolderGetter<SurfacedBiome> cells, ResourceKey<SurfacedBiome> cellKey) {
        return cell(cells.getOrThrow(cellKey));
    }

    public static CellTreeElement selector(CellSelector selector) {
        return new CellTreeElement(Either.right(Either.left(selector)));
    }

    public static CellTreeElement subtree(String id) {
        return new CellTreeElement(Either.right(Either.right(new Pair<>(0, id))));
    }

    public Stream<Holder<SurfacedBiome>> stream() {
        if (this.getCell().isPresent()) {
            return Stream.of(this.getCell().orElseThrow());
        }
        else if(this.getSelector().isPresent()) {
            return this.getSelector().get().streamCells();
        } else {
            return Stream.of();
        }
    }

    public Optional<Holder<SurfacedBiome>> getCell() {
        return this.value.left();
    }

    public Optional<CellSelector> getSelector() {
        return this.value.right().isPresent() ? this.value.right().orElseThrow().left() : Optional.empty();
    }

    public Optional<Either<CellSelector, Pair<Integer, String>>> getSelectorOrSubtreeKey() {
        return this.value.right();
    }

    public Optional<Pair<Integer, String>> getSubtreeKey() {
        return this.value.right().isPresent() ? this.value.right().orElseThrow().right() : Optional.empty();
    }

    public static final MapCodec<CellTreeElement> CODEC = Codec.mapEither(
            SurfacedBiome.CODEC.fieldOf("cell"),
            Codec.mapEither(
                    CellSelector.CODEC.fieldOf("cell_selector"),
                    Codec.mapPair(
                            Codec.INT.fieldOf("layer_index"),
                            Codec.string(1, 32).fieldOf("key")
                    ).fieldOf("subtree"))).xmap(CellTreeElement::new, e -> e.value);
}
