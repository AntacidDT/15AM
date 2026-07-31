package com.annoyances.forJava;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.Random;

public class WindSurgeAnnoyance {
    private static final Random RANDOM = new Random();
    private static int tickCounter = 0;
    private static int nextGustTick = 40 + RANDOM.nextInt(60);
    private static int gustDuration = 0;
    private static int gustTotalDuration = 0;
    private static double gustAngle = 0;
    private static double gustSpin = 0;
    private static double gustForce = 0;
    private static double gustDy = 0;

    public static void tickShared(ServerLevel level) {
        tickCounter++;

        if (gustDuration > 0) {
            gustDuration--;
            gustAngle += gustSpin;
            float progress = (float) gustDuration / gustTotalDuration;
            double forceMultiplier = progress < 0.3f ? progress / 0.3f : 1.0;
            double dx = Math.cos(gustAngle) * gustForce * forceMultiplier;
            double dz = Math.sin(gustAngle) * gustForce * forceMultiplier;

            for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                player.setDeltaMovement(
                        player.getDeltaMovement().add(dx, gustDy * forceMultiplier, dz)
                );
                player.hurtMarked = true;
                level.sendParticles(ParticleTypes.CLOUD,
                        player.getX(), player.getY() + 1, player.getZ(),
                        3, dx * 0.6, 0.1, dz * 0.6, 0.05);
            }

            if (gustDuration == gustTotalDuration - 1 || gustDuration % 15 == 0) {
                playWindSound(level);
            }
        }

        if (tickCounter >= nextGustTick) {
            gustTotalDuration = 40 + RANDOM.nextInt(60);
            gustDuration = gustTotalDuration;
            gustForce = 0.5 + RANDOM.nextDouble() * 0.9;
            gustAngle = RANDOM.nextDouble() * Math.PI * 2;
            gustSpin = (RANDOM.nextDouble() - 0.5) * 0.1;
            gustDy = RANDOM.nextDouble() < 0.6 ? 0.15 + RANDOM.nextDouble() * 0.35 : 0;

            tickCounter = 0;
            nextGustTick = 60 + RANDOM.nextInt(100);

            for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                player.sendSystemMessage(Component.literal("Wind Surge!")
                        .withStyle(ChatFormatting.AQUA));
            }
        }
    }

    public static void tickPlayer(ServerLevel level, ServerPlayer player) {
        if (gustDuration > 0) {
            float progress = (float) gustDuration / gustTotalDuration;
            double forceMultiplier = progress < 0.3f ? progress / 0.3f : 1.0;
            double dx = Math.cos(gustAngle) * gustForce * forceMultiplier;
            double dz = Math.sin(gustAngle) * gustForce * forceMultiplier;
            player.setDeltaMovement(player.getDeltaMovement().add(dx, gustDy * forceMultiplier, dz));
            player.hurtMarked = true;
        }
    }

    public static void tick(ServerLevel level) {
        tickShared(level);
    }

    private static void playWindSound(ServerLevel level) {
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            level.playSound(null, player.blockPosition(),
                    SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.WEATHER,
                    0.5f, 0.8f + RANDOM.nextFloat() * 0.4f);
        }
    }
}
