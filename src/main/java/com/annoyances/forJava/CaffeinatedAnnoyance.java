package com.annoyances.forJava;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class CaffeinatedAnnoyance {
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
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 40, 2, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 40, 1, false, false, true));

        if (player.onGround() && RANDOM.nextDouble() < 0.06) {
            player.jumpFromGround();
        }

        Vec3 motion = player.getDeltaMovement();
        if (motion.horizontalDistanceSqr() > 0.0025) {
            double jitterX = (RANDOM.nextDouble() - 0.5) * 0.2;
            double jitterZ = (RANDOM.nextDouble() - 0.5) * 0.2;
            player.setDeltaMovement(motion.x + jitterX, motion.y, motion.z + jitterZ);
            player.hurtMarked = true;
        }

        if (motion.horizontalDistanceSqr() > 0.25 && RANDOM.nextDouble() < 0.5) {
            level.sendParticles(new DustParticleOptions(0xFF2222, 0.8f),
                    player.getX(), player.getY() + 0.5, player.getZ(),
                    1, 0.1, 0.1, 0.1, 0);
        }

        if (RANDOM.nextDouble() < 0.01) {
            level.playSound(null, player.blockPosition(),
                    SoundEvents.PLAYER_BREATH, SoundSource.PLAYERS,
                    0.6f, 1.8f);
        }
    }
}
