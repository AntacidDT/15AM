package com.annoyances.forJava;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class IntrovertsAnnoyance {
    private static final Random RANDOM = new Random();
    private static final double RADIUS = 14.0;
    private static final double FLEE_DISTANCE = 32.0;
    private static final double FLEE_SPEED = 1.8;
    private static final Map<UUID, Long> startleCooldown = new HashMap<>();
    private static long lastScreamTick = Long.MIN_VALUE;
    private static int tickCounter = 0;

    public static void tick(ServerLevel level) {
        tickCounter++;
        if (tickCounter % 2 != 0) return;

        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            if (AnnoyanceManager.getForPlayer(level, player) != Annoyance.INTROVERTS) continue;
            if (player.isSpectator()) continue;

            List<Mob> mobs = level.getEntitiesOfClass(Mob.class,
                    new AABB(player.blockPosition()).inflate(RADIUS),
                    m -> m.isAlive());
            for (Mob mob : mobs) {
                Vec3 away = mob.position().subtract(player.position());
                if (away.lengthSqr() < 1.0E-4) {
                    away = new Vec3(RANDOM.nextDouble() - 0.5, 0, RANDOM.nextDouble() - 0.5);
                }
                away = away.normalize();
                Vec3 target = mob.position().add(away.scale(FLEE_DISTANCE));

                mob.setTarget(null);
                PathNavigation nav = mob.getNavigation();
                nav.stop();
                nav.moveTo(target.x, mob.getY(), target.z, FLEE_SPEED);

                long now = level.getGameTime();
                Long last = startleCooldown.get(mob.getUUID());
                if (last == null || now - last > 100) {
                    startleCooldown.put(mob.getUUID(), now);
                    level.sendParticles(ParticleTypes.CLOUD,
                            mob.getX(), mob.getY() + 0.5, mob.getZ(),
                            6, 0.3, 0.3, 0.3, 0.03);
                    if (now - lastScreamTick >= 15) {
                        lastScreamTick = now;
                        level.playSound(null, mob.blockPosition(), FifteenAnnoyances.SCARY_SCREAM,
                                SoundSource.AMBIENT, 1.0f, 1.0f);
                        FifteenAnnoyances.LOGGER.info("Scary scream fired at {}", mob.blockPosition());
                    }
                }
            }
        }
    }

    public static void reset() {
        startleCooldown.clear();
    }
}
