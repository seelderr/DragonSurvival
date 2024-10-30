package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.client.particles.BeaconParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.SeaSweepParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.TreasureParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.dragon.CaveDragon.LargeFireParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.dragon.CaveDragon.SmallFireParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.dragon.ForestDragon.LargePoisonParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.dragon.ForestDragon.SmallPoisonParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.dragon.SeaDragon.LargeLightningParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.dragon.SeaDragon.SmallLightningParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DSParticles {
    public static final DeferredRegister<ParticleType<?>> DS_PARTICLES = DeferredRegister.create(
            BuiltInRegistries.PARTICLE_TYPE,
            DragonSurvival.MODID
    );

    public static final DeferredHolder<ParticleType<?>, ParticleType<SmallFireParticle.Data>> FIRE = DS_PARTICLES.register(
            "fire",
            () -> SmallFireParticle.Data.TYPE
    );

    public static final DeferredHolder<ParticleType<?>, ParticleType<LargeFireParticle.Data>> LARGE_FIRE = DS_PARTICLES.register(
            "large_fire",
            () -> LargeFireParticle.Data.TYPE
    );

    public static final DeferredHolder<ParticleType<?>, ParticleType<SmallPoisonParticle.Data>> POISON = DS_PARTICLES.register(
            "poison",
            () -> SmallPoisonParticle.Data.TYPE
    );

    public static final DeferredHolder<ParticleType<?>, ParticleType<LargePoisonParticle.Data>> LARGE_POISON = DS_PARTICLES.register(
            "large_poison",
            () -> LargePoisonParticle.Data.TYPE
    );

    public static final DeferredHolder<ParticleType<?>, ParticleType<SmallLightningParticle.Data>> LIGHTNING = DS_PARTICLES.register(
            "lightning",
            () -> SmallLightningParticle.Data.TYPE
    );

    public static final DeferredHolder<ParticleType<?>, ParticleType<LargeLightningParticle.Data>> LARGE_LIGHTNING = DS_PARTICLES.register(
            "large_lightning",
            () -> LargeLightningParticle.Data.TYPE
    );

    public static final DeferredHolder<ParticleType<?>, ParticleType<TreasureParticle.Data>> TREASURE = DS_PARTICLES.register(
            "treasures",
            () -> TreasureParticle.Data.TYPE
    );

    public static final DeferredHolder<ParticleType<?>, SeaSweepParticle.Type> SEA_SWEEP = DS_PARTICLES.register(
            "sea_sweep",
            () -> SeaSweepParticle.Data.TYPE
    );

    public static final DeferredHolder<ParticleType<?>, BeaconParticle.FireType> FIRE_BEACON_PARTICLE = DS_PARTICLES.register(
            "netherite_particle",
            () -> BeaconParticle.FireData.TYPE
    );

    public static final DeferredHolder<ParticleType<?>, BeaconParticle.MagicType> MAGIC_BEACON_PARTICLE = DS_PARTICLES.register(
            "diamond_particle",
            () -> BeaconParticle.MagicData.TYPE
    );

    public static final DeferredHolder<ParticleType<?>, BeaconParticle.PeaceType> PEACE_BEACON_PARTICLE = DS_PARTICLES.register(
            "gold_particle",
            () -> BeaconParticle.PeaceData.TYPE
    );

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(DSParticles.FIRE.get(), SmallFireParticle.Factory::new);
        event.registerSpriteSet(DSParticles.LARGE_FIRE.get(), LargeFireParticle.Factory::new);
        event.registerSpriteSet(DSParticles.POISON.get(), SmallPoisonParticle.Factory::new);
        event.registerSpriteSet(DSParticles.LARGE_POISON.get(), LargePoisonParticle.Factory::new);
        event.registerSpriteSet(DSParticles.LIGHTNING.get(), SmallLightningParticle.Factory::new);
        event.registerSpriteSet(DSParticles.LARGE_LIGHTNING.get(), LargeLightningParticle.Factory::new);
        event.registerSpriteSet(DSParticles.TREASURE.get(), TreasureParticle.Factory::new);
        event.registerSpriteSet(DSParticles.SEA_SWEEP.get(), SeaSweepParticle.Factory::new);
        event.registerSpriteSet(DSParticles.FIRE_BEACON_PARTICLE.get(), BeaconParticle.FireFactory::new);
        event.registerSpriteSet(DSParticles.MAGIC_BEACON_PARTICLE.get(), BeaconParticle.MagicFactory::new);
        event.registerSpriteSet(DSParticles.PEACE_BEACON_PARTICLE.get(), BeaconParticle.PeaceFactory::new);
    }
}