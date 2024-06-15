package by.dragonsurvivalteam.dragonsurvival.common.entity.goals;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;

public class HunterEntityCheckProcedure {
    public static boolean execute(Entity entity) {
        if (entity == null)
            return false;
        if (entity.getType().is(TagKey.create(BuiltInRegistries.ENTITY_TYPE.key(), ResourceLocation.parse("dragonsurvival:hunters_goal")))) {
            return false;
        }
        return true;
    }
}
