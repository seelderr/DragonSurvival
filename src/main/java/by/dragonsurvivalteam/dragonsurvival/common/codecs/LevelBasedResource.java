package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

// TODO :: should be usable for ability and penalty icons as well
public record LevelBasedResource(List<TextureEntry> textureEntries) {
    public static final Codec<LevelBasedResource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TextureEntry.CODEC.listOf().fieldOf("texture_entries").forGetter(LevelBasedResource::textureEntries)
    ).apply(instance, LevelBasedResource::new));

    public LevelBasedResource {
        // Highest 'from_level' is the first element
        Collections.reverse(textureEntries);
    }

    public ResourceLocation get(int abilityLevel) {
        for (TextureEntry data : textureEntries()) {
            if (abilityLevel >= data.fromLevel()) {
                return data.location();
            }
        }

        // As long as the ability level is at least 1 this cannot really occur
        throw new IllegalStateException("Invalid texture definition for [" + this + "]");
    }

    public record TextureEntry(ResourceLocation location, int fromLevel) implements Comparable<TextureEntry> {
        public static final Codec<TextureEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("texture_resource").forGetter(TextureEntry::location),
                ExtraCodecs.intRange(DragonAbilityInstance.MIN_LEVEL, DragonAbilityInstance.MAX_LEVEL).fieldOf("from_level").forGetter(TextureEntry::fromLevel)
        ).apply(instance, TextureEntry::new));

        @Override
        public int compareTo(@NotNull final LevelBasedResource.TextureEntry other) {
            if (fromLevel() < other.fromLevel()) {
                return -1;
            } else if (fromLevel() > other.fromLevel()) {
                return 1;
            }

            return 0;
        }
    }
}
