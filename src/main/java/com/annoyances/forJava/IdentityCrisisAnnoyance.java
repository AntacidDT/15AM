package com.annoyances.forJava;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class IdentityCrisisAnnoyance {
    private static final Random RANDOM = new Random();
    private static final ConcurrentHashMap<UUID, MobIdentity> identities = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, Integer> ticksLeft = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, Long> teleportCooldown = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, Mob> mirrors = new ConcurrentHashMap<>();

    private static final int EFFECT_REFRESH = 60;
    private static final float EXPLODE_CHANCE = 0.25f;
    private static final String MIRROR_MARKER = "fifteenannoyances_mirror";

    private record Effect(Holder<MobEffect> effect, int amplifier) {}

    private record MobIdentity(EntityType<?> type) {
        static MobIdentity random() {
            return new MobIdentity(ALL_MOBS.get(RANDOM.nextInt(ALL_MOBS.size())));
        }

        String displayName() {
            return type.getDescription().getString();
        }

        boolean teleports() {
            return type == EntityType.ENDERMAN;
        }

        boolean explodes() {
            return type == EntityType.CREEPER;
        }

        boolean ignites() {
            return type == EntityType.BLAZE;
        }

        boolean withers() {
            return type == EntityType.WITHER_SKELETON;
        }

        boolean poisons() {
            return type == EntityType.SPIDER || type == EntityType.CAVE_SPIDER;
        }

        boolean flappy() {
            return type == EntityType.CHICKEN || type == EntityType.RABBIT || type == EntityType.BAT;
        }

        SoundEvent sound() {
            if (type == EntityType.ALLAY) return SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
            if (type == EntityType.AXOLOTL) return SoundEvents.AXOLOTL_IDLE_AIR;
            if (type == EntityType.BAT) return SoundEvents.BAT_AMBIENT;
            if (type == EntityType.BEE) return SoundEvents.BEE_LOOP;
            if (type == EntityType.BLAZE) return SoundEvents.BLAZE_AMBIENT;
            if (type == EntityType.CAT) return SoundEvents.CAT_AMBIENT;
            if (type == EntityType.CAVE_SPIDER) return SoundEvents.SPIDER_AMBIENT;
            if (type == EntityType.CHICKEN) return SoundEvents.CHICKEN_AMBIENT;
            if (type == EntityType.COD) return SoundEvents.COD_AMBIENT;
            if (type == EntityType.COW) return SoundEvents.COW_AMBIENT;
            if (type == EntityType.CREEPER) return SoundEvents.CREEPER_PRIMED;
            if (type == EntityType.DOLPHIN) return SoundEvents.DOLPHIN_AMBIENT;
            if (type == EntityType.DONKEY) return SoundEvents.DONKEY_AMBIENT;
            if (type == EntityType.DROWNED) return SoundEvents.DROWNED_AMBIENT;
            if (type == EntityType.ELDER_GUARDIAN) return SoundEvents.ELDER_GUARDIAN_AMBIENT;
            if (type == EntityType.ENDERMAN) return SoundEvents.ENDERMAN_AMBIENT;
            if (type == EntityType.ENDERMITE) return SoundEvents.ENDERMITE_AMBIENT;
            if (type == EntityType.EVOKER) return SoundEvents.EVOKER_AMBIENT;
            if (type == EntityType.FOX) return SoundEvents.FOX_AMBIENT;
            if (type == EntityType.FROG) return SoundEvents.FROG_AMBIENT;
            if (type == EntityType.GHAST) return SoundEvents.GHAST_AMBIENT;
            if (type == EntityType.GUARDIAN) return SoundEvents.GUARDIAN_AMBIENT;
            if (type == EntityType.HOGLIN) return SoundEvents.HOGLIN_AMBIENT;
            if (type == EntityType.HORSE) return SoundEvents.HORSE_AMBIENT;
            if (type == EntityType.HUSK) return SoundEvents.HUSK_AMBIENT;
            if (type == EntityType.IRON_GOLEM) return SoundEvents.IRON_GOLEM_STEP;
            if (type == EntityType.MAGMA_CUBE) return SoundEvents.MAGMA_CUBE_SQUISH;
            if (type == EntityType.MOOSHROOM) return SoundEvents.COW_AMBIENT;
            if (type == EntityType.MULE) return SoundEvents.MULE_AMBIENT;
            if (type == EntityType.OCELOT) return SoundEvents.OCELOT_AMBIENT;
            if (type == EntityType.PANDA) return SoundEvents.PANDA_AMBIENT;
            if (type == EntityType.PARROT) return SoundEvents.PARROT_AMBIENT;
            if (type == EntityType.PHANTOM) return SoundEvents.PHANTOM_AMBIENT;
            if (type == EntityType.PIG) return SoundEvents.PIG_AMBIENT;
            if (type == EntityType.PIGLIN) return SoundEvents.PIGLIN_AMBIENT;
            if (type == EntityType.PILLAGER) return SoundEvents.PILLAGER_AMBIENT;
            if (type == EntityType.POLAR_BEAR) return SoundEvents.POLAR_BEAR_AMBIENT;
            if (type == EntityType.RABBIT) return SoundEvents.RABBIT_AMBIENT;
            if (type == EntityType.RAVAGER) return SoundEvents.RAVAGER_AMBIENT;
            if (type == EntityType.SALMON) return SoundEvents.SALMON_AMBIENT;
            if (type == EntityType.SHEEP) return SoundEvents.SHEEP_AMBIENT;
            if (type == EntityType.SHULKER) return SoundEvents.SHULKER_AMBIENT;
            if (type == EntityType.SILVERFISH) return SoundEvents.SILVERFISH_AMBIENT;
            if (type == EntityType.SKELETON) return SoundEvents.SKELETON_AMBIENT;
            if (type == EntityType.SLIME) return SoundEvents.SLIME_SQUISH;
            if (type == EntityType.SNIFFER) return SoundEvents.SNIFFER_IDLE;
            if (type == EntityType.SNOW_GOLEM) return SoundEvents.SNOW_GOLEM_AMBIENT;
            if (type == EntityType.SPIDER) return SoundEvents.SPIDER_AMBIENT;
            if (type == EntityType.SQUID) return SoundEvents.SQUID_AMBIENT;
            if (type == EntityType.STRAY) return SoundEvents.STRAY_AMBIENT;
            if (type == EntityType.STRIDER) return SoundEvents.STRIDER_AMBIENT;
            if (type == EntityType.TURTLE) return SoundEvents.TURTLE_AMBIENT_LAND;
            if (type == EntityType.VEX) return SoundEvents.VEX_AMBIENT;
            if (type == EntityType.VILLAGER) return SoundEvents.VILLAGER_AMBIENT;
            if (type == EntityType.VINDICATOR) return SoundEvents.VINDICATOR_AMBIENT;
            if (type == EntityType.WARDEN) return SoundEvents.WARDEN_AMBIENT;
            if (type == EntityType.WITCH) return SoundEvents.WITCH_AMBIENT;
            if (type == EntityType.WITHER_SKELETON) return SoundEvents.WITHER_SKELETON_AMBIENT;
            if (type == EntityType.WOLF) return SoundEvents.WOLF_AMBIENT;
            if (type == EntityType.ZOGLIN) return SoundEvents.ZOGLIN_AMBIENT;
            if (type == EntityType.ZOMBIE) return SoundEvents.ZOMBIE_AMBIENT;
            if (type == EntityType.ZOMBIE_HORSE) return SoundEvents.ZOMBIE_HORSE_AMBIENT;
            if (type == EntityType.ZOMBIE_VILLAGER) return SoundEvents.ZOMBIE_VILLAGER_AMBIENT;
            if (type == EntityType.ZOMBIFIED_PIGLIN) return SoundEvents.ZOMBIFIED_PIGLIN_AMBIENT;
            return SoundEvents.ENDERMAN_TELEPORT;
        }

        Effect[] effects() {
            if (isAquatic()) {
                return new Effect[] { new Effect(MobEffects.WATER_BREATHING, 2), new Effect(MobEffects.SLOW_FALLING, 0) };
            }
            if (type == EntityType.BAT || type == EntityType.PARROT || type == EntityType.PHANTOM
                    || type == EntityType.VEX || type == EntityType.ALLAY || type == EntityType.BEE
                    || type == EntityType.GHAST || type == EntityType.BREEZE) {
                return new Effect[] { new Effect(MobEffects.SLOW_FALLING, 0) };
            }
            if (type == EntityType.SLIME || type == EntityType.RABBIT) {
                return new Effect[] { new Effect(MobEffects.JUMP, 4), new Effect(MobEffects.SLOW_FALLING, 0) };
            }
            if (type == EntityType.SPIDER || type == EntityType.CAVE_SPIDER) {
                return new Effect[] { new Effect(MobEffects.JUMP, 3), new Effect(MobEffects.SLOW_FALLING, 0) };
            }
            if (type == EntityType.BLAZE || type == EntityType.MAGMA_CUBE) {
                return new Effect[] { new Effect(MobEffects.FIRE_RESISTANCE, 0) };
            }
            if (type == EntityType.ENDERMAN) {
                return new Effect[] { new Effect(MobEffects.DARKNESS, 0), new Effect(MobEffects.DAMAGE_RESISTANCE, 0) };
            }
            if (isZombie()) {
                return new Effect[] { new Effect(MobEffects.REGENERATION, 0), new Effect(MobEffects.HUNGER, 2) };
            }
            if (isSkeleton()) {
                return new Effect[] { new Effect(MobEffects.NIGHT_VISION, 0) };
            }
            if (type == EntityType.WARDEN) {
                return new Effect[] { new Effect(MobEffects.DARKNESS, 0), new Effect(MobEffects.DAMAGE_RESISTANCE, 1), new Effect(MobEffects.DAMAGE_BOOST, 1) };
            }
            if (isTanky()) {
                return new Effect[] { new Effect(MobEffects.DAMAGE_RESISTANCE, 0), new Effect(MobEffects.DAMAGE_BOOST, 0) };
            }
            if (type == EntityType.WITCH || type == EntityType.PIGLIN) {
                return new Effect[] { new Effect(MobEffects.REGENERATION, 0) };
            }
            if (type == EntityType.SHULKER || type == EntityType.ARMADILLO || type == EntityType.TURTLE
                    || type == EntityType.SNIFFER || type == EntityType.POLAR_BEAR) {
                return new Effect[] { new Effect(MobEffects.DAMAGE_RESISTANCE, 0) };
            }
            return new Effect[] { new Effect(MobEffects.SLOW_FALLING, 0) };
        }

        private boolean isAquatic() {
            return type == EntityType.COD || type == EntityType.SALMON || type == EntityType.PUFFERFISH
                    || type == EntityType.TROPICAL_FISH || type == EntityType.SQUID
                    || type == EntityType.GLOW_SQUID || type == EntityType.DOLPHIN
                    || type == EntityType.AXOLOTL || type == EntityType.TADPOLE
                    || type == EntityType.TURTLE || type == EntityType.GUARDIAN
                    || type == EntityType.ELDER_GUARDIAN;
        }

        private boolean isZombie() {
            return type == EntityType.ZOMBIE || type == EntityType.HUSK || type == EntityType.DROWNED
                    || type == EntityType.ZOMBIE_VILLAGER || type == EntityType.ZOMBIE_HORSE
                    || type == EntityType.ZOMBIFIED_PIGLIN || type == EntityType.GIANT;
        }

        private boolean isSkeleton() {
            return type == EntityType.SKELETON || type == EntityType.STRAY || type == EntityType.BOGGED
                    || type == EntityType.WITHER_SKELETON || type == EntityType.SKELETON_HORSE;
        }

        private boolean isTanky() {
            return type == EntityType.IRON_GOLEM || type == EntityType.RAVAGER || type == EntityType.HOGLIN
                    || type == EntityType.ZOGLIN || type == EntityType.PIGLIN_BRUTE
                    || type == EntityType.WITHER || type == EntityType.ENDER_DRAGON;
        }
    }

    private static final List<EntityType<?>> ALL_MOBS = List.of(
            EntityType.ALLAY,
            EntityType.ARMADILLO,
            EntityType.AXOLOTL,
            EntityType.BAT,
            EntityType.BEE,
            EntityType.BLAZE,
            EntityType.BOGGED,
            EntityType.BREEZE,
            EntityType.CAMEL,
            EntityType.CAT,
            EntityType.CAVE_SPIDER,
            EntityType.CHICKEN,
            EntityType.COD,
            EntityType.COW,
            EntityType.CREAKING,
            EntityType.CREEPER,
            EntityType.DOLPHIN,
            EntityType.DONKEY,
            EntityType.DROWNED,
            EntityType.ELDER_GUARDIAN,
            EntityType.ENDER_DRAGON,
            EntityType.ENDERMAN,
            EntityType.ENDERMITE,
            EntityType.EVOKER,
            EntityType.FOX,
            EntityType.FROG,
            EntityType.GHAST,
            EntityType.GIANT,
            EntityType.GLOW_SQUID,
            EntityType.GOAT,
            EntityType.GUARDIAN,
            EntityType.HOGLIN,
            EntityType.HORSE,
            EntityType.HUSK,
            EntityType.ILLUSIONER,
            EntityType.IRON_GOLEM,
            EntityType.LLAMA,
            EntityType.MAGMA_CUBE,
            EntityType.MOOSHROOM,
            EntityType.MULE,
            EntityType.OCELOT,
            EntityType.PANDA,
            EntityType.PARROT,
            EntityType.PHANTOM,
            EntityType.PIG,
            EntityType.PIGLIN,
            EntityType.PIGLIN_BRUTE,
            EntityType.PILLAGER,
            EntityType.POLAR_BEAR,
            EntityType.PUFFERFISH,
            EntityType.RABBIT,
            EntityType.RAVAGER,
            EntityType.SALMON,
            EntityType.SHEEP,
            EntityType.SHULKER,
            EntityType.SILVERFISH,
            EntityType.SKELETON,
            EntityType.SKELETON_HORSE,
            EntityType.SLIME,
            EntityType.SNIFFER,
            EntityType.SNOW_GOLEM,
            EntityType.SPIDER,
            EntityType.SQUID,
            EntityType.STRAY,
            EntityType.STRIDER,
            EntityType.TADPOLE,
            EntityType.TRADER_LLAMA,
            EntityType.TROPICAL_FISH,
            EntityType.TURTLE,
            EntityType.VEX,
            EntityType.VILLAGER,
            EntityType.VINDICATOR,
            EntityType.WANDERING_TRADER,
            EntityType.WARDEN,
            EntityType.WITCH,
            EntityType.WITHER,
            EntityType.WITHER_SKELETON,
            EntityType.WOLF,
            EntityType.ZOGLIN,
            EntityType.ZOMBIE,
            EntityType.ZOMBIE_HORSE,
            EntityType.ZOMBIE_VILLAGER,
            EntityType.ZOMBIFIED_PIGLIN
    );

    public static void tick(ServerLevel level) {
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            if (AnnoyanceManager.getForPlayer(level, player) != Annoyance.IDENTITY_CRISIS) {
                clearIdentity(player);
                continue;
            }

            MobIdentity identity = identities.get(player.getUUID());
            if (identity == null) {
                applyIdentity(level, player, MobIdentity.random());
                continue;
            }

            if (!player.isAlive()) {
                clearIdentity(player);
                continue;
            }

            int left = ticksLeft.getOrDefault(player.getUUID(), 0) - 1;
            if (left <= 0) {
                applyIdentity(level, player, MobIdentity.random());
                continue;
            }
            ticksLeft.put(player.getUUID(), left);

            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, EFFECT_REFRESH, 0, false, false));
            updateMirror(level, player, identity);

            for (Effect e : identity.effects()) {
                player.addEffect(new MobEffectInstance(e.effect(), EFFECT_REFRESH, e.amplifier()));
            }
            if (identity.flappy() && !player.isInWater() && RANDOM.nextFloat() < 0.03f) {
                player.jumpFromGround();
            }
            if (identity.teleports() && tryTeleport(level, player)) {
                return;
            }
        }
    }

    private static void applyIdentity(ServerLevel level, ServerPlayer player, MobIdentity identity) {
        MobIdentity old = identities.put(player.getUUID(), identity);
        if (old != null && old != identity) {
            removeEffects(player, old);
        }
        int duration = 120 + RANDOM.nextInt(160);
        ticksLeft.put(player.getUUID(), duration);
        teleportCooldown.remove(player.getUUID());

        removeMirror(player);
        spawnMirror(level, player, identity);
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, EFFECT_REFRESH, 0, false, false));

        for (Effect e : identity.effects()) {
            player.addEffect(new MobEffectInstance(e.effect(), EFFECT_REFRESH, e.amplifier()));
        }
        level.playSound(null, player.blockPosition(), identity.sound(),
                SoundSource.AMBIENT, 1.0f, 1.0f);
        level.sendParticles(ParticleTypes.SOUL,
                player.getX(), player.getY() + 1, player.getZ(),
                20, 0.5, 1.0, 0.5, 0.05);
    }

    private static void spawnMirror(ServerLevel level, ServerPlayer player, MobIdentity identity) {
        Mob mirror = (Mob) identity.type().create(level, EntitySpawnReason.EVENT);
        if (mirror == null) return;

        mirror.setNoAi(true);
        mirror.setInvulnerable(true);
        mirror.setNoGravity(true);
        mirror.setSilent(true);
        mirror.noPhysics = true;
        mirror.setPersistenceRequired();
        mirror.setInvisible(false);
        mirror.setTarget(null);
        mirror.setCustomName(Component.literal(MIRROR_MARKER));
        mirror.setCustomNameVisible(false);

        mirror.moveTo(player.getX(), player.getY(), player.getZ(),
                player.getYRot(), player.getXRot());
        level.addFreshEntity(mirror);
        mirrors.put(player.getUUID(), mirror);
    }

    private static void updateMirror(ServerLevel level, ServerPlayer player, MobIdentity identity) {
        Mob mirror = mirrors.get(player.getUUID());
        if (mirror == null || !mirror.isAlive()) {
            removeMirror(player);
            spawnMirror(level, player, identity);
            return;
        }

        mirror.moveTo(player.getX(), player.getY(), player.getZ(),
                player.getYRot(), player.getXRot());
        mirror.yHeadRot = player.getYHeadRot();
        mirror.setDeltaMovement(player.getDeltaMovement());
    }

    private static void removeMirror(ServerPlayer player) {
        Mob mirror = mirrors.remove(player.getUUID());
        if (mirror != null) {
            mirror.discard();
        }
    }

    private static void clearIdentity(ServerPlayer player) {
        MobIdentity old = identities.remove(player.getUUID());
        if (old != null) {
            removeEffects(player, old);
        }
        ticksLeft.remove(player.getUUID());
        teleportCooldown.remove(player.getUUID());
        removeMirror(player);
        player.removeEffect(MobEffects.INVISIBILITY);
    }

    private static void removeEffects(ServerPlayer player, MobIdentity identity) {
        for (Effect e : identity.effects()) {
            player.removeEffect(e.effect());
        }
    }

    public static void onAttack(ServerLevel level, ServerPlayer player, Entity target) {
        MobIdentity identity = identities.get(player.getUUID());
        if (identity == null) return;
        if (!(target instanceof LivingEntity living)) return;

        if (identity.ignites() && !living.fireImmune()) {
            living.setRemainingFireTicks(60);
            level.sendParticles(ParticleTypes.FLAME, living.getX(), living.getY() + 1, living.getZ(),
                    8, 0.3, 0.3, 0.3, 0.02);
        }
        if (identity.withers()) {
            living.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 1));
        }
        if (identity.poisons()) {
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 1));
        }
    }

    public static void onHurt(ServerLevel level, ServerPlayer player) {
        MobIdentity identity = identities.get(player.getUUID());
        if (identity == null || !identity.explodes()) return;
        if (RANDOM.nextFloat() >= EXPLODE_CHANCE) return;

        level.explode(player, player.getX(), player.getY(), player.getZ(),
                2.0f, false, Level.ExplosionInteraction.NONE);
    }

    private static boolean tryTeleport(ServerLevel level, ServerPlayer player) {
        long now = level.getGameTime();
        Long last = teleportCooldown.get(player.getUUID());
        if (last != null && now - last < 50) return false;
        if (player.isInWater()) return false;
        if (RANDOM.nextFloat() >= 0.06f) return false;

        teleportCooldown.put(player.getUUID(), now);
        double x = player.getX() + (RANDOM.nextDouble() - 0.5) * 40;
        double y = player.getY() + (RANDOM.nextDouble() - 0.5) * 12;
        double z = player.getZ() + (RANDOM.nextDouble() - 0.5) * 40;

        level.sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 1, player.getZ(),
                12, 0.3, 0.3, 0.3, 0.05);
        player.teleportTo(x, y, z);
        level.sendParticles(ParticleTypes.PORTAL, x, y + 1, z, 12, 0.3, 0.3, 0.3, 0.05);
        level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.AMBIENT, 1.0f, 1.0f);
        return true;
    }

    public static void reset() {
        for (Mob mirror : mirrors.values()) {
            if (mirror != null) mirror.discard();
        }
        mirrors.clear();
        identities.clear();
        ticksLeft.clear();
        teleportCooldown.clear();
    }
}
