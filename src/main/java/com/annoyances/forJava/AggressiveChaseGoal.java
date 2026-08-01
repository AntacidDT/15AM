package com.annoyances.forJava;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

public class AggressiveChaseGoal extends Goal {
    private static final double ATTACK_RANGE_SQ = 2.2 * 2.2;
    private static final int ATTACK_INTERVAL = 20;
    private static final float ATTACK_DAMAGE = 3.0f;

    private final PathfinderMob mob;
    private final double speed;
    private int attackCooldown = 0;

    public AggressiveChaseGoal(PathfinderMob mob, double speed) {
        this.mob = mob;
        this.speed = speed;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) return false;
        return mob.distanceToSqr(target) < 64.0 * 64.0;
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        double distSq = mob.distanceToSqr(target);
        if (distSq > ATTACK_RANGE_SQ) {
            mob.getNavigation().moveTo(target, speed);
            return;
        }

        if (attackCooldown > 0) {
            attackCooldown--;
            return;
        }
        attackCooldown = ATTACK_INTERVAL;

        if (!mob.getSensing().hasLineOfSight(target)) return;
        if (mob.level() instanceof ServerLevel serverLevel) {
            target.hurtServer(serverLevel, mob.damageSources().mobAttack(mob), ATTACK_DAMAGE);
        }
    }
}
