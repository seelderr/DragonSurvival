package by.jackraidenph.dragonsurvival.network.status;

import by.jackraidenph.dragonsurvival.client.handlers.ClientEvents;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

/**
 * Jump animation length is 20.8 ticks
 */
public class PlayerJumpSync implements IMessage<PlayerJumpSync>
{
    public int playerId;
    public int ticks;

    public PlayerJumpSync(int playerId, int ticks) {
        this.playerId = playerId;
        this.ticks = ticks;
    }

    public PlayerJumpSync() {
    }
    
    @Override
    public void encode(PlayerJumpSync message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.playerId);
        buffer.writeByte(message.ticks);
    }
    
    @Override
    public PlayerJumpSync decode(FriendlyByteBuf buffer)
    {
        return new PlayerJumpSync(buffer.readInt(), buffer.readByte());
    }
    
    @Override
    public void handle(PlayerJumpSync message, Supplier<Context> supplier)
    {
        if (supplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            Entity entity = Minecraft.getInstance().level.getEntity(message.playerId);
            if (entity instanceof Player) {
                ClientEvents.dragonsJumpingTicks.put(entity.getId(), message.ticks);
            }
        }
        //the spam source was in this handler
        supplier.get().setPacketHandled(true);
    }
}
