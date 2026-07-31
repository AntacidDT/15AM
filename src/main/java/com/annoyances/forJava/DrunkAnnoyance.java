package com.annoyances.forJava;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class DrunkAnnoyance {

    public static void tick(ServerLevel level) {
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            tickPlayer(level, player);
        }
    }

    public static void tickShared(ServerLevel level) {
        tick(level);
    }

    public static void tickPlayer(ServerLevel level, ServerPlayer player) {
        MobEffectInstance current = player.getEffect(MobEffects.CONFUSION);
        if (current == null || current.getDuration() < 40) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION,
                    60, 255, false, false, true));
        }
    }
}
