package by.jackraidenph.dragonsurvival.magic;

import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.magic.common.DragonAbility;
import com.google.common.collect.Queues;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.concurrent.ConcurrentLinkedQueue;

public class AbilityTickingHandler {
    private ConcurrentLinkedQueue<ActiveDragonAbility> abilitiesToCoolDown = Queues.newConcurrentLinkedQueue();
    
    @SubscribeEvent
    public void tickAbilities(TickEvent.ClientTickEvent e) {
        abilitiesToCoolDown.forEach(this::decreaseCooldownTimer);
    }
    
    public void addToCoolDownList(ActiveDragonAbility ability) {
        if (!this.abilitiesToCoolDown.contains(ability))
            this.abilitiesToCoolDown.add(ability);
    }
    
    public void removeFromCoolDownList(DragonAbility ability) {
        this.abilitiesToCoolDown.remove(ability);
    }
    
    private void decreaseCooldownTimer(ActiveDragonAbility ability) {
        if (ability.getCooldown() != 0)
            ability.decreaseCooldownTimer();
        else
            this.removeFromCoolDownList(ability);
    }
}
