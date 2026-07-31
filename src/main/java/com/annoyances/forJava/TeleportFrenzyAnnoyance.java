package com.annoyances.forJava;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Random;

public class TeleportFrenzyAnnoyance {
    private static final Random RANDOM = new Random();
    private static final int RADIUS = 40;

    private static int tickCounter = 0;
    private static int nextTeleportTick = 300 + RANDOM.nextInt(200);

    public static void tickShared(ServerLevel level) {
        tickCounter++;

        if (tickCounter < nextTeleportTick) return;

        tickCounter = 0;
        nextTeleportTick = 400 + RANDOM.nextInt(600);

        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            double x = player.getX() + (RANDOM.nextDouble() * 2 - 1) * RADIUS;
            double z = player.getZ() + (RANDOM.nextDouble() * 2 - 1) * RADIUS;
            int y = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING,
                    new BlockPos((int) x, 0, (int) z)).getY();
            y = Math.max(y, 1);

            level.sendParticles(ParticleTypes.PORTAL,
                    player.getX(), player.getY() + 1, player.getZ(),
                    30, 0.3, 0.5, 0.3, 0.2);
            level.playSound(null, player.blockPosition(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS,
                    1.0f, 0.8f + RANDOM.nextFloat() * 0.4f);
            player.teleportTo(x, y + 1.1, z);
            level.sendParticles(ParticleTypes.PORTAL,
                    x, y + 1.1, z,
                    30, 0.3, 0.5, 0.3, 0.2);
            player.sendSystemMessage(Component.literal("You feel a sudden tug...")
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
        }
    }

    public static void tickPlayer(ServerLevel level, ServerPlayer player) {
        tickShared(level);
    }

    public static void tick(ServerLevel level) {
        tickShared(level);
    }
}
