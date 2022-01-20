package by.jackraidenph.dragonsurvival.network.entity.player;

import by.jackraidenph.dragonsurvival.common.handlers.SortingHandler;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class SortInventoryPacket implements IMessage<SortInventoryPacket>
{
	public SortInventoryPacket() {}
	
	@Override
	public void encode(SortInventoryPacket message, FriendlyByteBuf buffer) {}
	
	@Override
	public SortInventoryPacket decode(FriendlyByteBuf buffer)
	{
		return new SortInventoryPacket();
	}
	
	@Override
	public void handle(SortInventoryPacket message, Supplier<Context> supplier)
	{
		ServerPlayer player = supplier.get().getSender();
		
		if(player != null){
			SortingHandler.sortInventory(player);
		}
	}
}
