package by.jackraidenph.dragonsurvival.common.capability;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.util.FakeClientPlayer;
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.caps.GenericCapability;
import by.jackraidenph.dragonsurvival.common.capability.caps.VillageRelationShips;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.capability.provider.GenericCapabilityProvider;
import by.jackraidenph.dragonsurvival.common.capability.provider.VillageRelationshipsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class Capabilities {
    public static Capability<VillageRelationShips> VILLAGE_RELATIONSHIP = CapabilityManager.get(new CapabilityToken<>() {});
    public static Capability<GenericCapability> GENERIC_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static Capability<DragonStateHandler> DRAGON_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    
    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        event.addCapability(new ResourceLocation("dragonsurvival", "generic_capability_data"), new GenericCapabilityProvider());
    
        if (event.getObject() instanceof Player && !(event.getObject() instanceof FakeClientPlayer)) {
            event.addCapability(new ResourceLocation("dragonsurvival", "playerstatehandler"), new DragonStateProvider());
            event.addCapability(new ResourceLocation("dragonsurvival", "village_relations"), new VillageRelationshipsProvider());
            DragonSurvivalMod.LOGGER.info("Successfully attached capabilities to the " + event.getObject().getClass().getSimpleName());
        }
    }
    
    @Mod.EventBusSubscriber( modid = DragonSurvivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents
    {
        @SubscribeEvent
        public static void register(RegisterCapabilitiesEvent ev)
        {
            ev.register(DragonStateHandler.class);
            ev.register(VillageRelationShips.class);
            ev.register(GenericCapability.class);
        }
    }

    public static LazyOptional<VillageRelationShips> getVillageRelationships(Entity entity) {
        return entity.getCapability(VILLAGE_RELATIONSHIP, null);
    }
    public static LazyOptional<GenericCapability> getGenericCapability(Entity entity) {
        return entity.getCapability(GENERIC_CAPABILITY, null);
    }
    
}
