package by.dragonsurvivalteam.dragonsurvival.network.player;


import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncDragonTypeData implements IMessage<SyncDragonTypeData>{

	public int playerId;
	public CompoundTag dragonTypeData;

	public SyncDragonTypeData(){
	}

	public SyncDragonTypeData(int playerId, CompoundTag tag){
		this.playerId = playerId;
		dragonTypeData = tag;
	}
	
	public SyncDragonTypeData(int playerId, AbstractDragonType dragonType){
		this.playerId = playerId;
		dragonTypeData = dragonType.writeNBT();
	}
	
	
	@Override
	public void encode(SyncDragonTypeData message, FriendlyByteBuf buffer){
		buffer.writeInt(message.playerId);
		buffer.writeNbt(message.dragonTypeData);
	}

	@Override

	public SyncDragonTypeData decode(FriendlyByteBuf buffer){
		return new SyncDragonTypeData(buffer.readInt(), buffer.readNbt());
	}

	@Override
	public void handle(SyncDragonTypeData message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));
		supplier.get().setPacketHandled(true);
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(SyncDragonTypeData message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {

			Player thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				Level world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof Player){

					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						if(dragonStateHandler.getType() != null){
							dragonStateHandler.getType().readNBT(message.dragonTypeData);
						}
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}