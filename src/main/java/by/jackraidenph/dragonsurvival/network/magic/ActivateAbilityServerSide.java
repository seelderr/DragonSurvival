package by.jackraidenph.dragonsurvival.network.magic;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.handlers.ServerSide.NetworkHandler;
import by.jackraidenph.dragonsurvival.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.TargetPoint;

import java.util.function.Supplier;

public class ActivateAbilityServerSide implements IMessage<ActivateAbilityServerSide>
{

    private int slot;

    public ActivateAbilityServerSide(int slot) {
        this.slot = slot;
    }

    public ActivateAbilityServerSide() {
    }
    
    @Override
    public void encode(ActivateAbilityServerSide message, PacketBuffer buffer) {
        buffer.writeInt(message.slot);
    }

    @Override
    public ActivateAbilityServerSide decode(PacketBuffer buffer) {
        int slot = buffer.readInt();
        return new ActivateAbilityServerSide(slot);
    }

    @Override
    public void handle(ActivateAbilityServerSide message, Supplier<NetworkEvent.Context> supplier) {
        ServerPlayerEntity playerEntity = supplier.get().getSender();

        if(playerEntity == null)
            return;

        DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
            DragonAbility ability = dragonStateHandler.getAbilityFromSlot(message.slot);
            if(ability.getLevel() > 0) {
                ability.onKeyPressed(playerEntity);
            }
        });
        
        TargetPoint point = new TargetPoint(playerEntity, playerEntity.position().x, playerEntity.position().y, playerEntity.position().z, 64, playerEntity.level.dimension());
        NetworkHandler.CHANNEL.send(PacketDistributor.NEAR.with(() -> point), new SyncAbilityActivation(playerEntity.getId(), message.slot));
    }
}
