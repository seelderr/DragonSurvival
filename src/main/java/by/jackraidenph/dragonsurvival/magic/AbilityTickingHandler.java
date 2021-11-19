package by.jackraidenph.dragonsurvival.magic;

import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.magic.common.DragonAbility;
import com.google.common.collect.Queues;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.concurrent.ConcurrentLinkedQueue;

public class AbilityTickingHandler {

    private ConcurrentLinkedQueue<DragonAbility> abilitiesToTick = Queues.newConcurrentLinkedQueue();
    private ConcurrentLinkedQueue<ActiveDragonAbility> abilitiesToCoolDown = Queues.newConcurrentLinkedQueue();
    private ConcurrentLinkedQueue<DragonAbility> abilitiesToFrame = Queues.newConcurrentLinkedQueue();

    public void addToCoolDownList(ActiveDragonAbility ability) {
        if (!this.abilitiesToCoolDown.contains(ability))
            this.abilitiesToCoolDown.add(ability);
    }

    public void addToFrameList(DragonAbility ability) {
        if (!this.abilitiesToFrame.contains(ability))
            this.abilitiesToFrame.add(ability);
    }

    public void removeFromCoolDownList(DragonAbility ability) {
        this.abilitiesToCoolDown.remove(ability);
    }

    public void removeFromFrameList(DragonAbility ability) {
        this.abilitiesToFrame.remove(ability);
    }

    public void addToTickList(DragonAbility ability) {
        if (!this.abilitiesToTick.contains(ability))
            this.abilitiesToTick.add(ability);
    }

    public void removeFromTickList(DragonAbility ability) {
        this.abilitiesToTick.remove(ability);
    }

    @SubscribeEvent
    public void tickAbilities(TickEvent.ServerTickEvent e) {
        abilitiesToTick.forEach(DragonAbility::tick);
        abilitiesToCoolDown.forEach(this::decreaseCooldownTimer);
    }

    @SubscribeEvent
    public void frameAbilities(RenderWorldLastEvent e){
        abilitiesToFrame.forEach(x -> x.frame(e.getPartialTicks()));
    }

    private void decreaseCooldownTimer(ActiveDragonAbility ability) {
        if (ability.getCooldown() != 0)
            ability.decreaseCooldownTimer();
        else
            this.removeFromCoolDownList(ability);
    }
}
