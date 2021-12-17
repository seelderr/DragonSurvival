package by.jackraidenph.dragonsurvival.capability;

import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.handlers.ServerSide.NetworkHandler;
import by.jackraidenph.dragonsurvival.magic.DragonAbilities;
import by.jackraidenph.dragonsurvival.network.magic.SyncMagicStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
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
        return entity == null ? LazyOptional.empty() : entity.getCapability(DragonStateProvider.DRAGON_CAPABILITY);
    }

    public static boolean isDragon(Entity entity) {
        return getCap(entity).filter(DragonStateHandler::isDragon).isPresent();
    }
    
    public static int getCurrentMana(PlayerEntity entity) {
        return getCap(entity).map(cap -> Math.min(cap.getMagic().getCurrentMana(), getMaxMana(entity))).orElse(0);
    }
    
    public static int getMaxMana(PlayerEntity entity) {
        return getCap(entity).map(cap -> {
            int mana = 1;
    
            mana += ConfigHandler.SERVER.noEXPRequirements.get() ? 9 : Math.max(0, (Math.min(50, entity.experienceLevel) - 5) / 5);
    
            switch(cap.getType()){
                case SEA:
                    mana += cap.getMagic().getAbilityLevel(DragonAbilities.SEA_MAGIC);
                    break;
        
                case CAVE:
                    mana += cap.getMagic().getAbilityLevel(DragonAbilities.CAVE_MAGIC);
                    break;
        
                case FOREST:
                    mana += cap.getMagic().getAbilityLevel(DragonAbilities.FOREST_MAGIC);
                    break;
            }
    
            return mana;
        }).orElse(0);
    }
    
    public static void replenishMana(PlayerEntity entity, int mana) {
        if(entity.level.isClientSide){
            return;
        }
        
        getCap(entity).ifPresent(cap -> {
            cap.getMagic().setCurrentMana(Math.min(getMaxMana(entity), cap.getMagic().getCurrentMana() + mana));
            NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new SyncMagicStats(entity.getId(), cap.getMagic().getSelectedAbilitySlot(), cap.getMagic().getCurrentMana(), cap.getMagic().renderAbilityHotbar()));
        });
    }
    public static void consumeMana(PlayerEntity entity, int mana) {
        if(entity.isCreative()) return;
    
        if(ConfigHandler.SERVER.consumeEXPAsMana.get()) {
            if (entity.level.isClientSide) {
                if (getCurrentMana(entity) < mana && (getCurrentMana(entity) + (entity.totalExperience / 10) >= mana || entity.experienceLevel > 0)) {
                    entity.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.01F, 0.01F);
                }
            }
        }
        
        if(entity.level.isClientSide){
            return;
        }
        
        getCap(entity).ifPresent(cap -> {
            if(ConfigHandler.SERVER.consumeEXPAsMana.get()) {
                if (getCurrentMana(entity) < mana && (getCurrentMana(entity) + (entity.totalExperience / 10) >= mana || entity.experienceLevel > 0)) {
                    int missingMana = mana - getCurrentMana(entity);
                    int missingExp = (missingMana * 10);
                    entity.giveExperiencePoints(-missingExp);
                    cap.getMagic().setCurrentMana(0);
                } else {
                    cap.getMagic().setCurrentMana(Math.max(0, cap.getMagic().getCurrentMana() - mana));
                }
            } else {
                cap.getMagic().setCurrentMana(Math.max(0, cap.getMagic().getCurrentMana() - mana));
            }
    
            NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new SyncMagicStats(entity.getId(), cap.getMagic().getSelectedAbilitySlot(), cap.getMagic().getCurrentMana(), cap.getMagic().renderAbilityHotbar()));
        });
    }
	
	public static Vector3f getCameraOffset(Entity entity){
	    Vector3f lookVector = new Vector3f(0,0,0);
	
	    if(entity instanceof PlayerEntity){
	        PlayerEntity player = (PlayerEntity)entity;
	        DragonStateHandler handler = getCap(player).orElse(null);
	        if(handler != null && handler.isDragon()){
	            float f1 = -(float)handler.getMovementData().bodyYaw * ((float)Math.PI / 180F);
	        
	            float f4 = MathHelper.sin(f1);
	            float f5 = MathHelper.cos(f1);
	            lookVector.set((float)(f4 * (handler.getSize() / 40)), 0, (float)(f5 * (handler.getSize() / 40)));
	        }
	    }
	    
	    return lookVector;
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
