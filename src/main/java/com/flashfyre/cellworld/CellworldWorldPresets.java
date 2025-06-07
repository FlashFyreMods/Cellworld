package com.flashfyre.cellworld;

import com.flashfyre.cellworld.cells.*;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.List;
import java.util.Map;

public class CellworldWorldPresets {
    public static final ResourceKey<WorldPreset> CELLULAR = createKey("cellular");
    public static final ResourceKey<WorldPreset> CELLULAR_NETHER = createKey("cellular_nether");

    public static void bootstrap(BootstrapContext<WorldPreset> ctx) {
        HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
        HolderGetter<NoiseGeneratorSettings> noiseSettingsGetter = ctx.lookup(Registries.NOISE_SETTINGS);
        HolderGetter<RandomFromWeightedList.WeightedCellEntry> weightedCellEntries = ctx.lookup(Cellworld.WEIGHTED_CELL_ENTRY_REGISTRY_KEY);

        BiomeSource fixed = new FixedBiomeSource(biomes.getOrThrow(Biomes.MUSHROOM_FIELDS));
        BiomeSource cellularOverworld = new CellularBiomeSource(new CellMap(List.of(64, 16),
                CellSelector.randomFromList(
                        new CellEntry(CellSelector.randomFromList(
                                new CellEntry(Cell.of(biomes, Biomes.FROZEN_OCEAN)),
                                new CellEntry(Cell.of(biomes, Biomes.COLD_OCEAN)),
                                new CellEntry(Cell.of(biomes, Biomes.OCEAN)),
                                new CellEntry(Cell.of(biomes, Biomes.LUKEWARM_OCEAN)),
                                new CellEntry(Cell.of(biomes, Biomes.WARM_OCEAN))
                        )),
                        new CellEntry(CellSelector.randomFromList(
                                new CellEntry(Cell.of(biomes, Biomes.DARK_FOREST)),
                                new CellEntry(Cell.of(biomes, Biomes.PLAINS)),
                                new CellEntry(Cell.of(biomes, Biomes.FLOWER_FOREST))
                        )
                ))));

        /*BiomeSource cellularNether = new CellularBiomeSource(new CellMap(List.of(32), CellSelector.randomFromWeightedList(
                RandomFromWeightedList.entry(Cell.of(biomes, Biomes.NETHER_WASTES), 100),
                RandomFromWeightedList.entry(Cell.of(biomes, Biomes.SOUL_SAND_VALLEY), 95),
                RandomFromWeightedList.entry(Cell.of(biomes, Biomes.BASALT_DELTAS), 95),
                RandomFromWeightedList.entry(Cell.of(biomes, Biomes.WARPED_FOREST), 90),
                RandomFromWeightedList.entry(Cell.of(biomes, Biomes.CRIMSON_FOREST), 90),
                weightedCellEntries.getOrThrow(RandomFromWeightedList.OCEAN)
                )));*/
        /*BiomeSource cellularNether = new CellularBiomeSource(new CellMap(List.of(32), new RandomFromWeightedListHolderSet(HolderSet.direct(
                RandomFromWeightedList.entry(Cell.of(biomes, Biomes.NETHER_WASTES), 100),
                RandomFromWeightedList.entry(Cell.of(biomes, Biomes.OCEAN), 100)
        ))));*/
        BiomeSource cellularNether = new CellularBiomeSource(new CellMap(List.of(32), new RandomFromWeightedListHolderSet(weightedCellEntries.getOrThrow(RandomFromWeightedList.WeightedCellEntry.NETHER))));

        registerFlatWorldPreset(ctx, CELLULAR, cellularOverworld, biomes);
        registerNetherWorldPreset(ctx, CELLULAR_NETHER, cellularNether, noiseSettingsGetter, biomes);
    }

    public static void registerFlatWorldPreset(BootstrapContext<WorldPreset> ctx, ResourceKey<WorldPreset> presetKey, BiomeSource biomeSource, HolderGetter<Biome> biomeGetter) {
        HolderGetter<PlacedFeature> placedFeatures = ctx.lookup(Registries.PLACED_FEATURE);
        HolderGetter<StructureSet> structureSets = ctx.lookup(Registries.STRUCTURE_SET);
        ctx.register(presetKey, new WorldPreset(Map.of(LevelStem.OVERWORLD, new LevelStem(ctx.lookup(Registries.DIMENSION_TYPE).getOrThrow(BuiltinDimensionTypes.OVERWORLD), new BetterFlatLevelSource(FlatLevelGeneratorSettings.getDefault(biomeGetter, structureSets, placedFeatures), biomeSource)))));
    }

    public static void registerNetherWorldPreset(BootstrapContext<WorldPreset> ctx, ResourceKey<WorldPreset> presetKey, BiomeSource biomeSource, HolderGetter<NoiseGeneratorSettings> noiseSettingsGetter, HolderGetter<Biome> biomeGetter) {
        HolderGetter<PlacedFeature> placedFeatures = ctx.lookup(Registries.PLACED_FEATURE);
        HolderGetter<StructureSet> structureSets = ctx.lookup(Registries.STRUCTURE_SET);
        ctx.register(presetKey, new WorldPreset(Map.of(
                        LevelStem.OVERWORLD, new LevelStem(ctx.lookup(Registries.DIMENSION_TYPE).getOrThrow(BuiltinDimensionTypes.OVERWORLD), new FlatLevelSource(FlatLevelGeneratorSettings.getDefault(biomeGetter, structureSets, placedFeatures))),
                LevelStem.NETHER, new LevelStem(ctx.lookup(Registries.DIMENSION_TYPE).getOrThrow(BuiltinDimensionTypes.NETHER), new NoiseBasedChunkGenerator(biomeSource, noiseSettingsGetter.getOrThrow(NoiseGeneratorSettings.NETHER)))
        )));
    }

    private static ResourceKey<WorldPreset> createKey(String name) {
        return ResourceKey.create(Registries.WORLD_PRESET, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }
}
