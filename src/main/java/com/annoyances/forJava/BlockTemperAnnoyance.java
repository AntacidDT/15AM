package com.annoyances.forJava;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.Random;

public class BlockTemperAnnoyance {
    private static final Random RANDOM = new Random();

    public static void onBlockBreak(ServerLevel level, ServerPlayer player, BlockPos pos) {
        if (AnnoyanceManager.getForPlayer(level, player) != Annoyance.BLOCK_TEMPER) return;

        double chance = 0.3 + RANDOM.nextDouble() * 0.3;
        if (RANDOM.nextDouble() >= chance) return;

        level.explode(null,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                3.0f, false, Level.ExplosionInteraction.TNT);
    }
}
