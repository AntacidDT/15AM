package com.annoyances.forJava.data;

import com.annoyances.forJava.Annoyance;
import com.annoyances.forJava.AnnoyanceManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnnoyanceSavedData extends SavedData {
    private static final String DATA_NAME = "fifteen_annoyances";

    public static final SavedData.Factory<AnnoyanceSavedData> TYPE =
            new SavedData.Factory<>(AnnoyanceSavedData::new, AnnoyanceSavedData::load, null);

    private AnnoyanceManager.Mode mode = AnnoyanceManager.Mode.SHARED;
    private Annoyance sharedAnnoyance = Annoyance.UNKNOWN;
    private final Map<UUID, Annoyance> playerAnnoyances = new HashMap<>();

    public AnnoyanceSavedData() {
    }

    public static AnnoyanceSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        AnnoyanceSavedData data = new AnnoyanceSavedData();
        data.mode = AnnoyanceManager.Mode.valueOf(tag.getString("Mode"));
        data.sharedAnnoyance = Annoyance.fromId(tag.getInt("SharedAnnoyance"));

        data.playerAnnoyances.clear();
        ListTag playerList = tag.getList("PlayerAnnoyances", Tag.TAG_COMPOUND);
        for (int i = 0; i < playerList.size(); i++) {
            CompoundTag entry = playerList.getCompound(i);
            UUID uuid = entry.getUUID("UUID");
            Annoyance annoyance = Annoyance.fromId(entry.getInt("Annoyance"));
            data.playerAnnoyances.put(uuid, annoyance);
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putString("Mode", mode.name());
        tag.putInt("SharedAnnoyance", sharedAnnoyance.getId());

        ListTag playerList = new ListTag();
        for (Map.Entry<UUID, Annoyance> entry : playerAnnoyances.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putUUID("UUID", entry.getKey());
            playerTag.putInt("Annoyance", entry.getValue().getId());
            playerList.add(playerTag);
        }
        tag.put("PlayerAnnoyances", playerList);

        return tag;
    }

    public AnnoyanceManager.Mode getMode() {
        return mode;
    }

    public void setMode(AnnoyanceManager.Mode mode) {
        this.mode = mode;
        setDirty();
    }

    public Annoyance getSharedAnnoyance() {
        return sharedAnnoyance;
    }

    public void setSharedAnnoyance(Annoyance annoyance) {
        this.sharedAnnoyance = annoyance;
        setDirty();
    }

    public Annoyance getPlayerAnnoyance(UUID uuid) {
        return playerAnnoyances.computeIfAbsent(uuid, k -> Annoyance.randomExcludingUnknown());
    }

    public void setPlayerAnnoyance(UUID uuid, Annoyance annoyance) {
        playerAnnoyances.put(uuid, annoyance);
        setDirty();
    }

    public void clearPlayerAnnoyances() {
        playerAnnoyances.clear();
        setDirty();
    }

    public static AnnoyanceSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE, DATA_NAME);
    }
}
