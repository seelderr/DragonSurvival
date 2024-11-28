package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.Optional;

public record LevelBasedResource(ResourceLocation location, Optional<LevelBasedValue> value) {
    public static final Codec<LevelBasedResource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("location").forGetter(LevelBasedResource::location),
            LevelBasedValue.CODEC.optionalFieldOf("value").forGetter(LevelBasedResource::value)
    ).apply(instance, LevelBasedResource::new));

    public ResourceLocation rawLocation() {
        return location;
    }

    public ResourceLocation location(int level) {
        if(value.isEmpty()) return location;
        int indexedLevel = (int) value.get().calculate(level);
        if(indexedLevel <= 0) return location;
        return ResourceLocation.fromNamespaceAndPath(location.getNamespace(), location.getPath() + "_" + level);
    }
}
