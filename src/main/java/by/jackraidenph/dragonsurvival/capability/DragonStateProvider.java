package by.jackraidenph.dragonsurvival.capability;

import by.jackraidenph.dragonsurvival.abilities.common.DragonAbility;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

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
    
    public static boolean hasAbility(Entity entity, DragonAbility ability) {
        return getCap(entity).filter((cap) -> cap.getAbility(ability) != null).isPresent();
    }
    
    public static int getAbilityLevel(Entity entity, DragonAbility ability) {
        return !hasAbility(entity, ability) ? 0 : getCap(entity).map((cap) -> cap.getAbility(ability).getLevel()).get();
    }
    
    public static int getCurrentMana(Entity entity) {
        return getCap(entity).map(cap -> cap.getCurrentMana()).orElseGet(() -> 0);
    }
    
    public static void setMaxMana(Entity entity, int mana) {
        getCap(entity).ifPresent(cap -> cap.setMaxMana(mana));
    }
    
    public static void replenishMana(Entity entity, int mana) {
        getCap(entity).ifPresent(cap -> cap.replenishMana(mana));
    }
    
    public static void consumeMana(Entity entity, int mana) {
        getCap(entity).ifPresent(cap -> cap.consumeMana(mana));
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
