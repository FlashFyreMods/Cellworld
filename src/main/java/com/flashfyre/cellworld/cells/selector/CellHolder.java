package com.flashfyre.cellworld.cells.selector;

import com.flashfyre.cellworld.cells.Cell;
import com.flashfyre.cellworld.cells.CellTreeElement;
import com.flashfyre.cellworld.cells.ICellTreeElement;
import net.minecraft.core.Holder;

import java.util.stream.Stream;

public final class CellHolder implements ICellTreeElement {
    private final Holder<Cell> cell;

    public CellHolder(Holder<Cell> cell) {
        this.cell = cell;
    }

    public Holder<Cell> cell() {
        return cell;
    }

    /*@Override
    public Stream<Holder<Cell>> streamCells() {
        return Stream.of(cell);
    }*/
}
