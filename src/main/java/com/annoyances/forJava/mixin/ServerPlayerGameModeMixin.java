package com.annoyances.forJava.mixin;

import com.annoyances.forJava.BlockTemperAnnoyance;
import com.annoyances.forJava.GravityFlipAnnoyance;
import com.annoyances.forJava.TeleportFrenzyAnnoyance;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {

    @Shadow @Final protected ServerPlayer player;

    @Inject(method = "destroyBlock", at = @At("HEAD"))
    private void onDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (player.serverLevel() == null) return;
        GravityFlipAnnoyance.tryTrigger(player.serverLevel(), player);
        BlockTemperAnnoyance.onBlockBreak(player.serverLevel(), player, pos);
    }

    @Inject(method = "useItem", at = @At("HEAD"))
    private void onUseItem(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand,
                           CallbackInfoReturnable<InteractionResult> cir) {
        if (player.serverLevel() == null) return;
        GravityFlipAnnoyance.tryTrigger(player.serverLevel(), player);
        TeleportFrenzyAnnoyance.onUse(player.serverLevel(), player);
    }

    @Inject(method = "useItemOn", at = @At("HEAD"))
    private void onUseItemOn(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand,
                             BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (player.serverLevel() == null) return;
        GravityFlipAnnoyance.tryTrigger(player.serverLevel(), player);
        TeleportFrenzyAnnoyance.onUse(player.serverLevel(), player);
    }
}
