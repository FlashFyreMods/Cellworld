package com.flashfyre.cellworld;

import com.flashfyre.cellworld.cells.CellMap;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record CellMapRuleSource(Holder<CellMap> cellMap) implements SurfaceRules.RuleSource {
    static final MapCodec<CellMapRuleSource> MAP_CODEC =
            RecordCodecBuilder.mapCodec(
                    inst -> inst.group(
                                    CellMap.CODEC.fieldOf("cell_map").forGetter(CellMapRuleSource::cellMap)
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

    record CellMapRule(CellMap cellMap, SurfaceRules.Context ctx) implements SurfaceRules.SurfaceRule {
        @Override
        public @Nullable BlockState tryApply(int x, int y, int z) {
            Cell cell = this.cellMap.getCell(x, z);
            return cell.ruleSource().isPresent() ? cell.ruleSource().orElseThrow().apply(this.ctx()).tryApply(x, y, z) : null;
        }
    }
}
