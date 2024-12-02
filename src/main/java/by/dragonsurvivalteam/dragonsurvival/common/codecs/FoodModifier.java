package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public record FoodModifier(HolderSet<Item> items, FoodProperties properties) {
    public static final Codec<FoodModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("id").forGetter(FoodModifier::items),
            FoodProperties.DIRECT_CODEC.fieldOf("properties").forGetter(FoodModifier::properties)
    ).apply(instance, FoodModifier::new));
}
