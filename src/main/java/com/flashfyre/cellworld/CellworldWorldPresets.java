package com.flashfyre.cellworld;

import com.flashfyre.cellworld.cells.CellEntry;
import com.flashfyre.cellworld.cells.CellMap;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.LevelStem;
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

    public static void bootstrap(BootstrapContext<WorldPreset> ctx) {
        HolderGetter<Biome> biomeGetter = ctx.lookup(Registries.BIOME);

        BiomeSource fixed = new FixedBiomeSource(biomeGetter.getOrThrow(Biomes.MUSHROOM_FIELDS));
        BiomeSource cellular = new CellularBiomeSource(new CellMap(List.of(64, 16),
                new CellEntry(List.of(
                        new CellEntry(List.of(
                                new CellEntry(new Cell(biomeGetter.getOrThrow(Biomes.FROZEN_OCEAN), 0.0f, 0.0f)),
                                new CellEntry(new Cell(biomeGetter.getOrThrow(Biomes.COLD_OCEAN), 0.0f, 0.0f)),
                                new CellEntry(new Cell(biomeGetter.getOrThrow(Biomes.OCEAN), 0.0f, 0.0f)),
                                new CellEntry(new Cell(biomeGetter.getOrThrow(Biomes.LUKEWARM_OCEAN), 0.0f, 0.0f)),
                                new CellEntry(new Cell(biomeGetter.getOrThrow(Biomes.WARM_OCEAN), 0.0f, 0.0f))
                        )),
                        new CellEntry(List.of(
                                new CellEntry(new Cell(biomeGetter.getOrThrow(Biomes.DARK_FOREST), 0.0f, 0.0f)),
                                new CellEntry(new Cell(biomeGetter.getOrThrow(Biomes.PLAINS), 0.0f, 0.0f)),
                                new CellEntry(new Cell(biomeGetter.getOrThrow(Biomes.FLOWER_FOREST), 0.0f, 0.0f))
                        ))
                ))));
        registerFlatWorldPreset(ctx, CELLULAR, cellular, biomeGetter);
    }

    public static void registerWorldPreset(BootstrapContext<WorldPreset> ctx, ResourceKey<WorldPreset> presetKey, BiomeSource biomeSource, ResourceKey<NoiseGeneratorSettings> noiseGenSettings) {
        ctx.register(presetKey, new WorldPreset(Map.of(LevelStem.OVERWORLD, new LevelStem(ctx.lookup(Registries.DIMENSION_TYPE).getOrThrow(BuiltinDimensionTypes.OVERWORLD), new NoiseBasedChunkGenerator(biomeSource, ctx.lookup(Registries.NOISE_SETTINGS).getOrThrow(noiseGenSettings))))));
    }

    public static void registerFlatWorldPreset(BootstrapContext<WorldPreset> ctx, ResourceKey<WorldPreset> presetKey, BiomeSource biomeSource, HolderGetter<Biome> biomeGetter) {
        HolderGetter<PlacedFeature> placedFeatures = ctx.lookup(Registries.PLACED_FEATURE);
        HolderGetter<StructureSet> structureSets = ctx.lookup(Registries.STRUCTURE_SET);
        ctx.register(presetKey, new WorldPreset(Map.of(LevelStem.OVERWORLD, new LevelStem(ctx.lookup(Registries.DIMENSION_TYPE).getOrThrow(BuiltinDimensionTypes.OVERWORLD), new BetterFlatLevelSource(FlatLevelGeneratorSettings.getDefault(biomeGetter, structureSets, placedFeatures), biomeSource)))));
    }

    private static ResourceKey<WorldPreset> createKey(String name) {
        return ResourceKey.create(Registries.WORLD_PRESET, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }
}
