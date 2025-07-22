package com.flashfyre.cellworld.cells.provider;

import com.flashfyre.cellworld.cells.Cell;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;

public class BasicCell extends CellProvider {
    private final Holder<Cell> cell;

    private BasicCell(Holder<Cell> cell) {
        this.cell = cell;
    }

    public static BasicCell of(Holder<Cell> cell) {
       return new BasicCell(cell);
    }

    @Override
    public MapCodec<? extends CellProvider> type() {
        return null;
    }
}
