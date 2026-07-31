package com.annoyances.forJava.mixin;

import com.annoyances.forJava.Annoyance;
import com.annoyances.forJava.AnnoyanceManager;
import com.annoyances.forJava.MultifallAnnoyance;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Inject(method = "hurtServer", at = @At("HEAD"))
    private void onHurtServer(ServerLevel level, DamageSource source, float amount,
                              CallbackInfoReturnable<Boolean> cir) {
        if (!source.is(DamageTypes.FALL)) return;
        if (AnnoyanceManager.get(level) != Annoyance.MULTIFALL) return;
        if (!((Object) this instanceof ServerPlayer player)) return;
        MultifallAnnoyance.onFall(level, player, amount);
    }
}
