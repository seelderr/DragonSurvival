package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.Modifier;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ModifierType;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.AttributeModifierSupplier;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public record ModifierEffect(List<Modifier> modifiers) implements EntityEffect, AttributeModifierSupplier {
    public static final MapCodec<ModifierEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Modifier.CODEC.listOf().fieldOf("modifiers").forGetter(ModifierEffect::modifiers)
    ).apply(instance, ModifierEffect::new));

    @Override
    public void apply(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability, final Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            // TODO :: add a duration (to the codec as well) -> will need some sort of callback logic here
            //   or add a separate codec (duration_modifier) which stores entity + modifier (ids) and then ticks down the duration
            String dragonType = DragonStateProvider.getOptional(entity).map(DragonStateHandler::getTypeNameLowerCase).orElse(null);
            applyModifiers(livingEntity, dragonType, ability.getLevel());
        }
    }

    @Override
    public ModifierType getModifierType() {
        return ModifierType.CUSTOM;
    }

    @Override
    public void storeId(final Holder<Attribute> attribute, final ResourceLocation id) {
        // TODO :: probably will need to pass entity reference here or dragon state handler
        //  or this needs a custom application method in general (should be transient modifiers not permanent)
    }

    @Override
    public MapCodec<? extends EntityEffect> codec() {
        return CODEC;
    }
}
