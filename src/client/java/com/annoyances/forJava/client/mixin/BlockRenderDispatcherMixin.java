package com.annoyances.forJava.client.mixin;

import com.annoyances.forJava.Annoyance;
import com.annoyances.forJava.client.FifteenAnnoyancesClient;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Mixin(net.minecraft.client.renderer.block.BlockRenderDispatcher.class)
public abstract class BlockRenderDispatcherMixin {

    @Unique
    private static List<Block> fifteenannoyances$allBlocks = null;

    @Unique
    private static final ConcurrentHashMap<Block, Block> fifteenannoyances$swapMap = new ConcurrentHashMap<>();

    @Unique
    private static final ThreadLocal<Boolean> fifteenannoyances$recursionGuard = ThreadLocal.withInitial(() -> false);

    @Inject(method = "getBlockModel", at = @At("HEAD"), cancellable = true)
    private void onGetBlockModel(BlockState state, CallbackInfoReturnable<BakedModel> cir) {
        if (fifteenannoyances$recursionGuard.get()) return;

        Annoyance annoyance = FifteenAnnoyancesClient.getActiveAnnoyance();

        if (annoyance == Annoyance.SWAPPED_TEXTURES) {
            if (fifteenannoyances$allBlocks == null) {
                fifteenannoyances$allBlocks = BuiltInRegistries.BLOCK.stream().toList();
            }

            Block swapped = fifteenannoyances$swapMap.computeIfAbsent(state.getBlock(), b -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                Block candidate;
                do {
                    candidate = fifteenannoyances$allBlocks.get(random.nextInt(fifteenannoyances$allBlocks.size()));
                } while (candidate == Blocks.AIR || candidate == b);
                return candidate;
            });

            net.minecraft.client.renderer.block.BlockRenderDispatcher self =
                    (net.minecraft.client.renderer.block.BlockRenderDispatcher) (Object) this;
            fifteenannoyances$recursionGuard.set(true);
            try {
                cir.setReturnValue(self.getBlockModel(swapped.defaultBlockState()));
            } finally {
                fifteenannoyances$recursionGuard.set(false);
            }
        }
    }
}
