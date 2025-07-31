package com.flashfyre.cellworld;

import com.flashfyre.cellworld.cells.*;
import com.flashfyre.cellworld.cells.selector.*;
import com.flashfyre.cellworld.chunkgenerator.BetterFlatLevelSource;
import com.flashfyre.cellworld.levelgen.CellMapHeightDensityFunction;
import com.flashfyre.cellworld.levelgen.SeededEndIslandDensityFunction;
import com.flashfyre.cellworld.registry.*;
import com.flashfyre.cellworld.levelgen.CellMapRuleSource;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.registries.*;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod(Cellworld.MOD_ID)
public class Cellworld {
    public static final String MOD_ID = "cellworld";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<MapCodec<? extends BiomeSource>> BIOME_SOURCES = DeferredRegister.create(Registries.BIOME_SOURCE, Cellworld.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS = DeferredRegister.create(Registries.CHUNK_GENERATOR, Cellworld.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends DensityFunction>> DENSITY_FUNCTION_TYPES = DeferredRegister.create(Registries.DENSITY_FUNCTION_TYPE, Cellworld.MOD_ID);


    public static final DeferredHolder<MapCodec<? extends BiomeSource>, MapCodec<CellularBiomeSource>> CELLULAR = BIOME_SOURCES.register("cellular", () -> CellularBiomeSource.CODEC);

    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<BetterFlatLevelSource>> BETTER_FLAT = CHUNK_GENERATORS.register("cellular", () -> BetterFlatLevelSource.CODEC);

    //public static final DeferredHolder<MapCodec<? extends DensityFunction>, MapCodec<CellMapHeightDensityFunction>> CELL_MAP_DENSITY_FUNCTION = DENSITY_FUNCTION_TYPES.register("cell_map", CellMapHeightDensityFunction.CODEC::codec);

    public static final DeferredHolder<MapCodec<? extends DensityFunction>, MapCodec<SeededEndIslandDensityFunction>> SEEDED_END_ISLAND_DENSITY_FUNCTION = DENSITY_FUNCTION_TYPES.register("seeded_end_islands", SeededEndIslandDensityFunction.CODEC::codec);

    public static final DeferredRegister<MapCodec<? extends SurfaceRules.RuleSource>> MATERIAL_RULES = DeferredRegister.create(Registries.MATERIAL_RULE, Cellworld.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends SurfaceRules.RuleSource>, MapCodec<CellMapRuleSource>> CELL_MAP_RULE = MATERIAL_RULES.register("cell_map", () -> CellMapRuleSource.MAP_CODEC);

    public static final DeferredRegister<MapCodec<? extends CellSelector>> BIOME_CELL_SELECTOR_TYPES = DeferredRegister.create(CellworldRegistries.SELECTOR_TYPE_REGISTRY_KEY, MOD_ID);


    public static final DeferredRegister<MapCodec<? extends LevelParameter>> LEVEL_PARAMETER_TYPES = DeferredRegister.create(CellworldRegistries.LEVEL_PARAMETER_TYPES_REGISTRY_KEY, MOD_ID);
    public static final DeferredHolder<MapCodec<? extends LevelParameter>, MapCodec<? extends LevelParameter>> DIST_FROM_XZ_COORD =
            LEVEL_PARAMETER_TYPES.register("dist_from_xz_coord", () -> LevelParameter.DistFromXZCoord.CODEC);
    public static final DeferredHolder<MapCodec<? extends LevelParameter>, MapCodec<? extends LevelParameter>> HEIGHT =
            LEVEL_PARAMETER_TYPES.register("height", () -> LevelParameter.Height.CODEC);
    public static final DeferredHolder<MapCodec<? extends LevelParameter>, MapCodec<? extends LevelParameter>> ANGLE_FROM_XZ_COORD =
            LEVEL_PARAMETER_TYPES.register("angle_from_xz_coord", () -> LevelParameter.AngleFromXZCoord.CODEC);

    public static final DeferredHolder<MapCodec<? extends LevelParameter>, MapCodec<? extends LevelParameter>> DENSITY_FUNCTION_INPUT =
            LEVEL_PARAMETER_TYPES.register("density_function_input", () -> LevelParameter.DensityFunctionInput.CODEC);







    //public static final DeferredHolder<MapCodec<? extends CellSelectionSet>,MapCodec<? extends CellSelectionSet>> RANDOM_FROM_LIST = SELECTOR_TYPES.register("random_from_list", () -> RandomFromList.CODEC);
    //public static final DeferredHolder<MapCodec<? extends CellSelectionSet>,MapCodec<? extends CellSelectionSet>> RANDOM_FROM_WEIGHTED_LIST = SELECTOR_TYPES.register("random_from_weighted_list", () -> RandomFromWeightedList.CODEC);
    public static final DeferredHolder<MapCodec<? extends CellSelector>,MapCodec<? extends CellSelector>> RANDOM = BIOME_CELL_SELECTOR_TYPES.register("random", () -> RandomSelector.CODEC);
    public static final DeferredHolder<MapCodec<? extends CellSelector>,MapCodec<? extends CellSelector>> WEIGHTED_RANDOM = BIOME_CELL_SELECTOR_TYPES.register("weighted_random", () -> WeightedRandomSelector.CODEC);
    public static final DeferredHolder<MapCodec<? extends CellSelector>,MapCodec<? extends CellSelector>> LEVEL_PARAMETER_VALUE = BIOME_CELL_SELECTOR_TYPES.register("level_parameter_value", () -> LevelParameterValueSelector.CODEC);

    public Cellworld(IEventBus modBus, ModContainer container) {
        modBus.addListener(this::registerRegistries);
        modBus.addListener(this::registerDatapackRegistries);
        modBus.addListener(this::gatherData);
        NeoForge.EVENT_BUS.addListener(this::levelLoad);
        NeoForge.EVENT_BUS.addListener(this::createLevelSpawn);
        DENSITY_FUNCTION_TYPES.register(modBus);
        CellworldFeatures.FEATURES.register(modBus);
        BIOME_SOURCES.register(modBus);
        CHUNK_GENERATORS.register(modBus);
        LEVEL_PARAMETER_TYPES.register(modBus);
        BIOME_CELL_SELECTOR_TYPES.register(modBus);
        MATERIAL_RULES.register(modBus);
    }

    public void levelLoad(LevelEvent.Load event) {
        setWorldSeed(event);
    }

    public void createLevelSpawn(LevelEvent.CreateSpawnPosition event) {
        //setWorldSeed(event);
    }

    private void setWorldSeed(LevelEvent event) {
        if(event.getLevel() instanceof ServerLevel serverLevel) {
            ChunkGenerator generator = serverLevel.getChunkSource().getGenerator();
            if(generator instanceof NoiseBasedChunkGenerator noiseBasedChunkGenerator) {
                RandomState state = serverLevel.getChunkSource().chunkMap.randomState();
                CellworldNoiseWiringHelper wirer = new CellworldNoiseWiringHelper(serverLevel.getSeed(), state, noiseBasedChunkGenerator.generatorSettings().value().useLegacyRandomSource());
                CellSelectionTree tree = serverLevel.registryAccess().registryOrThrow(CellworldRegistries.CELL_SELECTION_TREE_REGISTRY_KEY).get(CellSelectionTree.END);
                tree.initSeeds(wirer);
            }
        }
    }

    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(
                event.includeServer(),
                (DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(
                        output,
                        lookupProvider,
                        new RegistrySetBuilder()
                                .add(Registries.NOISE, CellworldNoises::bootstrap)
                                .add(Registries.NOISE_SETTINGS, CellworldNoiseSettings::cellworldBootstrap)
                                .add(Registries.WORLD_PRESET, CellworldWorldPresets::bootstrap)
                                .add(Registries.CONFIGURED_FEATURE, CellworldFeatures.Configured::bootstrap)
                                .add(Registries.PLACED_FEATURE, CellworldFeatures.Placed::bootstrap)
                                .add(Registries.BIOME, CellworldBiomes::bootstrap)
                                .add(CellworldRegistries.CELL_REGISTRY_KEY, CellworldCells::bootstrap)
                                .add(CellworldRegistries.TERRAIN_CONFIGURED_CELL_REGISTRY_KEY, TerrainAugmentedCell::bootstrap)
                                .add(CellworldRegistries.SINGLE_INT_CONFIGURED_CELL, SingleIntConfiguredCell::bootstrap)
                                .add(CellworldRegistries.CELL_SELECTION_TREE_REGISTRY_KEY, CellSelectionTree::bootstrap),
                        Set.of(MOD_ID)
                )
        );
        // this doesn't work
        /*generator.addProvider(
                event.includeServer(),
                new WeightedCellEntryTagProvider(packOutput, lookupProvider, existingFileHelper)
        );*/
    }

    public void registerRegistries(NewRegistryEvent event) {
        event.register(CellworldRegistries.SELECTOR_TYPE_REGISTRY);
        event.register(CellworldRegistries.LEVEL_PARAMETER_TYPE_REGISTRY);
        //event.register(CellworldRegistries.CELL_SELECTION_TREE_REGISTRY);
    }

    /*public static final DeferredRegister<RuleTestType<?>> RULE_TEST_TYPES = DeferredRegister.create(Registries.RULE_TEST, Cellworld.MOD_ID);

    private static <P extends RuleTest> DeferredHolder<RuleTestType<?>, RuleTestType<P>> register(String name, MapCodec<P> codec) {
        return RULE_TEST_TYPES.register(name, () -> () -> codec);
    }*/

    public void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                CellworldRegistries.CELL_REGISTRY_KEY,
                Cell.DIRECT_CODEC,
                null
        );
        event.dataPackRegistry(
                CellworldRegistries.TERRAIN_CONFIGURED_CELL_REGISTRY_KEY,
                TerrainAugmentedCell.DIRECT_CODEC,
                null
        );
        event.dataPackRegistry(
                CellworldRegistries.SINGLE_INT_CONFIGURED_CELL,
                SingleIntConfiguredCell.DIRECT_CODEC,
                null
        );
        event.dataPackRegistry(
                CellworldRegistries.CELL_SELECTION_TREE_REGISTRY_KEY,
                CellSelectionTree.DIRECT_CODEC,
                null
        );
    }
}
