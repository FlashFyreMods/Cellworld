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
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public record CellSelectionTreeOld(List<Integer> layerScales, CellSelector cellSelector) {

    public static final Codec<CellSelectionTreeOld> DIRECT_CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.list(Codec.INT).fieldOf("layer_sizes").forGetter(cellMap -> cellMap.layerScales),
                    CellSelector.CODEC.fieldOf("cell_selector").forGetter(cellMap -> cellMap.cellSelector)
            ).apply(inst, CellSelectionTreeOld::new)
    );

    public static final Codec<Holder<CellSelectionTreeOld>> CODEC = RegistryFileCodec.create(CellworldRegistries.CELL_MAP_REGISTRY_KEY, DIRECT_CODEC);

    public Cell getCell(int x, int z) {
        BlockPos nucleusPos = new BlockPos(x, 0, z);
        List<BlockPos> nucleiPositions = new ArrayList<>();
        for(int i = this.layerScales.size()-1; i >= 0; i--) { // Stores the nucleus positions from smallest to largest
            nucleusPos = getClosestNucleus(nucleusPos.getX(), nucleusPos.getZ(), this.layerScales.get(i)); // Gets the small followed by big cellSelector
            nucleiPositions.add(nucleusPos);
        }
        /*Either<Holder<Cell>, CellSelector> current = Either.right(this.cellSelector);
        int cellIndex = nucleiPositions.size()-1;
        RandomSource r = new LegacyRandomSource(0);
        while (current.right().isPresent()) {
            CellSelector currentCells = current.right().orElseThrow();
            BlockPos currentNucleiPos = nucleiPositions.get(cellIndex);
            r.setSeed(BlockPos.asLong(currentNucleiPos.getX(), 0, currentNucleiPos.getZ()));

            LevelParameter.CellContext ctx = new LevelParameter.CellContext(r, currentNucleiPos.getX(), 0, currentNucleiPos.getZ());

            current = currentCells.get(ctx);;
            cellIndex--;
        }
        return current.left().orElseThrow().value();*/
        RandomSource r = new LegacyRandomSource(0);
        nucleusPos = nucleiPositions.getLast();
        r.setSeed(BlockPos.asLong(nucleusPos.getX(), 0, nucleusPos.getZ()));
        LevelParameter.CellContext ctx = new LevelParameter.CellContext(r, nucleusPos.getX(), 0, nucleusPos.getZ());
        CellTreeElement current = this.cellSelector.get(ctx);
        while (current.getSelector().isPresent()) { // If the element is a selector
            current = current.getSelector().orElseThrow().get(ctx);
        }
        return current.getCell().orElseThrow().value();
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

    public static final ResourceKey<CellSelectionTreeOld> NETHER = createKey("nether");

    public static void bootstrap(BootstrapContext<CellSelectionTreeOld> ctx) {
        HolderGetter<SingleIntConfiguredCell> weightedCellEntries = ctx.lookup(CellworldRegistries.SINGLE_INT_CONFIGURED_CELL);
        HolderGetter<Cell> cells = ctx.lookup(CellworldRegistries.CELL_REGISTRY_KEY);
        //ctx.register(NETHER, new CellMap(List.of(64, 32, 16), new WeightedRandomSelector(weightedCellEntries.getOrThrow(SingleIntConfiguredCell.NETHER))));
        ctx.register(NETHER, new CellSelectionTreeOld(List.of(32), new LevelParameterValueSelector(
                new LevelParameter.DistFromXZCoord(0, 0),
                List.of(
                    new Pair<>(100f, CellTreeElement.cell(cells.getOrThrow(CellworldCells.BASALT_DELTAS))),
                        new Pair<>(200f, CellTreeElement.selector(new RandomSelector(List.of(
                                CellTreeElement.cell(cells.getOrThrow(CellworldCells.WARPED_FOREST)),
                                CellTreeElement.cell(cells.getOrThrow(CellworldCells.CRIMSON_FOREST)))))),
                        new Pair<>(300f, CellTreeElement.selector(new RandomSelector(List.of(
                                CellTreeElement.cell(cells.getOrThrow(CellworldCells.SOUL_SAND_VALLEY)),
                                CellTreeElement.cell(cells.getOrThrow(CellworldCells.GILDED_DEPTHS))))))
                ),
                CellTreeElement.cell(cells.getOrThrow(CellworldCells.NETHER_WASTES))
        )));
    }


    private static ResourceKey<CellSelectionTreeOld> createKey(String name) {
        return ResourceKey.create(CellworldRegistries.CELL_MAP_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }
}
