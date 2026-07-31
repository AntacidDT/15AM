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

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if (player.serverLevel() == null) return;

        Annoyance annoyance = AnnoyanceManager.getForPlayer(player.serverLevel(), player);

        switch (annoyance) {
            case HELIUM_AIR -> HeliumAirAnnoyance.tickPlayer(player.serverLevel(), player);
            case DRUNK -> DrunkAnnoyance.tickPlayer(player.serverLevel(), player);
            case HONEYMOON -> HoneymoonAnnoyance.tickPlayer(player.serverLevel(), player);
            case CAFFEINATED -> CaffeinatedAnnoyance.tickPlayer(player.serverLevel(), player);
            default -> {}
        }
    }

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
}
