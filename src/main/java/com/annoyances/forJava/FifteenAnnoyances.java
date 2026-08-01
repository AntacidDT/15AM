package com.annoyances.forJava;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FifteenAnnoyances implements ModInitializer {
    public static final String MOD_ID = "fifteenannoyances";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final SoundEvent SCARY_SCREAM = registerSound("scary_scream");

    private static SoundEvent registerSound(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id,
                SoundEvent.createVariableRangeEvent(id));
    }

    @Override
    public void onInitialize() {
        ModNetworking.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("annoyance")
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        Annoyance active = AnnoyanceManager.get(player.serverLevel());
                        if (active == Annoyance.UNKNOWN) {
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    "You don't know what this annoyance is..."), false);
                        } else {
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    "Active annoyance: " + active.getDisplayName()), false);
                        }
                        return 1;
                    })
                    .then(Commands.literal("mode")
                            .then(Commands.literal("shared")
                                    .requires(src -> src.hasPermission(2))
                                    .executes(ctx -> {
                                        AnnoyanceManager.setMode(ctx.getSource().getLevel(),
                                                AnnoyanceManager.Mode.SHARED);
                                        ctx.getSource().sendSuccess(() ->
                                                Component.literal("Mode set to: Shared"), true);
                                        return 1;
                                    }))
                            .then(Commands.literal("random")
                                    .requires(src -> src.hasPermission(2))
                                    .executes(ctx -> {
                                        AnnoyanceManager.setMode(ctx.getSource().getLevel(),
                                                AnnoyanceManager.Mode.RANDOM);
                                        ctx.getSource().sendSuccess(() ->
                                                Component.literal("Mode set to: Random"), true);
                                        return 1;
                                    }))
                            .executes(ctx -> {
                                AnnoyanceManager.Mode mode = AnnoyanceManager.getMode(
                                        ctx.getSource().getLevel());
                                ctx.getSource().sendSuccess(() ->
                                        Component.literal("Current mode: " + mode), false);
                                return 1;
                            }))
            );
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            AnnoyanceManager.sendSyncPacket((ServerPlayer) handler.getPlayer());
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            AnnoyanceManager.init(server.overworld());
        });

        LOGGER.info("15 Annoyances loaded!");
    }
}
