package com.flashfyre.cellworld.registry;

import com.flashfyre.cellworld.Cellworld;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class CellworldNoises {

    public static final ResourceKey<NormalNoise.NoiseParameters> OBSIDIAN_SPIRES_SURFACE = createKey("obsidian_spires_surface");
    public static final ResourceKey<NormalNoise.NoiseParameters> AMETHYST_FIELDS_SURFACE = createKey("amethyst_fields_surface");

    public static void bootstrap(BootstrapContext<NormalNoise.NoiseParameters> ctx) {
        register(ctx, OBSIDIAN_SPIRES_SURFACE, -6, 1.0, 1.0, 2.0, 2.0, 3.0);
        register(ctx, AMETHYST_FIELDS_SURFACE, -3, 1.0, 2.0, 1.0);
    }

    private static void register(
            BootstrapContext<NormalNoise.NoiseParameters> context,
            ResourceKey<NormalNoise.NoiseParameters> key,
            int firstOctave,
            double amplitude,
            double... otherAmplitudes
    ) {
        context.register(key, new NormalNoise.NoiseParameters(firstOctave, amplitude, otherAmplitudes));
    }

    private static ResourceKey<NormalNoise.NoiseParameters> createKey(String key) {
        return ResourceKey.create(Registries.NOISE, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, key));
    }
}
