package com.annoyances.forJava.mixin;

import com.annoyances.forJava.AnnoyanceManager;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ServerLevel level = (ServerLevel) (Object) this;
        if (level.dimension().equals(level.getServer().overworld().dimension())) {
            AnnoyanceManager.tick(level);
        }
    }
}
