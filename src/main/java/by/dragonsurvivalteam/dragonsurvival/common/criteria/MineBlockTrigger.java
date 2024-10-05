package by.dragonsurvivalteam.dragonsurvival.common.criteria;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.BeeNestDestroyedTrigger;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class MineBlockTrigger extends SimpleCriterionTrigger<MineBlockTrigger.MineBlockInstance> {
    public void trigger(ServerPlayer pPlayer, Block block) {
        this.trigger(pPlayer, triggerInstance -> {
            if (triggerInstance.block.isPresent()) {
                return triggerInstance.block.get().value().equals(block);
            }
            return true;
        });
    }

    @Override
    public Codec<MineBlockTrigger.MineBlockInstance> codec() {
        return MineBlockTrigger.MineBlockInstance.CODEC;
    }

    public record MineBlockInstance(Optional<ContextAwarePredicate> player, Optional<Holder<Block>> block) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<MineBlockTrigger.MineBlockInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(MineBlockTrigger.MineBlockInstance::player),
                BuiltInRegistries.BLOCK.holderByNameCodec().optionalFieldOf("block").forGetter(MineBlockTrigger.MineBlockInstance::block)
        ).apply(instance, MineBlockTrigger.MineBlockInstance::new));
    }
}
