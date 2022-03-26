package by.dragonsurvivalteam.dragonsurvival.network.magic;


import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;


public class SyncAbilityCastTime implements IMessage<SyncAbilityCastTime>{

	public int playerId;
	public int castTime;

	public SyncAbilityCastTime(){

	}

	public SyncAbilityCastTime(int playerId, int castTime){
		this.playerId = playerId;
		this.castTime = castTime;
	}

	@Override

	public void encode(SyncAbilityCastTime message, FriendlyByteBuf buffer){

		buffer.writeInt(message.playerId);
		buffer.writeInt(message.castTime);
	}

	@Override

	public SyncAbilityCastTime decode(FriendlyByteBuf buffer){

		int playerId = buffer.readInt();
		int castTime = buffer.readInt();

		return new SyncAbilityCastTime(playerId, castTime);
	}

	@Override
	public void handle(SyncAbilityCastTime message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));


		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER){
			ServerPlayer player = supplier.get().getSender();


			DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
				dragonStateHandler.getMagic().getAbilityFromSlot(dragonStateHandler.getMagic().getSelectedAbilitySlot()).setCastTime(message.castTime);
			});

			NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncAbilityCastTime(message.playerId, message.castTime));
		}
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(SyncAbilityCastTime message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {

			Player thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				Level world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof Player){

					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						if(message.castTime == 0 || message.castTime > dragonStateHandler.getMagic().getAbilityFromSlot(dragonStateHandler.getMagic().getSelectedAbilitySlot()).getCurrentCastTimer()){
							dragonStateHandler.getMagic().getAbilityFromSlot(dragonStateHandler.getMagic().getSelectedAbilitySlot()).setCastTime(message.castTime);
						}
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}