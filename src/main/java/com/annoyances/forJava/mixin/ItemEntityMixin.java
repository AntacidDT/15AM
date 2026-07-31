package com.annoyances.forJava.mixin;

import com.annoyances.forJava.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ItemEntity item = (ItemEntity) (Object) this;
        if (!(item.level() instanceof ServerLevel level)) return;

        Annoyance annoyance = AnnoyanceManager.get(level);
        if (annoyance != Annoyance.HELIUM_AIR) return;

        item.setDeltaMovement(
                item.getDeltaMovement().add(0, 0.04, 0)
        );
    }
}
