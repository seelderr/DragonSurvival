package by.dragonsurvivalteam.dragonsurvival.common.criteria;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import com.mojang.serialization.Codec;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class BecomeDragonTrigger extends SimpleCriterionTrigger<BecomeDragonInstance> {
    public void trigger(ServerPlayer player) {
        this.trigger(player, triggerInstance -> DragonStateProvider.isDragon(player));
    }

    @Override
    public @NotNull Codec<BecomeDragonInstance> codec() {
        return BecomeDragonInstance.CODEC;
    }
}
