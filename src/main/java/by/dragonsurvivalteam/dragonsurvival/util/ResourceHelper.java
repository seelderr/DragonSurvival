package by.dragonsurvivalteam.dragonsurvival.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;

public class ResourceHelper {
    public static ResourceLocation getKey(Block object) {
        return BuiltInRegistries.BLOCK.getKey(object);
    }

    public static ResourceLocation getKey(Item object) {
        return BuiltInRegistries.ITEM.getKey(object);
    }

    public static ResourceLocation getKey(EntityType<?> object) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(object);
    }

    public static ResourceLocation getKey(Entity object) {
        return getKey(object.getType());
    }

    public static ResourceLocation getKey(SoundEvent event) {
        return BuiltInRegistries.SOUND_EVENT.getKey(event);
    }

    public static Potion getPotionFromItem(Item item) {
        return BuiltInRegistries.POTION.getHolder(PotionItem.getId(item)).get().value();
    }
}
