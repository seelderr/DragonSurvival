package by.dragonsurvivalteam.dragonsurvival.network.magic;


import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
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

public class SyncMagicStats implements IMessage<SyncMagicStats>{

	public int playerid;
	public int selectedSlot;
	public int currentMana;
	public boolean renderHotbar;

	public SyncMagicStats(){}

	public SyncMagicStats(int playerid, int selectedSlot, int currentMana, boolean renderHotbar){
		this.playerid = playerid;
		this.currentMana = currentMana;
		this.selectedSlot = selectedSlot;
		this.renderHotbar = renderHotbar;
	}

	@Override

	public void encode(SyncMagicStats message, FriendlyByteBuf buffer){

		buffer.writeInt(message.playerid);
		buffer.writeInt(message.selectedSlot);
		buffer.writeInt(message.currentMana);
		buffer.writeBoolean(message.renderHotbar);
	}

	@Override

	public SyncMagicStats decode(FriendlyByteBuf buffer){

		int playerid = buffer.readInt();
		int selectedSlot = buffer.readInt();
		int currentMana = buffer.readInt();
		boolean renderHotbar = buffer.readBoolean();

		return new SyncMagicStats(playerid, selectedSlot, currentMana, renderHotbar);
	}

	@Override
	public void handle(SyncMagicStats message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> run(message, supplier));
		supplier.get().setPacketHandled(true);
	}

	@OnlyIn( Dist.CLIENT )
	public void run(SyncMagicStats message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {

			Player thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				Level world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerid);
				if(entity instanceof Player){

					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						dragonStateHandler.getMagicData().setCurrentMana(message.currentMana);
						dragonStateHandler.getMagicData().setSelectedAbilitySlot(message.selectedSlot);
						dragonStateHandler.getMagicData().setRenderAbilities(message.renderHotbar);
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}