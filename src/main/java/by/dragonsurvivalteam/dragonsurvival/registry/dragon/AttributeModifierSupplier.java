package by.dragonsurvivalteam.dragonsurvival.registry.dragon;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.DSAttributeModifier;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ScalingAttributeModifier;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public interface AttributeModifierSupplier {
    default void applyModifiers(final Player player) {
        Set<Holder<Attribute>> attributes = new HashSet<>();
        // The player attribute map doesn't contain attributes that weren't accessed before
        modifiers().forEach(modifier -> attributes.add(modifier.attribute()));
        scalingModifiers().forEach(modifier -> attributes.add(modifier.attribute()));

        DragonStateHandler data = DragonStateProvider.getData(player);
        attributes.forEach(attribute -> applyModifiers(data.getTypeNameLowerCase(), data.getSize(), player.getAttribute(attribute)));
    }

    /** Intended for usage within descriptions */
    default double getAttributeValue(final String dragonType, double size, final Holder<Attribute> attribute) {
        AttributeInstance attributeInstance = new AttributeInstance(attribute, instance -> { /* Nothing to do */ });
        applyModifiers(dragonType, size, attributeInstance);
        return attributeInstance.getValue();
    }

    private void applyModifiers(final String dragonType, double size, @Nullable final AttributeInstance instance) {
        if (instance == null) {
            return;
        }

        modifiers().forEach(modifier -> {
            if (!modifier.attribute().is(instance.getAttribute()) || modifier.dragonType().isPresent() && !modifier.dragonType().get().equals(dragonType)) {
                return;
            }

            instance.addPermanentModifier(modifier.modifier());
        });

        scalingModifiers().forEach(modifier -> {
            if (!modifier.attribute().is(instance.getAttribute()) || modifier.dragonType().isPresent() && !modifier.dragonType().get().equals(dragonType)) {
                return;
            }

            instance.addPermanentModifier(modifier.getModifier(size));
        });
    }

    default List<DSAttributeModifier> modifiers() {
        return List.of();
    }

    default List<ScalingAttributeModifier> scalingModifiers() {
        return List.of();
    }
}
