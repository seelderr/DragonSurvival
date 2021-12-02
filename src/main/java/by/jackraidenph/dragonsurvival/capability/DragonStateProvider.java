package by.jackraidenph.dragonsurvival.capability;

import by.jackraidenph.dragonsurvival.handlers.ServerSide.NetworkHandler;
import by.jackraidenph.dragonsurvival.magic.DragonAbilities;
import by.jackraidenph.dragonsurvival.network.magic.SyncMagicStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

public class DragonStateProvider implements ICapabilitySerializable<CompoundNBT> {

    @CapabilityInject(DragonStateHandler.class)
    public static Capability<DragonStateHandler> DRAGON_CAPABILITY;
    private final LazyOptional<DragonStateHandler> instance = LazyOptional.of(DRAGON_CAPABILITY::getDefaultInstance);

    public static LazyOptional<DragonStateHandler> getCap(Entity entity) {
        return entity.getCapability(DragonStateProvider.DRAGON_CAPABILITY);
    }

    public static boolean isDragon(Entity entity) {
        return getCap(entity).filter(DragonStateHandler::isDragon).isPresent();
    }
    
    public static int getCurrentMana(PlayerEntity entity) {
        return getCap(entity).map(cap -> Math.min(cap.getCurrentMana(), getMaxMana(entity))).orElse(0);
    }
    
    public static int getMaxMana(PlayerEntity entity) {
        return getCap(entity).map(cap -> {
            int mana = 1;
    
            mana += Math.max(0, (Math.min(50, entity.experienceLevel) - 5) / 5);
    
            switch(cap.getType()){
                case SEA:
                    mana += cap.getAbilityLevel(DragonAbilities.SEA_MAGIC);
                    break;
        
                case CAVE:
                    mana += cap.getAbilityLevel(DragonAbilities.CAVE_MAGIC);
                    break;
        
                case FOREST:
                    mana += cap.getAbilityLevel(DragonAbilities.FOREST_MAGIC);
                    break;
            }
    
            return mana;
        }).orElse(0);
    }
    
    public static void replenishMana(PlayerEntity entity, int mana) {
        if(entity.level.isClientSide) return;
    
        getCap(entity).ifPresent(cap -> {
            cap.setCurrentMana(Math.min(getMaxMana(entity), cap.getCurrentMana() + mana));
            if(!entity.level.isClientSide){
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)entity), new SyncMagicStats(entity.getId(), cap.getSelectedAbilitySlot(), cap.getCurrentMana(), cap.renderAbilityHotbar()));
            }
        });
    }
    public static void consumeMana(PlayerEntity entity, int mana) {
        if(entity.isCreative()) return;
    
        if(entity.level.isClientSide){
            if (getCurrentMana(entity) < mana && (getCurrentMana(entity) + (entity.totalExperience / 10) >= mana || entity.experienceLevel > 0)) {
                entity.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.01F, 0.01F);
            }
            return;
        }
        
        getCap(entity).ifPresent(cap -> {
            if (getCurrentMana(entity) < mana && (getCurrentMana(entity) + (entity.totalExperience / 10) >= mana || entity.experienceLevel > 0)) {
                int missingMana = mana - getCurrentMana(entity);
                int missingExp = (missingMana * 10);
                entity.giveExperiencePoints(-missingExp);
                cap.setCurrentMana(0);
            }else {
                cap.setCurrentMana(Math.max(0, cap.getCurrentMana() - mana));
            }
            
            
            if (!entity.level.isClientSide) {
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)entity), new SyncMagicStats(entity.getId(), cap.getSelectedAbilitySlot(), cap.getCurrentMana(), cap.renderAbilityHotbar()));
            }
        });
    }
    
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == DRAGON_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) DRAGON_CAPABILITY.getStorage().writeNBT(DRAGON_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        DRAGON_CAPABILITY.getStorage().readNBT(DRAGON_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null, nbt);
    }
}
