package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.common.criteria.BeDragonTrigger;
import by.dragonsurvivalteam.dragonsurvival.common.criteria.SleepOnTreasureTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

@SuppressWarnings("unused")
public class DSAdvancementTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> DS_TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, MODID);

    public static final Supplier<BeDragonTrigger> BE_DRAGON = DS_TRIGGERS.register(
            "be_dragon", BeDragonTrigger::new
    );
    public static final Supplier<SleepOnTreasureTrigger> SLEEP_ON_TREASURE = DS_TRIGGERS.register(
            "sleep_on_treasure", SleepOnTreasureTrigger::new
    );
}
