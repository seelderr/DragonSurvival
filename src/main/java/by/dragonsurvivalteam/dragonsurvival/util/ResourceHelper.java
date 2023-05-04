package by.dragonsurvivalteam.dragonsurvival.util;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class ResourceHelper
{
    public static ResourceLocation getKey(Block object)
    {
        return ForgeRegistries.BLOCKS.getKey(object);
    }

    public static ResourceLocation getKey(Item object)
    {
        return ForgeRegistries.ITEMS.getKey(object);
    }
    public static ResourceLocation getKey(EntityType<?> object) {
        return ForgeRegistries.ENTITY_TYPES.getKey(object);
    }

    public static ResourceLocation getKey(Entity object) {
        return getKey(object.getType());
    }
    public static ResourceLocation getKey(SoundEvent event)
    {
        return ForgeRegistries.SOUND_EVENTS.getKey(event);
    }
}
