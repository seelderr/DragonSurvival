package by.dragonsurvivalteam.dragonsurvival.common.entity.goals;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class HunterEntityCheckProcedure {
    private static final TagKey<EntityType<?>> TYPE = TagKey.create(BuiltInRegistries.ENTITY_TYPE.key(), ResourceLocation.parse("dragonsurvival:hunters_goal"));

    public static boolean execute(Entity entity) {
        if (entity == null)
            return false;
        return entity.getType().is(TYPE);
    }
}
