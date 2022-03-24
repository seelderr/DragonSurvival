package by.dragonsurvivalteam.dragonsurvival.network.entity.player;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/entity/player/SortInventoryPacket.java
import by.jackraidenph.dragonsurvival.common.handlers.SortingHandler;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
=======
import by.dragonsurvivalteam.dragonsurvival.common.handlers.SortingHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/entity/player/SortInventoryPacket.java

import java.util.function.Supplier;

public class SortInventoryPacket implements IMessage<SortInventoryPacket>{
	public SortInventoryPacket(){}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/entity/player/SortInventoryPacket.java
	public void encode(SortInventoryPacket message, FriendlyByteBuf buffer) {}
	
	@Override
	public SortInventoryPacket decode(FriendlyByteBuf buffer)
	{
=======
	public void encode(SortInventoryPacket message, PacketBuffer buffer){}

	@Override
	public SortInventoryPacket decode(PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/entity/player/SortInventoryPacket.java
		return new SortInventoryPacket();
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/entity/player/SortInventoryPacket.java
	public void handle(SortInventoryPacket message, Supplier<Context> supplier)
	{
		ServerPlayer player = supplier.get().getSender();
		
=======
	public void handle(SortInventoryPacket message, Supplier<Context> supplier){
		ServerPlayerEntity player = supplier.get().getSender();

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/entity/player/SortInventoryPacket.java
		if(player != null){
			SortingHandler.sortInventory(player);
		}
	}
}