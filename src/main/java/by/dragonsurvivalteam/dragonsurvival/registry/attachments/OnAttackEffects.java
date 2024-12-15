package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.OnAttackEffectInstance;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber
public class OnAttackEffects {
    private final Map<String, OnAttackEffectInstance> effectsToApply = new HashMap<>();

    public static OnAttackEffects getData(Entity entity) {
        return entity.getData(DSDataAttachments.ON_ATTACK_EFFECTS);
    }

    public void addEffect(String id, OnAttackEffectInstance effect) {
        effectsToApply.put(id, effect);
    }

    public void removeEffect(String id) {
        effectsToApply.remove(id);
    }

    @SubscribeEvent
    public static void applyOnAttackEffects(AttackEntityEvent event) {
        OnAttackEffects onAttackEffects = getData(event.getEntity());
        for (OnAttackEffectInstance effect : onAttackEffects.effectsToApply.values()) {
            effect.apply(event.getTarget());
        }
    }
}
