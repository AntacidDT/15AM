package com.annoyances.forJava;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class OutrovertAnnoyance {
    private static final Random RANDOM = new Random();
    private static final double RADIUS = 20.0;
    private static final double RUSH_SPEED = 1.5;
    private static final double BOOSTED_SPEED = 0.35;
    private static final Map<UUID, Double> originalSpeed = new HashMap<>();
    private static final Set<UUID> affectedMobs = new HashSet<>();

    public static void tick(ServerLevel level) {
        Set<UUID> currentlyAffected = new HashSet<>();

        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            if (AnnoyanceManager.getForPlayer(level, player) != Annoyance.OUTROVERT) continue;
            if (player.isSpectator()) continue;

            List<Mob> mobs = level.getEntitiesOfClass(Mob.class,
                    new AABB(player.blockPosition()).inflate(RADIUS),
                    m -> m.isAlive());
            for (Mob mob : mobs) {
                UUID id = mob.getUUID();
                currentlyAffected.add(id);
                mob.setTarget(null);

                AttributeInstance speedAttr = mob.getAttribute(Attributes.MOVEMENT_SPEED);
                if (speedAttr != null) {
                    if (!originalSpeed.containsKey(id)) {
                        originalSpeed.put(id, speedAttr.getBaseValue());
                    }
                    speedAttr.setBaseValue(BOOSTED_SPEED);
                }

                PathNavigation nav = mob.getNavigation();
                nav.stop();
                nav.moveTo(player.getX(), player.getY(), player.getZ(), RUSH_SPEED);

                if (mob.isInWater()) {
                    Vec3 toPlayer = player.position().subtract(mob.position());
                    double dist = toPlayer.length();
                    if (dist > 1.5) {
                        Vec3 dir = toPlayer.normalize();
                        mob.setDeltaMovement(dir.scale(0.3));
                        mob.hurtMarked = true;
                    }
                }
            }
        }

        for (UUID id : affectedMobs) {
            if (!currentlyAffected.contains(id)) {
                Mob mob = (Mob) level.getEntity(id);
                if (mob != null) {
                    Double orig = originalSpeed.remove(id);
                    if (orig != null) {
                        AttributeInstance speedAttr = mob.getAttribute(Attributes.MOVEMENT_SPEED);
                        if (speedAttr != null) speedAttr.setBaseValue(orig);
                    }
                }
            }
        }
        affectedMobs.clear();
        affectedMobs.addAll(currentlyAffected);
    }

    public static void reset() {
        originalSpeed.clear();
        affectedMobs.clear();
    }
}
