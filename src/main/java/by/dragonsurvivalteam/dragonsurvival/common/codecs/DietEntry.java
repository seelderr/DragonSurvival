package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record DietEntry(ResourceLocationWrapper items, FoodProperties properties) {
    public static final Codec<DietEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            // TODO :: move wrapper field in here so you don't have 'items: { entry: "" }' but rather 'items: ""' (and maybe rename 'items')
            //  (would only be annoying if we intend to re-use it)
            ResourceLocationWrapper.CODEC.fieldOf("items").forGetter(DietEntry::items), // TODO :: should probably be a list
            FoodProperties.DIRECT_CODEC.fieldOf("properties").forGetter(DietEntry::properties)
    ).apply(instance, DietEntry::new));

    public static DietEntry from(final ResourceLocation location, final FoodProperties properties) {
        return from(location.toString(), properties);
    }

    public static DietEntry from(final String location, final FoodProperties properties) {
        return new DietEntry(new ResourceLocationWrapper(location), properties);
    }

    public static Map<ResourceLocation, FoodProperties> map(final List<DietEntry> entries) {
        Map<ResourceLocation, FoodProperties> diet = new HashMap<>();

        entries.forEach(entry -> {
            Set<ResourceLocation> items = entry.items().getEntries(BuiltInRegistries.ITEM);
            items.forEach(item -> diet.put(item, entry.properties()));
        });

        return diet;
    }
}
