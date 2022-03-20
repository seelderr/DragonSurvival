package by.dragonsurvivalteam.dragonsurvival.network.magic;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
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
	public void encode(SyncMagicStats message, PacketBuffer buffer){
		buffer.writeInt(message.playerid);
		buffer.writeInt(message.selectedSlot);
		buffer.writeInt(message.currentMana);
		buffer.writeBoolean(message.renderHotbar);
	}

	@Override
	public SyncMagicStats decode(PacketBuffer buffer){
		int playerid = buffer.readInt();
		int selectedSlot = buffer.readInt();
		int currentMana = buffer.readInt();
		boolean renderHotbar = buffer.readBoolean();

		return new SyncMagicStats(playerid, selectedSlot, currentMana, renderHotbar);
	}

	@Override
	public void handle(SyncMagicStats message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> run(message, supplier));
	}

	@OnlyIn( Dist.CLIENT )
	public void run(SyncMagicStats message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerid);
				if(entity instanceof PlayerEntity){
					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						dragonStateHandler.getMagic().setCurrentMana(message.currentMana);
						dragonStateHandler.getMagic().setSelectedAbilitySlot(message.selectedSlot);
						dragonStateHandler.getMagic().setRenderAbilities(message.renderHotbar);
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}