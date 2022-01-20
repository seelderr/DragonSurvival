package by.jackraidenph.dragonsurvival.network.flight;

import by.jackraidenph.dragonsurvival.network.IMessage;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.TargetPoint;

import java.util.function.Supplier;

public class SyncFlightSpeed implements IMessage<SyncFlightSpeed>
{
    public int playerId;
    public Vec3 flightSpeed;

    public SyncFlightSpeed() {
    }
    
    public SyncFlightSpeed(int playerId, Vec3 flightSpeed)
    {
        this.playerId = playerId;
        this.flightSpeed = flightSpeed;
    }
    
    @Override
    public void encode(SyncFlightSpeed message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.playerId);
        buffer.writeDouble(message.flightSpeed.x);
        buffer.writeDouble(message.flightSpeed.y);
        buffer.writeDouble(message.flightSpeed.z);
    }

    @Override
    public SyncFlightSpeed decode(FriendlyByteBuf buffer) {
        return new SyncFlightSpeed(buffer.readInt(), new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
    }

    @Override
    public void handle(SyncFlightSpeed message, Supplier<NetworkEvent.Context> supplier) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> run(message, supplier));
    
        if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER){
            ServerPlayer entity = supplier.get().getSender();
            if(entity != null){
                entity.setDeltaMovement(message.flightSpeed);
                
                TargetPoint point = new TargetPoint(entity, entity.position().x, entity.position().y, entity.position().z, 32, entity.level.dimension());
                NetworkHandler.CHANNEL.send(PacketDistributor.NEAR.with(() -> point), new SyncFlightSpeed(entity.getId(), message.flightSpeed));
            }
        }
    }
    
    @OnlyIn( Dist.CLIENT)
    public void run(SyncFlightSpeed message, Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Player thisPlayer = Minecraft.getInstance().player;
            if (thisPlayer != null) {
                Level world = thisPlayer.level;
                Entity entity = world.getEntity(message.playerId);
                if (entity instanceof Player) {
                    entity.setDeltaMovement(message.flightSpeed);
                }
            }
            context.setPacketHandled(true);
        });
    }
}
