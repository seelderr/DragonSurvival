package by.jackraidenph.dragonsurvival.handlers.Magic;

import by.jackraidenph.dragonsurvival.handlers.ServerSide.NetworkHandler;
import by.jackraidenph.dragonsurvival.magic.DragonAbilities;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.network.magic.SyncAbilityCooldown;
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
    
    private void decreaseCooldownTimer(ActiveDragonAbility ability) {
        if (ability.getCooldown() != 0) {
            ability.decreaseCooldownTimer();
        } else {
            this.abilitiesToCoolDown.remove(ability);
    
            int abilityId = DragonAbilities.getAbilitySlot(ability);
            
            if(abilityId != -1){
                NetworkHandler.CHANNEL.sendToServer(new SyncAbilityCooldown(abilityId, 0));
            }
        }
    }
}
