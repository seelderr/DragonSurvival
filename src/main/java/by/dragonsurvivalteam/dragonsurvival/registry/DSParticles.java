package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.particles.CaveDragon.LargeFireParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.CaveDragon.SmallFireParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.CaveDragon.SmallFireParticleData;
import by.dragonsurvivalteam.dragonsurvival.client.particles.ForestDragon.LargePoisonParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.ForestDragon.LargePoisonParticleData;
import by.dragonsurvivalteam.dragonsurvival.client.particles.ForestDragon.SmallPoisonParticle.ForestFactory;
import by.dragonsurvivalteam.dragonsurvival.client.particles.ForestDragon.SmallPoisonParticleData;
import by.dragonsurvivalteam.dragonsurvival.client.particles.SeaDragon.LargeLightningParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.SeaDragon.LargeLightningParticleData;
import by.dragonsurvivalteam.dragonsurvival.client.particles.SeaDragon.SmallLightningParticle.SeaFactory;
import by.dragonsurvivalteam.dragonsurvival.client.particles.SeaDragon.SmallLightningParticleData;
import by.dragonsurvivalteam.dragonsurvival.client.particles.TreasureParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.TreasureParticleData;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber( bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DSParticles{
	public static final DeferredRegister<ParticleType<?>> DS_PARTICLES = DeferredRegister.create(
			BuiltInRegistries.PARTICLE_TYPE,
			DragonSurvivalMod.MODID
	);

	public static final Holder<ParticleType<TreasureParticleData>> TREASURE = DS_PARTICLES.register("treasures", () -> new ParticleType<TreasureParticleData>(false, TreasureParticleData.DESERIALIZER){
		@Override
		public StreamCodec<TreasureParticleData> codec(){
			return TreasureParticleData.CODEC;
		}

		@Override
		public StreamCodec<? super RegistryFriendlyByteBuf, TreasureParticleData> streamCodec() {
			return TreasureParticleData.CODEC;
		}
	});
	public static SimpleParticleType fireBeaconParticle, magicBeaconParticle, peaceBeaconParticle;
	public static SimpleParticleType seaSweep;

	public static void register()
	{
		fireBeaconParticle = new SimpleParticleType(false);
		DS_PARTICLES.register("netherite_particle", ()->fireBeaconParticle);

		peaceBeaconParticle = new SimpleParticleType(false);
		DS_PARTICLES.register("gold_particle", ()->peaceBeaconParticle);

		magicBeaconParticle = new SimpleParticleType(false);
		DS_PARTICLES.register("diamond_particle", ()->magicBeaconParticle);

		seaSweep = new SimpleParticleType(false);
		DS_PARTICLES.register("sea_sweep", ()->seaSweep);
	}

	@SubscribeEvent( priority = EventPriority.LOWEST)
	public static void registerParticles(RegisterParticleProvidersEvent event){
		event.registerSpriteSet(DSParticles.FIRE.get(), SmallFireParticle.FireFactory::new);
		event.registerSpriteSet(DSParticles.LARGE_FIRE.get(), LargeFireParticle.FireFactory::new);
		event.registerSpriteSet(DSParticles.POISON.get(), ForestFactory::new);
		event.registerSpriteSet(DSParticles.LARGE_POISON.get(), LargePoisonParticle.ForestFactory::new);
		event.registerSpriteSet(DSParticles.LIGHTNING.get(), SeaFactory::new);
		event.registerSpriteSet(DSParticles.LARGE_LIGHTNING.get(), LargeLightningParticle.SeaFactory::new);
		event.registerSpriteSet(DSParticles.TREASURE.get(), TreasureParticle.Factory::new);
	}

	public static final RegistryObject<ParticleType<SmallFireParticleData>> FIRE = DS_PARTICLES.register("fire", () -> new ParticleType<>(false, SmallFireParticleData.DESERIALIZER) {
        @Override
        public Codec<SmallFireParticleData> codec() {
            return SmallFireParticleData.CODEC(FIRE.get());
        }
    });

	public static final DeferredHolder<ParticleType<?>, ParticleType<LargeFireParticle>> LARGE_FIRE = DS_PARTICLES.register("large_fire", () -> new ParticleType<>(false, LargeFireParticleData.DESERIALIZER) {
		@Override
		public Codec<LargeFireParticleData> codec() {
			return LargeFireParticleData.CODEC(LARGE_FIRE.get());
		}
	});


	public static final RegistryObject<ParticleType<LargeFireParticleData>> LARGE_FIRE = DS_PARTICLES.register("large_fire", () -> new ParticleType<>(false, LargeFireParticleData.DESERIALIZER) {
        @Override
        public Codec<LargeFireParticleData> codec() {
            return LargeFireParticleData.CODEC(LARGE_FIRE.get());
        }
    });


	public static final RegistryObject<ParticleType<SmallPoisonParticleData>> POISON = DS_PARTICLES.register("poison", () -> new ParticleType<>(false, SmallPoisonParticleData.DESERIALIZER) {
        @Override
        public Codec<SmallPoisonParticleData> codec() {
            return SmallPoisonParticleData.CODEC(POISON.get());
        }
    });

	public static final RegistryObject<ParticleType<LargePoisonParticleData>> LARGE_POISON = DS_PARTICLES.register("large_poison", () -> new ParticleType<>(false, LargePoisonParticleData.DESERIALIZER) {
        @Override
        public Codec<LargePoisonParticleData> codec() {
            return LargePoisonParticleData.CODEC(LARGE_POISON.get());
        }
    });

	public static final RegistryObject<ParticleType<SmallLightningParticleData>> LIGHTNING = DS_PARTICLES.register("lightning", () -> new ParticleType<>(false, SmallLightningParticleData.DESERIALIZER) {
        @Override
        public Codec<SmallLightningParticleData> codec() {
            return SmallLightningParticleData.CODEC(LIGHTNING.get());
        }
    });

	public static final RegistryObject<ParticleType<LargeLightningParticleData>> LARGE_LIGHTNING = DS_PARTICLES.register("large_lightning", () -> new ParticleType<>(false, LargeLightningParticleData.DESERIALIZER) {
        @Override
        public Codec<LargeLightningParticleData> codec() {
            return LargeLightningParticleData.CODEC(LARGE_LIGHTNING.get());
        }
    });
}