package by.jackraidenph.dragonsurvival.common.capability.provider;

import by.jackraidenph.dragonsurvival.client.util.FakeClientPlayer;
import by.jackraidenph.dragonsurvival.common.capability.Capabilities;
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.entity.creatures.hitbox.DragonHitBox;
import by.jackraidenph.dragonsurvival.common.entity.creatures.hitbox.DragonHitboxPart;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class DragonStateProvider implements ICapabilitySerializable<CompoundTag> {

    private final DragonStateHandler handlerObject = new DragonStateHandler();
    private final LazyOptional<DragonStateHandler> instance = LazyOptional.of(() -> handlerObject);
    
    public void invalidate(){
      //  instance.invalidate();
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
            LazyOptional<DragonStateHandler> cap = entity.getCapability(Capabilities.DRAGON_CAPABILITY);
            return cap;
        }
    }
    
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == Capabilities.DRAGON_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }
    
    @Override
    public CompoundTag serializeNBT() {
        return this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")).writeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")).readNBT(nbt);
    }
}
