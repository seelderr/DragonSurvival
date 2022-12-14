package by.dragonsurvivalteam.dragonsurvival.network.player;


import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.CaveDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.ForestDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.SeaDragonType;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
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

public class SyncCapabilityDebuff implements IMessage<SyncCapabilityDebuff>{

	public int playerId;
	public double timeWithoutWater;
	public int timeInDarkness;
	public int timeInRain;
	public int lavaSwimTicks;

	public SyncCapabilityDebuff(){
	}

	public SyncCapabilityDebuff(int playerId, double timeWithoutWater, int timeInDarkness, int timeInRain, int lavaSwimTicks){
		this.playerId = playerId;
		this.timeWithoutWater = timeWithoutWater;
		this.timeInDarkness = timeInDarkness;
		this.timeInRain = timeInRain;
		this.lavaSwimTicks = lavaSwimTicks;
	}

	@Override

	public void encode(SyncCapabilityDebuff message, FriendlyByteBuf buffer){
		buffer.writeInt(message.playerId);
		buffer.writeDouble(message.timeWithoutWater);
		buffer.writeInt(message.timeInDarkness);
		buffer.writeInt(message.timeInRain);
		buffer.writeInt(message.lavaSwimTicks);
	}

	@Override

	public SyncCapabilityDebuff decode(FriendlyByteBuf buffer){

		return new SyncCapabilityDebuff(buffer.readInt(), buffer.readDouble(), buffer.readInt(), buffer.readInt(), buffer.readInt());
	}

	@Override
	public void handle(SyncCapabilityDebuff message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(SyncCapabilityDebuff message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {

			Player thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				Level world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof Player){

					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						if(dragonStateHandler.getType() instanceof SeaDragonType seaDragonType){
							seaDragonType.timeWithoutWater = message.timeWithoutWater;

						}else if(dragonStateHandler.getType() instanceof ForestDragonType forestDragonType){
							forestDragonType.timeInDarkness = message.timeInDarkness;

						}else if(dragonStateHandler.getType() instanceof CaveDragonType caveDragonType){
							caveDragonType.timeInRain = message.timeInRain;
							caveDragonType.lavaAirSupply = message.lavaSwimTicks;
						}
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}