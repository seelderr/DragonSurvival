package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.particles.BeaconParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.CaveDragon.LargeFireParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.CaveDragon.SmallFireParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.DragonParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.ForestDragon.LargePoisonParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.ForestDragon.SmallPoisonParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.SeaDragon.LargeLightningParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.SeaDragon.SmallLightningParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.SeaSweepParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.TreasureParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber( bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DSParticles{
	public static final DeferredRegister<ParticleType<?>> DS_PARTICLES = DeferredRegister.create(
			BuiltInRegistries.PARTICLE_TYPE,
			DragonSurvivalMod.MODID
	);

	public static final DeferredHolder<ParticleType<?>, ParticleType<DragonParticle.Data>> FIRE = DS_PARTICLES.register(
			"fire",
			() -> new DragonParticle.Type(false)
	);

	public static final DeferredHolder<ParticleType<?>, ParticleType<DragonParticle.Data>> LARGE_FIRE = DS_PARTICLES.register(
			"large_fire",
			() -> new DragonParticle.Type(false)
	);

	public static final DeferredHolder<ParticleType<?>, ParticleType<DragonParticle.Data>> POISON = DS_PARTICLES.register(
			"poison",
			() -> new DragonParticle.Type(false)
	);

	public static final DeferredHolder<ParticleType<?>, ParticleType<DragonParticle.Data>> LARGE_POISON = DS_PARTICLES.register(
			"large_poison",
			() -> new DragonParticle.Type(false)
	);

	public static final DeferredHolder<ParticleType<?>, ParticleType<DragonParticle.Data>> LIGHTNING = DS_PARTICLES.register(
			"lightning",
			() -> new DragonParticle.Type(false)
	);

	public static final DeferredHolder<ParticleType<?>, ParticleType<DragonParticle.Data>> LARGE_LIGHTNING = DS_PARTICLES.register(
			"large_lightning",
			() -> new DragonParticle.Type(false)
	);

	public static final DeferredHolder<ParticleType<?>, ParticleType<TreasureParticle.Data>> TREASURE = DS_PARTICLES.register(
			"treasures",
			() -> new TreasureParticle.Type(false)
	);

	public static final DeferredHolder<ParticleType<?>, ParticleType<SimpleParticleType>> SEA_SWEEP = DS_PARTICLES.register(
			"sea_sweep",
			() -> new SimpleParticleType(false)
	);

	public static final DeferredHolder<ParticleType<?>, ParticleType<SimpleParticleType>> FIRE_BEACON_PARTICLE = DS_PARTICLES.register(
			"netherite_particle",
			() -> new SimpleParticleType(false)
	);

	public static final DeferredHolder<ParticleType<?>, ParticleType<SimpleParticleType>> MAGIC_BEACON_PARTICLE = DS_PARTICLES.register(
			"diamond_particle",
			() -> new SimpleParticleType(false)
	);

	public static final DeferredHolder<ParticleType<?>, ParticleType<SimpleParticleType>> PEACE_BEACON_PARTICLE = DS_PARTICLES.register(
			"gold_particle",
			() -> new SimpleParticleType(false)
	);

	@SubscribeEvent( priority = EventPriority.LOWEST)
	public static void registerParticles(RegisterParticleProvidersEvent event){
		event.registerSpriteSet(DSParticles.FIRE.get(), SmallFireParticle.Factory::new);
		event.registerSpriteSet(DSParticles.LARGE_FIRE.get(), LargeFireParticle.Factory::new);
		event.registerSpriteSet(DSParticles.POISON.get(), SmallPoisonParticle.Factory::new);
		event.registerSpriteSet(DSParticles.LARGE_POISON.get(), LargePoisonParticle.Factory::new);
		event.registerSpriteSet(DSParticles.LIGHTNING.get(), SmallLightningParticle.Factory::new);
		event.registerSpriteSet(DSParticles.LARGE_LIGHTNING.get(), LargeLightningParticle.Factory::new);
		event.registerSpriteSet(DSParticles.TREASURE.get(), TreasureParticle.Factory::new);
		event.registerSpriteSet(DSParticles.SEA_SWEEP.get(), SeaSweepParticle.Factory::new);
		event.registerSpriteSet(DSParticles.FIRE_BEACON_PARTICLE.get(), BeaconParticle.Factory::new);
		event.registerSpriteSet(DSParticles.MAGIC_BEACON_PARTICLE.get(), BeaconParticle.Factory::new);
		event.registerSpriteSet(DSParticles.PEACE_BEACON_PARTICLE.get(), BeaconParticle.Factory::new);
	}
}