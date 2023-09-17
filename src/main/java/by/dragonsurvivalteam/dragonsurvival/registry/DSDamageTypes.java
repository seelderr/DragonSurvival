package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class DSDamageTypes {
	public static final ResourceKey<DamageType> STAR_DRAIN = createKey("star_drain");
	public static final ResourceKey<DamageType> WATER_BURN = createKey("water_burn");
	public static final ResourceKey<DamageType> RAIN_BURN = createKey("rain_burn");
	public static final ResourceKey<DamageType> DEHYDRATION = createKey("dehydration");
	public static final ResourceKey<DamageType> SPECTRA_IMPACT = createKey("spectra_impact"); // TODO 1.20 :: tags -> BYPASSES_ARMOR
	public static final ResourceKey<DamageType> DRAGON_BREATH = createKey("dragon_breath");
	public static final ResourceKey<DamageType> FOREST_DRAGON_DRAIN = createKey("forest_dragon_drain"); // TODO 1.20 :: tags -> BYPASSES_ARMOR / setMagic()
	public static final ResourceKey<DamageType> CAVE_DRAGON_BURN = createKey("cave_dragon_burn"); // TODO 1.20 :: tags -> BYPASSES_ARMOR / DamageTypeTags.IS_FIRE

	public static void bootstrap(final BootstapContext<DamageType> context) {
		/* FIXME :: Unused - still needed? */ context.register(STAR_DRAIN, new DamageType("dragonsurvival.star_drain", 0.1F));
		context.register(WATER_BURN, new DamageType("dragonsurvival.water_burn", 0.1F));
		context.register(RAIN_BURN, new DamageType("dragonsurvival.rain_burn", 0.1F));
		context.register(DEHYDRATION, new DamageType("dragonsurvival.dehydration", 0.1F));
		context.register(DRAGON_BREATH, new DamageType("dragonsurvival.dragon_breath", 0.1F));
		context.register(FOREST_DRAGON_DRAIN, new DamageType("dragonsurvival.forest_dragon_drain", 0.1F));
		context.register(FOREST_DRAGON_DRAIN, new DamageType("dragonsurvival.cave_dragon_burn", 0.1F));
	}

	public static DamageSource damageSource(final Level level, final ResourceKey<DamageType> damageType) {
		return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageType));
	}

	public static DamageSource entityDamageSource(final Level level, final ResourceKey<DamageType> damageType, final Entity entity) {
		return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageType), entity);
	}

	private static ResourceKey<DamageType> createKey(final String name) {
		return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(DragonSurvivalMod.MODID, name));
	}
}