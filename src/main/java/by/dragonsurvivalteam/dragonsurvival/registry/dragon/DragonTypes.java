package by.dragonsurvivalteam.dragonsurvival.registry.dragon;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class DragonTypes {
    public static ResourceKey<DragonType> key(final ResourceLocation location) {
        return ResourceKey.create(DragonType.REGISTRY, location);
    }
}
