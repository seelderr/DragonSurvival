package by.dragonsurvivalteam.dragonsurvival.common.capability.provider;

import by.dragonsurvivalteam.dragonsurvival.common.capability.VillageRelationShips;
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

public class VillageRelationshipsProvider implements ICapabilitySerializable<CompoundNBT>{
	private final LazyOptional<VillageRelationShips> instance = LazyOptional.of(() -> (VillageRelationShips)Objects.<Object>requireNonNull(VILLAGE_RELATIONSHIP.getDefaultInstance()));
	@CapabilityInject( VillageRelationShips.class )
	public static Capability<VillageRelationShips> VILLAGE_RELATIONSHIP;

	public static LazyOptional<VillageRelationShips> getVillageRelationships(Entity entity){
		return entity.getCapability(VILLAGE_RELATIONSHIP, null);
	}

	@Nonnull
	public <T> LazyOptional<T> getCapability(
		@Nonnull
			Capability<T> cap,
		@Nullable
			Direction side){
		return (cap == VILLAGE_RELATIONSHIP) ? this.instance.cast() : LazyOptional.empty();
	}

	public CompoundNBT serializeNBT(){
		return (CompoundNBT)VILLAGE_RELATIONSHIP.getStorage().writeNBT(VILLAGE_RELATIONSHIP, this.instance.orElse(null), null);
	}

	public void deserializeNBT(CompoundNBT nbt){
		VILLAGE_RELATIONSHIP.getStorage().readNBT(VILLAGE_RELATIONSHIP, this.instance.orElse(null), null, nbt);
	}
}