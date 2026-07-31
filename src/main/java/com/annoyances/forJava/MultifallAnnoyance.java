package com.annoyances.forJava;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class MultifallAnnoyance {
    private static final long BOUNCE_COOLDOWN = 60;
    private static long lastBounceTime = Long.MIN_VALUE;

    public static void onFall(ServerLevel level, ServerPlayer player, float damage) {
        if (level.getGameTime() - lastBounceTime < BOUNCE_COOLDOWN) return;

        double launchVelocity = Math.sqrt(0.16 * (damage + 3));
        player.setDeltaMovement(player.getDeltaMovement().add(0, launchVelocity, 0));
        player.hurtMarked = true;
        level.sendParticles(ParticleTypes.POOF,
                player.getX(), player.getY(), player.getZ(),
                14, 0.3, 0.1, 0.3, 0.15);
        lastBounceTime = level.getGameTime();
    }

    public static void reset() {
        lastBounceTime = Long.MIN_VALUE;
    }
}
