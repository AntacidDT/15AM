package com.annoyances.forJava;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class GravityFlipAnnoyance {
    private static final Random RANDOM = new Random();
    private static int tickCounter = 0;
    private static int nextFlipTick = 200 + RANDOM.nextInt(300);
    private static int flipTicksLeft = 0;

    public static void tickShared(ServerLevel level) {
        tickCounter++;

        if (flipTicksLeft > 0) {
            flipTicksLeft--;
            for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                if (player.isInWater()) continue;
                Vec3 motion = player.getDeltaMovement();
                player.setDeltaMovement(motion.x, Math.min(motion.y * 0.92 + 0.09, 0.6), motion.z);
                player.hurtMarked = true;
                level.sendParticles(ParticleTypes.DRAGON_BREATH,
                        player.getX() + (RANDOM.nextDouble() - 0.5) * 2,
                        player.getY() + RANDOM.nextDouble() * 2,
                        player.getZ() + (RANDOM.nextDouble() - 0.5) * 2,
                        1, 0, 0.15, 0, 0.05);
            }
        }

        if (tickCounter >= nextFlipTick) {
            flipTicksLeft = 80 + RANDOM.nextInt(120);
            tickCounter = 0;
            nextFlipTick = 300 + RANDOM.nextInt(500);

            for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                player.sendSystemMessage(Component.literal("GRAVITY FLIPPED!")
                        .withStyle(ChatFormatting.DARK_PURPLE));
                level.playSound(null, player.blockPosition(),
                        SoundEvents.ENDERMAN_TELEPORT, SoundSource.AMBIENT,
                        1.0f, 0.7f);
            }
        }
    }

    public static void tickPlayer(ServerLevel level, ServerPlayer player) {
        if (flipTicksLeft > 0 && !player.isInWater()) {
            Vec3 motion = player.getDeltaMovement();
            player.setDeltaMovement(motion.x, Math.min(motion.y * 0.92 + 0.09, 0.6), motion.z);
            player.hurtMarked = true;
        }
    }

    public static void tick(ServerLevel level) {
        tickShared(level);
    }
}
