package com.flashfyre.cellworld.registry;

import com.flashfyre.cellworld.Cellworld;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

import java.util.List;
import java.util.function.Supplier;

public class CellworldFeatures {
    public static class Configured {
        public static final ResourceKey<ConfiguredFeature<?,?>> GILDED_BLACKSTONE_ORE = createKey("gilded_blackstone_ore");

        private static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name) {
            return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
        }

        public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> ctx) {
            RuleTest blackstoneRuleTest = new BlockMatchTest(Blocks.BLACKSTONE);
            register(ctx, GILDED_BLACKSTONE_ORE, Feature.ORE, new OreConfiguration(blackstoneRuleTest, Blocks.GILDED_BLACKSTONE.defaultBlockState(), 7));
        }

        private static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<?, ?>> register(BootstrapContext<ConfiguredFeature<?, ?>> ctx, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC config) {
            return ctx.register(key, new ConfiguredFeature<>(feature, config));
        }
    }
    public static class Placed {
        public static final ResourceKey<PlacedFeature> GILDED_BLACKSTONE_ORE = createKey("gilded_blackstone_ore");

        private static ResourceKey<PlacedFeature> createKey(String name) {
            return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
        }

        public static void bootstrap(BootstrapContext<PlacedFeature> ctx) {
            register(ctx, GILDED_BLACKSTONE_ORE, Configured.GILDED_BLACKSTONE_ORE, List.of()); // Add this
        }

        private static Holder<PlacedFeature> register(BootstrapContext<PlacedFeature> ctx, ResourceKey<PlacedFeature> key, ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureKey, List<PlacementModifier> placement) {
            return ctx.register(key, new PlacedFeature(ctx.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(configuredFeatureKey), placement));
        }
    }
}
