package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.BreathAbility;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DSDamageTypes {
    public static final DeferredRegister<DamageType> DAMAGE_TYPES = DeferredRegister.create(
            Registries.DAMAGE_TYPE,
            DragonSurvivalMod.MODID
    );

    public static final Holder<DamageType> WATER_BURN = DAMAGE_TYPES.register(
            "water_burn",
            () -> new DamageType("dragonsurvival.water_burn", 0.1F)
    );

    public static final Holder<DamageType> RAIN_BURN = DAMAGE_TYPES.register(
            "rain_burn",
            () -> new DamageType("dragonsurvival.rain_burn", 0.1F)
    );

    public static final Holder<DamageType> DEHYDRATION = DAMAGE_TYPES.register(
            "dehydration",
            () -> new DamageType("dragonsurvival.dehydration", 0.1F)
    );

    public static final Holder<DamageType> SPECTRAL_IMPACT = DAMAGE_TYPES.register(
            "spectral_impact",
            () -> new DamageType("dragonsurvival.spectral_impact", 0.1F)
    );

    public static final Holder<DamageType> DRAGON_BREATH = DAMAGE_TYPES.register(
            "dragon_breath",
            () -> new DamageType("dragonsurvival.dragon_breath", 0.1F)
    );

    public static final Holder<DamageType> CAVE_DRAGON_BREATH = DAMAGE_TYPES.register(
            "cave_dragon_breath",
            () -> new DamageType("cave_dragon_breath", 0.1F)
    );

    public static final Holder<DamageType> FOREST_DRAGON_BREATH = DAMAGE_TYPES.register(
            "forest_dragon_breath",
            () -> new DamageType("forest_dragon_breath", 0.1F)
    );

    public static final Holder<DamageType> SEA_DRAGON_BREATH = DAMAGE_TYPES.register(
            "sea_dragon_breath",
            () -> new DamageType("sea_dragon_breath", 0.1F)
    );

    public static final Holder<DamageType> FOREST_DRAGON_DRAIN = DAMAGE_TYPES.register(
            "forest_dragon_drain",
            () -> new DamageType("dragonsurvival.forest_dragon_drain", 0.1F)
    );

    public static final Holder<DamageType> CAVE_DRAGON_BURN = DAMAGE_TYPES.register(
            "cave_dragon_burn",
            () -> new DamageType("dragonsurvival.cave_dragon_burn", 0.1F)
    );

    public static final Holder<DamageType> DRAGON_BALL_LIGHTNING = DAMAGE_TYPES.register(
            "dragon_ball_lightning",
            () -> new DamageType("dragonsurvival.dragon_ball_lightning", 0.1F)
    );

    public static final Holder<DamageType> CRUSHED = DAMAGE_TYPES.register("crushed",
            () -> new DamageType("dragonsurvival.crushed", 0.1F)
    );
}