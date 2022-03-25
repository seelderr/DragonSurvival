package by.dragonsurvivalteam.dragonsurvival.network.magic;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.DragonAbility;
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


public class SyncAbilityCasting implements IMessage<SyncAbilityCasting>{

	public int playerId;
	public DragonAbility currentAbility;

	public SyncAbilityCasting(){

	}

	public SyncAbilityCasting(int playerId, DragonAbility currentAbility){
		this.playerId = playerId;
		this.currentAbility = currentAbility;
	}

	@Override

	public void encode(SyncAbilityCasting message, FriendlyByteBuf buffer){

		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.currentAbility != null);

		if(message.currentAbility != null){
			buffer.writeUtf(message.currentAbility.getId());
			buffer.writeNbt(message.currentAbility.saveNBT());
		}
	}

	@Override

	public SyncAbilityCasting decode(FriendlyByteBuf buffer){

		int playerId = buffer.readInt();
		DragonAbility ability = null;
		boolean hasAbility = buffer.readBoolean();

		if(hasAbility){
			String id = buffer.readUtf();
			ability = DragonAbilities.ABILITY_LOOKUP.get(id).createInstance();
			ability.loadNBT(buffer.readNbt());
		}

		return new SyncAbilityCasting(playerId, ability);
	}

	@Override
	public void handle(SyncAbilityCasting message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));


		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER){
			ServerPlayer player = supplier.get().getSender();


			DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
				if(message.currentAbility != dragonStateHandler.getMagic().getCurrentlyCasting() && dragonStateHandler.getMagic().getCurrentlyCasting() != null){
					dragonStateHandler.getMagic().getCurrentlyCasting().stopCasting();
				}

				dragonStateHandler.getMagic().setCurrentlyCasting((ActiveDragonAbility)message.currentAbility);
			});

			NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncAbilityCasting(message.playerId, message.currentAbility));
		}
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(SyncAbilityCasting message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {

			Player thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				Level world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof Player){

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