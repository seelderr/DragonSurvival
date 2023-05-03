package by.dragonsurvivalteam.dragonsurvival.network.magic;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.magic.ClientCastingHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
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
	public boolean isCasting;
	public CompoundTag tag;

	public SyncAbilityCasting(){

	}

	public SyncAbilityCasting(int playerId, boolean isCasting, CompoundTag nbt){
		this.playerId = playerId;
		this.isCasting = isCasting;
		tag = nbt;
	}

	@Override

	public void encode(SyncAbilityCasting message, FriendlyByteBuf buffer){
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.isCasting);
		buffer.writeNbt(message.tag);
	}

	@Override

	public SyncAbilityCasting decode(FriendlyByteBuf buffer){
		int playerId = buffer.readInt();
		return new SyncAbilityCasting(playerId, buffer.readBoolean(), buffer.readNbt());
	}

	@Override
	public void handle(SyncAbilityCasting message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> run(message, supplier));

		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER){
			ServerPlayer player = supplier.get().getSender();

			DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
				ActiveDragonAbility ability = dragonStateHandler.getMagicData().getAbilityFromSlot(dragonStateHandler.getMagicData().getSelectedAbilitySlot());
				ability.loadNBT(message.tag);

				dragonStateHandler.getMagicData().isCasting = message.isCasting;
				if(message.isCasting){
					ability.onKeyPressed(player, () -> {});
				}else{
					ability.onKeyReleased(player);
				}
			});

			NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncAbilityCasting(player.getId(), message.isCasting, message.tag));
		}
		supplier.get().setPacketHandled(true);
	}

	@OnlyIn( Dist.CLIENT )
	public void run(SyncAbilityCasting message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			Player thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				Level world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof Player player){
					DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
						ActiveDragonAbility ability = dragonStateHandler.getMagicData().getAbilityFromSlot(dragonStateHandler.getMagicData().getSelectedAbilitySlot());
						ability.loadNBT(message.tag);
						dragonStateHandler.getMagicData().isCasting = message.isCasting;
						if(message.isCasting){
							ability.onKeyPressed(player, () -> {
								if(player.getId() == thisPlayer.getId()) {
									ClientCastingHandler.hasCast = true;
									ClientCastingHandler.status = 2;
								}
							});
						}else{
							ability.onKeyReleased(player);
						}
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}