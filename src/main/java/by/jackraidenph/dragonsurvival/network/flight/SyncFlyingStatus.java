package by.jackraidenph.dragonsurvival.network.flight;

import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.IMessage;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import net.minecraft.client.Minecraft;
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


public class SyncFlyingStatus implements IMessage<SyncFlyingStatus>
{
    public int playerId;
    public boolean state;

    public SyncFlyingStatus() {
    }

    public SyncFlyingStatus(int playerId, boolean state) {
        this.state = state;
        this.playerId = playerId;
    }

    @Override
    public void encode(SyncFlyingStatus message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.playerId);
        buffer.writeBoolean(message.state);
    }

    @Override
    public SyncFlyingStatus decode(FriendlyByteBuf buffer) {
        return new SyncFlyingStatus(buffer.readInt(), buffer.readBoolean());
    }

    @Override
    public void handle(SyncFlyingStatus message, Supplier<NetworkEvent.Context> supplier) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> run(message, supplier));
    
        if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER){
            ServerPlayer entity = supplier.get().getSender();
            if(entity != null){
                DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
                    dragonStateHandler.setWingsSpread(message.state);
                });
    
                NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SyncFlyingStatus(entity.getId(), message.state));
            }
        }
    }
    
    @OnlyIn( Dist.CLIENT)
    public void run(SyncFlyingStatus message, Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Player thisPlayer = Minecraft.getInstance().player;
            if (thisPlayer != null) {
                Level world = thisPlayer.level;
                Entity entity = world.getEntity(message.playerId);
                if (entity instanceof Player) {
                    DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
                        dragonStateHandler.setWingsSpread(message.state);
                    });
                }
            }
            context.setPacketHandled(true);
        });
    }
}
