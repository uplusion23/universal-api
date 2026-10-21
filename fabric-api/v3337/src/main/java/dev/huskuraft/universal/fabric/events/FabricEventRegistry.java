package dev.huskuraft.universal.fabric.events;

import com.google.auto.service.AutoService;

import dev.huskuraft.universal.api.events.impl.EventRegistry;
import dev.huskuraft.universal.api.events.lifecycle.ServerTick;
import dev.huskuraft.universal.fabric.events.common.PlatformLifecycleEvents;
import dev.huskuraft.universal.fabric.events.common.ServerPlayerEvents;
import dev.huskuraft.universal.fabric.networking.FabricNetworking;
import dev.huskuraft.universal.vanilla.core.MinecraftPlayer;
import dev.huskuraft.universal.vanilla.core.MinecraftServer;
import dev.huskuraft.universal.vanilla.core.MinecraftWorld;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

@AutoService(EventRegistry.class)
public class FabricEventRegistry extends EventRegistry {

    public FabricEventRegistry() {
        PlatformLifecycleEvents.COMMON_START.register(() -> {
            getRegisterNetworkEvent().invoker().onRegisterNetwork(FabricNetworking::register);
        });

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            getPlayerChangeWorldEvent().invoker().onPlayerChangeWorld(MinecraftPlayer.ofNullable(player), MinecraftWorld.ofNullable(origin), MinecraftWorld.ofNullable(destination));
        });
        ServerPlayerEvents.LOGGED_IN.register(player -> {
            getPlayerLoggedInEvent().invoker().onPlayerLoggedIn(MinecraftPlayer.ofNullable(player));
        });
        ServerPlayerEvents.LOGGED_OUT.register(player -> {
            getPlayerLoggedOutEvent().invoker().onPlayerLoggedOut(MinecraftPlayer.ofNullable(player));
        });
        ServerPlayerEvents.RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            getPlayerRespawnEvent().invoker().onPlayerRespawn(MinecraftPlayer.ofNullable(oldPlayer), MinecraftPlayer.ofNullable(newPlayer), alive);
        });
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            getServerStartingEvent().invoker().onServerStarting(new MinecraftServer(server));
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            getServerStartedEvent().invoker().onServerStarted(new MinecraftServer(server));
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            getServerStoppingEvent().invoker().onServerStopping(new MinecraftServer(server));
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            getServerStoppedEvent().invoker().onServerStopped(new MinecraftServer(server));
        });
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            getServerTickEvent().invoker().onServerTick(new MinecraftServer(server), ServerTick.Phase.START);
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            getServerTickEvent().invoker().onServerTick(new MinecraftServer(server), ServerTick.Phase.END);
        });

    }

}
