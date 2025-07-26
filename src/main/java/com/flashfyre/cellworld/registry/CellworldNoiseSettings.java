package com.flashfyre.cellworld.registry;

import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.levelgen.CellworldNoiseRouting;
import com.flashfyre.cellworld.levelgen.CellworldSurfaceRules;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.*;

import java.util.List;

public class CellworldNoiseSettings {

    public static final ResourceKey<NoiseGeneratorSettings> FLAT = createKey("flat");
    public static final ResourceKey<NoiseGeneratorSettings> NETHER_CELLULAR = createKey("nether_cellular");

    private static ResourceKey<NoiseGeneratorSettings> createKey(String name) {
        return ResourceKey.create(Registries.NOISE_SETTINGS, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }

    public static void bootstrap(BootstrapContext<NoiseGeneratorSettings> ctx) {
        ctx.register(FLAT, flatEnd(ctx));
    }

    /*public static NoiseGeneratorSettings flatNether(BootstrapContext<NoiseGeneratorSettings> ctx) {
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
    }*/

    public static NoiseGeneratorSettings flatEnd(BootstrapContext<NoiseGeneratorSettings> ctx) {
        return new NoiseGeneratorSettings(
                NoiseSettings.create(0, 128, 1, 2),
                Blocks.END_STONE.defaultBlockState(),
                Blocks.LAVA.defaultBlockState(),
                //NoiseRouterData.nether(context.lookup(Registries.DENSITY_FUNCTION), context.lookup(Registries.NOISE)),
                CellworldNoiseRouting.flat(ctx.lookup(Registries.DENSITY_FUNCTION), ctx.lookup(Registries.NOISE)),
                CellworldSurfaceRules.end(ctx),
                List.of(),
                32,
                false,
                false,
                false,
                true
        );
    }

    /*public static NoiseGeneratorSettings cellNether(BootstrapContext<NoiseGeneratorSettings> ctx) {
        return new NoiseGeneratorSettings(
                NoiseSettings.create(0, 128, 1, 2),
                Blocks.NETHERRACK.defaultBlockState(),
                Blocks.LAVA.defaultBlockState(),
                NoiseRouterData.nether(ctx.lookup(Registries.DENSITY_FUNCTION), ctx.lookup(Registries.NOISE)),
                //CellworldNoiseRouting.flat(ctx.lookup(Registries.DENSITY_FUNCTION), ctx.lookup(Registries.NOISE)),
                CellworldSurfaceRules.nether(ctx),
                List.of(),
                32,
                false,
                false,
                false,
                true
        );
    }*/




}
