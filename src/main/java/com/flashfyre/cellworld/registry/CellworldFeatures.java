package com.flashfyre.cellworld.registry;

import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.levelgen.feature.CrystalClusterFeature;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.EndFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Optional;

public class CellworldFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, Cellworld.MOD_ID);
    public static final DeferredHolder<Feature<? extends FeatureConfiguration>, CrystalClusterFeature> CRYSTAL_CLUSTER = FEATURES.register("crystal_cluster", CrystalClusterFeature::new);

    public static class Configured {
        public static final ResourceKey<ConfiguredFeature<?,?>> GILDED_BLACKSTONE_ORE = createKey("gilded_blackstone_ore");
        public static final ResourceKey<ConfiguredFeature<?,?>> OBSIDIAN_SPIRE = createKey("obsidian_spire");

        public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> ctx) {
            RuleTest blackstoneRuleTest = new BlockMatchTest(Blocks.BLACKSTONE);
            register(ctx, GILDED_BLACKSTONE_ORE, Feature.ORE, new OreConfiguration(blackstoneRuleTest, Blocks.GILDED_BLACKSTONE.defaultBlockState(), 7));
            register(ctx, OBSIDIAN_SPIRE, CellworldFeatures.CRYSTAL_CLUSTER.get(), new CrystalClusterFeature.CrystalClusterConfig(
                    UniformInt.of(4, 51), 0.3F,
                    twoWeighted(Blocks.OBSIDIAN, 5, Blocks.CRYING_OBSIDIAN, 1),
                    Optional.empty()));
        }

        private static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name) {
            return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
        }

        private static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<?, ?>> register(BootstrapContext<ConfiguredFeature<?, ?>> ctx, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC config) {
            return ctx.register(key, new ConfiguredFeature<>(feature, config));
        }
    }
    public static class Placed {
        public static final ResourceKey<PlacedFeature> GILDED_BLACKSTONE_ORE = createKey("gilded_blackstone_ore");
        public static final ResourceKey<PlacedFeature> OBSIDIAN_SPIRE = createKey("obsidian_spire");
        public static final ResourceKey<PlacedFeature> CHORUS_PLANT_SPARSE = createKey("chorus_plant_sparse");

        public static void bootstrap(BootstrapContext<PlacedFeature> ctx) {
            //register(ctx, GILDED_BLACKSTONE_ORE, Configured.GILDED_BLACKSTONE_ORE, List.of()); // Add this

            register(ctx, OBSIDIAN_SPIRE, Configured.OBSIDIAN_SPIRE, commonSurface(1, PlacementUtils.HEIGHTMAP));
            register(ctx, CHORUS_PLANT_SPARSE, EndFeatures.CHORUS_PLANT, rareSurface(2, PlacementUtils.HEIGHTMAP));
        }

        private static ResourceKey<PlacedFeature> createKey(String name) {
            return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
        }

        private static Holder<PlacedFeature> register(BootstrapContext<PlacedFeature> ctx, ResourceKey<PlacedFeature> key, ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureKey, List<PlacementModifier> placement) {
            return ctx.register(key, new PlacedFeature(ctx.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(configuredFeatureKey), placement));
        }
    }

    private static <B extends Block> SimpleStateProvider simple(Block block) {
        return BlockStateProvider.simple(block);
    }

    private static <B extends Block> WeightedStateProvider twoWeighted(Block blockA, int aWeight, Block blockB, int bWeight) {
        return new WeightedStateProvider(new SimpleWeightedRandomList.Builder<BlockState>().add(blockA.defaultBlockState(), aWeight).add(blockB.defaultBlockState(), bWeight).build());
    }

    private static List<PlacementModifier> commonSurface(int count, PlacementModifier heightmapPlacement) {
        return List.of(CountPlacement.of(count), InSquarePlacement.spread(), heightmapPlacement, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonSurface(IntProvider count, PlacementModifier heightmapPlacement) {
        return List.of(CountPlacement.of(count), InSquarePlacement.spread(), heightmapPlacement, BiomeFilter.biome());
    }

    private static List<PlacementModifier> rareSurface(int chance, PlacementModifier heightmapPlacement) {
        return List.of(RarityFilter.onAverageOnceEvery(chance), InSquarePlacement.spread(), heightmapPlacement, BiomeFilter.biome());
    }
}
