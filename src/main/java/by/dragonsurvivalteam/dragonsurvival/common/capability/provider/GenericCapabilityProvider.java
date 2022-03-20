package by.dragonsurvivalteam.dragonsurvival.common.capability.provider;

import by.dragonsurvivalteam.dragonsurvival.common.capability.GenericCapability;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class GenericCapabilityProvider implements ICapabilitySerializable<CompoundNBT>{
	private final LazyOptional<GenericCapability> instance = LazyOptional.of(() -> (GenericCapability)Objects.<Object>requireNonNull(GENERIC_CAPABILITY.getDefaultInstance()));
	@CapabilityInject( GenericCapability.class )
	public static Capability<GenericCapability> GENERIC_CAPABILITY;

	public static LazyOptional<GenericCapability> getGenericCapability(Entity entity){
		return entity.getCapability(GENERIC_CAPABILITY, null);
	}

	@Nonnull
	public <T> LazyOptional<T> getCapability(
		@Nonnull
			Capability<T> cap,
		@Nullable
			Direction side){
		return (cap == GENERIC_CAPABILITY) ? this.instance.cast() : LazyOptional.empty();
	}

	public CompoundNBT serializeNBT(){
		return (CompoundNBT)GENERIC_CAPABILITY.getStorage().writeNBT(GENERIC_CAPABILITY, this.instance.orElse(null), null);
	}

	public void deserializeNBT(CompoundNBT nbt){
		GENERIC_CAPABILITY.getStorage().readNBT(GENERIC_CAPABILITY, this.instance.orElse(null), null, nbt);
	}
}