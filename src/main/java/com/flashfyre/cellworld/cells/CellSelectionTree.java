package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.CellworldNoiseWiringHelper;
import com.flashfyre.cellworld.cells.selector.CellSelector;
import com.flashfyre.cellworld.cells.selector.LevelParameter;
import com.flashfyre.cellworld.cells.selector.LevelParameterValueSelector;
import com.flashfyre.cellworld.cells.selector.RandomSelector;
import com.flashfyre.cellworld.levelgen.SeededEndIslandDensityFunction;
import com.flashfyre.cellworld.registry.CellworldCells;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.*;
import java.util.stream.Stream;

public record CellSelectionTree(List<Integer> layerScales, Pair<Integer,CellSelector> initialLayer, Optional<Map<String, Pair<Integer, CellTreeElement>>> layers) {

    public CellSelectionTree(int scale, CellSelector selector) {
        this(List.of(scale), new Pair<>(0, selector), Optional.empty());
    }

    public static final Codec<CellSelectionTree> DIRECT_CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    ExtraCodecs.POSITIVE_INT.listOf().fieldOf("layer_scales").forGetter(CellSelectionTree::layerScales),
                    Codec.mapPair(Codec.INT.fieldOf("scale_index"), CellSelector.CODEC.fieldOf("selector")).codec().fieldOf("first_layer").forGetter(tree -> tree.initialLayer),
                    Codec.unboundedMap(Codec.string(1, 32), Codec.mapPair(Codec.INT.fieldOf("scale_index"), CellTreeElement.CODEC.codec().fieldOf("element")).codec()).optionalFieldOf("layers").forGetter(CellSelectionTree::layers)
                    //Codec.mapPair(Codec.INT.fieldOf("scale_index"), Codec.unboundedMap(Codec.string(1, 32), CellTreeElement.CODEC.codec()).fieldOf("element")).codec().listOf().optionalFieldOf("layers").forGetter(cellMap -> cellMap.layers)
            ).apply(inst, CellSelectionTree::new)
    );

    public static final Codec<Holder<CellSelectionTree>> CODEC = RegistryFileCodec.create(CellworldRegistries.CELL_SELECTION_TREE_REGISTRY_KEY, DIRECT_CODEC);

    public Cell getCell(int x, int z) {

        /*NormalNoise shift = NormalNoise.create(new WorldgenRandom(new LegacyRandomSource(0)), -5, 1.0, 0.5);
        int multiplier = 15;
        int xOffset = (int) (shift.getValue(x, 0, z) * multiplier);
        int zOffset = (int) (shift.getValue(x, 100, z) * multiplier);

        System.out.println("xo: "+xOffset);
        System.out.println("zo: "+zOffset);*/

        return this.resolveCell(this.initialLayer.getSecond(), x, z);
    }

    public Stream<Holder<Cell>> streamCells() {

        Stream<Holder<Cell>> base = this.initialLayer.getSecond().streamCells();

        if(this.layers.isEmpty()) {
            return base;
        }

        Stream<Holder<Cell>> layered = this.layers.orElseThrow().values().stream().flatMap(p -> p.getSecond().stream());

        return Stream.concat(base, layered);
    }

    private Cell resolveCell(CellSelector selector, int x, int z) {
        List<BlockPos> nucleiiPositions = new ArrayList<>();
        BlockPos nucleusPos = new BlockPos(x, 0, z);
        BlockPos originalBlockPos = new BlockPos(x, 0, z);
        if(this.layers.isPresent()) {
            for(int i = this.layerScales.size()-1; i>=0; i--) { // Iterate from smallest to largest
                nucleusPos = getClosestNucleus(nucleusPos.getX(), nucleusPos.getZ(), this.layerScales.get(i));
                nucleiiPositions.add(nucleusPos);
            }
        }

        nucleusPos = getClosestNucleus(nucleusPos.getX(), nucleusPos.getZ(), this.layerScales.getFirst());
        nucleiiPositions.add(nucleusPos); // Now we have a nucleii position for each cell size, to get cell info
        nucleiiPositions = nucleiiPositions.reversed(); //now list is from largest to smallest

        // If initial layer is set to -1, don't use cellular sampling
        // If it is postive, start at the specified layer index

        BlockPos seedPos = this.initialLayer.getFirst() < 0 ? originalBlockPos : nucleiiPositions.get(initialLayer.getFirst());


        RandomSource r = new LegacyRandomSource(0);
        r.setSeed(BlockPos.asLong(seedPos.getX(), 0, seedPos.getZ()));
        LevelParameter.CellContext ctx = new LevelParameter.CellContext(r, seedPos.getX(), 0, seedPos.getZ());

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
                ctx = new LevelParameter.CellContext(r, seedPos.getX(), 0, seedPos.getZ()); // Update the context
            }
        }
        return currentElement.getCell().orElseThrow().value();
    }

    public void initSeeds(CellworldNoiseWiringHelper noiseWirer) {
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
            if(selector instanceof LevelParameterValueSelector valueSelector) {
                if(valueSelector.getParameter() instanceof LevelParameter.DensityFunctionInput densityFunctionInput) {
                    densityFunctionInput.wireNoise(noiseWirer);
                }
            }
            for (CellTreeElement e : selector.elements()) {
                initSeeds(e, noiseWirer);
            }
        }
    }

    private static BlockPos getClosestNucleus(int blockX, int blockZ, int cellSize) {
        // We need to get the corner of the current square cell that we are in
        int cellX = Math.floorDiv(blockX, cellSize);
        int cellZ = Math.floorDiv(blockZ, cellSize);
        Random r = new Random();
        XoroshiroRandomSource rand = new XoroshiroRandomSource(0);
        PositionalRandomFactory factory = rand.forkPositional();
        double distToClosestCellCentre = Double.MAX_VALUE;
        BlockPos.MutableBlockPos closestCellCentrePos = new BlockPos.MutableBlockPos();
        // We need to check the distances to adjacent cellSelector as well as our current cell
        for (int currentCellX = cellX - 1; currentCellX <= cellX + 1; currentCellX++) {
            for (int currentCellZ = cellZ - 1; currentCellZ <= cellZ + 1; currentCellZ++) {
                // Seed the rng using the blockpos of the cell being checked currently - this ensures the random centre is always calculated the same.
                r.setSeed(BlockPos.asLong((currentCellX * cellSize), 0, (currentCellZ * cellSize)));
                RandomSource randomSource = factory.at(currentCellX * cellSize, 0, currentCellZ * cellSize);
                int cellCentreOffsetX = randomSource.nextInt(cellSize);
                int cellCentreOffsetZ = randomSource.nextInt(cellSize);
                // We need to convert the cell centre to a world coordinate
                int xCentreWorld = currentCellX * cellSize + cellCentreOffsetX;
                int zCentreWorld = currentCellZ * cellSize + cellCentreOffsetZ;

                // Get the distance from the current block to the cell centre currently being checked in the loop
                double dist = Mth.square(blockX - xCentreWorld) + Mth.square(blockZ - zCentreWorld);

                // Compare with existing distances - overwrite if the current centre is closer
                if(dist < distToClosestCellCentre) {
                    distToClosestCellCentre = dist;
                    closestCellCentrePos.set(xCentreWorld, 0, zCentreWorld);
                }
            }
        }
        return closestCellCentrePos.immutable();
    }

    public static final ResourceKey<CellSelectionTree> END = createKey("end");

    public static void bootstrap(BootstrapContext<CellSelectionTree> ctx) {
        HolderGetter<SingleIntConfiguredCell> weightedCellEntries = ctx.lookup(CellworldRegistries.SINGLE_INT_CONFIGURED_CELL);
        HolderGetter<Cell> cells = ctx.lookup(CellworldRegistries.CELL_REGISTRY_KEY);
        /*ctx.register(END, new CellSelectionTree(List.of(360, 180, 90, 45, 22, 11), new Pair<>(0, new LevelParameterValueSelector(
                new LevelParameter.DistFromXZCoord(0, 0),
                List.of(
                        new Pair<>(900f, CellTreeElement.cell(cells.getOrThrow(CellworldCells.THE_END))),
                        new Pair<>(1000f, CellTreeElement.cell(cells.getOrThrow(CellworldCells.SMALL_END_ISLANDS)))
                ),
                CellTreeElement.selector(new RandomSelector(List.of(
                        CellTreeElement.cell(cells.getOrThrow(CellworldCells.END_HIGHLANDS)),
                        CellTreeElement.cell(cells.getOrThrow(CellworldCells.OBSIDIAN_SPIRES)),
                        CellTreeElement.cell(cells.getOrThrow(CellworldCells.AMETHYST_FIELDS)))))
        )), Optional.empty()));*/

        ctx.register(END, new CellSelectionTree(List.of(360, 180, 90, 45, 22, 11), new Pair<>(-1, new LevelParameterValueSelector(
                new LevelParameter.DensityFunctionInput(DensityFunctions.flatCache(DensityFunctions.endIslands(0))),
                List.of(
                        new Pair<>(-0.22f, CellTreeElement.cell(cells.getOrThrow(CellworldCells.SMALL_END_ISLANDS)))
                ),
                CellTreeElement.subtree("land_biomes")
        )), Optional.of(
                Map.of("land_biomes", new Pair<>(0, CellTreeElement.selector(new RandomSelector(List.of(
                        CellTreeElement.cell(cells.getOrThrow(CellworldCells.END_HIGHLANDS)),
                        CellTreeElement.cell(cells.getOrThrow(CellworldCells.OBSIDIAN_SPIRES)),
                        CellTreeElement.cell(cells.getOrThrow(CellworldCells.AMETHYST_FIELDS))
                )))))
        )));

    }


    private static ResourceKey<CellSelectionTree> createKey(String name) {
        return ResourceKey.create(CellworldRegistries.CELL_SELECTION_TREE_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }
}
