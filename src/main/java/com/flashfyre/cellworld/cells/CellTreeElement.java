package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.cells.selector.CellSelector;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;

import java.util.Optional;

public class CellTreeElement {
    /*public static final Codec<CellEntry> CODEC = Codec.recursive(
            CellEntry.class.getSimpleName(), // This is for the toString method
            recursedCodec -> RecordCodecBuilder.create(instance -> instance.group(
                    Codec.either(Cell.CODEC, Codec.list(recursedCodec)).fieldOf("value").forGetter(CellEntry::value)
            ).apply(instance, CellEntry::new))
    );*/

    private Either<Holder<Cell>, CellSelector> value;

    private CellTreeElement(Either<Holder<Cell>, CellSelector> value) {
        this.value = value;
    }

    public static CellTreeElement cell(Holder<Cell> cell) {
        return new CellTreeElement(Either.left(cell));
    }

    public static CellTreeElement selector(CellSelector selector) {
        return new CellTreeElement(Either.right(selector));
    }

    public Optional<Holder<Cell>> left() {
        return this.value.left();
    }

    public Optional<CellSelector> right() {
        return this.value.right();
    }

    public static final MapCodec<CellTreeElement> CODEC = Codec.mapEither(Cell.CODEC.fieldOf("cell"), CellSelector.CODEC.fieldOf("cell_selector")).xmap(CellTreeElement::new, e -> e.value);
}
