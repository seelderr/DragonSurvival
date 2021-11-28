package by.jackraidenph.dragonsurvival.network.magic;

import by.jackraidenph.dragonsurvival.PacketProxy;
import by.jackraidenph.dragonsurvival.magic.DragonAbilities;
import by.jackraidenph.dragonsurvival.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncCurrentAbilityCasting implements IMessage<SyncCurrentAbilityCasting>
{
	
	public int playerId;
	public DragonAbility currentAbility;
	
	public SyncCurrentAbilityCasting() {}
	
	public SyncCurrentAbilityCasting(int playerId, DragonAbility currentAbility) {
		this.playerId = playerId;
		this.currentAbility = currentAbility;
	}
	
	@Override
	public void encode(SyncCurrentAbilityCasting message, PacketBuffer buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.currentAbility != null);
		
		if(message.currentAbility != null){
			buffer.writeUtf(message.currentAbility.getId());
			buffer.writeNbt(message.currentAbility.saveNBT());
		}
	}
	
	@Override
	public SyncCurrentAbilityCasting decode(PacketBuffer buffer) {
		int playerId = buffer.readInt();
		DragonAbility ability = null;
		boolean hasAbility = buffer.readBoolean();
		
		if(hasAbility){
			String id = buffer.readUtf();
			ability = DragonAbilities.ABILITY_LOOKUP.get(id).createInstance();
			ability.loadNBT(buffer.readNbt());
		}
		
		return new SyncCurrentAbilityCasting(playerId, ability);
	}
	
	@Override
	public void handle(SyncCurrentAbilityCasting message, Supplier<NetworkEvent.Context> supplier) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> new PacketProxy().handleSkillAnimation(message, supplier));
	}
}