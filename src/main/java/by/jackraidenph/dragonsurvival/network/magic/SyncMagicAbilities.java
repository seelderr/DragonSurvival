package by.jackraidenph.dragonsurvival.network.magic;

import by.jackraidenph.dragonsurvival.PacketProxy;
import by.jackraidenph.dragonsurvival.magic.DragonAbilities;
import by.jackraidenph.dragonsurvival.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.function.Supplier;

public class SyncMagicAbilities implements IMessage<SyncMagicAbilities>
{
	public int playerId;
	public ArrayList<DragonAbility> abilities = new ArrayList<>();
	
	public SyncMagicAbilities() {}
	public SyncMagicAbilities(int playerId, ArrayList<DragonAbility> abilities) {
		this.abilities = abilities;
		this.playerId = playerId;
	}
	
	@Override
	public void encode(SyncMagicAbilities message, PacketBuffer buffer) {
		buffer.writeInt(message.playerId);
		
		CompoundNBT tag = new CompoundNBT();
		for(DragonAbility ab : message.abilities){
			tag.put(ab.getId(), ab.saveNBT());
		}
		
		buffer.writeNbt(tag);
	}
	
	@Override
	public SyncMagicAbilities decode(PacketBuffer buffer) {
		int playerId = buffer.readInt();
		
		ArrayList<DragonAbility> abilities = new ArrayList<>();
		CompoundNBT tag = buffer.readNbt();
		
		for(DragonAbility staticAbility : DragonAbilities.ABILITY_LOOKUP.values()){
			if(tag.contains(staticAbility.getId())){
				DragonAbility ability = staticAbility.createInstance();
				ability.loadNBT(tag.getCompound(staticAbility.getId()));
				abilities.add(ability);
			}
		}
		
		return new SyncMagicAbilities(playerId, abilities);
	}
	
	@Override
	public void handle(SyncMagicAbilities message, Supplier<NetworkEvent.Context> supplier) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> new PacketProxy().handleMagicAbilities(message, supplier));
	}
}