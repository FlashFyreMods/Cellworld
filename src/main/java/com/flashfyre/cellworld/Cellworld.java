package com.flashfyre.cellworld;

import com.flashfyre.cellworld.cells.CellSelector;
import com.flashfyre.cellworld.cells.SelectorType;
import com.flashfyre.cellworld.cells.RandomFromList;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.*;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod(Cellworld.MOD_ID)
public class Cellworld {
    public static final String MOD_ID = "cellworld";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<MapCodec<? extends BiomeSource>> BIOME_SOURCES = DeferredRegister.create(Registries.BIOME_SOURCE, Cellworld.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS = DeferredRegister.create(Registries.CHUNK_GENERATOR, Cellworld.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends BiomeSource>, MapCodec<CellularBiomeSource>> CELLULAR = BIOME_SOURCES.register("cellular", () -> CellularBiomeSource.CODEC);
    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<BetterFlatLevelSource>> BETTER_FLAT = CHUNK_GENERATORS.register("cellular", () -> BetterFlatLevelSource.CODEC);

    public static final ResourceKey<Registry<SelectorType<? extends CellSelector>>> SELECTOR_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MOD_ID, "cell_entry_picker_types"));
    public static final Registry<SelectorType<? extends CellSelector>> SELECTOR_TYPE_REGISTRY = new RegistryBuilder<>(SELECTOR_TYPE_REGISTRY_KEY).create();


    public static final DeferredRegister<SelectorType<? extends CellSelector>> SELECTOR_TYPES = DeferredRegister.create(SELECTOR_TYPE_REGISTRY, MOD_ID);
    public static final DeferredHolder<SelectorType<? extends CellSelector>, SelectorType<RandomFromList>> RANDOM_FROM_LIST = SELECTOR_TYPES.register("random_from_list", () -> RandomFromList.CODEC);

    public Cellworld(IEventBus modBus, ModContainer container) {
        modBus.addListener(this::registerRegistries);
        modBus.addListener(this::gatherData);
        BIOME_SOURCES.register(modBus);
        CHUNK_GENERATORS.register(modBus);
        SELECTOR_TYPES.register(modBus);
    }

    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        PackOutput packOutput = generator.getPackOutput();
        generator.addProvider(event.includeClient(), new CellworldDataProvider(packOutput, lookupProvider));
    }

    public void registerRegistries(NewRegistryEvent event) {
        event.register(SELECTOR_TYPE_REGISTRY);
    }
}
