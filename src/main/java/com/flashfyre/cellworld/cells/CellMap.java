package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.Cell;
import com.flashfyre.cellworld.Cellworld;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.LegacyRandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public record CellMap(List<Integer> layerScales, CellSelector cells) {

    public static final Codec<CellMap> DIRECT_CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.list(Codec.INT).fieldOf("cell_size").forGetter(cellMap -> cellMap.layerScales),
                    CellSelector.CODEC.fieldOf("cells").forGetter(cellMap -> cellMap.cells)
            ).apply(inst, CellMap::new)
    );

    public static final Codec<Holder<CellMap>> CODEC = RegistryFileCodec.create(Cellworld.CELL_MAP_REGISTRY_KEY, DIRECT_CODEC);

    public Cell getCell(int x, int z) {
        BlockPos nucleusPos = new BlockPos(x, 0, z);
        List<BlockPos> nucleiPositions = new ArrayList<>();
        for(int i = this.layerScales.size()-1; i >= 0; i--) {
            nucleusPos = getClosestNucleus(nucleusPos.getX(), nucleusPos.getZ(), this.layerScales.get(i)); // Gets the small followed by big cells
            nucleiPositions.add(nucleusPos);
        }
        Either<Cell, CellSelector> current = Either.right(this.cells);
        int cellIndex = nucleiPositions.size()-1;
        RandomSource r = new LegacyRandomSource(0);
        while (current.right().isPresent()) {
            CellSelector currentCells = current.right().orElseThrow();
            BlockPos currentNucleiPos = nucleiPositions.get(cellIndex);
            r.setSeed(BlockPos.asLong(currentNucleiPos.getX(), 0, currentNucleiPos.getZ()));

            current = currentCells.get(r).value();;
            cellIndex--;
        }

        return current.left().orElseThrow();
    }

    private static BlockPos getClosestNucleus(int blockX, int blockZ, int cellSize) {
        // We need to get the corner of the current square cell that we are in
        int cellX = Math.floorDiv(blockX, cellSize);
        int cellZ = Math.floorDiv(blockZ, cellSize);
        Random r = new Random();
        double distToClosestCellCentre = Double.MAX_VALUE;
        BlockPos.MutableBlockPos closestCellCentrePos = new BlockPos.MutableBlockPos();
        // We need to check the distances to adjacent cells as well as our current cell
        for (int currentCellX = cellX - 1; currentCellX <= cellX + 1; currentCellX++) {
            for (int currentCellZ = cellZ - 1; currentCellZ <= cellZ + 1; currentCellZ++) {
                // Seed the rng using the blockpos of the cell being checked currently - this ensures the random centre is always calculated the same.
                r.setSeed(BlockPos.asLong((currentCellX * cellSize), 0, (currentCellZ * cellSize)));
                int cellCentreOffsetX = r.nextInt(cellSize);
                int cellCentreOffsetZ = r.nextInt(cellSize);
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

    public static final ResourceKey<CellMap> NETHER = createKey("nether");

    public static void bootstrap(BootstrapContext<CellMap> ctx) {
        HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
        HolderGetter<WeightedCell> weightedCellEntries = ctx.lookup(Cellworld.WEIGHTED_CELL_ENTRY_REGISTRY_KEY);
        ctx.register(NETHER, new CellMap(List.of(64), new RandomFromWeightedListHolderSet(weightedCellEntries.getOrThrow(WeightedCell.NETHER))));
    }


    private static ResourceKey<CellMap> createKey(String name) {
        return ResourceKey.create(Cellworld.CELL_MAP_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(Cellworld.MOD_ID, name));
    }
}
