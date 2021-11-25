package by.jackraidenph.dragonsurvival.network.magic;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.magic.Abilities.DragonAbilities;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.TargetPoint;

import java.util.function.Supplier;

public class SyncAbilityCastingToServer implements IMessage<SyncAbilityCastingToServer>
{
	
	public int playerId;
	public DragonAbility currentAbility;
	
	public SyncAbilityCastingToServer() {
	
	}
	
	public SyncAbilityCastingToServer(int playerId, DragonAbility currentAbility) {
		this.playerId = playerId;
		this.currentAbility = currentAbility;
	}
	
	@Override
	public void encode(SyncAbilityCastingToServer message, PacketBuffer buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.currentAbility != null);
		
		if(message.currentAbility != null){
			buffer.writeUtf(message.currentAbility.getId());
			buffer.writeNbt(message.currentAbility.saveNBT());
		}
	}
	
	@Override
	public SyncAbilityCastingToServer decode(PacketBuffer buffer) {
		int playerId = buffer.readInt();
		DragonAbility ability = null;
		boolean hasAbility = buffer.readBoolean();
		
		if(hasAbility){
			String id = buffer.readUtf();
			ability = DragonAbilities.ABILITY_LOOKUP.get(id).createInstance();
			ability.loadNBT(buffer.readNbt());
		}
		
		return new SyncAbilityCastingToServer(playerId, ability);
	}
	
	@Override
	public void handle(SyncAbilityCastingToServer message, Supplier<NetworkEvent.Context> supplier) {
		ServerPlayerEntity player = supplier.get().getSender();
		
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			dragonStateHandler.setCurrentlyCasting((ActiveDragonAbility)message.currentAbility);
		});
		
		TargetPoint point = new TargetPoint(player, player.position().x, player.position().y, player.position().z, 64, player.level.dimension());
		DragonSurvivalMod.CHANNEL.send(PacketDistributor.NEAR.with(() -> point), new SyncCurrentAbilityCasting(message.playerId, message.currentAbility));
	}
}