package by.dragonsurvivalteam.dragonsurvival.common.capability.provider;

import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.hitbox.DragonHitBox;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.hitbox.DragonHitboxPart;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class DragonStateProvider implements ICapabilitySerializable<CompoundNBT>{

	private final LazyOptional<DragonStateHandler> instance = LazyOptional.of(DRAGON_CAPABILITY::getDefaultInstance);
	@CapabilityInject( DragonStateHandler.class )
	public static Capability<DragonStateHandler> DRAGON_CAPABILITY;

	public static LazyOptional<DragonStateHandler> getCap(Entity entity){
		if(entity != null && entity.level != null && entity.level.isClientSide){
			if(entity instanceof FakeClientPlayer){
				return ((FakeClientPlayer)entity).handler != null ? LazyOptional.of(() -> ((FakeClientPlayer)entity).handler) : LazyOptional.empty();
			}
		}

		if(entity instanceof DragonHitBox){
			return ((DragonHitBox)entity).player == null ? LazyOptional.empty() : ((DragonHitBox)entity).player.getCapability(DragonStateProvider.DRAGON_CAPABILITY);
		}else if(entity instanceof DragonHitboxPart){
			return ((DragonHitboxPart)entity).parentMob.player == null ? LazyOptional.empty() : ((DragonHitboxPart)entity).parentMob.player.getCapability(DragonStateProvider.DRAGON_CAPABILITY);
		}
		return entity == null ? LazyOptional.empty() : entity.getCapability(DragonStateProvider.DRAGON_CAPABILITY);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		return cap == DRAGON_CAPABILITY ? instance.cast() : LazyOptional.empty();
	}

	@Override
	public CompoundNBT serializeNBT(){
		return (CompoundNBT)DRAGON_CAPABILITY.getStorage().writeNBT(DRAGON_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null);
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt){
		DRAGON_CAPABILITY.getStorage().readNBT(DRAGON_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null, nbt);
	}
}