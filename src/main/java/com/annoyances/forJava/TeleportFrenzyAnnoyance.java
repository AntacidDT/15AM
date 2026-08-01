package com.annoyances.forJava;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportFrenzyAnnoyance {
    private static final Random RANDOM = new Random();
    private static final int RADIUS = 40;
    private static final long ACTION_COOLDOWN = 25;

    private static final Map<UUID, Long> actionCooldowns = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> stormTicks = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> stormHops = new ConcurrentHashMap<>();

    private static int tickCounter = 0;
    private static int nextTeleportTick = 120 + RANDOM.nextInt(120);

    public static void tickShared(ServerLevel level) {
        tickCounter++;

        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            if (!hasFrenzy(level, player)) continue;

            Integer hop = stormTicks.get(player.getUUID());
            if (hop != null) {
                int ticks = hop - 1;
                if (ticks <= 0) {
                    randomTeleport(level, player, 15);
                    int left = stormHops.getOrDefault(player.getUUID(), 0) - 1;
                    if (left <= 0) {
                        stormTicks.remove(player.getUUID());
                        stormHops.remove(player.getUUID());
                    } else {
                        stormHops.put(player.getUUID(), left);
                        stormTicks.put(player.getUUID(), 8);
                    }
                } else {
                    stormTicks.put(player.getUUID(), ticks);
                }
            }
        }

        if (tickCounter < nextTeleportTick) return;
        tickCounter = 0;
        nextTeleportTick = 120 + RANDOM.nextInt(120);

        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            if (!hasFrenzy(level, player)) continue;
            if (player.isSpectator()) continue;
            if (stormTicks.containsKey(player.getUUID())) continue;

            if (RANDOM.nextFloat() < 0.15f) {
                startStorm(level, player);
            } else {
                randomTeleport(level, player, RADIUS);
            }
        }
    }

    public static void tickPlayer(ServerLevel level, ServerPlayer player) {
    }

    public static void tick(ServerLevel level) {
        tickShared(level);
    }

    public static void onAttack(ServerLevel level, ServerPlayer player, Entity target) {
        if (!hasFrenzy(level, player) || !consumeCooldown(player)) return;
        if (player.isSpectator()) return;

        Vec3 destination;
        if (target != null && target.isAlive()) {
            Vec3 targetPos = target.position();
            Vec3 away = player.position().subtract(targetPos);
            if (away.lengthSqr() < 0.01) away = new Vec3(1, 0, 0);
            destination = targetPos.add(away.normalize().scale(2.5)).add(0, 0.5, 0);
        } else {
            destination = player.position().add(randomOffset(6, 4));
        }
        teleportTo(level, player, destination);
    }

    public static void onHurt(ServerLevel level, ServerPlayer player) {
        if (!hasFrenzy(level, player) || !consumeCooldown(player)) return;
        randomTeleport(level, player, 24);
    }

    public static void onUse(ServerLevel level, ServerPlayer player) {
        if (!hasFrenzy(level, player) || !consumeCooldown(player)) return;
        if (player.isSpectator()) return;

        double x = player.getX() + (RANDOM.nextDouble() * 2 - 1) * 20;
        double z = player.getZ() + (RANDOM.nextDouble() * 2 - 1) * 20;
        int ground = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING,
                new BlockPos((int) x, 0, (int) z)).getY();
        int y = Math.min(ground + 2 + RANDOM.nextInt(10), level.getMinY() + level.getHeight() - 5);
        teleportTo(level, player, new Vec3(x, y + 0.1, z));
    }

    public static void onJump(ServerLevel level, ServerPlayer player) {
        if (!hasFrenzy(level, player) || !consumeCooldown(player)) return;
        if (player.isSpectator()) return;

        double y = Math.min(player.getY() + 15 + RANDOM.nextInt(25), level.getMinY() + level.getHeight() - 3);
        teleportTo(level, player, new Vec3(player.getX(), y, player.getZ()));
    }

    public static void reset() {
        actionCooldowns.clear();
        stormTicks.clear();
        stormHops.clear();
    }

    private static void startStorm(ServerLevel level, ServerPlayer player) {
        stormHops.put(player.getUUID(), 3 + RANDOM.nextInt(3));
        stormTicks.put(player.getUUID(), 1);
        level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.AMBIENT, 1.0f, 0.5f);
    }

    private static void randomTeleport(ServerLevel level, ServerPlayer player, int radius) {
        teleportTo(level, player, destinationFor(level, player, radius));
    }

    private static Vec3 destinationFor(ServerLevel level, ServerPlayer player, int radius) {
        if (level.dimension() == Level.NETHER) return netherDestination(level, player, radius);
        if (level.dimension() == Level.END) return endDestination(level, player, radius);
        return overworldDestination(level, player, radius);
    }

    private static Vec3 netherDestination(ServerLevel level, ServerPlayer player, int radius) {
        int top = level.getMinY() + level.getHeight() - 3;
        for (int attempt = 0; attempt < 8; attempt++) {
            double x = player.getX() + (RANDOM.nextDouble() * 2 - 1) * radius;
            double z = player.getZ() + (RANDOM.nextDouble() * 2 - 1) * radius;
            int ground = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING,
                    new BlockPos((int) x, 0, (int) z)).getY();
            int y;
            if (RANDOM.nextFloat() < 0.35f) {
                y = Math.min(ground + 4 + RANDOM.nextInt(8), top);
            } else {
                y = ground + 1;
            }
            if (y - 1 >= level.getMinY()) {
                BlockPos land = new BlockPos((int) x, y - 1, (int) z);
                if (!level.getBlockState(land).is(Blocks.LAVA)) {
                    return new Vec3(x, y + 0.2, z);
                }
            }
        }
        double y = Math.min(player.getY() + 30 + RANDOM.nextInt(20), top);
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0));
        return new Vec3(player.getX(), y, player.getZ());
    }

    private static Vec3 endDestination(ServerLevel level, ServerPlayer player, int radius) {
        double x = player.getX() + (RANDOM.nextDouble() * 2 - 1) * radius;
        double z = player.getZ() + (RANDOM.nextDouble() * 2 - 1) * radius;
        double y = Math.min(Math.max(player.getY() + (RANDOM.nextDouble() * 2 - 1) * 8, level.getMinY() + 1),
                level.getMinY() + level.getHeight() - 3);
        return new Vec3(x, y, z);
    }

    private static Vec3 overworldDestination(ServerLevel level, ServerPlayer player, int radius) {
        double x = player.getX() + (RANDOM.nextDouble() * 2 - 1) * radius;
        double z = player.getZ() + (RANDOM.nextDouble() * 2 - 1) * radius;
        int y = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING,
                new BlockPos((int) x, 0, (int) z)).getY();
        y = Math.max(y, 1);
        return new Vec3(x, y + 1.1, z);
    }

    private static void teleportTo(ServerLevel level, ServerPlayer player, Vec3 destination) {
        level.sendParticles(ParticleTypes.PORTAL,
                player.getX(), player.getY() + 1, player.getZ(),
                30, 0.3, 0.5, 0.3, 0.2);
        level.playSound(null, player.blockPosition(),
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS,
                1.0f, 0.8f + RANDOM.nextFloat() * 0.4f);
        player.teleportTo(destination.x, destination.y, destination.z);
        level.sendParticles(ParticleTypes.PORTAL,
                destination.x, destination.y, destination.z,
                30, 0.3, 0.5, 0.3, 0.2);
    }

    private static Vec3 randomOffset(double horizontal, double vertical) {
        return new Vec3(
                (RANDOM.nextDouble() * 2 - 1) * horizontal,
                (RANDOM.nextDouble() * 2 - 1) * vertical,
                (RANDOM.nextDouble() * 2 - 1) * horizontal);
    }

    private static boolean consumeCooldown(ServerPlayer player) {
        long now = player.serverLevel().getGameTime();
        Long last = actionCooldowns.get(player.getUUID());
        if (last != null && now - last < ACTION_COOLDOWN) return false;
        actionCooldowns.put(player.getUUID(), now);
        return true;
    }

    private static boolean hasFrenzy(ServerLevel level, ServerPlayer player) {
        return AnnoyanceManager.getForPlayer(level, player) == Annoyance.TELEPORT_FRENZY;
    }
}
