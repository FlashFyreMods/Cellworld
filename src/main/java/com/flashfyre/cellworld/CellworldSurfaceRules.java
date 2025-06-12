package com.flashfyre.cellworld;

import com.flashfyre.cellworld.cells.CellMap;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

public class CellworldSurfaceRules {
    public static final SurfaceRules.RuleSource BEDROCK = stateRule(Blocks.BEDROCK);
    public static final SurfaceRules.RuleSource NETHERRACK = stateRule(Blocks.NETHERRACK);



    public static SurfaceRules.RuleSource nether(BootstrapContext<NoiseGeneratorSettings> ctx) {
        HolderGetter<CellMap> cellMaps = ctx.lookup(Cellworld.CELL_MAP_REGISTRY_KEY);
        SurfaceRules.ConditionSource condition4 = SurfaceRules.yBlockCheck(VerticalAnchor.belowTop(5), 0);
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK),
                SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.verticalGradient("bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.top())), BEDROCK),
                SurfaceRules.ifTrue(condition4, NETHERRACK),
                new CellMapRuleSource(cellMaps.getOrThrow(CellMap.NETHER))
        );
    }

    public static SurfaceRules.RuleSource stateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
}
