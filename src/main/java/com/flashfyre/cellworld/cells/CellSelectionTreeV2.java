package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.cells.selector.CellSelector;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Map;

public record CellSelectionTreeV2(List<Pair<Integer, Map<String, CellTreeElement>>> subtrees) {
    public static final Codec<CellSelectionTreeV2> DIRECT_CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.mapPair(Codec.INT.fieldOf("cell_scale"), Codec.unboundedMap(Codec.string(1, 32), CellTreeElement.CODEC.codec()).fieldOf("subtrees")).codec().listOf().fieldOf("layers").forGetter(cellMap -> cellMap.subtrees)
            ).apply(inst, CellSelectionTreeV2::new)
    );

    /*public record Layer(int size, List<Pair<String, Either<CellSet, Cell>>> cellSelector) {
        public static final Codec<Layer> CODEC = RecordCodecBuilder.create(
                inst -> inst.group(
                        Codec.INT.fieldOf("size").forGetter(Layer::size),
                        Codec.list(CellSet.CODEC.fieldOf("cell_selector").forGetter(Layer::cellSelector)
                ).apply(inst, Layer::new)
        );
    }*/
}
