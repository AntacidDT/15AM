package com.annoyances.forJava;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FloorIsLavaAnnoyance {
    private static final int IGNITE_TICKS = 35;
    private static final int EVAPORATE_INTERVAL = 20;
    private static final int EVAPORATE_RADIUS = 4;

    private static final Random RANDOM = new Random();
    private static final Map<UUID, Integer> STANDING_TICKS = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> EVAPORATE_TICKS = new ConcurrentHashMap<>();

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

        int evapTicks = EVAPORATE_TICKS.getOrDefault(player.getUUID(), 0) + 1;
        if (evapTicks >= EVAPORATE_INTERVAL) {
            EVAPORATE_TICKS.put(player.getUUID(), 0);
            evaporateWater(level, player);
        } else {
            EVAPORATE_TICKS.put(player.getUUID(), evapTicks);
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

        for (int i = 0; i < 2; i++) {
            BlockPos target = pos.offset(RANDOM.nextInt(5) - 2, 0, RANDOM.nextInt(5) - 2);
            if (level.getBlockState(target).isAir()) {
                level.setBlockAndUpdate(target, Blocks.FIRE.defaultBlockState());
            }
        }

        if (pos != null) {
            BlockPos below = pos.below();
            BlockState belowState = level.getBlockState(below);
            if (belowState.isAir() || belowState.getFluidState().is(FluidTags.WATER)) {
                level.setBlockAndUpdate(below, Blocks.LAVA.defaultBlockState());
            }
            if (RANDOM.nextDouble() < 0.25) {
                BlockPos adjacent = below.offset(RANDOM.nextInt(3) - 1, 0, RANDOM.nextInt(3) - 1);
                BlockState adjState = level.getBlockState(adjacent);
                if (adjState.isAir() || adjState.getFluidState().is(FluidTags.WATER)) {
                    level.setBlockAndUpdate(adjacent, Blocks.LAVA.defaultBlockState());
                }
            }
        }

        player.setRemainingFireTicks(60);
    }

    private static void evaporateWater(ServerLevel level, ServerPlayer player) {
        BlockPos center = player.blockPosition();
        boolean evaporated = false;
        for (int dx = -EVAPORATE_RADIUS; dx <= EVAPORATE_RADIUS; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -EVAPORATE_RADIUS; dz <= EVAPORATE_RADIUS; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(pos);
                    if (!state.getFluidState().is(FluidTags.WATER)) continue;

                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    level.sendParticles(ParticleTypes.CLOUD,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            4, 0.3, 0.3, 0.3, 0.02);
                    level.sendParticles(ParticleTypes.SMOKE,
                            pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                            2, 0.1, 0.1, 0.1, 0.01);
                    evaporated = true;
                }
            }
        }
        if (evaporated) {
            level.playSound(null, center, net.minecraft.sounds.SoundEvents.FIRE_EXTINGUISH,
                    net.minecraft.sounds.SoundSource.BLOCKS, 0.8f, 0.5f + RANDOM.nextFloat() * 0.5f);
        }
    }

    public static void tick(ServerLevel level) {
        tickShared(level);
    }
}
