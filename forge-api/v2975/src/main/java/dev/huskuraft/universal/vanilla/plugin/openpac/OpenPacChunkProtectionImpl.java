package dev.huskuraft.universal.vanilla.plugin.openpac;

import javax.annotation.Nonnull;

import dev.huskuraft.universal.api.core.BlockInteraction;
import dev.huskuraft.universal.api.core.BlockPosition;
import dev.huskuraft.universal.api.core.Player;
import dev.huskuraft.universal.api.core.World;
import dev.huskuraft.universal.api.plugin.openpac.OpenPacChunkProtection;
import dev.huskuraft.universal.vanilla.core.MinecraftConvertor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import xaero.pac.common.server.claims.protection.api.IChunkProtectionAPI;

public record OpenPacChunkProtectionImpl(IChunkProtectionAPI refs) implements OpenPacChunkProtection {

    // Older versions of Open Parties and Claims do not have the overload with target exceptions.
    private static volatile boolean targetExceptionsSupported = true;

    public static OpenPacChunkProtection ofNullable(IChunkProtectionAPI refs) {
        if (refs == null) return null;
        return new OpenPacChunkProtectionImpl(refs);
    }

    @Override
    public boolean canBreakBlock(@Nonnull Player player, @Nonnull World world, @Nonnull BlockPosition blockPosition) {
        if (!(world.reference() instanceof ServerLevel level)) {
            return true;
        }
        return !isBlockInteractionProtected(player.reference(), InteractionHand.MAIN_HAND, level, MinecraftConvertor.toPlatformBlockPosition(blockPosition), Direction.UP, true);
    }

    @Override
    public boolean canPlaceBlock(@Nonnull Player player, @Nonnull World world, @Nonnull BlockPosition blockPosition) {
        if (!(world.reference() instanceof ServerLevel level)) {
            return true;
        }
        return !refs.onEntityPlaceBlock(player.reference(), level, MinecraftConvertor.toPlatformBlockPosition(blockPosition));
    }

    @Override
    public boolean canInteractBlock(@Nonnull Player player, @Nonnull World world, @Nonnull BlockInteraction blockInteraction) {
        if (!(world.reference() instanceof ServerLevel level)) {
            return true;
        }
        return !isBlockInteractionProtected(player.reference(), MinecraftConvertor.toPlatformInteractionHand(blockInteraction.getHand()), level, MinecraftConvertor.toPlatformBlockPosition(blockInteraction.getBlockPosition()), MinecraftConvertor.toPlatformDirection(blockInteraction.getDirection()), false);
    }

    private boolean isBlockInteractionProtected(Entity entity, InteractionHand hand, ServerLevel level, BlockPos blockPos, Direction direction, boolean breaking) {
        // Do not send chat messages, because one build can examine thousands of blocks.
        if (targetExceptionsSupported) {
            try {
                return refs.onBlockInteraction(entity, hand, null, level, blockPos, direction, breaking, false, true);
            } catch (NoSuchMethodError e) {
                targetExceptionsSupported = false;
            }
        }
        return refs.onBlockInteraction(entity, hand, null, level, blockPos, direction, breaking, false);
    }

}
