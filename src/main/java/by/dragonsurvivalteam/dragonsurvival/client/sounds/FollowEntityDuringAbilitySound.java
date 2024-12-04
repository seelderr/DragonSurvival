package by.dragonsurvivalteam.dragonsurvival.client.sounds;

import by.dragonsurvivalteam.dragonsurvival.registry.DSSounds;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public class FollowEntityDuringAbilitySound extends AbstractTickableSoundInstance {
    private final Entity entity;
    private final DragonAbilityInstance ability;

    public FollowEntityDuringAbilitySound(SoundEvent event, SoundSource source, Entity entity, DragonAbilityInstance abilityInstance) {
        super(DSSounds.FIRE_BREATH_LOOP.get(), SoundSource.PLAYERS, entity.getRandom());
        looping = true;

        this.ability = abilityInstance;
        this.entity = entity;
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
    }

    @Override
    public void tick() {
        if (ability.canBeCast())
            stop();

        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }
}
