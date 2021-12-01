package by.jackraidenph.dragonsurvival.sounds;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SoundRegistry
{
    public static SoundEvent activateBeacon, deactivateBeacon, upgradeBeacon, applyEffect;
    public static SoundEvent fireBreathStart, fireBreathLoop, fireBreathEnd;
    public static SoundEvent poisonBreathLoop;

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> registryEvent) {
        IForgeRegistry<SoundEvent> forgeRegistry = registryEvent.getRegistry();
        activateBeacon = register("activate_beacon", forgeRegistry);
        deactivateBeacon = register("deactivate_beacon", forgeRegistry);
        upgradeBeacon = register("upgrade_beacon", forgeRegistry);
        applyEffect = register("apply_effect", forgeRegistry);
    
        fireBreathStart = register("breath_start", forgeRegistry);
        fireBreathLoop = register("breath_loop", forgeRegistry);
        fireBreathEnd = register("breath_end", forgeRegistry);
    
        poisonBreathLoop = register("poison_breath_loop", forgeRegistry);
    
    }

    private static SoundEvent register(String name, IForgeRegistry<SoundEvent> forgeRegistry) {
        SoundEvent soundEvent = new SoundEvent(new ResourceLocation(DragonSurvivalMod.MODID, name));
        soundEvent.setRegistryName(DragonSurvivalMod.MODID, name);
        forgeRegistry.register(soundEvent);
        return soundEvent;
    }
}
