package com.annoyances.forJava.client.mixin;

import com.annoyances.forJava.client.FifteenAnnoyancesClient;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(net.minecraft.client.renderer.SkyRenderer.class)
public abstract class SkyRendererMixin {

    @Redirect(method = "renderMoon", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(I)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private static VertexConsumer fifteenannoyances$tintMoonYellow(VertexConsumer consumer, int color) {
        if (FifteenAnnoyancesClient.isHoneymoonNight()) {
            color = ARGB.scaleRGB(color, 1.0f, 0.85f, 0.2f);
        }
        return consumer.setColor(color);
    }
}
