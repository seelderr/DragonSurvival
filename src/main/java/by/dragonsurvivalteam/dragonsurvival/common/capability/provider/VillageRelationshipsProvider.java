package by.dragonsurvivalteam.dragonsurvival.common.capability.provider;

import by.dragonsurvivalteam.dragonsurvival.common.capability.Capabilities;
import by.dragonsurvivalteam.dragonsurvival.common.capability.VillageRelationShips;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class VillageRelationshipsProvider implements ICapabilitySerializable<CompoundTag>{
	private final VillageRelationShips handlerObject = new VillageRelationShips();
	private final LazyOptional<VillageRelationShips> instance = LazyOptional.of(() -> handlerObject);

	public static LazyOptional<VillageRelationShips> getVillageRelationships(Entity entity){
		return entity.getCapability(Capabilities.VILLAGE_RELATIONSHIP, null);
	}

	public void invalidate(){
		//		instance.invalidate();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		return cap == Capabilities.VILLAGE_RELATIONSHIP ? instance.cast() : LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT(){
		return this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")).writeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag nbt){
		this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")).readNBT(nbt);
	}
}