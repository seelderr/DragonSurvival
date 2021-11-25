package by.jackraidenph.dragonsurvival.registration;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.magic.entity.particle.ParticleSnowFlake;
import by.jackraidenph.dragonsurvival.magic.entity.particle.ParticleSnowFlake.SnowflakeData;
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
    
    public static final DeferredRegister<ParticleType<?>> REG = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, DragonSurvivalMod.MODID);
    
    public static final RegistryObject<ParticleType<SnowflakeData>> SNOWFLAKE = REG.register("snowflake", () -> new ParticleType<ParticleSnowFlake.SnowflakeData>(false, ParticleSnowFlake.SnowflakeData.DESERIALIZER) {
        @Override
        public Codec<SnowflakeData> codec() {
            return ParticleSnowFlake.SnowflakeData.CODEC(SNOWFLAKE.get());
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
    }
}
