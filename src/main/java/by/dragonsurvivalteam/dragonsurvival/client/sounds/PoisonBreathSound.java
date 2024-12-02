//package by.dragonsurvivalteam.dragonsurvival.client.sounds;
//
//
//import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.active.ForestBreathAbility;
//import by.dragonsurvivalteam.dragonsurvival.registry.DSSounds;
//import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
//import net.minecraft.sounds.SoundSource;
//
//public class PoisonBreathSound extends AbstractTickableSoundInstance {
//    private final ForestBreathAbility ability;
//
//    public PoisonBreathSound(ForestBreathAbility ability) {
//        super(DSSounds.FOREST_BREATH_LOOP.get(), SoundSource.PLAYERS, ability.getPlayer().getRandom());
//
//        looping = true;
//        this.x = ability.getPlayer().getX();
//        this.y = ability.getPlayer().getY();
//        this.z = ability.getPlayer().getZ();
//
//        this.ability = ability;
//    }
//
//    @Override
//    public void tick() {
//        if (ability.getPlayer() == null || ability.chargeTime == 0)
//            stop();
//
//        this.x = ability.getPlayer().getX();
//        this.y = ability.getPlayer().getY();
//        this.z = ability.getPlayer().getZ();
//    }
//
//    @Override
//    public boolean canStartSilent() {
//        return true;
//    }
//}