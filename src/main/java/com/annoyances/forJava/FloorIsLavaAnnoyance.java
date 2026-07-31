package com.annoyances.forJava;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FloorIsLavaAnnoyance {
    private static final int IGNITE_TICKS = 60;

    private static final Random RANDOM = new Random();
    private static final Map<UUID, Integer> STANDING_TICKS = new ConcurrentHashMap<>();

    public static void tickShared(ServerLevel level) {
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            tickPlayer(level, player);
        }
    }

    public static void tickPlayer(ServerLevel level, ServerPlayer player) {
        if (player.getRemainingFireTicks() > 0 && RANDOM.nextDouble() < 0.4) {
            level.sendParticles(ParticleTypes.FLAME,
                    player.getX() + (RANDOM.nextDouble() - 0.5) * 1.5,
                    player.getY() + 0.1,
                    player.getZ() + (RANDOM.nextDouble() - 0.5) * 1.5,
                    1, 0, 0, 0, 0);
        }

        boolean standing = player.onGround()
                && player.getDeltaMovement().horizontalDistanceSqr() < 0.0025;

        if (!standing) {
            STANDING_TICKS.remove(player.getUUID());
            return;
        }

        int ticks = STANDING_TICKS.getOrDefault(player.getUUID(), 0) + 1;
        if (ticks < IGNITE_TICKS) {
            STANDING_TICKS.put(player.getUUID(), ticks);
            return;
        }

        STANDING_TICKS.put(player.getUUID(), 0);

        BlockPos pos = player.getOnPos();
        if (pos != null && level.getBlockState(pos).isAir()) {
            level.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
        }

        player.setRemainingFireTicks(60);
        player.sendSystemMessage(Component.literal("The floor is lava!")
                .withStyle(ChatFormatting.RED));
    }

    public static void tick(ServerLevel level) {
        tickShared(level);
    }
}
