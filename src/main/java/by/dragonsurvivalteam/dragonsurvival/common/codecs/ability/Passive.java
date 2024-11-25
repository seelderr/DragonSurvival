package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record Passive(Either<Reserved, Ticked> manaHandling) {
    public static final Codec<Passive> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.either(Reserved.CODEC, Ticked.CODEC).fieldOf("mana_handling").forGetter(Passive::manaHandling)
    ).apply(instance, Passive::new));

    public record Reserved(LevelBasedValue reservedMana) {
        public static final Codec<Reserved> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                LevelBasedValue.CODEC.fieldOf("reserved_mana").forGetter(Reserved::reservedMana)
        ).apply(instance, Reserved::new));
    }

    public record Ticked(LevelBasedValue manaCost, LevelBasedValue tickRate) {
        public static final Codec<Ticked> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                LevelBasedValue.CODEC.fieldOf("mana_cost").forGetter(Ticked::manaCost),
                LevelBasedValue.CODEC.fieldOf("tick_Rate").forGetter(Ticked::tickRate)
        ).apply(instance, Ticked::new));
    }
}