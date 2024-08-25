package by.dragonsurvivalteam.dragonsurvival.common.criteria;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class BecomeDragonTrigger extends SimpleCriterionTrigger<BecomeDragonTrigger.BecomeDragonInstance> {
    public void trigger(ServerPlayer player) {
        this.trigger(player, triggerInstance -> DragonStateProvider.isDragon(player));
    }

    @Override
    public Codec<BecomeDragonInstance> codec() {
        return BecomeDragonInstance.CODEC;
    }

    public record BecomeDragonInstance(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<BecomeDragonInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(BecomeDragonInstance::player)
        ).apply(instance, BecomeDragonInstance::new));
    }
}
