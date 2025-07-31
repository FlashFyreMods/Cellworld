package com.flashfyre.cellworld.levelgen;

import com.flashfyre.cellworld.cells.CellSelectionTree;
import com.flashfyre.cellworld.cells.CellSelectionTreeOld;
import com.flashfyre.cellworld.registry.CellworldNoises;
import com.flashfyre.cellworld.registry.CellworldRegistries;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

public class CellworldSurfaceRules {
    public static final SurfaceRules.RuleSource BEDROCK = stateRule(Blocks.BEDROCK);
    public static final SurfaceRules.RuleSource NETHERRACK = stateRule(Blocks.NETHERRACK);
    public static final SurfaceRules.RuleSource BLACKSTONE = stateRule(Blocks.BLACKSTONE);
    public static final SurfaceRules.RuleSource BASALT = stateRule(Blocks.BASALT);
    public static final SurfaceRules.RuleSource GRAVEL = stateRule(Blocks.GRAVEL);
    public static final SurfaceRules.RuleSource LAVA = stateRule(Blocks.LAVA);
    public static final SurfaceRules.RuleSource SOUL_SAND = stateRule(Blocks.SOUL_SAND);
    public static final SurfaceRules.RuleSource SOUL_SOIL = stateRule(Blocks.SOUL_SOIL);
    public static final SurfaceRules.RuleSource WARPED_WART_BLOCK = stateRule(Blocks.WARPED_WART_BLOCK);
    public static final SurfaceRules.RuleSource WARPED_NYLIUM = stateRule(Blocks.WARPED_NYLIUM);
    public static final SurfaceRules.RuleSource NETHER_WART_BLOCK = stateRule(Blocks.NETHER_WART_BLOCK);
    public static final SurfaceRules.RuleSource CRIMSON_NYLIUM = stateRule(Blocks.CRIMSON_NYLIUM);

    public static final SurfaceRules.RuleSource OBSIDIAN = stateRule(Blocks.OBSIDIAN);
    public static final SurfaceRules.RuleSource CRYING_OBSIDIAN = stateRule(Blocks.CRYING_OBSIDIAN);
    public static final SurfaceRules.RuleSource AMETHYST = stateRule(Blocks.AMETHYST_BLOCK);
    public static final SurfaceRules.RuleSource END_STONE = stateRule(Blocks.END_STONE);


    /*public static SurfaceRules.RuleSource nether(BootstrapContext<NoiseGeneratorSettings> ctx) {
        HolderGetter<CellSelectionTreeOld> cellMaps = ctx.lookup(CellworldRegistries.CELL_MAP_REGISTRY_KEY);
        SurfaceRules.ConditionSource condition4 = net.minecraft.world.level.levelgen.SurfaceRules.yBlockCheck(VerticalAnchor.belowTop(5), 0);
        return net.minecraft.world.level.levelgen.SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK),
                SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.verticalGradient("bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.top())), BEDROCK),
                SurfaceRules.ifTrue(condition4, NETHERRACK),
                new CellMapRuleSource(cellMaps.getOrThrow(CellSelectionTreeOld.NETHER))
        );
    }*/

    public static SurfaceRules.RuleSource end(BootstrapContext<NoiseGeneratorSettings> ctx) {
        HolderGetter<CellSelectionTree> cellTrees = ctx.lookup(CellworldRegistries.CELL_SELECTION_TREE_REGISTRY_KEY);
        SurfaceRules.ConditionSource condition4 = net.minecraft.world.level.levelgen.SurfaceRules.yBlockCheck(VerticalAnchor.belowTop(5), 0);
        return net.minecraft.world.level.levelgen.SurfaceRules.sequence(
                new CellMapRuleSource(cellTrees.getOrThrow(CellSelectionTree.END)),
                END_STONE
        );
    }

    static SurfaceRules.ConditionSource aboveY31 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(31), 0);
    static SurfaceRules.ConditionSource aboveY32 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(32), 0);
    static SurfaceRules.ConditionSource aboveY30checked = SurfaceRules.yStartCheck(VerticalAnchor.absolute(30), 0);
    static SurfaceRules.ConditionSource belowY35checked = SurfaceRules.not(SurfaceRules.yStartCheck(VerticalAnchor.absolute(35), 0));
    static SurfaceRules.ConditionSource isHole = SurfaceRules.hole();
    static SurfaceRules.ConditionSource noiseSoulSand = SurfaceRules.noiseCondition(Noises.SOUL_SAND_LAYER, -0.012);
    static SurfaceRules.ConditionSource noiseGravel = SurfaceRules.noiseCondition(Noises.GRAVEL_LAYER, -0.012);
    static SurfaceRules.ConditionSource noisePatch = SurfaceRules.noiseCondition(Noises.PATCH, -0.012);
    static SurfaceRules.ConditionSource noiseNetherrack = SurfaceRules.noiseCondition(Noises.NETHERRACK, 0.54);
    static SurfaceRules.ConditionSource noiseNetherWart = SurfaceRules.noiseCondition(Noises.NETHER_WART, 1.17);
    static SurfaceRules.ConditionSource noiseNetherState = SurfaceRules.noiseCondition(Noises.NETHER_STATE_SELECTOR, 0.0);
    static SurfaceRules.RuleSource isGravel = SurfaceRules.ifTrue(
            noisePatch, SurfaceRules.ifTrue(aboveY30checked, SurfaceRules.ifTrue(belowY35checked, GRAVEL))
    );

    public static SurfaceRules.RuleSource basaltDeltas() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.UNDER_CEILING, BASALT),
                SurfaceRules.ifTrue(
                        SurfaceRules.UNDER_FLOOR,
                        SurfaceRules.sequence(isGravel, SurfaceRules.ifTrue(noiseNetherState, BASALT), BLACKSTONE)
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.ON_FLOOR,
                        SurfaceRules.ifTrue(SurfaceRules.not(aboveY32), SurfaceRules.ifTrue(isHole, LAVA))
                )
        );
    }

    public static SurfaceRules.RuleSource soulSandValley() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.UNDER_CEILING, SurfaceRules.sequence(SurfaceRules.ifTrue(noiseNetherState, SOUL_SAND), SOUL_SOIL)
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.UNDER_FLOOR,
                        SurfaceRules.sequence(isGravel, SurfaceRules.ifTrue(noiseNetherState, SOUL_SAND), SOUL_SOIL)
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.ON_FLOOR,
                        SurfaceRules.ifTrue(SurfaceRules.not(aboveY32), SurfaceRules.ifTrue(isHole, LAVA))
                )
        );
    }

    public static SurfaceRules.RuleSource warpedForest() {
        return SurfaceRules.ifTrue(
                SurfaceRules.ON_FLOOR,
                SurfaceRules.sequence(
                        SurfaceRules.ifTrue(SurfaceRules.not(aboveY32), SurfaceRules.ifTrue(isHole, LAVA)),
                        SurfaceRules.ifTrue(
                                SurfaceRules.not(noiseNetherrack),
                                SurfaceRules.ifTrue(
                                        aboveY31,
                                        SurfaceRules.sequence(SurfaceRules.ifTrue(noiseNetherWart, WARPED_WART_BLOCK), WARPED_NYLIUM)
                                )
                        )
                )
        );
    }

    public static SurfaceRules.RuleSource crimsonForest() {
        return SurfaceRules.ifTrue(
                SurfaceRules.ON_FLOOR,
                SurfaceRules.sequence(
                        SurfaceRules.ifTrue(SurfaceRules.not(aboveY32), SurfaceRules.ifTrue(isHole, LAVA)),
                        SurfaceRules.ifTrue(
                                SurfaceRules.not(noiseNetherrack),
                                SurfaceRules.ifTrue(
                                        aboveY31,
                                        SurfaceRules.sequence(SurfaceRules.ifTrue(noiseNetherWart, NETHER_WART_BLOCK), CRIMSON_NYLIUM)
                                )
                        )
                )
        );
    }

    public static SurfaceRules.RuleSource netherWastes() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.ON_FLOOR,
                        SurfaceRules.ifTrue(SurfaceRules.not(aboveY32), SurfaceRules.ifTrue(isHole, LAVA))
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.UNDER_FLOOR,
                        SurfaceRules.ifTrue(
                                noiseSoulSand,
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(
                                                SurfaceRules.not(isHole),
                                                SurfaceRules.ifTrue(aboveY30checked, SurfaceRules.ifTrue(belowY35checked, SOUL_SAND))
                                        ),
                                        NETHERRACK
                                )
                        )
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.ON_FLOOR,
                        SurfaceRules.ifTrue(
                                aboveY31,
                                SurfaceRules.ifTrue(
                                        belowY35checked,
                                        SurfaceRules.ifTrue(
                                                noiseGravel,
                                                SurfaceRules.sequence(
                                                        SurfaceRules.ifTrue(aboveY32, GRAVEL),
                                                        SurfaceRules.ifTrue(SurfaceRules.not(isHole), GRAVEL)
                                                )
                                        )
                                )
                        )
                )
        );
    }

    public static SurfaceRules.RuleSource gildedDepths() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.ON_FLOOR,
                        SurfaceRules.ifTrue(SurfaceRules.not(aboveY32), SurfaceRules.ifTrue(isHole, LAVA))
                ),
                BLACKSTONE
        );
    }

    public static SurfaceRules.RuleSource obsidianSpires() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.ON_FLOOR,
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(CellworldNoises.OBSIDIAN_SPIRES_SURFACE, -0.06, -0.015), OBSIDIAN),
                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(CellworldNoises.OBSIDIAN_SPIRES_SURFACE, -0.015, 0.015), CRYING_OBSIDIAN),
                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(CellworldNoises.OBSIDIAN_SPIRES_SURFACE, 0.015, 0.06), OBSIDIAN)
                        )
                ),
                END_STONE
        );
    }

    public static SurfaceRules.RuleSource amethystFields() {
        return AMETHYST;
    }

    public static SurfaceRules.RuleSource stateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
}
