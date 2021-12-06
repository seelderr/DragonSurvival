package by.jackraidenph.dragonsurvival.capability;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

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
    
    public static class VillageRelationshipsProvider implements ICapabilitySerializable<CompoundNBT>
    {
        private final LazyOptional<VillageRelationShips> instance = LazyOptional.of(() -> (VillageRelationShips) Objects.<Object>requireNonNull(VILLAGE_RELATIONSHIP.getDefaultInstance()));
    
        @Nonnull
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable
                Direction side) {
            return (cap == VILLAGE_RELATIONSHIP) ? this.instance.cast() : LazyOptional.empty();
        }
    
        public CompoundNBT serializeNBT() {
            return (CompoundNBT) VILLAGE_RELATIONSHIP.getStorage().writeNBT(VILLAGE_RELATIONSHIP, this.instance.orElse(null), null);
        }
    
        public void deserializeNBT(CompoundNBT nbt) {
            VILLAGE_RELATIONSHIP.getStorage().readNBT(VILLAGE_RELATIONSHIP, this.instance.orElse(null), null, (INBT) nbt);
        }
    }
    
    public static class GenericCapabilityProvider implements ICapabilitySerializable<CompoundNBT> {
        private final LazyOptional<GenericCapability> instance = LazyOptional.of(() -> (GenericCapability) Objects.<Object>requireNonNull(GENERIC_CAPABILITY.getDefaultInstance()));
    
        @Nonnull
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return (cap == GENERIC_CAPABILITY) ? this.instance.cast() : LazyOptional.empty();
        }
    
        public CompoundNBT serializeNBT() {
            return (CompoundNBT) GENERIC_CAPABILITY.getStorage().writeNBT(GENERIC_CAPABILITY, this.instance.orElse(null), null);
        }
    
        public void deserializeNBT(CompoundNBT nbt) {
            GENERIC_CAPABILITY.getStorage().readNBT(GENERIC_CAPABILITY, this.instance.orElse(null), null, (INBT) nbt);
        }
    }
}
