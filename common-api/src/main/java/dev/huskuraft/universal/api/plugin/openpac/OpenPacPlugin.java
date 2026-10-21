package dev.huskuraft.universal.api.plugin.openpac;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dev.huskuraft.universal.api.platform.Plugin;
import dev.huskuraft.universal.api.platform.Server;

public interface OpenPacPlugin extends Plugin {

    OpenPacChunkClaimsManager getServerClaimManager(@Nonnull Server server);

    /**
     * @return the claim protection of the server, or null if the platform does not support it
     */
    @Nullable
    default OpenPacChunkProtection getChunkProtection(@Nonnull Server server) {
        return null;
    }

}
