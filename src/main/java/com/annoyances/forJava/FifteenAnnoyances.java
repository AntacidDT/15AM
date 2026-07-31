package com.annoyances.forJava;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Locale;

public class FifteenAnnoyances implements ModInitializer {
    public static final String MOD_ID = "fifteenannoyances";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

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
                    .then(Commands.literal("set")
                            .then(Commands.argument("name", StringArgumentType.word())
                                    .suggests((ctx, builder) -> {
                                        for (Annoyance a : Annoyance.values()) {
                                            if (a != Annoyance.UNKNOWN) {
                                                builder.suggest(a.name().toLowerCase(Locale.ROOT).replace("_", ""));
                                            }
                                        }
                                        return builder.buildFuture();
                                    })
                                    .executes(ctx -> {
                                        String name = StringArgumentType.getString(ctx, "name").toUpperCase(Locale.ROOT).replace("_", "");
                                        Annoyance found = null;
                                        for (Annoyance a : Annoyance.values()) {
                                            if (a.name().replace("_", "").equals(name)) {
                                                found = a;
                                                break;
                                            }
                                        }
                                        if (found == null) {
                                            ctx.getSource().sendFailure(Component.literal(
                                                    "Unknown annoyance: " + name).withStyle(ChatFormatting.RED));
                                            return 0;
                                        }
                                        if (found == Annoyance.UNKNOWN) {
                                            ctx.getSource().sendFailure(Component.literal(
                                                    "Cannot set to UNKNOWN").withStyle(ChatFormatting.RED));
                                            return 0;
                                        }

                                        Annoyance chosen = found;
                                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                                        AnnoyanceManager.setForced(player.serverLevel(), chosen);

                                        for (ServerPlayer online : player.serverLevel().getServer().getPlayerList().getPlayers()) {
                                            AnnoyanceReveal.playReveal(player.serverLevel(), online, chosen);
                                        }

                                        ctx.getSource().sendSuccess(() ->
                                                Component.literal("Annoyance set to: " + chosen.getDisplayName())
                                                        .withStyle(ChatFormatting.GREEN), true);
                                        return 1;
                                    })))
            );
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            AnnoyanceManager.sendSyncPacket((ServerPlayer) handler.getPlayer());
        });

        ServerTickEvents.END_WORLD_TICK.register(level -> {
            if (level.dimension().equals(level.getServer().overworld().dimension())) {
                AnnoyanceManager.tick(level);
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            AnnoyanceManager.init(server.overworld());
        });

        LOGGER.info("15 Annoyances loaded!");
    }
}
