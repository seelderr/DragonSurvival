package by.dragonsurvivalteam.dragonsurvival.network.claw;


import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.ClawInventory;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncDragonClawsMenu implements IMessage<SyncDragonClawsMenu>{
	public int playerId;
	public boolean state;

	public SimpleContainer inv;

	public SyncDragonClawsMenu(){}

	public SyncDragonClawsMenu(int playerId, boolean state, SimpleContainer inv){

		this.playerId = playerId;
		this.state = state;
		this.inv = inv;
	}

	@Override

	public void encode(SyncDragonClawsMenu message, FriendlyByteBuf buffer){

		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.state);
		CompoundTag nbt = new CompoundTag();
		nbt.put("inv", ClawInventory.saveClawInventory(message.inv));
		buffer.writeNbt(nbt);
	}

	@Override

	public SyncDragonClawsMenu decode(FriendlyByteBuf buffer){

		int playerId = buffer.readInt();
		boolean state = buffer.readBoolean();
		CompoundTag tag = buffer.readNbt();
		SimpleContainer inventory = ClawInventory.readClawInventory(tag.getList("inv", 10));
		return new SyncDragonClawsMenu(playerId, state, inventory);
	}

	@Override
	public void handle(SyncDragonClawsMenu message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> run(message, supplier));
	}

	@OnlyIn( Dist.CLIENT )
	public void run(SyncDragonClawsMenu message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {

			Player thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				Level world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof Player){

					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						dragonStateHandler.getClawInventory().setClawsMenuOpen(message.state);
						dragonStateHandler.getClawInventory().setClawsInventory(message.inv);
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}