package by.dragonsurvivalteam.dragonsurvival.common.codecs.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record RandomPredicate(LevelBasedValue probability) {
    public static final Codec<RandomPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("probability").forGetter(RandomPredicate::probability)
    ).apply(instance, RandomPredicate::new));

    public boolean matches(ServerLevel serverLevel, int level) {
        return serverLevel.random.nextFloat() < probability.calculate(level);
    }
}
