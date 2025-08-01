package com.flashfyre.cellworld;

import com.flashfyre.cellworld.cells.*;
import com.flashfyre.cellworld.cells.selector.*;
import com.flashfyre.cellworld.chunkgenerator.BetterFlatLevelSource;
import com.flashfyre.cellworld.levelgen.densityfunction.DistToXZCoordFunction;
import com.flashfyre.cellworld.registry.*;
import com.flashfyre.cellworld.levelgen.CellMapRuleSource;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
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

    // Vanilla registries
    public static final DeferredRegister<MapCodec<? extends BiomeSource>> BIOME_SOURCES = DeferredRegister.create(Registries.BIOME_SOURCE, Cellworld.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS = DeferredRegister.create(Registries.CHUNK_GENERATOR, Cellworld.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends DensityFunction>> DENSITY_FUNCTION_TYPES = DeferredRegister.create(Registries.DENSITY_FUNCTION_TYPE, Cellworld.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends SurfaceRules.RuleSource>> MATERIAL_RULES = DeferredRegister.create(Registries.MATERIAL_RULE, Cellworld.MOD_ID);

    // Custom registries
    public static final DeferredRegister<MapCodec<? extends CellSelector>> BIOME_CELL_SELECTOR_TYPES = DeferredRegister.create(CellworldRegistries.SELECTOR_TYPE_REGISTRY_KEY, MOD_ID);


    public static final DeferredHolder<MapCodec<? extends BiomeSource>, MapCodec<CellularBiomeSource>> CELLULAR = BIOME_SOURCES.register("cellular", () -> CellularBiomeSource.CODEC);

    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<BetterFlatLevelSource>> BETTER_FLAT = CHUNK_GENERATORS.register("cellular", () -> BetterFlatLevelSource.CODEC);

    public static final DeferredHolder<MapCodec<? extends DensityFunction>, MapCodec<DistToXZCoordFunction>> DIST_TO_XZ_COORD = DENSITY_FUNCTION_TYPES.register("dist_to_xz_coord", DistToXZCoordFunction.CODEC::codec);

    public static final DeferredHolder<MapCodec<? extends SurfaceRules.RuleSource>, MapCodec<CellMapRuleSource>> CELL_MAP_RULE = MATERIAL_RULES.register("cell_selection_tree", () -> CellMapRuleSource.MAP_CODEC);







    //public static final DeferredHolder<MapCodec<? extends CellSelectionSet>,MapCodec<? extends CellSelectionSet>> RANDOM_FROM_LIST = SELECTOR_TYPES.register("random_from_list", () -> RandomFromList.CODEC);
    //public static final DeferredHolder<MapCodec<? extends CellSelectionSet>,MapCodec<? extends CellSelectionSet>> RANDOM_FROM_WEIGHTED_LIST = SELECTOR_TYPES.register("random_from_weighted_list", () -> RandomFromWeightedList.CODEC);
    public static final DeferredHolder<MapCodec<? extends CellSelector>,MapCodec<? extends CellSelector>> RANDOM = BIOME_CELL_SELECTOR_TYPES.register("random", () -> RandomSelector.CODEC);
    public static final DeferredHolder<MapCodec<? extends CellSelector>,MapCodec<? extends CellSelector>> WEIGHTED_RANDOM = BIOME_CELL_SELECTOR_TYPES.register("weighted_random", () -> WeightedRandomSelector.CODEC);
    public static final DeferredHolder<MapCodec<? extends CellSelector>,MapCodec<? extends CellSelector>> FUNCTION_VALUE = BIOME_CELL_SELECTOR_TYPES.register("function_value", () -> FunctionValueSelector.CODEC);

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
                CellSelectionTree end = serverLevel.registryAccess().registryOrThrow(CellworldRegistries.CELL_SELECTION_TREE_REG_KEY).get(CellSelectionTree.END);
                CellSelectionTree nether = serverLevel.registryAccess().registryOrThrow(CellworldRegistries.CELL_SELECTION_TREE_REG_KEY).get(CellSelectionTree.NETHER);
                end.initSeeds(wirer, serverLevel.getSeed());
                nether.initSeeds(wirer, serverLevel.getSeed());
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
                                .add(CellworldRegistries.SURFACED_BIOME_REG_KEY, CellworldSurfacedBiomes::bootstrap)
                                .add(CellworldRegistries.WEIGHTED_SURFACED_BIOME_REG_KEY, WeightedSurfacedBiome::bootstrap)
                                .add(CellworldRegistries.CELL_SELECTION_TREE_REG_KEY, CellSelectionTree::bootstrap),
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
        //event.register(CellworldRegistries.CELL_SELECTION_TREE_REGISTRY);
    }

    /*public static final DeferredRegister<RuleTestType<?>> RULE_TEST_TYPES = DeferredRegister.create(Registries.RULE_TEST, Cellworld.MOD_ID);

    private static <P extends RuleTest> DeferredHolder<RuleTestType<?>, RuleTestType<P>> register(String name, MapCodec<P> codec) {
        return RULE_TEST_TYPES.register(name, () -> () -> codec);
    }*/

    public void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                CellworldRegistries.SURFACED_BIOME_REG_KEY,
                SurfacedBiome.DIRECT_CODEC,
                null
        );
        event.dataPackRegistry(
                CellworldRegistries.WEIGHTED_SURFACED_BIOME_REG_KEY,
                WeightedSurfacedBiome.DIRECT_CODEC,
                null
        );
        event.dataPackRegistry(
                CellworldRegistries.CELL_SELECTION_TREE_REG_KEY,
                CellSelectionTree.DIRECT_CODEC,
                null
        );
    }
}
