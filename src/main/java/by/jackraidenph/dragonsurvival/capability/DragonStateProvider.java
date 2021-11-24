package by.jackraidenph.dragonsurvival.capability;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.network.magic.SyncMagicStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

public class DragonStateProvider implements ICapabilitySerializable<CompoundNBT> {

    @CapabilityInject(DragonStateHandler.class)
    public static Capability<DragonStateHandler> DRAGON_CAPABILITY = null;
    private final LazyOptional<DragonStateHandler> instance = LazyOptional.of(DRAGON_CAPABILITY::getDefaultInstance);

    public static LazyOptional<DragonStateHandler> getCap(Entity entity) {
        return entity.getCapability(DragonStateProvider.DRAGON_CAPABILITY);
    }

    public static boolean isDragon(Entity entity) {
        return getCap(entity).filter(DragonStateHandler::isDragon).isPresent();
    }
    
    public static int getCurrentMana(PlayerEntity entity) {
        return getCap(entity).map(cap -> Math.min(cap.getCurrentMana(), cap.getMaxMana(entity))).orElse(0);
    }
    
    public static int getMaxMana(PlayerEntity entity) {
        return getCap(entity).map(cap -> cap.getMaxMana(entity)).orElse(0);
    }
    
    public static void replenishMana(PlayerEntity entity, int mana) {
        if(entity.level.isClientSide) return;
    
        getCap(entity).ifPresent(cap -> {
            cap.setCurrentMana(Math.min(cap.getMaxMana(entity), cap.getCurrentMana() + mana));
            if(!entity.level.isClientSide){
                DragonSurvivalMod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)entity), new SyncMagicStats(entity.getId(), cap.getSelectedAbilitySlot(), cap.getCurrentMana(), cap.renderAbilityHotbar()));
            }
        });
    }
    public static void consumeMana(PlayerEntity entity, int mana) {
        if(entity.level.isClientSide) return;
        
        getCap(entity).ifPresent(cap -> {
            cap.setCurrentMana(Math.max(0, cap.getCurrentMana() - mana));
    
            if(!entity.level.isClientSide){
                DragonSurvivalMod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)entity), new SyncMagicStats(entity.getId(), cap.getSelectedAbilitySlot(), cap.getCurrentMana(), cap.renderAbilityHotbar()));
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
