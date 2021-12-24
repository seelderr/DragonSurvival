package by.jackraidenph.dragonsurvival.network.entity.player;

import by.jackraidenph.dragonsurvival.common.handlers.SortingHandler;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class SortInventoryPacket implements IMessage<SortInventoryPacket>
{
	public SortInventoryPacket() {}
	
	@Override
	public void encode(SortInventoryPacket message, PacketBuffer buffer) {}
	
	@Override
	public SortInventoryPacket decode(PacketBuffer buffer)
	{
		return new SortInventoryPacket();
	}
	
	@Override
	public void handle(SortInventoryPacket message, Supplier<Context> supplier)
	{
		ServerPlayerEntity player = supplier.get().getSender();
		
		if(player != null){
			SortingHandler.sortInventory(player);
		}
	}
}
