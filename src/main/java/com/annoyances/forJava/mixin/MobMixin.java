package com.annoyances.forJava.mixin;

import com.annoyances.forJava.Annoyance;
import com.annoyances.forJava.AnnoyanceManager;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Mob mob = (Mob) (Object) this;
        if (!(mob.level() instanceof ServerLevel level)) return;

        Annoyance annoyance = AnnoyanceManager.get(level);
        if (annoyance != Annoyance.AGGRESSIVE_MOBS) return;

        boolean isMonster = mob.getType().getCategory() == MobCategory.MONSTER;

        for (ServerPlayer player : level.players()) {
            if (player.isSpectator() || player.getAbilities().instabuild) continue;

            double distSq = player.distanceToSqr(mob);
            if (!isMonster && distSq < 2.25) {
                player.hurtServer(level, level.damageSources().mobAttack(mob), 2.0f);
            }
            if (!isMonster && distSq < 64.0 * 64.0) {
                mob.getMoveControl().setWantedPosition(player.getX(), player.getY(), player.getZ(), 1.6);
            }
        }

        if (mob.getTarget() instanceof ServerPlayer && mob.tickCount % 5 == 0) {
            level.sendParticles(new DustParticleOptions(0xFF0000, 0.9f),
                    mob.getX(), mob.getY() + mob.getEyeHeight(), mob.getZ(),
                    1, 0.1, 0.1, 0.1, 0);
        }

        if (mob.tickCount % 10 != 0) return;

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

        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive() || target.distanceToSqr(mob) > bestDistSq) {
            mob.setTarget(nearest);
        }
    }
}
