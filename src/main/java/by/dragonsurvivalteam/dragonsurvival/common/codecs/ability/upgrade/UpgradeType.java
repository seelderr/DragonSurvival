package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.upgrade;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;

import java.lang.reflect.ParameterizedType;

/** The type cannot be a parameterized type because the parameter from the input cannot be validated to match (due to type erasure) */
public abstract class UpgradeType<T> {
    @SuppressWarnings("unchecked") // ignore
    public boolean attemptUpgrade(final DragonAbilityInstance instance, final Object input) { // TODO :: technically its up- and downgrade
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        Class<?> classOfType = (Class<?>) type.getActualTypeArguments()[0];

        if (classOfType.isAssignableFrom(input.getClass())) {
            return upgrade(instance, (T) input);
        }

        return false;
    }

    public float getExperienceCost(int abilityLevel) {
        return 0;
    }

    abstract protected boolean upgrade(final DragonAbilityInstance ability, final T input);
}
