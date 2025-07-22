package com.flashfyre.cellworld.levelgen;

import com.flashfyre.cellworld.cells.Cell;
import com.flashfyre.cellworld.cells.CellSelectionTree;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.Nullable;

public record CellMapRuleSource(Holder<CellSelectionTree> cellMap) implements SurfaceRules.RuleSource {
    public static final MapCodec<CellMapRuleSource> MAP_CODEC =
            RecordCodecBuilder.mapCodec(
                    inst -> inst.group(
                                    CellSelectionTree.CODEC.fieldOf("cell_map").forGetter(CellMapRuleSource::cellMap)
                            )
                            .apply(inst, CellMapRuleSource::new)
            );

    static final KeyDispatchDataCodec<CellMapRuleSource> CODEC = KeyDispatchDataCodec.of(MAP_CODEC);

    @Override
    public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
        return CODEC;
    }

    public SurfaceRules.SurfaceRule apply(SurfaceRules.Context ctx) {
        return new CellMapRule(this.cellMap.value(), ctx);
    }

    record CellMapRule(CellSelectionTree cellMap, SurfaceRules.Context ctx) implements SurfaceRules.SurfaceRule {
        @Override
        public @Nullable BlockState tryApply(int x, int y, int z) {
            Cell cell = this.cellMap.getCell(x, z);
            return cell.ruleSource().isPresent() ? cell.ruleSource().orElseThrow().apply(this.ctx()).tryApply(x, y, z) : null;
        }
    }
}
