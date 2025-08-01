package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.CellUtil;
import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.CellworldNoiseWiringHelper;
import com.flashfyre.cellworld.cells.selector.CellSelector;
import com.flashfyre.cellworld.cells.selector.FunctionValueSelector;
import com.flashfyre.cellworld.cells.selector.RandomSelector;
import com.flashfyre.cellworld.cells.selector.WeightedRandomSelector;
import com.flashfyre.cellworld.levelgen.densityfunction.DistToXZCoordFunction;
import com.flashfyre.cellworld.registry.CellworldSurfacedBiomes;
import com.flashfyre.cellworld.registry.CellworldRegistries;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.LegacyRandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class CellSelectionTree {

    private final List<Integer> layerScales;
    private final Pair<Integer, CellSelector> initialLayer;
    private final Optional<Map<String, Pair<Integer, CellTreeElement>>> layers;
    private long seed = 0;

    public CellSelectionTree(List<Integer> layerScales, Pair<Integer, CellSelector> initialLayer, Optional<Map<String, Pair<Integer, CellTreeElement>>> layers) {
        this.layerScales = layerScales;
        this.initialLayer = initialLayer;
        this.layers = layers;
    }

    public CellSelectionTree(int scale, CellSelector selector) {
        this(List.of(scale), new Pair<>(0, selector), Optional.empty());
    }

    public CellSelectionTree(List<Integer> layerScales, int intialLayerIndex, CellSelector initialLayer, Map<String, Pair<Integer, CellTreeElement>> layers) {
        this(layerScales, new Pair<>(intialLayerIndex, initialLayer), Optional.of(layers));
    }

    public CellSelectionTree(List<Integer> layerScales, int intialLayerIndex, CellSelector initialLayer) {
        this(layerScales, new Pair<>(intialLayerIndex, initialLayer), Optional.empty());
    }

    public static final Codec<CellSelectionTree> DIRECT_CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    ExtraCodecs.POSITIVE_INT.listOf().fieldOf("layer_scales").forGetter(t -> t.layerScales),
                    Codec.mapPair(Codec.INT.fieldOf("scale_index"), CellSelector.CODEC.fieldOf("selector")).codec().fieldOf("first_layer").forGetter(t -> t.initialLayer),
                    Codec.unboundedMap(Codec.string(1, 32), Codec.mapPair(Codec.INT.fieldOf("scale_index"), CellTreeElement.CODEC.codec().fieldOf("element")).codec()).optionalFieldOf("layers").forGetter(t -> t.layers)
            ).apply(inst, CellSelectionTree::new)
    );

    public static final Codec<Holder<CellSelectionTree>> CODEC = RegistryFileCodec.create(CellworldRegistries.CELL_SELECTION_TREE_REG_KEY, DIRECT_CODEC);

    public SurfacedBiome getCell(int x, int z) {

        /*NormalNoise shift = NormalNoise.create(new WorldgenRandom(new LegacyRandomSource(0)), -5, 1.0, 0.5);
        int multiplier = 15;
        int xOffset = (int) (shift.getValue(x, 0, z) * multiplier);
        int zOffset = (int) (shift.getValue(x, 100, z) * multiplier);

        System.out.println("xo: "+xOffset);
        System.out.println("zo: "+zOffset);*/

        return this.resolveCell(this.initialLayer.getSecond(), x, z);
    }

    /**
     * This method exists to be called by BiomeSource#collectPossibleBiomes
     * so that all biomes within entries can be collected
     *
     * @returns A stream of all entries used in the tree
     */
    public Stream<Holder<SurfacedBiome>> streamCells() {
        Stream<Holder<SurfacedBiome>> base = this.initialLayer.getSecond().streamCells();
        if(this.layers.isEmpty()) {
            return base;
        }

        Stream<Holder<SurfacedBiome>> layered = this.layers.orElseThrow().values().stream().flatMap(p -> p.getSecond().stream());

        return Stream.concat(base, layered);
    }



    private SurfacedBiome resolveCell(CellSelector selector, int x, int z) {
        List<BlockPos> nucleiiPositions = new ArrayList<>();
        BlockPos nucleusPos = new BlockPos(x, 0, z);
        BlockPos originalBlockPos = new BlockPos(x, 0, z);
        for(int i = this.layerScales.size()-1; i>=0; i--) { // Iterate from smallest to largest
            nucleusPos = CellUtil.getClosestNucleus(nucleusPos.getX(), nucleusPos.getZ(), this.layerScales.get(i), this.seed);
            nucleiiPositions.add(nucleusPos);
        }

        nucleusPos = CellUtil.getClosestNucleus(nucleusPos.getX(), nucleusPos.getZ(), this.layerScales.getFirst(), this.seed);
        nucleiiPositions.add(nucleusPos); // Now we have a nucleii position for each cell size, to get cell info
        nucleiiPositions = nucleiiPositions.reversed(); //now list is from largest to smallest

        // If initial layer is set to -1, don't use cellular sampling
        // If it is postive, start at the specified layer index

        BlockPos seedPos = this.initialLayer.getFirst() < 0 ? originalBlockPos : nucleiiPositions.get(initialLayer.getFirst());


        RandomSource r = new LegacyRandomSource(0);
        r.setSeed(BlockPos.asLong(seedPos.getX(), 0, seedPos.getZ()));
        PositionalContext ctx = new PositionalContext(r, seedPos.getX(), 0, seedPos.getZ());

        CellTreeElement currentElement = selector.get(ctx);
        while (currentElement.getSelectorOrSubtreeKey().isPresent()) { // If the node is a branch
            if(currentElement.getSelector().isPresent()) { // If the node is a selector
                currentElement = currentElement.getSelector().orElseThrow().get(ctx);
            } else { // If the node is a subtree key
                Pair<Integer, String> subtreeInfo = currentElement.getSubtreeKey().orElseThrow(); // We get the layer and key of the subtree to lookup

                int layerIndex = this.layers.orElseThrow().get(subtreeInfo.getSecond()).getFirst();

                currentElement = this.layers.orElseThrow().get(subtreeInfo.getSecond()).getSecond(); // We lookup the subtree to get its element and update the current element

                seedPos = layerIndex < 0 ? originalBlockPos : nucleiiPositions.get(layerIndex); // Update the nucleus pos that we are now at, to the one sampled earlier
                r.setSeed(BlockPos.asLong(seedPos.getX(), 0, seedPos.getZ()));
                ctx = new PositionalContext(r, seedPos.getX(), 0, seedPos.getZ()); // Update the context
            }
        }
        return currentElement.getCell().orElseThrow().value();
    }

    public void initSeeds(CellworldNoiseWiringHelper noiseWirer, long seed) {
        this.seed = seed;
        CellTreeElement initialLayerElement = CellTreeElement.selector(this.initialLayer.getSecond());
        initSeeds(initialLayerElement, noiseWirer);
        if(this.layers.isPresent()) {
            for(Pair<Integer, CellTreeElement> p : this.layers.orElseThrow().values()) {
                initSeeds(p.getSecond(), noiseWirer);
            }
        }

    }

    private void initSeeds(CellTreeElement element, CellworldNoiseWiringHelper noiseWirer) {
        if(element.getSelector().isPresent()) {
            CellSelector selector = element.getSelector().orElseThrow();
            if(selector instanceof FunctionValueSelector valueSelector) {
                valueSelector.wireNoise(noiseWirer);
            }
            for (CellTreeElement e : selector.elements()) {
                initSeeds(e, noiseWirer);
            }
        }
    }





    public static final ResourceKey<CellSelectionTree> END = createKey("end");
    public static final ResourceKey<CellSelectionTree> NETHER = createKey("nether");

    public static void bootstrap(BootstrapContext<CellSelectionTree> ctx) {
        HolderGetter<WeightedSurfacedBiome> weightedSurfacedBiomes = ctx.lookup(CellworldRegistries.WEIGHTED_SURFACED_BIOME_REG_KEY);
        HolderGetter<SurfacedBiome> surfacedBiomes = ctx.lookup(CellworldRegistries.SURFACED_BIOME_REG_KEY);
        /*ctx.register(END, new CellSelectionTree(List.of(360, 180, 90, 45, 22, 11), new Pair<>(0, new LevelParameterValueSelector(
                new LevelParameter.DistFromXZCoord(0, 0),
                List.of(
                        new Pair<>(900f, CellTreeElement.cell(entries.getOrThrow(CellworldCells.THE_END))),
                        new Pair<>(1000f, CellTreeElement.cell(entries.getOrThrow(CellworldCells.SMALL_END_ISLANDS)))
                ),
                CellTreeElement.selector(new RandomSelector(List.of(
                        CellTreeElement.cell(entries.getOrThrow(CellworldCells.END_HIGHLANDS)),
                        CellTreeElement.cell(entries.getOrThrow(CellworldCells.OBSIDIAN_SPIRES)),
                        CellTreeElement.cell(entries.getOrThrow(CellworldCells.AMETHYST_FIELDS)))))
        )), Optional.empty()));*/

        ctx.register(END, new CellSelectionTree(
                List.of(360, 180, 90, 45, 22, 11),
                -1, new FunctionValueSelector(
                        DistToXZCoordFunction.zero(),
                        CellTreeElement.cell(surfacedBiomes, CellworldSurfacedBiomes.THE_END), 850f,
                        CellTreeElement.selector(new FunctionValueSelector(
                            DensityFunctions.flatCache(DensityFunctions.endIslands(0)),
                            CellTreeElement.cell(surfacedBiomes, CellworldSurfacedBiomes.SMALL_END_ISLANDS), -0.3f,
                            CellTreeElement.subtree("land_biomes")
        ))), Map.of("land_biomes", new Pair<>(0, CellTreeElement.selector(new FunctionValueSelector(
                DistToXZCoordFunction.zero(),
                CellTreeElement.cell(surfacedBiomes, CellworldSurfacedBiomes.END_HIGHLANDS), 1500f,
                CellTreeElement.selector(WeightedRandomSelector.holderSet((weightedSurfacedBiomes.getOrThrow(WeightedSurfacedBiome.OUTER_END))))
                ))
        ))));

        ctx.register(NETHER, new CellSelectionTree(
                List.of(240, 120, 60, 30, 15, 7),
                0, WeightedRandomSelector.holderSet(weightedSurfacedBiomes.getOrThrow(WeightedSurfacedBiome.NETHER))
        ));

    }


    private static ResourceKey<CellSelectionTree> createKey(String name) {
        return ResourceKey.create(CellworldRegistries.CELL_SELECTION_TREE_REG_KEY, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }

    public record PositionalContext(RandomSource rand, int nucleusBlockX, int nucleusBlockY, int nucleusBlockZ) { }
}
