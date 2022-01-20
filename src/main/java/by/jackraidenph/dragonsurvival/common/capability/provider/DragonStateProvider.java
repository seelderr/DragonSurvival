package by.jackraidenph.dragonsurvival.common.capability.provider;

import by.jackraidenph.dragonsurvival.client.util.FakeClientPlayer;
import by.jackraidenph.dragonsurvival.common.DragonEffects;
import by.jackraidenph.dragonsurvival.common.capability.Capabilities;
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.storage.DragonCapStorage;
import by.jackraidenph.dragonsurvival.common.entity.creatures.hitbox.DragonHitBox;
import by.jackraidenph.dragonsurvival.common.entity.creatures.hitbox.DragonHitboxPart;
import by.jackraidenph.dragonsurvival.common.magic.DragonAbilities;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.magic.SyncMagicStats;
import com.mojang.math.Vector3f;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class DragonStateProvider implements ICapabilitySerializable<CompoundTag> {

    private final LazyOptional<DragonStateHandler> instance;
    private final DragonCapStorage storage;
    
    public DragonStateProvider()
    {
        instance = LazyOptional.of(DragonStateHandler::new);
        storage  = new DragonCapStorage();
    }
    
    public static LazyOptional<DragonStateHandler> getCap(Entity entity) {
        if(entity instanceof FakeClientPlayer){
            return ((FakeClientPlayer)entity).handler != null ? LazyOptional.of(() -> ((FakeClientPlayer)entity).handler) : LazyOptional.empty();
        }
        
        if(entity instanceof DragonHitBox){
            return ((DragonHitBox)entity).player == null ? LazyOptional.empty() : getCap(((DragonHitBox)entity).player);
        }else  if(entity instanceof DragonHitboxPart){
            return ((DragonHitboxPart)entity).parentMob.player == null ? LazyOptional.empty() : getCap(((DragonHitboxPart)entity).parentMob.player);
        }
    
        if (entity == null) {
            return LazyOptional.empty();
        } else {
            entity.getCapability(Capabilities.DRAGON_CAPABILITY).orElse(null);
            return entity.getCapability(Capabilities.DRAGON_CAPABILITY);
        }
    }

    public static boolean isDragon(Entity entity) {
        return getCap(entity).filter(DragonStateHandler::isDragon).isPresent();
    }
    
    public static DragonType getDragonType(Entity entity) {
        DragonStateHandler handler = getCap(entity).orElse(null);
        return handler != null ? handler.getType() : DragonType.NONE;
    }
    
    public static int getCurrentMana(Player entity) {
        return getCap(entity).map(cap -> Math.min(cap.getMagic().getCurrentMana(), getMaxMana(entity))).orElse(0);
    }
    
    public static int getMaxMana(Player entity) {
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
    
    public static void replenishMana(Player entity, int mana) {
        if(entity.level.isClientSide){
            return;
        }
        
        getCap(entity).ifPresent(cap -> {
            cap.getMagic().setCurrentMana(Math.min(getMaxMana(entity), cap.getMagic().getCurrentMana() + mana));
            NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) entity), new SyncMagicStats(entity.getId(), cap.getMagic().getSelectedAbilitySlot(), cap.getMagic().getCurrentMana(), cap.getMagic().renderAbilityHotbar()));
        });
    }
    public static void consumeMana(Player entity, int mana) {
        if(entity == null) return;
        if(entity.isCreative()) return;
        if(entity.hasEffect(DragonEffects.SOURCE_OF_MAGIC)) return;
    
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
    
            NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) entity), new SyncMagicStats(entity.getId(), cap.getMagic().getSelectedAbilitySlot(), cap.getMagic().getCurrentMana(), cap.getMagic().renderAbilityHotbar()));
        });
    }
	
	public static Vector3f getCameraOffset(Entity entity){
        Vector3f lookVector = new Vector3f(0,0,0);
	
	    if(entity instanceof Player){
            Player player = (Player)entity;
	        DragonStateHandler handler = getCap(player).orElse(null);
	        if(handler != null && handler.isDragon()){
	            float f1 = -(float)handler.getMovementData().bodyYaw * ((float)Math.PI / 180F);
	        
	            float f4 = (float)Math.sin(f1);
	            float f5 = (float)Math.cos(f1);
             
	            lookVector.set((float)(f4 * (handler.getSize() / 40)), 0, (float)(f5 * (handler.getSize() / 40)));
	        }
	    }
	    
	    return lookVector;
	}
	
	@Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == Capabilities.DRAGON_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }
    
    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap)
    {
        return cap == Capabilities.DRAGON_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }
    
    @Override
    public CompoundTag serializeNBT() {
        return (CompoundTag)storage.writeNBT(Capabilities.DRAGON_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        storage.readNBT(Capabilities.DRAGON_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null, nbt);
    }
}
