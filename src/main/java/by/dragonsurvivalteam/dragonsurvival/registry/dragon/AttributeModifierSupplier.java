package by.dragonsurvivalteam.dragonsurvival.registry.dragon;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.Modifier;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ModifierType;
import by.dragonsurvivalteam.dragonsurvival.mixins.AttributeMapAccessor;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public interface AttributeModifierSupplier {
    static void removeModifiers(final ModifierType type, final LivingEntity entity) {
        removeModifiers(type, ((AttributeMapAccessor) entity.getAttributes()).dragonSurvival$getAttributes());
    }

    // TODO :: Throw an exception if this is called with the type 'CUSTOM'? Since CUSTOM would remove modifiers it shouldn't remove
    static void removeModifiers(final ModifierType type, final Map<Holder<Attribute>, AttributeInstance> attributes) {
        attributes.values().forEach(instance -> instance.getModifiers().forEach(modifier -> {
            if (modifier.id().getPath().startsWith(type.path())) {
                instance.removeModifier(modifier);
            }
        }));
    }

    default void applyModifiers(final LivingEntity entity, final String dragonType, double value) {
        modifiers().forEach(modifier -> {
            AttributeInstance instance = entity.getAttribute(modifier.attribute());
            applyModifier(modifier, instance, dragonType, value);
        });
    }

    /** Intended for usage within descriptions */
    default double getAttributeValue(final String dragonType, double value, final Holder<Attribute> attribute) {
        AttributeInstance instance = new AttributeInstance(attribute, ignored -> { /* Nothing to do */ });
        applyModifiers(instance, dragonType, value);
        return instance.getValue();
    }

    private void applyModifier(final Modifier modifier, @Nullable final AttributeInstance instance, final String dragonType, double value) {
        if (instance == null || modifier.dragonType().isPresent() && !modifier.dragonType().get().equals(dragonType)) {
            return;
        }

        AttributeModifier attributeModifier = modifier.getModifier(getModifierType(), value);
        instance.addPermanentModifier(attributeModifier);
        storeId(instance.getAttribute(), attributeModifier.id());
    }

    private void applyModifier(final Modifier modifier, @Nullable final AttributeInstance instance, final String dragonType, int value) {
        if (instance == null || modifier.dragonType().isPresent() && !modifier.dragonType().get().equals(dragonType)) {
            return;
        }

        instance.addPermanentModifier(modifier.getModifier(getModifierType(), value));
    }

    private void applyModifiers(@Nullable final AttributeInstance instance, final String dragonType, double value) {
        if (instance == null) {
            return;
        }

        modifiers().forEach(modifier -> {
            if (modifier.attribute().is(instance.getAttribute())) {
                applyModifier(modifier, instance, dragonType, value);
            }
        });
    }

    default void storeId(final Holder<Attribute> attribute, final ResourceLocation id) { /* Nothing to do */ }

    default List<Modifier> modifiers() {
        return List.of();
    }

    ModifierType getModifierType();
}
