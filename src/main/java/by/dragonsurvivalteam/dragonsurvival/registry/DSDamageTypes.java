package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

public class DSDamageTypes {
    // We don't need to use a DeferredRegister for DamageTypes, as they are fully data driven.
    public static final ResourceKey<DamageType> WATER_BURN = createKey("water_burn");
    public static final ResourceKey<DamageType> RAIN_BURN = createKey("rain_burn");
    public static final ResourceKey<DamageType> DEHYDRATION = createKey("dehydration");
    public static final ResourceKey<DamageType> SPECTRAL_IMPACT = createKey("spectral_impact");
    public static final ResourceKey<DamageType> DRAGON_BREATH = createKey("dragon_breath");
    public static final ResourceKey<DamageType> CAVE_DRAGON_BREATH = createKey("cave_dragon_breath");
    public static final ResourceKey<DamageType> FOREST_DRAGON_BREATH = createKey("forest_dragon_breath");
    public static final ResourceKey<DamageType> SEA_DRAGON_BREATH = createKey("sea_dragon_breath");
    public static final ResourceKey<DamageType> FOREST_DRAGON_DRAIN = createKey("forest_dragon_drain"); // TODO 1.20 :: tags -> setMagic()
    public static final ResourceKey<DamageType> CAVE_DRAGON_BURN = createKey("cave_dragon_burn");
    public static final ResourceKey<DamageType> DRAGON_BALL_LIGHTNING = createKey("dragon_ball_lightning");
    public static final ResourceKey<DamageType> CRUSHED = createKey("crushed");

    public static Holder<DamageType> get(final Level level, final ResourceKey<DamageType> damageType) {
        return level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageType);
    }

    private static ResourceKey<DamageType> createKey(final String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, name));
    }
}