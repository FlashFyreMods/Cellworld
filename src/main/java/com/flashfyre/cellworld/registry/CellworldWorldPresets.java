package com.flashfyre.cellworld.registry;

import com.flashfyre.cellworld.chunkgenerator.BetterFlatLevelSource;
import com.flashfyre.cellworld.CellularBiomeSource;
import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.cells.*;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.Map;

public class CellworldWorldPresets {
    public static final ResourceKey<WorldPreset> DEFAULT_CELLULAR = createKey("default_cellular");

    public static void bootstrap(BootstrapContext<WorldPreset> ctx) {
        HolderGetter<NoiseGeneratorSettings> noiseSettingsGetter = ctx.lookup(Registries.NOISE_SETTINGS);
        HolderGetter<CellSelectionTree> cellMaps = ctx.lookup(CellworldRegistries.CELL_SELECTION_TREE_REG_KEY);
        HolderGetter<MultiNoiseBiomeSourceParameterList> parameterLists = ctx.lookup(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);

        BiomeSource defaultOverworldSource = MultiNoiseBiomeSource.createFromPreset(parameterLists.getOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD));
        BiomeSource netherBiomeSource = new CellularBiomeSource(cellMaps.getOrThrow(CellSelectionTree.NETHER));
        BiomeSource cellularEndBiomeSource = new CellularBiomeSource(cellMaps.getOrThrow(CellSelectionTree.END));

        ctx.register(DEFAULT_CELLULAR, new WorldPreset(Map.of(
                LevelStem.OVERWORLD, new LevelStem(
                        ctx.lookup(Registries.DIMENSION_TYPE).getOrThrow(BuiltinDimensionTypes.OVERWORLD),
                        new NoiseBasedChunkGenerator(defaultOverworldSource, noiseSettingsGetter.getOrThrow(NoiseGeneratorSettings.OVERWORLD))),
                LevelStem.NETHER, new LevelStem(
                        ctx.lookup(Registries.DIMENSION_TYPE).getOrThrow(BuiltinDimensionTypes.NETHER),
                        new NoiseBasedChunkGenerator(netherBiomeSource, noiseSettingsGetter.getOrThrow(CellworldNoiseSettings.NETHER_CELLULAR))),
                LevelStem.END, new LevelStem(
                        ctx.lookup(Registries.DIMENSION_TYPE).getOrThrow(BuiltinDimensionTypes.END),
                        new NoiseBasedChunkGenerator(cellularEndBiomeSource, noiseSettingsGetter.getOrThrow(CellworldNoiseSettings.END_CELLULAR)))
        )));
    }

    public static void registerFlat(BootstrapContext<WorldPreset> ctx, ResourceKey<WorldPreset> presetKey, BiomeSource biomeSource, HolderGetter<Biome> biomeGetter) {
        HolderGetter<PlacedFeature> placedFeatures = ctx.lookup(Registries.PLACED_FEATURE);
        HolderGetter<StructureSet> structureSets = ctx.lookup(Registries.STRUCTURE_SET);
        ctx.register(presetKey, new WorldPreset(Map.of(LevelStem.OVERWORLD, new LevelStem(ctx.lookup(Registries.DIMENSION_TYPE).getOrThrow(BuiltinDimensionTypes.OVERWORLD), new BetterFlatLevelSource(FlatLevelGeneratorSettings.getDefault(biomeGetter, structureSets, placedFeatures), biomeSource)))));
    }

    public static void registerNether(BootstrapContext<WorldPreset> ctx, ResourceKey<WorldPreset> presetKey, BiomeSource biomeSource, HolderGetter<NoiseGeneratorSettings> noiseSettingsGetter, HolderGetter<Biome> biomeGetter) {
        HolderGetter<PlacedFeature> placedFeatures = ctx.lookup(Registries.PLACED_FEATURE);
        HolderGetter<StructureSet> structureSets = ctx.lookup(Registries.STRUCTURE_SET);
        ctx.register(presetKey, new WorldPreset(Map.of(
                        LevelStem.OVERWORLD, new LevelStem(ctx.lookup(Registries.DIMENSION_TYPE).getOrThrow(BuiltinDimensionTypes.OVERWORLD), new FlatLevelSource(FlatLevelGeneratorSettings.getDefault(biomeGetter, structureSets, placedFeatures))),
                LevelStem.NETHER, new LevelStem(ctx.lookup(Registries.DIMENSION_TYPE).getOrThrow(BuiltinDimensionTypes.NETHER), new NoiseBasedChunkGenerator(biomeSource, noiseSettingsGetter.getOrThrow(NoiseGeneratorSettings.NETHER)))
        )));
    }

    public static void defaultCellular(BootstrapContext<WorldPreset> ctx, ResourceKey<WorldPreset> presetKey, BiomeSource biomeSource, HolderGetter<NoiseGeneratorSettings> noiseSettingsGetter) {
        HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
        ctx.register(presetKey, new WorldPreset(Map.of(
                LevelStem.OVERWORLD, new LevelStem(
                    ctx.lookup(Registries.DIMENSION_TYPE).getOrThrow(BuiltinDimensionTypes.OVERWORLD),
                    new NoiseBasedChunkGenerator(biomeSource, noiseSettingsGetter.getOrThrow(CellworldNoiseSettings.END_CELLULAR))),
                LevelStem.END, new LevelStem(
                        ctx.lookup(Registries.DIMENSION_TYPE).getOrThrow(BuiltinDimensionTypes.END),
                        new NoiseBasedChunkGenerator(biomeSource, noiseSettingsGetter.getOrThrow(CellworldNoiseSettings.END_CELLULAR)))
        )));
    }

    private static ResourceKey<WorldPreset> createKey(String name) {
        return ResourceKey.create(Registries.WORLD_PRESET, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }
}
