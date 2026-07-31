package com.annoyances.forJava;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;

import java.util.List;
import java.util.Random;

public class MobRainAnnoyance {
    private static final Random RANDOM = new Random();

    private static final List<EntityType<? extends Monster>> MOBS = List.of(
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.SPIDER,
            EntityType.CREEPER,
            EntityType.HUSK,
            EntityType.STRAY
    );

    private static int tickCounter = 0;
    private static int nextRainTick = 600 + RANDOM.nextInt(400);

    public static void tickShared(ServerLevel level) {
        tickCounter++;

        if (tickCounter < nextRainTick) return;

        tickCounter = 0;
        nextRainTick = 500 + RANDOM.nextInt(600);

        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            if (player.isSpectator()) continue;

            int count = 3 + RANDOM.nextInt(4);
            for (int i = 0; i < count; i++) {
                double x = player.getX() + (RANDOM.nextDouble() * 2 - 1) * 6;
                double z = player.getZ() + (RANDOM.nextDouble() * 2 - 1) * 6;
                double y = player.getY() + 20 + RANDOM.nextDouble() * 8;

                Mob mob = MOBS.get(RANDOM.nextInt(MOBS.size())).create(level, net.minecraft.world.entity.EntitySpawnReason.EVENT);
                if (mob == null) continue;
                mob.setPos(x, y, z);
                level.addFreshEntity(mob);
                level.sendParticles(ParticleTypes.PORTAL, x, y, z, 10, 0.4, 0.2, 0.4, 0.1);
            }

            level.playSound(null, player.blockPosition(),
                    SoundEvents.CAT_HISS, SoundSource.HOSTILE,
                    0.6f, 1.2f);
            player.sendSystemMessage(Component.literal("It's raining mobs!")
                    .withStyle(ChatFormatting.DARK_RED));
        }
    }

    public static void tickPlayer(ServerLevel level, ServerPlayer player) {
        tickShared(level);
    }

    public static void tick(ServerLevel level) {
        tickShared(level);
    }
}
