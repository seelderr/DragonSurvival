package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import net.minecraft.world.item.Item;

import java.util.HashSet;

public class DragonConfigHandler{
    public static HashSet<Item> DRAGON_BLACKLISTED_ITEMS = new HashSet<>();

    public static void rebuildBlacklistedItems(){
        DRAGON_BLACKLISTED_ITEMS = ConfigHandler.getResourceElements(Item.class, ServerConfig.blacklistedItems);
    }
}