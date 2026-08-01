package com.annoyances.forJava;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class GravityFlipAnnoyance {
    private static final Random RANDOM = new Random();
    private static int tickCounter = 0;
    private static int nextFlipTick = 200 + RANDOM.nextInt(300);
    private static int flipTicksLeft = 0;
    private static int graceTicks = 0;

    private static boolean isAffected(ServerLevel level, ServerPlayer player) {
        return AnnoyanceManager.getForPlayer(level, player) == Annoyance.GRAVITY_FLIP
                && !player.isCreative() && !player.isSpectator();
    }

    public static void tryTrigger(ServerLevel level, ServerPlayer player) {
        if (AnnoyanceManager.getForPlayer(level, player) != Annoyance.GRAVITY_FLIP) return;
        if (player.isCreative() || player.isSpectator()) return;
        if (flipTicksLeft > 0) return;
        if (RANDOM.nextFloat() >= 0.18f) return;

        startFlip(level);
    }

    private static void startFlip(ServerLevel level) {
        flipTicksLeft = 80 + RANDOM.nextInt(120);
        tickCounter = 0;
        nextFlipTick = 300 + RANDOM.nextInt(500);
        graceTicks = 0;

        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            level.playSound(null, player.blockPosition(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.AMBIENT,
                    1.0f, 0.7f);
        }
    }

    public static void tickShared(ServerLevel level) {
        tickCounter++;

        if (tickCounter == nextFlipTick - 40) {
            for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                if (!isAffected(level, player)) continue;
                level.sendParticles(ParticleTypes.PORTAL,
                        player.getX(), player.getY() + 1, player.getZ(),
                        6, 0.5, 1.0, 0.5, 0.02);
            }
        }

        if (tickCounter >= nextFlipTick) {
            startFlip(level);
        }

        if (flipTicksLeft > 0) {
            flipTicksLeft--;
            graceTicks = 40;
            for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                if (!isAffected(level, player)) continue;
                tickFlipped(level, player);
            }
        } else if (graceTicks > 0) {
            graceTicks--;
            for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                if (!isAffected(level, player)) continue;
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0, false, true));
            }
        }
    }

    private static void tickFlipped(ServerLevel level, ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0, false, true));

        if (player.isInWater()) return;

        Vec3 motion = player.getDeltaMovement();

        if (player.isShiftKeyDown()) {
            // Counter: sneaking stabilizes you — normal gravity pulls you down, no lift.
            player.setDeltaMovement(motion.x * 0.95, Math.max(motion.y * 0.95 - 0.06, -0.5), motion.z * 0.95);
            player.hasImpulse = true;
            if (RANDOM.nextDouble() < 0.1) {
                level.sendParticles(ParticleTypes.FALLING_OBSIDIAN_TEAR,
                        player.getX() + (RANDOM.nextDouble() - 0.5) * 1.5,
                        player.getY() + 1.0,
                        player.getZ() + (RANDOM.nextDouble() - 0.5) * 1.5,
                        1, 0, 0, 0, 0);
            }
            return;
        }

        player.setDeltaMovement(motion.x, Math.min(motion.y * 0.92 + 0.09, 0.6), motion.z);
        player.hasImpulse = true;
        level.sendParticles(ParticleTypes.DRAGON_BREATH,
                player.getX() + (RANDOM.nextDouble() - 0.5) * 2,
                player.getY() + RANDOM.nextDouble() * 2,
                player.getZ() + (RANDOM.nextDouble() - 0.5) * 2,
                1, 0, 0.15, 0, 0.05);
    }

    public static void tick(ServerLevel level) {
        tickShared(level);
    }
}
