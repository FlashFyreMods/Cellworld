package com.flashfyre.cellworld.cells.provider;

import com.mojang.serialization.MapCodec;

public abstract class CellProvider {

    public abstract MapCodec<? extends CellProvider> type();
}
