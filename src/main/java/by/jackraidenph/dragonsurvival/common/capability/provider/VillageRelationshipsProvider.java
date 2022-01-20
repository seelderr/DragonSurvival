package by.jackraidenph.dragonsurvival.common.capability.provider;

import by.jackraidenph.dragonsurvival.common.capability.Capabilities;
import by.jackraidenph.dragonsurvival.common.capability.caps.VillageRelationShips;
import by.jackraidenph.dragonsurvival.common.capability.storage.VillageRelationshipsStorage;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VillageRelationshipsProvider implements ICapabilitySerializable<CompoundTag>
{
	private final LazyOptional<VillageRelationShips> instance;
	private final VillageRelationshipsStorage storage;
	
	public VillageRelationshipsProvider()
	{
		instance = LazyOptional.of(VillageRelationShips::new);
		storage  = new VillageRelationshipsStorage();
	}
	
	@Nonnull
	public <T> LazyOptional<T> getCapability(
			@Nonnull
					Capability<T> cap,
			@Nullable
					Direction side)
	{
		return (cap == Capabilities.VILLAGE_RELATIONSHIP) ? this.instance.cast() : LazyOptional.empty();
	}
	
	public CompoundTag serializeNBT()
	{
		return (CompoundTag)storage.writeNBT(Capabilities.VILLAGE_RELATIONSHIP, this.instance.orElse(null), null);
	}
	
	public void deserializeNBT(CompoundTag nbt)
	{
		storage.readNBT(Capabilities.VILLAGE_RELATIONSHIP, this.instance.orElse(null), null, (Tag)nbt);
	}
	
}
