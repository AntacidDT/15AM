package com.annoyances.forJava;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class DrunkAnnoyance {

    private static final Random RANDOM = new Random();

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

        if (RANDOM.nextDouble() < 0.02) {
            double angle = RANDOM.nextDouble() * Math.PI * 2;
            double strength = 0.15 + RANDOM.nextDouble() * 0.2;
            Vec3 motion = player.getDeltaMovement();
            player.setDeltaMovement(
                    motion.x + Math.cos(angle) * strength,
                    motion.y + RANDOM.nextDouble() * 0.1,
                    motion.z + Math.sin(angle) * strength);
            player.hurtMarked = true;
            level.sendParticles(ParticleTypes.SMOKE,
                    player.getX(), player.getY() + 0.6, player.getZ(),
                    2, 0.3, 0.3, 0.3, 0.02);
        }
    }
}
