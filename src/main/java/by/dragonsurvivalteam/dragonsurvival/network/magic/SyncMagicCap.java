package by.dragonsurvivalteam.dragonsurvival.network.magic;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.MagicCap;
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

public class SyncMagicCap implements IMessage<SyncMagicCap>{
	public int playerId;
	public MagicCap cap;
	public CompoundTag nbt;

	public SyncMagicCap(){}

	public SyncMagicCap(int playerId, MagicCap cap){
		this.cap = cap;
		this.playerId = playerId;
	}

	public SyncMagicCap(int playerId, CompoundTag nbt){
		this.nbt = nbt;
		this.playerId = playerId;
	}

	@Override

	public void encode(SyncMagicCap message, FriendlyByteBuf buffer){
		buffer.writeInt(message.playerId);
		buffer.writeNbt(message.cap != null ? message.cap.writeNBT() : nbt);
	}

	@Override
	public SyncMagicCap decode(FriendlyByteBuf buffer){
		return new SyncMagicCap(buffer.readInt(), buffer.readNbt());
	}

	@Override
	public void handle(SyncMagicCap message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> run(message, supplier));

		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER){
			ServerPlayer player = supplier.get().getSender();

			DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
				dragonStateHandler.getMagicData().readNBT(message.nbt);
				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncMagicCap(player.getId(), dragonStateHandler.getMagicData()));
			});
		}
		supplier.get().setPacketHandled(true);
	}

	@OnlyIn( Dist.CLIENT )
	public void run(SyncMagicCap message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			Player thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				Level world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof Player){
					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						dragonStateHandler.getMagicData().readNBT(message.nbt);
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}