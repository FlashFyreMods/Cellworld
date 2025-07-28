package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.Cellworld;
import com.flashfyre.cellworld.cells.selector.CellSelector;
import com.flashfyre.cellworld.cells.selector.LevelParameter;
import com.flashfyre.cellworld.cells.selector.LevelParameterValueSelector;
import com.flashfyre.cellworld.cells.selector.RandomSelector;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.*;
import java.util.stream.Stream;

public record CellSelectionTree(Pair<Integer,CellSelector> initialLayer, Optional<List<Pair<Integer, Optional<Map<String, CellTreeElement>>>>> layers) {

    public CellSelectionTree(int scale, CellSelector selector) {
        this(new Pair<>(scale, selector), Optional.empty());
    }

    public CellSelectionTree(int scale, CellSelector selector, Optional<List<Pair<Integer, Optional<Map<String, CellTreeElement>>>>> layers) {
        this(new Pair<>(scale, selector), layers);
    }

    public static final Codec<CellSelectionTree> DIRECT_CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.mapPair(Codec.INT.fieldOf("first_layer_scale"), CellSelector.CODEC.fieldOf("selector")).codec().fieldOf("first_layer").forGetter(tree -> tree.initialLayer),
                    Codec.mapPair(Codec.INT.fieldOf("layer_scale"), Codec.unboundedMap(Codec.string(1, 32), CellTreeElement.CODEC.codec()).optionalFieldOf("element")).codec().listOf().optionalFieldOf("layers").forGetter(cellMap -> cellMap.layers)
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

        Stream<Holder<Cell>> layered = this.layers.orElseThrow().stream().filter(e -> e.getSecond().isPresent()).flatMap(p -> p.getSecond().orElseThrow().values().stream().flatMap(element -> {
                if (element.getCell().isPresent()) {
                    return Stream.of(element.getCell().orElseThrow());
                }
                else if(element.getSelector().isPresent()) {
                    return element.getSelector().get().streamCells();
                } else {
                    return Stream.of();
                }
        }));

        return Stream.concat(base, layered);
    }

    private Cell resolveCell(CellSelector selector, int x, int z) {

        List<BlockPos> nucleiiPositions = new ArrayList<>();
        BlockPos nucleusPos = new BlockPos(x, 0, z);
        if(this.layers.isPresent()) {
            nucleiiPositions.add(nucleusPos);
            for(Pair<Integer, Optional<Map<String, CellTreeElement>>> pair : this.layers.orElseThrow().reversed()) { // Iterate from smallest to largest
                nucleusPos = getClosestNucleus(nucleusPos.getX(), nucleusPos.getZ(), pair.getFirst());
                nucleiiPositions.add(nucleusPos);
            }
        }

        nucleusPos = getClosestNucleus(nucleusPos.getX(), nucleusPos.getZ(), this.initialLayer.getFirst());
        nucleiiPositions.add(nucleusPos);
        nucleiiPositions = nucleiiPositions.reversed(); //now list is from largest to smallest, position 0 being the initial layer



        RandomSource r = new LegacyRandomSource(0);
        r.setSeed(BlockPos.asLong(nucleusPos.getX(), 0, nucleusPos.getZ()));
        LevelParameter.CellContext ctx = new LevelParameter.CellContext(r, nucleusPos.getX(), 0, nucleusPos.getZ());

        CellTreeElement currentElement = selector.get(ctx);
        int currentLayerIndex = -1;
        while (currentElement.getSelectorOrSubtreeKey().isPresent()) { // If the node is a branch
            if(currentElement.getSelector().isPresent()) { // If the node is a selector
                currentElement = currentElement.getSelector().orElseThrow().get(ctx);
            } else { // If the node is a subtree key
                Pair<Integer, String> subtreeInfo = currentElement.getSubtreeKey().orElseThrow(); // We get the layer and key of the subtree to lookup
                Pair<Integer, Optional<Map<String, CellTreeElement>>> layer = this.layers.orElseThrow().get(subtreeInfo.getFirst());
                currentElement = layer.getSecond().orElseThrow().get(subtreeInfo.getSecond()); // We lookup the subtree and update the current element
                int layerIndexDiff = subtreeInfo.getFirst() - currentLayerIndex; // Calculate how many layers we need to sample
                //int layerScale = 0;
                for(int i = 0; i<layerIndexDiff; i++) {
                    //layerScale = this.layers.orElseThrow().get(subtreeInfo.getFirst()).getFirst();
                    nucleusPos = nucleiiPositions.get(subtreeInfo.getFirst()+1); // Update the nucleus pos that we are now at, to the one sampled earlier
                    r.setSeed(BlockPos.asLong(nucleusPos.getX(), 0, nucleusPos.getZ()));
                    ctx = new LevelParameter.CellContext(r, nucleusPos.getX(), 0, nucleusPos.getZ()); // Update the context
                }
            }
        }
        return currentElement.getCell().orElseThrow().value();
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
        ctx.register(END, new CellSelectionTree(64, new LevelParameterValueSelector(
                new LevelParameter.DistFromXZCoord(0, 0),
                List.of(
                        new Pair<>(1000f, CellTreeElement.cell(cells.getOrThrow(CellworldCells.THE_END)))
                ),
                CellTreeElement.selector(new RandomSelector(List.of(
                        CellTreeElement.cell(cells.getOrThrow(CellworldCells.END_HIGHLANDS)),
                        CellTreeElement.cell(cells.getOrThrow(CellworldCells.OBSIDIAN_SPIRES)),
                        CellTreeElement.cell(cells.getOrThrow(CellworldCells.AMETHYST_FIELDS)))))
        ), Optional.of(List.of(
                new Pair<>(32, Optional.empty()),
                new Pair<>(16, Optional.empty())
                )))
        );

    }


    private static ResourceKey<CellSelectionTree> createKey(String name) {
        return ResourceKey.create(CellworldRegistries.CELL_SELECTION_TREE_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }
}
