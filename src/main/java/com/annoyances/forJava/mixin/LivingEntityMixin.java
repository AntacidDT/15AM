package com.annoyances.forJava.mixin;

import com.annoyances.forJava.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "jumpFromGround", at = @At("HEAD"))
    private void onJump(CallbackInfo ci) {
        if (!((Object) this instanceof ServerPlayer player)) return;
        if (player.serverLevel() == null) return;
        GravityFlipAnnoyance.tryTrigger(player.serverLevel(), player);
        TeleportFrenzyAnnoyance.onJump(player.serverLevel(), player);
    }

    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    private void onJumpTail(CallbackInfo ci) {
        if (!((Object) this instanceof ServerPlayer player)) return;
        if (player.serverLevel() == null) return;

        Annoyance annoyance = AnnoyanceManager.getForPlayer(player.serverLevel(), player);
        switch (annoyance) {
            default -> {}
        }
    }
}
