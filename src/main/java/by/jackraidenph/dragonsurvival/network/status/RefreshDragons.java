package by.jackraidenph.dragonsurvival.network.status;

import by.jackraidenph.dragonsurvival.client.render.ClientDragonRender;
import by.jackraidenph.dragonsurvival.common.entity.DSEntities;
import by.jackraidenph.dragonsurvival.common.entity.DragonEntity;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class RefreshDragons implements IMessage<RefreshDragons>
{
    public int playerId;

    public RefreshDragons(int playerId) {
        this.playerId = playerId;
    }

    public RefreshDragons() {
    }
    
    @Override
    public void encode(RefreshDragons message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.playerId);
    }
    
    @Override
    public RefreshDragons decode(FriendlyByteBuf buffer)
    {
        return new RefreshDragons(buffer.readInt());
    }
    
    @Override
    public void handle(RefreshDragons message, Supplier<Context> supplier)
    {
        if (supplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LocalPlayer myPlayer = Minecraft.getInstance().player;
                ClientDragonRender.dragonArmor = DSEntities.DRAGON_ARMOR.create(myPlayer.level);
                if (ClientDragonRender.dragonArmor != null)
                    ClientDragonRender.dragonArmor.player = myPlayer.getId();
                Player thatPlayer = (Player) myPlayer.level.getEntity(message.playerId);
                if (thatPlayer != null) {
                    DragonEntity dragonEntity = DSEntities.DRAGON.create(myPlayer.level);
                    dragonEntity.player = thatPlayer.getId();
                    ClientDragonRender.playerDragonHashMap.computeIfAbsent(thatPlayer.getId(), integer -> new AtomicReference<>(dragonEntity)).getAndSet(dragonEntity);
                }
            });
            thread.start();
        }
        supplier.get().setPacketHandled(true);
    }
}
