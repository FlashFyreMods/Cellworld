package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.cells.selector.CellSelectionSet;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;

public record CellEntry(Either<Holder<Cell>, CellSelectionSet> value) {
    /*public static final Codec<CellEntry> CODEC = Codec.recursive(
            CellEntry.class.getSimpleName(), // This is for the toString method
            recursedCodec -> RecordCodecBuilder.create(instance -> instance.group(
                    Codec.either(Cell.CODEC, Codec.list(recursedCodec)).fieldOf("value").forGetter(CellEntry::value)
            ).apply(instance, CellEntry::new))
    );*/

    public static final Codec<CellEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.either(Cell.CODEC, CellSelectionSet.CODEC).fieldOf("value").forGetter(CellEntry::value)
    ).apply(instance, CellEntry::new)
    );

    public CellEntry(Holder<Cell> cell) {
        this(Either.left(cell));
    }

    public CellEntry(CellSelectionSet cells) {
        this(Either.right(cells));
    }
}
