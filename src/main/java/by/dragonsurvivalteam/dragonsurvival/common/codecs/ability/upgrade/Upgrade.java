package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.upgrade;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.MutableComponent;

public record Upgrade(Either<ValueBasedUpgrade, ItemBasedUpgrade> upgrade) {
    public static final Codec<Upgrade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.either(ValueBasedUpgrade.CODEC, ItemBasedUpgrade.CODEC).fieldOf("upgrade").forGetter(Upgrade::upgrade)
    ).apply(instance, Upgrade::new));

    public MutableComponent getDescription(int level) {
        return upgrade.map(levelBasedUpgrade -> levelBasedUpgrade.getDescription(level), itemBasedUpgrade -> itemBasedUpgrade.getDescription(level));
    }

    public ValueBasedUpgrade.Type type() {
        return upgrade.left().map(ValueBasedUpgrade::type).orElse(null);
    }

    public int maximumLevel() {
        return upgrade.map(ValueBasedUpgrade::maximumLevel, itemBasedUpgrade -> itemBasedUpgrade.itemsPerLevel().size());
    }

    public boolean isLevelBased() {
        return upgrade.left().isPresent();
    }

    public boolean isItemBased() {
        return upgrade.right().isPresent();
    }

    public ValueBasedUpgrade asLevelBased() {
        return upgrade.left().orElse(null);
    }

    public ItemBasedUpgrade asItemBased() {
        return upgrade.right().orElse(null);
    }
}