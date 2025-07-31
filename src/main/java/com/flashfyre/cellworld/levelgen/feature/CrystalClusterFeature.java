package com.flashfyre.cellworld.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.Optional;

public class CrystalClusterFeature extends Feature<CrystalClusterFeature.CrystalClusterConfig> {

    public CrystalClusterFeature() {
        super(CrystalClusterConfig.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<CrystalClusterConfig> context) {
        return this.place(context.level(), context.chunkGenerator(), context.random(), context.origin(), context.config());
    }

    private boolean place(WorldGenLevel world, ChunkGenerator chunkGen, RandomSource rand, BlockPos pos,
                          CrystalClusterConfig config) {

        if (!world.getBlockState(pos).isAir()) return false;

        int maxHeight = config.height().sample(rand);
        int radius = (int) (Math.sqrt(maxHeight) * 0.6F + 2.0F);
        float radiusVariation = rand.nextFloat() * radius * config.relativeRadiusVariation(); // between 0 and 3
        radiusVariation = radiusVariation * 2 - radiusVariation;
        radius += radiusVariation;

        int size = radius * 2 + 1;

        int[] heights = new int[size * size];
        BlockPos basePos = this.populateHeights(world, rand, pos, heights, radius, maxHeight, size, config);

        if (basePos == null || !this.canGenerate(world, pos, heights, radius, size, config)) {
            return false;
        }

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int localZ = -radius; localZ <= radius; localZ++) {
            for (int localX = -radius; localX <= radius; localX++) {
                int height = heights[localX + radius + (localZ + radius) * size];
                if (height > 0) {
                    mutablePos.set(basePos.getX() + localX, basePos.getY(), basePos.getZ() + localZ);
                    this.generatePillar(world, rand, mutablePos, height, config);
                }
            }
        }

        return true;
    }

    private boolean canGenerate(WorldGenLevel world, BlockPos origin, int[] heights, int radius, int size, CrystalClusterConfig config) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos().set(origin);
        int centerHeight = heights[radius + radius * size] + 1;
        for (int localY = 0; localY < centerHeight; localY++) {
            mutablePos.setY(origin.getY() + localY);
            if (!world.getBlockState(mutablePos).isAir()) {
                return false;
            }
        }
        return true;
    }

    private void generatePillar(WorldGenLevel world, RandomSource rand, BlockPos.MutableBlockPos mutablePos, int height, CrystalClusterConfig config) {
        int originY = mutablePos.getY();
        boolean canGenCrystal = false;
        for (int offsetY = 0; offsetY < height; offsetY++) {
            mutablePos.setY(originY + offsetY);
            canGenCrystal = this.placeBlock(world, mutablePos, config.mainStateProvider().getState(rand, mutablePos), config);
        }
        if (canGenCrystal && rand.nextInt(2) == 0) {
            mutablePos.setY(originY + height);
            if(config.topStateProvider().isPresent()) {
                this.placeBlock(world, mutablePos, config.topStateProvider().orElseThrow().getState(rand, mutablePos), config);
            }
        }
    }

    private boolean placeBlock(WorldGenLevel world, BlockPos.MutableBlockPos mutablePos, BlockState state, CrystalClusterConfig config) {
        if (!world.getBlockState(mutablePos).isAir()) return false;
        this.setBlock(world, mutablePos, state);
        return true;
    }

    private BlockPos populateHeights(WorldGenLevel world, RandomSource rand, BlockPos origin, int[] heights, int radius, int maxHeight, int size, CrystalClusterConfig config) {
        BlockPos.MutableBlockPos basePos = new BlockPos.MutableBlockPos().set(origin);

        for (int localZ = -radius; localZ <= radius; localZ++) {
            for (int localX = -radius; localX <= radius; localX++) {
                int index = localX + radius + (localZ + radius) * size;

                double deltaX = localX + rand.nextDouble() * 2.0 - 1.0;
                double deltaZ = localZ + rand.nextDouble() * 2.0 - 1.0;
                double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
                double alpha = (radius - distance) / radius;

                int height = Mth.floor(alpha * maxHeight);
                if (height > 0) {
                    BlockPos surfacePos = this.findSurfaceBelow(world, origin.offset(localX, 0, localZ), config);
                    if (surfacePos == null) return null;
                    if (surfacePos.getY() < basePos.getY()) {
                        basePos.setY(surfacePos.getY());
                    }
                    heights[index] = height;
                }
            }
        }

        return basePos.immutable();
    }

    private BlockPos findSurfaceBelow(WorldGenLevel world, BlockPos origin, CrystalClusterConfig config) {
        BlockState currentState = world.getBlockState(origin);
        BlockPos.MutableBlockPos currentPos = new BlockPos.MutableBlockPos().set(origin);
        for (int i = 0; i <= 6; i++) {
            currentPos.move(Direction.DOWN);
            BlockState nextState = world.getBlockState(currentPos);
            if (nextState == Blocks.BEDROCK.defaultBlockState()) return null;
            if (currentState.isAir() && nextState.isCollisionShapeFullBlock(world, origin)) {
                currentPos.move(Direction.UP);
                return currentPos.immutable();
            }
            currentState = nextState;
        }
        return null;
    }

    public record CrystalClusterConfig(IntProvider height, float relativeRadiusVariation,
                                       BlockStateProvider mainStateProvider,
                                       Optional<BlockStateProvider> topStateProvider) implements FeatureConfiguration {
        public static final Codec<CrystalClusterConfig> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        IntProvider.CODEC.fieldOf("height").forGetter(config -> config.height),
                        Codec.FLOAT.fieldOf("relative_radius_variation").forGetter(config -> config.relativeRadiusVariation),
                        BlockStateProvider.CODEC.fieldOf("main_state_provider").forGetter(config -> config.mainStateProvider),
                        BlockStateProvider.CODEC.optionalFieldOf("top_state_provider").forGetter(config -> config.topStateProvider)
                ).apply(instance, CrystalClusterConfig::new));
    }
}
