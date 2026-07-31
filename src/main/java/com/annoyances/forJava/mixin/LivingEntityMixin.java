package com.annoyances.forJava.mixin;

import com.annoyances.forJava.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (!(entity.level() instanceof ServerLevel level)) return;

        Annoyance annoyance = AnnoyanceManager.get(level);
        if (annoyance != Annoyance.HELIUM_AIR) return;

        if (entity instanceof ServerPlayer player) return;

        if (entity.isInWater()) return;

        entity.setDeltaMovement(
                entity.getDeltaMovement().add(0, 0.03, 0)
        );
    }
}
