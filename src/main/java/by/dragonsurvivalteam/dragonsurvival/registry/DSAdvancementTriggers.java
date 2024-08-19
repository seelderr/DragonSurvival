package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.common.criteria.BecomeDragonTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

@SuppressWarnings("unused")
public class DSAdvancementTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> DS_TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, MODID);

    public static final Supplier<BecomeDragonTrigger> BECOME_DRAGON = DS_TRIGGERS.register(
            "become_dragon", BecomeDragonTrigger::new
    );
}
