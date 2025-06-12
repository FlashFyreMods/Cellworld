package com.flashfyre.cellworld;

import com.flashfyre.cellworld.cells.*;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.List;

public class CellworldNoiseSettings {

    public static final ResourceKey<NoiseGeneratorSettings> FLAT = createKey("flat");

    private static ResourceKey<NoiseGeneratorSettings> createKey(String name) {
        return ResourceKey.create(Registries.NOISE_SETTINGS, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }

    public static void bootstrap(BootstrapContext<NoiseGeneratorSettings> ctx) {
        ctx.register(FLAT, flatNether(ctx));
    }

    public static NoiseGeneratorSettings flatNether(BootstrapContext<NoiseGeneratorSettings> ctx) {
        HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
        return new NoiseGeneratorSettings(
                NoiseSettings.create(0, 128, 1, 2),
                Blocks.NETHERRACK.defaultBlockState(),
                Blocks.LAVA.defaultBlockState(),
                //NoiseRouterData.nether(context.lookup(Registries.DENSITY_FUNCTION), context.lookup(Registries.NOISE)),
                CellworldNoiseRouting.flat(ctx.lookup(Registries.DENSITY_FUNCTION), ctx.lookup(Registries.NOISE)),
                CellworldSurfaceRules.nether(ctx),
                List.of(),
                32,
                false,
                false,
                false,
                true
        );
    }




}
