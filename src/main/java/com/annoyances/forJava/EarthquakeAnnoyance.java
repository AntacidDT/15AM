package com.annoyances.forJava;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.Random;

public class EarthquakeAnnoyance {
    private static final Random RANDOM = new Random();
    private static int tickCounter = 0;
    private static int nextQuakeTick = 200 + RANDOM.nextInt(300);
    private static int quakeTicksLeft = 0;

    public static void tickShared(ServerLevel level) {
        tickCounter++;

        if (quakeTicksLeft > 0) {
            quakeTicksLeft--;
            double strength = 0.4 + RANDOM.nextDouble() * 0.8;
            double angle = RANDOM.nextDouble() * Math.PI * 2;
            double dx = Math.cos(angle) * strength;
            double dz = Math.sin(angle) * strength;
            double dy = (RANDOM.nextDouble() - 0.35) * strength;

            for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                player.setDeltaMovement(player.getDeltaMovement().add(dx, dy, dz));
                player.hurtMarked = true;
                level.sendParticles(ParticleTypes.POOF,
                        player.getX() + (RANDOM.nextDouble() - 0.5) * 2,
                        player.getY(),
                        player.getZ() + (RANDOM.nextDouble() - 0.5) * 2,
                        2, 0.5, 0.2, 0.5, 0.1);
            }

            if (quakeTicksLeft == 29 || quakeTicksLeft % 10 == 0) {
                for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                    level.playSound(null, player.blockPosition(),
                            SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS,
                            0.3f, 0.5f + RANDOM.nextFloat() * 0.5f);
                }
            }
        }

        if (tickCounter >= nextQuakeTick) {
            quakeTicksLeft = 40 + RANDOM.nextInt(60);
            tickCounter = 0;
            nextQuakeTick = 300 + RANDOM.nextInt(400);

            for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                player.setDeltaMovement(player.getDeltaMovement().add(0, 1.2, 0));
                player.hurtMarked = true;
                player.sendSystemMessage(Component.literal("EARTHQUAKE!")
                        .withStyle(ChatFormatting.RED));
            }
        }
    }

    public static void tickPlayer(ServerLevel level, ServerPlayer player) {
        if (quakeTicksLeft > 0) {
            player.setDeltaMovement(player.getDeltaMovement().add(0, 0.3, 0));
            player.hurtMarked = true;
        }
    }

    public static void tick(ServerLevel level) {
        tickShared(level);
    }
}
