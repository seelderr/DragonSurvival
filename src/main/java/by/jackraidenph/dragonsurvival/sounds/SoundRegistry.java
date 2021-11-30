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
    public static SoundEvent breathStart, breathLoop, breathEnd;

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> registryEvent) {
        IForgeRegistry<SoundEvent> forgeRegistry = registryEvent.getRegistry();
        activateBeacon = register("activate_beacon", forgeRegistry);
        deactivateBeacon = register("deactivate_beacon", forgeRegistry);
        upgradeBeacon = register("upgrade_beacon", forgeRegistry);
        applyEffect = register("apply_effect", forgeRegistry);
    
        breathStart = register("breath_start", forgeRegistry);
        breathLoop = register("breath_loop", forgeRegistry);
        breathEnd = register("breath_end", forgeRegistry);
    }

    private static SoundEvent register(String name, IForgeRegistry<SoundEvent> forgeRegistry) {
        SoundEvent soundEvent = new SoundEvent(new ResourceLocation(DragonSurvivalMod.MODID, name));
        soundEvent.setRegistryName(DragonSurvivalMod.MODID, name);
        forgeRegistry.register(soundEvent);
        return soundEvent;
    }
}
