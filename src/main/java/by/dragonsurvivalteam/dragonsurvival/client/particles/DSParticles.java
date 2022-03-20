package by.dragonsurvivalteam.dragonsurvival.client.particles;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.particles.CaveDragon.LargeFireParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.CaveDragon.LargeFireParticleData;
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
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.MOD )
public class DSParticles{
	public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, DragonSurvivalMod.MODID);
	public static final RegistryObject<ParticleType<TreasureParticleData>> TREASURE = REGISTRY.register("treasures", () -> new ParticleType<TreasureParticleData>(false, TreasureParticleData.DESERIALIZER){
		@Override
		public Codec<TreasureParticleData> codec(){
			return TreasureParticleData.CODEC;
		}
	});
	public static BasicParticleType fireBeaconParticle, magicBeaconParticle, peaceBeaconParticle;
	public static BasicParticleType seaSweep;	public static final RegistryObject<ParticleType<SmallFireParticleData>> FIRE = REGISTRY.register("fire", () -> new ParticleType<SmallFireParticleData>(false, SmallFireParticleData.DESERIALIZER){
		@Override
		public Codec<SmallFireParticleData> codec(){
			return SmallFireParticleData.CODEC(FIRE.get());
		}
	});

	@SubscribeEvent
	public static void registerParticles(RegistryEvent.Register<ParticleType<?>> registryEvent){
		IForgeRegistry<ParticleType<?>> particleTypes = registryEvent.getRegistry();
		fireBeaconParticle = new BasicParticleType(false);
		fireBeaconParticle.setRegistryName(DragonSurvivalMod.MODID, "netherite_particle");
		particleTypes.register(fireBeaconParticle);

		peaceBeaconParticle = new BasicParticleType(false);
		peaceBeaconParticle.setRegistryName(DragonSurvivalMod.MODID, "gold_particle");
		particleTypes.register(peaceBeaconParticle);

		magicBeaconParticle = new BasicParticleType(false);
		magicBeaconParticle.setRegistryName(DragonSurvivalMod.MODID, "diamond_particle");
		particleTypes.register(magicBeaconParticle);

		seaSweep = new BasicParticleType(false);
		seaSweep.setRegistryName(DragonSurvivalMod.MODID, "sea_sweep");
		particleTypes.register(seaSweep);
	}	public static final RegistryObject<ParticleType<LargeFireParticleData>> LARGE_FIRE = REGISTRY.register("large_fire", () -> new ParticleType<LargeFireParticleData>(false, LargeFireParticleData.DESERIALIZER){
		@Override
		public Codec<LargeFireParticleData> codec(){
			return LargeFireParticleData.CODEC(LARGE_FIRE.get());
		}
	});

	//LargePoisonParticleData
	@SubscribeEvent( priority = EventPriority.LOWEST )
	public static void registerParticles(ParticleFactoryRegisterEvent event){
		Minecraft.getInstance().particleEngine.register(DSParticles.FIRE.get(), SmallFireParticle.FireFactory::new);
		Minecraft.getInstance().particleEngine.register(DSParticles.LARGE_FIRE.get(), LargeFireParticle.FireFactory::new);
		Minecraft.getInstance().particleEngine.register(DSParticles.POISON.get(), ForestFactory::new);
		Minecraft.getInstance().particleEngine.register(DSParticles.LARGE_POISON.get(), LargePoisonParticle.ForestFactory::new);
		Minecraft.getInstance().particleEngine.register(DSParticles.LIGHTNING.get(), SeaFactory::new);
		Minecraft.getInstance().particleEngine.register(DSParticles.LARGE_LIGHTNING.get(), LargeLightningParticle.SeaFactory::new);
		Minecraft.getInstance().particleEngine.register(DSParticles.TREASURE.get(), TreasureParticle.Factory::new);
	}	public static final RegistryObject<ParticleType<SmallPoisonParticleData>> POISON = REGISTRY.register("poison", () -> new ParticleType<SmallPoisonParticleData>(false, SmallPoisonParticleData.DESERIALIZER){
		@Override
		public Codec<SmallPoisonParticleData> codec(){
			return SmallPoisonParticleData.CODEC(POISON.get());
		}
	});

	public static final RegistryObject<ParticleType<LargePoisonParticleData>> LARGE_POISON = REGISTRY.register("large_poison", () -> new ParticleType<LargePoisonParticleData>(false, LargePoisonParticleData.DESERIALIZER){
		@Override
		public Codec<LargePoisonParticleData> codec(){
			return LargePoisonParticleData.CODEC(LARGE_POISON.get());
		}
	});

	public static final RegistryObject<ParticleType<SmallLightningParticleData>> LIGHTNING = REGISTRY.register("lightning", () -> new ParticleType<SmallLightningParticleData>(false, SmallLightningParticleData.DESERIALIZER){
		@Override
		public Codec<SmallLightningParticleData> codec(){
			return SmallLightningParticleData.CODEC(LIGHTNING.get());
		}
	});

	public static final RegistryObject<ParticleType<LargeLightningParticleData>> LARGE_LIGHTNING = REGISTRY.register("large_lightning", () -> new ParticleType<LargeLightningParticleData>(false, LargeLightningParticleData.DESERIALIZER){
		@Override
		public Codec<LargeLightningParticleData> codec(){
			return LargeLightningParticleData.CODEC(LARGE_LIGHTNING.get());
		}
	});







}