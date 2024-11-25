package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.Modifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;

import java.util.Optional;

public record Penalty(
        EntityPredicate penaltyConditions,
        Modifier modifierPenalties,
        EnchantmentEntityEffect effectPenalties,
        // TODO: Also need to store on the player
        int durationToTrigger,
        // Ticks per each penalty effect activation
        int triggerRate,
        // TODO: Will need to break up the sprite sheet for each penalty ability to show the correct icons
        // No resource sprites = this penalty triggers instantly (cave dragon in water)
        Optional<ResourceLocation> resourceSprites) {
    public static final Codec<Penalty> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityPredicate.CODEC.fieldOf("penalty_conditions").forGetter(Penalty::penaltyConditions),
            Modifier.CODEC.fieldOf("modifier_penalties").forGetter(Penalty::modifierPenalties),
            EnchantmentEntityEffect.CODEC.fieldOf("effect_penalties").forGetter(Penalty::effectPenalties),
            Codec.INT.fieldOf("duration_to_trigger").forGetter(Penalty::durationToTrigger),
            Codec.INT.fieldOf("trigger_rate").forGetter(Penalty::triggerRate),
            ResourceLocation.CODEC.optionalFieldOf("resource_sprites").forGetter(Penalty::resourceSprites)
    ).apply(instance, instance.stable(Penalty::new)));
}