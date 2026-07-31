package com.annoyances.forJava;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class HoneymoonAnnoyance {
    private static boolean isNight(ServerLevel level) {
        long time = level.getDayTime() % 24000;
        return time >= 13000 && time < 23000;
    }

    public static void tick(ServerLevel level) {
        if (!isNight(level)) return;

        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            tickPlayer(level, player);
        }
    }

    public static void tickShared(ServerLevel level) {
        tick(level);
    }

    public static void tickPlayer(ServerLevel level, ServerPlayer player) {
        if (!isNight(level)) return;

        Vec3 motion = player.getDeltaMovement();

        if (player.onGround()) {
            player.setDeltaMovement(motion.multiply(0.4, 1, 0.4));
            player.hurtMarked = true;
            return;
        }

        if (motion.y < -0.5) {
            player.setDeltaMovement(motion.x, motion.y * 0.85, motion.z);
            player.hurtMarked = true;
        }
    }

    public static boolean isActiveAtNight() {
        return true;
    }
}
