package com.annoyances.forJava;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HeliumAirAnnoyance {
    private static final Random RANDOM = new Random();
    private static final int LEVITATE_GROUND_TICKS = 60;
    private static final int LEVITATION_DURATION = 100;

    private static final Item[] HEAVY_BLOCKS = {
            Items.ANVIL, Items.OBSIDIAN, Items.NETHERITE_BLOCK, Items.IRON_BLOCK,
            Items.GOLD_BLOCK, Items.DIAMOND_BLOCK
    };

    private static final AABB WORLD_BOX = new AABB(-30000000, -64, -30000000,
            30000000, 320, 30000000);

    private static final Map<UUID, Integer> GROUND_TICKS = new ConcurrentHashMap<>();

    public static void tickShared(ServerLevel level) {
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            tickPlayer(level, player);
        }

        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, WORLD_BOX)) {
            item.setDeltaMovement(item.getDeltaMovement().add(0, 0.04, 0));
        }
        for (Villager villager : level.getEntitiesOfClass(Villager.class, WORLD_BOX)) {
            double bob = Math.sin(villager.tickCount * 0.1) * 0.02;
            villager.setDeltaMovement(villager.getDeltaMovement().add(0, bob, 0));
        }
    }

    public static void tickPlayer(ServerLevel level, ServerPlayer player) {
        if (player.isInWater()) return;

        if (hasHeavyBlock(player)) {
            GROUND_TICKS.remove(player.getUUID());
            return;
        }

        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 0, false, false, true));

        if (player.tickCount % 15 == 0) {
            level.sendParticles(ParticleTypes.END_ROD,
                    player.getX() + (RANDOM.nextDouble() - 0.5) * 2,
                    player.getY() + RANDOM.nextDouble() * 2,
                    player.getZ() + (RANDOM.nextDouble() - 0.5) * 2,
                    1, 0, 0.1, 0, 0.02);
        }

        Vec3 motion = player.getDeltaMovement();
        if (motion.y < -0.01) {
            player.setDeltaMovement(motion.x, Math.min(motion.y * 0.5 + 0.2, 0.15), motion.z);
            player.hurtMarked = true;
        }

        if (player.onGround()) {
            boolean standing = player.getDeltaMovement().horizontalDistanceSqr() < 0.0025;
            if (!standing) {
                GROUND_TICKS.remove(player.getUUID());
                return;
            }

            int ticks = GROUND_TICKS.getOrDefault(player.getUUID(), 0) + 1;
            if (ticks >= LEVITATE_GROUND_TICKS) {
                ticks = 0;
                player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, LEVITATION_DURATION, 0, false, false, true));
            }
            GROUND_TICKS.put(player.getUUID(), ticks);
        } else {
            GROUND_TICKS.remove(player.getUUID());
        }
    }

    public static void tick(ServerLevel level) {
        tickShared(level);
    }

    private static boolean hasHeavyBlock(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            var stack = player.getInventory().getItem(i);
            if (stack.isEmpty()) continue;
            for (Item heavy : HEAVY_BLOCKS) {
                if (stack.is(heavy)) return true;
            }
        }
        return false;
    }
}
