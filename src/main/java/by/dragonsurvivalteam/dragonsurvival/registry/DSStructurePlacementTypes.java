package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.AdvancedRandomSpread;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DSStructurePlacementTypes {
    public static final DeferredRegister<StructurePlacementType<?>> DS_STRUCTURE_PLACEMENT_TYPES = DeferredRegister.create(BuiltInRegistries.STRUCTURE_PLACEMENT, DragonSurvivalMod.MODID);

    public static final DeferredHolder<StructurePlacementType<?>, StructurePlacementType<AdvancedRandomSpread>> ADVANCED_RANDOM_SPREAD = DS_STRUCTURE_PLACEMENT_TYPES.register("advanced_random_spread", () -> () -> AdvancedRandomSpread.CODEC);
}
