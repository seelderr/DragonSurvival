package by.dragonsurvivalteam.dragonsurvival.client.sounds;


import by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.active.StormBreathAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DSSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StormBreathSound extends AbstractTickableSoundInstance {
    private final StormBreathAbility ability;

    public StormBreathSound(StormBreathAbility ability) {
        super(DSSounds.STORM_BREATH_LOOP.get(), SoundSource.PLAYERS, ability.getPlayer().getRandom());

        looping = true;

        this.x = ability.getPlayer().getX();
        this.y = ability.getPlayer().getY();
        this.z = ability.getPlayer().getZ();

        this.ability = ability;
    }

    @Override
    public void tick() {
        if (ability.getPlayer() == null || ability.chargeTime == 0)
            stop();

        this.x = ability.getPlayer().getX();
        this.y = ability.getPlayer().getY();
        this.z = ability.getPlayer().getZ();
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }
}