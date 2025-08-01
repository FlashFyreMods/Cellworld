package com.flashfyre.cellworld;

import com.flashfyre.cellworld.cells.CellSelectionTree;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

import java.util.Random;

public class CellUtil {
    public static BlockPos getClosestNucleus(int blockX, int blockZ, int cellSize, long worldSeed) {
        // We need to get the corner of the current square cell that we are in
        int cellX = Math.floorDiv(blockX, cellSize);
        int cellZ = Math.floorDiv(blockZ, cellSize);
        Random r = new Random();
        XoroshiroRandomSource rand = new XoroshiroRandomSource(worldSeed);
        PositionalRandomFactory factory = rand.forkPositional();
        double distToClosestCellCentre = Double.MAX_VALUE;
        BlockPos.MutableBlockPos closestCellCentrePos = new BlockPos.MutableBlockPos();
        // We need to check the distances to adjacent cellSelector as well as our current cell
        for (int currentCellX = cellX - 1; currentCellX <= cellX + 1; currentCellX++) {
            for (int currentCellZ = cellZ - 1; currentCellZ <= cellZ + 1; currentCellZ++) {
                // Seed the rng using the blockpos of the cell being checked currently - this ensures the random centre is always calculated the same.
                r.setSeed(asLong((currentCellX * cellSize), 0, (currentCellZ * cellSize)));
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

    private static CellSelectionTree.PositionalContext seedContext(RandomSource r, BlockPos seedPos) {
        r.setSeed(BlockPos.asLong(seedPos.getX(), seedPos.getY(), seedPos.getZ()));
        return new CellSelectionTree.PositionalContext(r, seedPos.getX(), 0, seedPos.getZ());
    }

    public static long asLong(int x, int y, int z) {
        return BlockPos.asLong(x, y, z);
    }
}
