package dev.huskuraft.universal.vanilla.renderer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

// Gives the tints and shade of the level with full light and no adjacent blocks. Thus a block looks the same in dark areas.
record FullBrightBlockGetter(Level level) implements BlockAndTintGetter {

    private static final int FULL_BRIGHTNESS = 15;

    @Override
    public float getShade(Direction direction, boolean shade) {
        return level.getShade(direction, shade);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return level.getLightEngine();
    }

    @Override
    public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
        return level.getBlockTint(blockPos, colorResolver);
    }

    @Override
    public int getBrightness(LightLayer lightLayer, BlockPos blockPos) {
        return FULL_BRIGHTNESS;
    }

    @Override
    public int getRawBrightness(BlockPos blockPos, int amount) {
        return FULL_BRIGHTNESS;
    }

    @Override
    public BlockEntity getBlockEntity(BlockPos blockPos) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public int getHeight() {
        return level.getHeight();
    }

    @Override
    public int getMinBuildHeight() {
        return level.getMinBuildHeight();
    }

}
