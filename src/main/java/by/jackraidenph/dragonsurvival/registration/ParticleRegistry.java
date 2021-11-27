package by.jackraidenph.dragonsurvival.registration;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.magic.entity.particle.CaveDragon.LargeFireParticle;
import by.jackraidenph.dragonsurvival.magic.entity.particle.CaveDragon.LargeFireParticleData;
import by.jackraidenph.dragonsurvival.magic.entity.particle.CaveDragon.SmallFireParticle;
import by.jackraidenph.dragonsurvival.magic.entity.particle.CaveDragon.SmallFireParticleData;
import by.jackraidenph.dragonsurvival.magic.entity.particle.ForestDragon.LargePoisonParticle;
import by.jackraidenph.dragonsurvival.magic.entity.particle.ForestDragon.LargePoisonParticleData;
import by.jackraidenph.dragonsurvival.magic.entity.particle.ForestDragon.SmallPoisonParticle.ForestFactory;
import by.jackraidenph.dragonsurvival.magic.entity.particle.ForestDragon.SmallPoisonParticleData;
import by.jackraidenph.dragonsurvival.magic.entity.particle.SeaDragon.LargeLightningParticle;
import by.jackraidenph.dragonsurvival.magic.entity.particle.SeaDragon.LargeLightningParticleData;
import by.jackraidenph.dragonsurvival.magic.entity.particle.SeaDragon.SmallLightningParticle.SeaFactory;
import by.jackraidenph.dragonsurvival.magic.entity.particle.SeaDragon.SmallLightningParticleData;
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
    
    public static final RegistryObject<ParticleType<SmallPoisonParticleData>> POISON = REGISTRY.register("poison", () -> new ParticleType<SmallPoisonParticleData>(false, SmallPoisonParticleData.DESERIALIZER) {
        @Override
        public Codec<SmallPoisonParticleData> codec() {
            return SmallPoisonParticleData.CODEC(POISON.get());
        }
    });
    
    public static final RegistryObject<ParticleType<LargePoisonParticleData>> LARGE_POISON = REGISTRY.register("large_poison", () -> new ParticleType<LargePoisonParticleData>(false, LargePoisonParticleData.DESERIALIZER) {
        @Override
        public Codec<LargePoisonParticleData> codec() {
            return LargePoisonParticleData.CODEC(LARGE_POISON.get());
        }
    });
    
    public static final RegistryObject<ParticleType<SmallLightningParticleData>> LIGHTNING = REGISTRY.register("lightning", () -> new ParticleType<SmallLightningParticleData>(false, SmallLightningParticleData.DESERIALIZER) {
        @Override
        public Codec<SmallLightningParticleData> codec() {
            return SmallLightningParticleData.CODEC(LIGHTNING.get());
        }
    });
    
    public static final RegistryObject<ParticleType<LargeLightningParticleData>> LARGE_LIGHTNING = REGISTRY.register("large_lightning", () -> new ParticleType<LargeLightningParticleData>(false, LargeLightningParticleData.DESERIALIZER) {
        @Override
        public Codec<LargeLightningParticleData> codec() {
            return LargeLightningParticleData.CODEC(LARGE_LIGHTNING.get());
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
    //LargePoisonParticleData
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticles(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.FIRE.get(), SmallFireParticle.FireFactory::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.LARGE_FIRE.get(), LargeFireParticle.FireFactory::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.POISON.get(), ForestFactory::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.LARGE_POISON.get(), LargePoisonParticle.ForestFactory::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.LIGHTNING.get(), SeaFactory::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.LARGE_LIGHTNING.get(), LargeLightningParticle.SeaFactory::new);
    
    }
}
