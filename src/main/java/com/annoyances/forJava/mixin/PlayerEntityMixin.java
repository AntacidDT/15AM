package com.annoyances.forJava.mixin;

import com.annoyances.forJava.*;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "startSleepInBed", at = @At("HEAD"), cancellable = true)
    private void onStartSleepInBed(BlockPos pos, CallbackInfoReturnable<Either<Player.BedSleepingProblem, ?>> cir) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if (player.serverLevel() == null) return;

        Annoyance annoyance = AnnoyanceManager.getForPlayer(player.serverLevel(), player);
        if (annoyance == Annoyance.CAFFEINATED) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("You're too caffeinated to sleep!"),
                    true);
            cir.setReturnValue(Either.left(Player.BedSleepingProblem.OTHER_PROBLEM));
        }
    }

    @Inject(method = "attack(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"))
    private void onAttack(net.minecraft.world.entity.Entity target, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if (player.serverLevel() == null) return;

        IdentityCrisisAnnoyance.onAttack(player.serverLevel(), player, target);
        GravityFlipAnnoyance.tryTrigger(player.serverLevel(), player);
        TeleportFrenzyAnnoyance.onAttack(player.serverLevel(), player, target);
    }
}
