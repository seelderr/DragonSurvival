package by.jackraidenph.dragonsurvival.capability;

import by.jackraidenph.dragonsurvival.capability.entity.GenericCapability;
import by.jackraidenph.dragonsurvival.capability.entity.GenericCapabilityStorage;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.LazyOptional;

public class Capabilities {
    @CapabilityInject( VillageRelationShips.class)
    public static Capability<VillageRelationShips> VILLAGE_RELATIONSHIP;
    
    @CapabilityInject( GenericCapability.class)
    public static Capability<GenericCapability> GENERIC_CAPABILITY;
    
    public static void register() {
        CapabilityManager.INSTANCE.register(DragonStateHandler.class, new DragonCapStorage(), DragonStateHandler::new);
        CapabilityManager.INSTANCE.register(VillageRelationShips.class, new VillageRelationshipsStorage(), VillageRelationShips::new);
        CapabilityManager.INSTANCE.register(GenericCapability.class, new GenericCapabilityStorage(), GenericCapability::new);
    }

    public static LazyOptional<VillageRelationShips> getVillageRelationships(Entity entity) {
        return entity.getCapability(VILLAGE_RELATIONSHIP, null);
    }
    public static LazyOptional<GenericCapability> getGenericCapability(Entity entity) {
        return entity.getCapability(GENERIC_CAPABILITY, null);
    }
    
}
