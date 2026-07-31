package com.annoyances.forJava;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnnoyanceReveal {
    private static final int TOTAL_TICKS = 80;
    private static final int INITIAL_INTERVAL = 3;
    private static final int FINAL_INTERVAL = 10;

    public static void playReveal(ServerLevel level, ServerPlayer player, Annoyance chosen) {
        List<Annoyance> shuffled = new ArrayList<>(List.of(Annoyance.values()));
        shuffled.remove(Annoyance.UNKNOWN);
        shuffled.remove(chosen);
        Collections.shuffle(shuffled);
        shuffled.add(0, chosen);

        player.connection.send(new ClientboundClearTitlesPacket(true));
        player.connection.send(new ClientboundSetTitlesAnimationPacket(0, 9999, 0));

        level.getServer().execute(new Runnable() {
            int tick = 0;
            int nameIndex = 0;

            @Override
            public void run() {
                if (tick >= TOTAL_TICKS || player.isRemoved()) {
                    return;
                }

                float progress = (float) tick / TOTAL_TICKS;
                int interval = (int) (INITIAL_INTERVAL + (FINAL_INTERVAL - INITIAL_INTERVAL) * progress * progress);

                if (tick % interval == 0) {
                    Annoyance current = shuffled.get(nameIndex % shuffled.size());
                    nameIndex++;

                    ChatFormatting color;
                    if (progress < 0.5f) {
                        color = ChatFormatting.WHITE;
                    } else if (progress < 0.8f) {
                        color = ChatFormatting.YELLOW;
                    } else {
                        color = ChatFormatting.GOLD;
                    }

                    player.connection.send(new ClientboundSetTitleTextPacket(
                            Component.literal(current.getDisplayName())
                                    .withStyle(Style.EMPTY.withColor(color).withBold(true))));

                    if (tick > 0) {
                        level.playSound(null, player.blockPosition(),
                                SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 1.5f);
                    }
                }

                if (tick == TOTAL_TICKS - 1) {
                    player.connection.send(new ClientboundSetTitleTextPacket(
                            Component.literal(chosen.getDisplayName())
                                    .withStyle(ChatFormatting.BOLD, ChatFormatting.GREEN)));

                    player.connection.send(new ClientboundSetSubtitleTextPacket(
                            Component.literal(chosen.getDescription())
                                    .withStyle(ChatFormatting.GRAY)));

                    player.connection.send(new ClientboundSetTitlesAnimationPacket(0, 60, 20));

                    level.playSound(null, player.blockPosition(),
                            SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.0f);
                }

                tick++;
                if (tick <= TOTAL_TICKS) {
                    level.getServer().execute(this);
                }
            }
        });
    }
}
