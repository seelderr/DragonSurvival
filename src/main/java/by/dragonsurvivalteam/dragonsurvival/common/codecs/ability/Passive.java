package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record Passive(Optional<Either<ManaCost.Ticked, ManaCost.Reserved>> manaHandling) {
    public static final Codec<Passive> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.either(ManaCost.Ticked.CODEC, ManaCost.Reserved.CODEC).optionalFieldOf("usage_mana_cost").forGetter(Passive::manaHandling)
    ).apply(instance, Passive::new));
}