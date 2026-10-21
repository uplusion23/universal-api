package dev.huskuraft.universal.api.plugin.openpac;

import javax.annotation.Nonnull;

import dev.huskuraft.universal.api.core.BlockInteraction;
import dev.huskuraft.universal.api.core.BlockPosition;
import dev.huskuraft.universal.api.core.Player;
import dev.huskuraft.universal.api.core.World;

/**
 * Claim protection checks on the server from Open Parties and Claims.
 * <p>
 * These checks use the protection configuration of the claim. Thus they include party members, allies, exceptions and
 * the claim options of the server.
 */
public interface OpenPacChunkProtection {

    boolean canBreakBlock(@Nonnull Player player, @Nonnull World world, @Nonnull BlockPosition blockPosition);

    boolean canPlaceBlock(@Nonnull Player player, @Nonnull World world, @Nonnull BlockPosition blockPosition);

    boolean canInteractBlock(@Nonnull Player player, @Nonnull World world, @Nonnull BlockInteraction blockInteraction);

}
