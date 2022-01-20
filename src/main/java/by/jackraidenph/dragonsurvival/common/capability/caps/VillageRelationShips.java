package by.jackraidenph.dragonsurvival.common.capability.caps;

import by.jackraidenph.dragonsurvival.common.capability.storage.VillageRelationshipsStorage;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.util.Functions;

public class VillageRelationShips {
    public VillageRelationshipsStorage storage = new VillageRelationshipsStorage();
    
    public int crimeLevel;
    public int evilStatusDuration;
    //change to minutes
    public int hunterSpawnDelay = Functions.minutesToTicks(ConfigHandler.COMMON.hunterSpawnDelay.get() / 6);
}
