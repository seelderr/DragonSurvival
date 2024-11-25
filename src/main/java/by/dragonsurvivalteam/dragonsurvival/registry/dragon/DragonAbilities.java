package by.dragonsurvivalteam.dragonsurvival.registry.dragon;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.DragonAbility;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class DragonAbilities {
    public static ResourceKey<DragonAbility> key(final ResourceLocation location) {
        return ResourceKey.create(DragonAbility.REGISTRY, location);
    }
}
