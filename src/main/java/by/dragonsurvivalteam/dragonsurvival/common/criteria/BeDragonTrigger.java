package by.dragonsurvivalteam.dragonsurvival.common.criteria;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class BeDragonTrigger extends SimpleCriterionTrigger<BeDragonTrigger.BeDragonInstance> {
    public void trigger(ServerPlayer player, double size, String type) {
        this.trigger(player, triggerInstance -> {
            boolean flag = DragonStateProvider.isDragon(player);
            if (triggerInstance.size.isPresent()) {
                flag = size > triggerInstance.size.get();
            }
            if (triggerInstance.type.isPresent()) {
                flag = flag && type.equals(triggerInstance.type.get());
            }
            return flag;
        });
    }

    @Override
    public Codec<BeDragonInstance> codec() {
        return BeDragonInstance.CODEC;
    }

    public record BeDragonInstance(Optional<ContextAwarePredicate> player, Optional<Double> size,
                                Optional<String> type) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<BeDragonInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(BeDragonInstance::player),
                Codec.DOUBLE.optionalFieldOf("size").forGetter(BeDragonInstance::size),
                Codec.STRING.optionalFieldOf("type").forGetter(BeDragonInstance::type)
        ).apply(instance, BeDragonInstance::new));
    }
}