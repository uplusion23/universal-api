package dev.huskuraft.universal.neoforge.events;

import com.google.auto.service.AutoService;

import dev.huskuraft.universal.api.events.impl.EventRegistry;
import dev.huskuraft.universal.api.events.lifecycle.ServerTick;
import dev.huskuraft.universal.neoforge.networking.NeoForgeNetworking;
import dev.huskuraft.universal.neoforge.platform.NeoForgeInitializer;
import dev.huskuraft.universal.vanilla.core.MinecraftPlayer;
import dev.huskuraft.universal.vanilla.core.MinecraftServer;
import dev.huskuraft.universal.vanilla.core.MinecraftWorld;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@AutoService(EventRegistry.class)
public class NeoForgeEventRegistry extends EventRegistry {


    public NeoForgeEventRegistry() {
        NeoForgeInitializer.EVENT_BUS.addListener(this::onCommonSetup);
        NeoForgeInitializer.EVENT_BUS.addListener(this::onRegisterNetwork);

        NeoForge.EVENT_BUS.register(this);
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
    }

    public void onRegisterNetwork(RegisterPayloadHandlersEvent event) {
        getRegisterNetworkEvent().invoker().onRegisterNetwork(NeoForgeNetworking::register);
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        getPlayerChangeWorldEvent().invoker().onPlayerChangeWorld(MinecraftPlayer.ofNullable(event.getEntity()), MinecraftWorld.ofNullable(event.getEntity().getServer().getLevel(event.getFrom())), MinecraftWorld.ofNullable(event.getEntity().getServer().getLevel(event.getTo())));
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        getPlayerRespawnEvent().invoker().onPlayerRespawn(MinecraftPlayer.ofNullable(event.getEntity()), MinecraftPlayer.ofNullable(event.getEntity()), event.isEndConquered());
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        getPlayerLoggedInEvent().invoker().onPlayerLoggedIn(MinecraftPlayer.ofNullable(event.getEntity()));
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        getPlayerLoggedOutEvent().invoker().onPlayerLoggedOut(MinecraftPlayer.ofNullable(event.getEntity()));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        getServerStartingEvent().invoker().onServerStarting(new MinecraftServer(event.getServer()));
    }

    @SubscribeEvent
    public void onSeverrStarted(ServerStartedEvent event) {
        getServerStartedEvent().invoker().onServerStarted(new MinecraftServer(event.getServer()));
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        getServerStoppingEvent().invoker().onServerStopping(new MinecraftServer(event.getServer()));
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        getServerStoppedEvent().invoker().onServerStopped(new MinecraftServer(event.getServer()));
    }

    @SubscribeEvent
    public void onServerTickPre(ServerTickEvent.Pre event) {
        getServerTickEvent().invoker().onServerTick(new MinecraftServer(event.getServer()), ServerTick.Phase.START);
    }

    @SubscribeEvent
    public void onServerTickPost(ServerTickEvent.Post event) {
        getServerTickEvent().invoker().onServerTick(new MinecraftServer(event.getServer()), ServerTick.Phase.END);
    }

}
