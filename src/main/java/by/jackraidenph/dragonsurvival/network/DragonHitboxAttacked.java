package by.jackraidenph.dragonsurvival.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class DragonHitboxAttacked implements IMessage<DragonHitboxAttacked>
{
	public DragonHitboxAttacked() {}
	
	public int targetId;
	public float damage;
	
	public DragonHitboxAttacked(int targetId, float damage)
	{
		this.targetId = targetId;
		this.damage = damage;
	}
	
	@Override
	public void encode(DragonHitboxAttacked message, PacketBuffer buffer) {
		buffer.writeInt(message.targetId);
		buffer.writeFloat(message.damage);
	}
	
	@Override
	public DragonHitboxAttacked decode(PacketBuffer buffer)
	{
		return new DragonHitboxAttacked(buffer.readInt(), buffer.readFloat());
	}
	
	@Override
	public void handle(DragonHitboxAttacked message, Supplier<Context> supplier)
	{
		ServerPlayerEntity player = supplier.get().getSender();
		if(player == null) return;
		
		World world = player.level;
		Entity entity = world.getEntity(message.targetId);
		if (entity instanceof PlayerEntity) {
			player.attack(entity);
		}
	}
}
