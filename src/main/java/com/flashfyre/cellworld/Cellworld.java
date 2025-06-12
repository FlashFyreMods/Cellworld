package com.flashfyre.cellworld;

import com.flashfyre.cellworld.cells.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
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

    public static final DeferredHolder<MapCodec<? extends BiomeSource>, MapCodec<CellularBiomeSource>> CELLULAR = BIOME_SOURCES.register("cellular", () -> CellularBiomeSource.CODEC);
    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<BetterFlatLevelSource>> BETTER_FLAT = CHUNK_GENERATORS.register("cellular", () -> BetterFlatLevelSource.CODEC);

    public static final DeferredRegister<MapCodec<? extends SurfaceRules.RuleSource>> MATERIAL_RULES = DeferredRegister.create(Registries.MATERIAL_RULE, Cellworld.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends SurfaceRules.RuleSource>, MapCodec<CellMapRuleSource>> CELL_MAP_RULE = MATERIAL_RULES.register("cell_map", () -> CellMapRuleSource.MAP_CODEC);

    public static final ResourceKey<Registry<MapCodec<? extends CellSelector>>> SELECTOR_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MOD_ID, "cell_entry_picker_types"));
    public static final Registry<MapCodec<? extends CellSelector>> SELECTOR_TYPE_REGISTRY = new RegistryBuilder<>(SELECTOR_TYPE_REGISTRY_KEY).create();

    public static final ResourceKey<Registry<WeightedCell>> WEIGHTED_CELL_ENTRY_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MOD_ID, "weighted_cell_entry"));
    public static final ResourceKey<Registry<CellMap>> CELL_MAP_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MOD_ID, "cell_map"));
    //public static final Registry<RandomFromWeightedList.WeightedCellEntry> WEIGHTED_CELL_ENTRY_REGISTRY = new RegistryBuilder<>(WEIGHTED_CELL_ENTRY_REGISTRY_KEY).create();


    public static final DeferredRegister<MapCodec<? extends CellSelector>> SELECTOR_TYPES = DeferredRegister.create(SELECTOR_TYPE_REGISTRY, MOD_ID);
    public static final DeferredHolder<MapCodec<? extends CellSelector>,MapCodec<? extends CellSelector>> RANDOM_FROM_LIST = SELECTOR_TYPES.register("random_from_list", () -> RandomFromList.CODEC);
    public static final DeferredHolder<MapCodec<? extends CellSelector>,MapCodec<? extends CellSelector>> RANDOM_FROM_WEIGHTED_LIST = SELECTOR_TYPES.register("random_from_weighted_list", () -> RandomFromWeightedList.CODEC);
    public static final DeferredHolder<MapCodec<? extends CellSelector>,MapCodec<? extends CellSelector>> RANDOM_FROM_WEIGHTED_LIST_HOLDERSET = SELECTOR_TYPES.register("random_from_weighted_list_holderset", () -> RandomFromWeightedListHolderSet.CODEC);

    public Cellworld(IEventBus modBus, ModContainer container) {
        modBus.addListener(this::registerRegistries);
        modBus.addListener(this::registerDatapackRegistries);
        modBus.addListener(this::gatherData);
        BIOME_SOURCES.register(modBus);
        CHUNK_GENERATORS.register(modBus);
        SELECTOR_TYPES.register(modBus);
        MATERIAL_RULES.register(modBus);
    }

    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        generator.addProvider(
                event.includeServer(),
                (DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(
                        output,
                        lookupProvider,
                        new RegistrySetBuilder()
                                .add(Registries.NOISE_SETTINGS, CellworldNoiseSettings::bootstrap)
                                .add(Registries.WORLD_PRESET, CellworldWorldPresets::bootstrap)
                                .add(Registries.BIOME, CellworldBiomes::bootstrap)
                                .add(Cellworld.WEIGHTED_CELL_ENTRY_REGISTRY_KEY, WeightedCell::bootstrap)
                                .add(Cellworld.CELL_MAP_REGISTRY_KEY, CellMap::bootstrap),
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
        event.register(SELECTOR_TYPE_REGISTRY);
    }

    /*public static final DeferredRegister<RuleTestType<?>> RULE_TEST_TYPES = DeferredRegister.create(Registries.RULE_TEST, Cellworld.MOD_ID);

    private static <P extends RuleTest> DeferredHolder<RuleTestType<?>, RuleTestType<P>> register(String name, MapCodec<P> codec) {
        return RULE_TEST_TYPES.register(name, () -> () -> codec);
    }*/

    public void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                WEIGHTED_CELL_ENTRY_REGISTRY_KEY,
                WeightedCell.DIRECT_CODEC,
                null
        );
        event.dataPackRegistry(
                CELL_MAP_REGISTRY_KEY,
                CellMap.DIRECT_CODEC,
                null
        );
    }
}
