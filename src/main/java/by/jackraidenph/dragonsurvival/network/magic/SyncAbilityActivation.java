package by.jackraidenph.dragonsurvival.network.magic;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.network.IMessage;
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

public class SyncAbilityActivation implements IMessage<SyncAbilityActivation>
{
    
    public int playerId;
    public int slot;

    public SyncAbilityActivation(int playerId, int slot) {
        this.playerId = playerId;
        this.slot = slot;
    }

    public SyncAbilityActivation() {
    }
    
    @Override
    public void encode(SyncAbilityActivation message, PacketBuffer buffer) {
        buffer.writeInt(message.playerId);
        buffer.writeInt(message.slot);
    }

    @Override
    public SyncAbilityActivation decode(PacketBuffer buffer) {
        int playerId = buffer.readInt();
        int slot = buffer.readInt();
        return new SyncAbilityActivation(playerId, slot);
    }
    
    @Override
    public void handle(SyncAbilityActivation message, Supplier<NetworkEvent.Context> supplier) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> run(message, supplier));
    }
    
    @OnlyIn(Dist.CLIENT)
    public void run(SyncAbilityActivation message, Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            PlayerEntity thisPlayer = Minecraft.getInstance().player;
            if (thisPlayer != null) {
                World world = thisPlayer.level;
                Entity entity = world.getEntity(message.playerId);
                if (entity instanceof PlayerEntity) {
                    DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
                        DragonAbility ability = dragonStateHandler.getMagic().getAbilityFromSlot(message.slot);
                        if(ability.getLevel() > 0) {
                            ability.onKeyPressed((PlayerEntity)entity);
                        }
                    });
                }
            }
            context.setPacketHandled(true);
        });
    }
}
