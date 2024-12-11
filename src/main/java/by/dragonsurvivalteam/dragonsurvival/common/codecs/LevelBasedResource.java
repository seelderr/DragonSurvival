package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public record LevelBasedResource(List<TextureEntry> textureEntries) {
    public static final Codec<LevelBasedResource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TextureEntry.CODEC.listOf().xmap(list -> {
                List<TextureEntry> sorted = new ArrayList<>(list);
                Collections.sort(sorted);
                return sorted.reversed();
            }, Function.identity()).fieldOf("texture_entries").forGetter(LevelBasedResource::textureEntries)
    ).apply(instance, LevelBasedResource::new));

    public ResourceLocation get(int abilityLevel) {
        for (TextureEntry entry : textureEntries) {
            if (abilityLevel >= entry.fromLevel()) {
                return entry.location();
            }
        }

        // Fallback to returning the first entry (this is intended, as it happens for a single tick as the client is receiving projectile data from the server)
        return textureEntries().getFirst().location();
    }

    public record TextureEntry(ResourceLocation location, int fromLevel) implements Comparable<TextureEntry> {
        public static final Codec<TextureEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("texture_resource").forGetter(TextureEntry::location),
                ExtraCodecs.intRange(DragonAbilityInstance.MIN_LEVEL, DragonAbilityInstance.MAX_LEVEL).fieldOf("from_level").forGetter(TextureEntry::fromLevel)
        ).apply(instance, TextureEntry::new));

        @Override
        public int compareTo(@NotNull final TextureEntry other) {
            if (fromLevel() < other.fromLevel()) {
                return -1;
            } else if (fromLevel() > other.fromLevel()) {
                return 1;
            }

            return 0;
        }
    }
}
