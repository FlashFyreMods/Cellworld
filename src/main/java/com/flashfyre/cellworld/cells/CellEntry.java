package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.Cell;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record CellEntry(Either<Cell, List<CellEntry>> value) {
    public static final Codec<CellEntry> CODEC = Codec.recursive(
            CellEntry.class.getSimpleName(), // This is for the toString method
            recursedCodec -> RecordCodecBuilder.create(instance -> instance.group(
                    Codec.either(Cell.CODEC, Codec.list(recursedCodec)).fieldOf("value").forGetter(CellEntry::value)
            ).apply(instance, CellEntry::new))
    );

    public CellEntry(Cell cell) {
        this(Either.left(cell));
    }

    public CellEntry(List<CellEntry> cells) {
        this(Either.right(cells));
    }
}
