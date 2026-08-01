package com.annoyances.forJava;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;
import java.util.Random;

public class ChaosAnnoyance {
    private static final Random RANDOM = new Random();

    private static final List<EntityType<? extends Mob>> MOBS = List.of(
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.SPIDER,
            EntityType.CREEPER,
            EntityType.HUSK,
            EntityType.PHANTOM
    );

    private static final Item[] LOOT = {
            Items.DIAMOND, Items.GOLD_INGOT, Items.IRON_INGOT, Items.EMERALD,
            Items.ROTTEN_FLESH, Items.BONE, Items.SLIME_BALL, Items.ENDER_PEARL,
            Items.POTATO, Items.BLAZE_ROD, Items.ARROW, Items.SNOWBALL
    };

    private static final Holder<MobEffect>[] WEIRD_EFFECTS = new Holder[] {
            MobEffects.POISON,
            MobEffects.HUNGER,
            MobEffects.BLINDNESS,
            MobEffects.DARKNESS,
            MobEffects.MOVEMENT_SLOWDOWN,
            MobEffects.DIG_SLOWDOWN,
            MobEffects.WEAKNESS,
            MobEffects.CONFUSION,
            MobEffects.JUMP,
            MobEffects.MOVEMENT_SPEED,
            MobEffects.LEVITATION,
            MobEffects.REGENERATION,
            MobEffects.FIRE_RESISTANCE,
            MobEffects.INVISIBILITY,
            MobEffects.NIGHT_VISION,
            MobEffects.GLOWING
    };

    private static int tickCounter = 0;
    private static int nextChaosTick = 30 + RANDOM.nextInt(40);

    public static void tick(ServerLevel level) {
        tickCounter++;
        if (tickCounter < nextChaosTick) return;
        tickCounter = 0;
        nextChaosTick = 30 + RANDOM.nextInt(40);

        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            if (AnnoyanceManager.getForPlayer(level, player) != Annoyance.CHAOS) continue;
            if (player.isSpectator()) continue;
            fireEvent(level, player);
        }
    }

    private static void fireEvent(ServerLevel level, ServerPlayer player) {
        switch (RANDOM.nextInt(9)) {
            case 0 -> lightningStrike(level, player);
            case 1 -> explosion(level, player);
            case 2 -> mobBurst(level, player);
            case 3 -> effectBurst(level, player);
            case 4 -> lootRain(level, player);
            case 5 -> launch(level, player);
            case 6 -> fireOutbreak(level, player);
            case 7 -> miniTeleport(level, player);
            default -> primedCreeper(level, player);
        }
    }

    private static void lightningStrike(ServerLevel level, ServerPlayer player) {
        double x = player.getX() + (RANDOM.nextDouble() * 2 - 1) * (5 + RANDOM.nextDouble() * 4);
        double z = player.getZ() + (RANDOM.nextDouble() * 2 - 1) * (5 + RANDOM.nextDouble() * 4);
        int y = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING,
                new BlockPos((int) x, 0, (int) z)).getY();

        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.EVENT);
        if (bolt != null) {
            bolt.setPos(x, y, z);
            level.addFreshEntity(bolt);
        }
    }

    private static void explosion(ServerLevel level, ServerPlayer player) {
        double x = player.getX() + (RANDOM.nextDouble() * 2 - 1) * 3;
        double z = player.getZ() + (RANDOM.nextDouble() * 2 - 1) * 3;
        double y = player.getY() + (RANDOM.nextDouble() * 2 - 1) * 2;
        level.explode(null, x, y, z, 2.2f, false, Level.ExplosionInteraction.NONE);
    }

    private static void mobBurst(ServerLevel level, ServerPlayer player) {
        int count = 1 + RANDOM.nextInt(3);
        for (int i = 0; i < count; i++) {
            double x = player.getX() + (RANDOM.nextDouble() * 2 - 1) * (5 + RANDOM.nextDouble() * 4);
            double z = player.getZ() + (RANDOM.nextDouble() * 2 - 1) * (5 + RANDOM.nextDouble() * 4);
            int y = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING,
                    new BlockPos((int) x, 0, (int) z)).getY();

            Mob mob = MOBS.get(RANDOM.nextInt(MOBS.size())).create(level, EntitySpawnReason.EVENT);
            if (mob == null) continue;
            mob.setPos(x, y + 0.1, z);
            level.addFreshEntity(mob);
            level.sendParticles(ParticleTypes.SMOKE, x, y + 1, z, 8, 0.4, 0.4, 0.4, 0.05);
        }
    }

    private static void effectBurst(ServerLevel level, ServerPlayer player) {
        Holder<MobEffect> effect = WEIRD_EFFECTS[RANDOM.nextInt(WEIRD_EFFECTS.length)];
        player.addEffect(new MobEffectInstance(effect, 60 + RANDOM.nextInt(80), RANDOM.nextInt(2)));
    }

    private static void lootRain(ServerLevel level, ServerPlayer player) {
        int count = 3 + RANDOM.nextInt(4);
        for (int i = 0; i < count; i++) {
            double x = player.getX() + (RANDOM.nextDouble() * 2 - 1) * 3;
            double z = player.getZ() + (RANDOM.nextDouble() * 2 - 1) * 3;
            double y = player.getY() + 4 + RANDOM.nextDouble() * 4;

            ItemStack stack = new ItemStack(LOOT[RANDOM.nextInt(LOOT.length)]);
            ItemEntity item = new ItemEntity(level, x, y, z, stack);
            item.setDeltaMovement((RANDOM.nextDouble() - 0.5) * 0.3, -0.1,
                    (RANDOM.nextDouble() - 0.5) * 0.3);
            level.addFreshEntity(item);
        }
    }

    private static void launch(ServerLevel level, ServerPlayer player) {
        player.setDeltaMovement(player.getDeltaMovement().add(0, 1.2 + RANDOM.nextDouble() * 0.6, 0));
        player.hurtMarked = true;
        level.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(),
                20, 0.4, 0.1, 0.4, 0.05);
        level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(),
                SoundSource.AMBIENT, 0.6f, 1.6f);
    }

    private static void fireOutbreak(ServerLevel level, ServerPlayer player) {
        player.setRemainingFireTicks(40);
        BlockPos pos = player.getOnPos();
        for (int i = 0; i < 3; i++) {
            BlockPos target = pos.offset(
                    RANDOM.nextInt(7) - 3,
                    RANDOM.nextInt(2),
                    RANDOM.nextInt(7) - 3);
            if (level.getBlockState(target).isAir()) {
                level.setBlockAndUpdate(target, Blocks.FIRE.defaultBlockState());
            }
        }
    }

    private static void miniTeleport(ServerLevel level, ServerPlayer player) {
        double x = player.getX() + (RANDOM.nextDouble() * 2 - 1) * 8;
        double z = player.getZ() + (RANDOM.nextDouble() * 2 - 1) * 8;
        int y = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING,
                new BlockPos((int) x, 0, (int) z)).getY();

        level.sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 1, player.getZ(),
                20, 0.3, 0.5, 0.3, 0.2);
        player.teleportTo(x, y + 1.1, z);
        level.sendParticles(ParticleTypes.PORTAL, x, y + 1.1, z, 20, 0.3, 0.5, 0.3, 0.2);
        level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.AMBIENT, 1.0f, 0.9f);
    }

    private static void primedCreeper(ServerLevel level, ServerPlayer player) {
        double x = player.getX() + (RANDOM.nextDouble() * 2 - 1) * (6 + RANDOM.nextDouble() * 4);
        double z = player.getZ() + (RANDOM.nextDouble() * 2 - 1) * (6 + RANDOM.nextDouble() * 4);
        int y = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING,
                new BlockPos((int) x, 0, (int) z)).getY();

        Creeper creeper = EntityType.CREEPER.create(level, EntitySpawnReason.EVENT);
        if (creeper == null) return;
        creeper.setPos(x, y + 0.1, z);
        creeper.ignite();
        level.addFreshEntity(creeper);
    }
}
