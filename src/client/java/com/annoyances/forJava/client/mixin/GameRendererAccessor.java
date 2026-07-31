package com.annoyances.forJava.client.mixin;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

    @Accessor("postEffectId")
    void fifteenannoyances$setPostEffectId(ResourceLocation id);

    @Accessor("effectActive")
    void fifteenannoyances$setEffectActive(boolean active);
}
