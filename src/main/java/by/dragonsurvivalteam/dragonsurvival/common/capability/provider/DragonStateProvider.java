package by.dragonsurvivalteam.dragonsurvival.common.capability.provider;

import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.Capabilities;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import com.ibm.icu.impl.Pair;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

// FIXME :: Why does this still exist as a duplicate
public class DragonStateProvider implements ICapabilitySerializable<CompoundTag>{
	private final DragonStateHandler handlerObject = new DragonStateHandler();
	private final LazyOptional<DragonStateHandler> instance = LazyOptional.of(() -> handlerObject);

	@OnlyIn( Dist.CLIENT )
	private static Pair<Boolean, LazyOptional<DragonStateHandler>> getFakePlayer(Entity entity){
		if(entity instanceof FakeClientPlayer){
			if(((FakeClientPlayer)entity).handler != null){
				return Pair.of(true, LazyOptional.of(() -> ((FakeClientPlayer)entity).handler));
			}
		}

		return Pair.of(false, LazyOptional.empty());
	}

	public static LazyOptional<DragonStateHandler> getCap(Entity entity){
		if(entity == null){
			return LazyOptional.empty();
		}else{
			if(entity.level.isClientSide){
				Pair<Boolean, LazyOptional<DragonStateHandler>> fakeState = getFakePlayer(entity);

				if(fakeState.first){
					return fakeState.second;
				}
			}

			return entity.getCapability(Capabilities.DRAGON_CAPABILITY);
		}
	}

	public void invalidate(){
		//  instance.invalidate();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		return cap == Capabilities.DRAGON_CAPABILITY ? instance.cast() : LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT(){
		return instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")).writeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag nbt){
		instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")).readNBT(nbt);
	}
}