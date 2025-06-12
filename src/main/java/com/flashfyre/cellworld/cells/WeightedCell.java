package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.Cell;
import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.CellworldBiomes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

public record WeightedCell(CellEntry cell, int weight) {
    public static final Codec<WeightedCell> DIRECT_CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                            CellEntry.CODEC.fieldOf("cell").forGetter(entry -> entry.cell),
                            ExtraCodecs.POSITIVE_INT.fieldOf("weight").forGetter(entry -> entry.weight)
                    )
                    .apply(inst, WeightedCell::new)
    );

    public static final Codec<Holder<WeightedCell>> CODEC = RegistryFileCodec.create(Cellworld.WEIGHTED_CELL_ENTRY_REGISTRY_KEY, DIRECT_CODEC);

    public static final TagKey<WeightedCell> NETHER = create("nether");

    private static TagKey<WeightedCell> create(String name) {
        return TagKey.create(Cellworld.WEIGHTED_CELL_ENTRY_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }

    public static final ResourceKey<WeightedCell> NETHER_WASTES = createKey("nether_wastes");
    public static final ResourceKey<WeightedCell> SOUL_SAND_VALLEY = createKey("soul_sand_valley");
    public static final ResourceKey<WeightedCell> WARPED_FOREST = createKey("warped_forest");
    public static final ResourceKey<WeightedCell> CRIMSON_FOREST = createKey("crimson_forest");
    public static final ResourceKey<WeightedCell> BASALT_DELTAS = createKey("basalt_deltas");
    public static final ResourceKey<WeightedCell> GILDED_DEPTHS = createKey("gilded_depths");

    public static void bootstrap(BootstrapContext<WeightedCell> ctx) {
        HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
        ctx.register(NETHER_WASTES, new WeightedCell(new CellEntry(Cell.of(biomes, Biomes.BASALT_DELTAS)), 100));
        ctx.register(SOUL_SAND_VALLEY, new WeightedCell(new CellEntry(Cell.of(biomes, Biomes.BASALT_DELTAS)), 90));
        ctx.register(WARPED_FOREST, new WeightedCell(new CellEntry(Cell.of(biomes, Biomes.BASALT_DELTAS)), 80));
        ctx.register(CRIMSON_FOREST, new WeightedCell(new CellEntry(Cell.of(biomes, Biomes.CRIMSON_FOREST)), 90));
        ctx.register(BASALT_DELTAS, new WeightedCell(new CellEntry(Cell.of(biomes, Biomes.BASALT_DELTAS)), 90));
        ctx.register(GILDED_DEPTHS, new WeightedCell(new CellEntry(Cell.of(biomes, CellworldBiomes.GILDED_DEPTHS, SurfaceRules.state(Blocks.BLACKSTONE.defaultBlockState()))), 80));
    }

    private static ResourceKey<WeightedCell> createKey(String name) {
        return ResourceKey.create(Cellworld.WEIGHTED_CELL_ENTRY_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }
}
