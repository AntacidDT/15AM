package com.annoyances.forJava.client.mixin;

import com.annoyances.forJava.Annoyance;
import com.annoyances.forJava.client.FifteenAnnoyancesClient;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @Inject(method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"))
    private void onRender(LivingEntityRenderState state, PoseStack poseStack,
                          MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        Annoyance annoyance = FifteenAnnoyancesClient.getActiveAnnoyance();

        if (annoyance == Annoyance.UPSIDE_DOWN) {
            if (state.isUpsideDown) {
                poseStack.scale(1.0f, -1.0f, 1.0f);
                poseStack.translate(0, -state.boundingBoxHeight, 0);
            }
        }
    }
}
