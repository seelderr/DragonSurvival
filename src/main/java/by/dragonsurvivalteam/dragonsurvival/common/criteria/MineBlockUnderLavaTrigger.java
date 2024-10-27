package by.dragonsurvivalteam.dragonsurvival.common.criteria;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class MineBlockUnderLavaTrigger extends SimpleCriterionTrigger<MineBlockUnderLavaTrigger.MineBlockUnderLavaInstance> {
    public void trigger(ServerPlayer player, Block block) {
        // If no block is specified it will act as any block should trigger the advancement
        this.trigger(player, instance -> instance.block.map(holder -> holder.value().equals(block)).orElse(true));
    }

    @Override
    public @NotNull Codec<MineBlockUnderLavaTrigger.MineBlockUnderLavaInstance> codec() {
        return MineBlockUnderLavaTrigger.MineBlockUnderLavaInstance.CODEC;
    }

    public record MineBlockUnderLavaInstance(Optional<ContextAwarePredicate> player, Optional<Holder<Block>> block) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<MineBlockUnderLavaTrigger.MineBlockUnderLavaInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(MineBlockUnderLavaTrigger.MineBlockUnderLavaInstance::player),
                BuiltInRegistries.BLOCK.holderByNameCodec().optionalFieldOf("block").forGetter(MineBlockUnderLavaTrigger.MineBlockUnderLavaInstance::block)
        ).apply(instance, MineBlockUnderLavaTrigger.MineBlockUnderLavaInstance::new));
    }
}
