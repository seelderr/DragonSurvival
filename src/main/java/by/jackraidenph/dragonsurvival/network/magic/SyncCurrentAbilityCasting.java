package by.jackraidenph.dragonsurvival.network.magic;

import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.magic.DragonAbilities;
import by.jackraidenph.dragonsurvival.common.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.common.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
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
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> run(message, supplier));
	}
	
	@OnlyIn(Dist.CLIENT)
	public void run(SyncCurrentAbilityCasting message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if (thisPlayer != null) {
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if (entity instanceof PlayerEntity) {
					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						if(message.currentAbility == null && dragonStateHandler.getMagic().getCurrentlyCasting() != null){
							dragonStateHandler.getMagic().getCurrentlyCasting().stopCasting();
						}
						
						dragonStateHandler.getMagic().setCurrentlyCasting((ActiveDragonAbility)message.currentAbility);
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}