package com.annoyances.forJava.client.mixin;

import com.annoyances.forJava.Annoyance;
import com.annoyances.forJava.client.FifteenAnnoyancesClient;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

    private static final String MIRROR_MARKER = "fifteenannoyances_mirror";

    @Inject(method = "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"), cancellable = true)
    private void fifteenannoyances$hideOwnMirror(Entity entity, double x, double y, double z, float partialTick,
                                                 PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight,
                                                 CallbackInfo ci) {
        if (FifteenAnnoyancesClient.getActiveAnnoyance() != Annoyance.IDENTITY_CRISIS) return;

        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        if (!client.options.getCameraType().isFirstPerson()) return;

        if (entity.getCustomName() != null && MIRROR_MARKER.equals(entity.getCustomName().getString())) {
            ci.cancel();
        }
    }
}
