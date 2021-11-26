package by.jackraidenph.dragonsurvival.registration;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.magic.entity.particle.*;
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

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ParticleRegistry {
    public static ParticleType<BasicParticleType> fireBeaconParticle, magicBeaconParticle, peaceBeaconParticle;
    
    public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, DragonSurvivalMod.MODID);
    
    public static final RegistryObject<ParticleType<SnowflakeData>> SNOWFLAKE = REGISTRY.register("snowflake", () -> new ParticleType<SnowflakeData>(false, SnowflakeData.DESERIALIZER) {
        @Override
        public Codec<SnowflakeData> codec() {
            return SnowflakeData.CODEC(SNOWFLAKE.get());
        }
    });
    
    public static final RegistryObject<ParticleType<SmallFireParticleData>> FIRE = REGISTRY.register("fire", () -> new ParticleType<SmallFireParticleData>(false, SmallFireParticleData.DESERIALIZER) {
        @Override
        public Codec<SmallFireParticleData> codec() {
            return SmallFireParticleData.CODEC(FIRE.get());
        }
    });
    
    public static final RegistryObject<ParticleType<LargeFireParticleData>> LARGE_FIRE = REGISTRY.register("large_fire", () -> new ParticleType<LargeFireParticleData>(false, LargeFireParticleData.DESERIALIZER) {
        @Override
        public Codec<LargeFireParticleData> codec() {
            return LargeFireParticleData.CODEC(LARGE_FIRE.get());
        }
    });
    
    @SubscribeEvent
    public static void registerParticles(RegistryEvent.Register<ParticleType<?>> registryEvent) {
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
    }
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticles(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.SNOWFLAKE.get(), ParticleSnowFlake.SnowFlakeFactory::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.FIRE.get(), SmallFireParticle.FireFactory::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.LARGE_FIRE.get(), LargeFireParticle.FireFactory::new);
    
    }
}
