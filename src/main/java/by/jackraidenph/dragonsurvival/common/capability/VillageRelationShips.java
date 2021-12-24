package by.jackraidenph.dragonsurvival.common.capability;

import by.jackraidenph.dragonsurvival.util.Functions;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;

public class VillageRelationShips {
    public int crimeLevel;
    public int evilStatusDuration;
    //change to minutes
    public int hunterSpawnDelay = Functions.minutesToTicks(ConfigHandler.COMMON.hunterSpawnDelay.get() / 6);
}
