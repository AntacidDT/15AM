package com.annoyances.forJava.mixin;

import com.annoyances.forJava.AggressiveChaseGoal;
import com.annoyances.forJava.Annoyance;
import com.annoyances.forJava.AnnoyanceManager;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(Mob.class)
public abstract class MobMixin {

    private static final Map<UUID, Goal> BUFFED_MOBS = new HashMap<>();
    private static final Map<UUID, Double> ORIGINAL_SPEED = new HashMap<>();
    private static final double BOOSTED_SPEED = 0.35;

    @Shadow
    protected GoalSelector goalSelector;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Mob mob = (Mob) (Object) this;
        if (!(mob.level() instanceof ServerLevel level)) return;

        boolean active = AnnoyanceManager.isActiveForAny(level, Annoyance.AGGRESSIVE_MOBS);

        if (!active) {
            Goal buffed = BUFFED_MOBS.remove(mob.getUUID());
            if (buffed != null) {
                goalSelector.removeGoal(buffed);
            }
            Double orig = ORIGINAL_SPEED.remove(mob.getUUID());
            if (orig != null) {
                AttributeInstance speedAttr = mob.getAttribute(Attributes.MOVEMENT_SPEED);
                if (speedAttr != null) speedAttr.setBaseValue(orig);
            }
            return;
        }

        UUID id = mob.getUUID();
        boolean isMonster = mob.getType().getCategory() == MobCategory.MONSTER;

        if (!isMonster && mob instanceof PathfinderMob pathfinder && !BUFFED_MOBS.containsKey(id)) {
            Goal goal = new AggressiveChaseGoal(pathfinder, 1.3);
            goalSelector.addGoal(3, goal);
            BUFFED_MOBS.put(id, goal);
        }

        AttributeInstance speedAttr = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            if (!ORIGINAL_SPEED.containsKey(id)) {
                ORIGINAL_SPEED.put(id, speedAttr.getBaseValue());
            }
            speedAttr.setBaseValue(BOOSTED_SPEED);
        }

        ServerPlayer nearest = null;
        double bestDistSq = 64.0 * 64.0;
        for (ServerPlayer player : level.players()) {
            if (player.isSpectator() || player.getAbilities().instabuild) continue;

            double distSq = player.distanceToSqr(mob);
            if (distSq < bestDistSq) {
                bestDistSq = distSq;
                nearest = player;
            }
        }

        if (nearest == null) return;

        mob.setTarget(nearest);

        if (mob.isInWater()) {
            Vec3 toTarget = nearest.position().subtract(mob.position());
            double dist = toTarget.length();
            if (dist > 1.5) {
                Vec3 dir = toTarget.normalize();
                mob.setDeltaMovement(dir.scale(0.3));
                mob.hurtMarked = true;
            }
        }

        if (mob.tickCount % 5 == 0 && mob.getTarget() instanceof ServerPlayer) {
            level.sendParticles(new DustParticleOptions(0xFF0000, 0.9f),
                    mob.getX(), mob.getY() + mob.getEyeHeight(), mob.getZ(),
                    1, 0.1, 0.1, 0.1, 0);
        }
    }
}
