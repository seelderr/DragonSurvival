package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record ResourceLocationWrapper(String entry) {
    public static final Codec<ResourceLocationWrapper> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        validatedCodec().fieldOf("entry").forGetter(ResourceLocationWrapper::entry)
    ).apply(instance, ResourceLocationWrapper::new));

    public <T> Set<ResourceLocation> getEntries(final Registry<T> registry) {
        if (entry.startsWith("#")) {
            Optional<HolderSet.Named<T>> optional = registry.getTag(TagKey.create(registry.key(), ResourceLocation.parse(entry.substring(1))));
            //noinspection DataFlowIssue -> key is expected to be present
            return optional.map(entries -> entries.stream().map(entry -> entry.getKey().location()).collect(Collectors.toSet())).orElse(Set.of());
        }

        return Set.of(ResourceLocation.parse(entry));
    }

    private static Codec<String> validatedCodec() {
        return Codec.STRING.validate(value -> {
            String location = value.startsWith("#") ? value.substring(1) : value;
            ResourceLocation parsed = ResourceLocation.tryParse(location);

            if (parsed == null) {
                return DataResult.error(() -> "[" + location + "] is not a valid resource location");
            }

            return DataResult.success(value);
        });
    }
}
