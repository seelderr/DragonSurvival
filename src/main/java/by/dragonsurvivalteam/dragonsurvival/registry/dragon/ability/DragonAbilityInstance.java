package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability;

import net.minecraft.core.Holder;

public class DragonAbilityInstance {
    private final Holder<DragonAbility> ability;
    private int level;

    public DragonAbilityInstance(final Holder<DragonAbility> ability) {
        this.ability = ability;
    }
}
