package by.dragonsurvivalteam.dragonsurvival.registry.dragon;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.Modifier;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import javax.annotation.Nullable;

public interface AttributeModifierSupplier {
    default void applyModifiers(final Player player) {
        DragonStateHandler data = DragonStateProvider.getData(player);

        modifiers().forEach(modifier -> {
            AttributeInstance instance = player.getAttribute(modifier.attribute());
            applyModifier(modifier, instance, data.getTypeNameLowerCase(), data.getSize());
        });
    }

    /** Intended for usage within descriptions */
    default double getAttributeValue(final String dragonType, double size, final Holder<Attribute> attribute) {
        AttributeInstance instance = new AttributeInstance(attribute, ignored -> { /* Nothing to do */ });
        applyModifiers(instance, dragonType, size);
        return instance.getValue();
    }

    private void applyModifier(final Modifier modifier, @Nullable final AttributeInstance instance, final String dragonType, double size) {
        if (instance == null || modifier.dragonType().isPresent() && !modifier.dragonType().get().equals(dragonType)) {
            return;
        }

        instance.addPermanentModifier(modifier.getModifier(size));
    }

    private void applyModifiers(@Nullable final AttributeInstance instance, final String dragonType, double size) {
        if (instance == null) {
            return;
        }

        modifiers().forEach(modifier -> {
            if (modifier.attribute().is(instance.getAttribute())) {
                applyModifier(modifier, instance, dragonType, size);
            }
        });
    }

    default List<Modifier> modifiers() {
        return List.of();
    }
}
