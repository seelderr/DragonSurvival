package by.jackraidenph.dragonsurvival.network.magic;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncAbilityCooldown implements IMessage<SyncAbilityCooldown>
{

    private int slot;
    private int cooldown;

    public SyncAbilityCooldown(int slot, int cooldown) {
        this.slot = slot;
        this.cooldown = cooldown;
    }

    public SyncAbilityCooldown() {}
    
    @Override
    public void encode(SyncAbilityCooldown message, PacketBuffer buffer) {
        buffer.writeInt(message.slot);
        buffer.writeInt(message.cooldown);
    }

    @Override
    public SyncAbilityCooldown decode(PacketBuffer buffer) {
        int slot = buffer.readInt();
        int cooldown = buffer.readInt();
        return new SyncAbilityCooldown(slot, cooldown);
    }

    @Override
    public void handle(SyncAbilityCooldown message, Supplier<NetworkEvent.Context> supplier) {
        ServerPlayerEntity playerEntity = supplier.get().getSender();

        if(playerEntity == null) return;

        DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
            ActiveDragonAbility ability = dragonStateHandler.getMagic().getAbilityFromSlot(message.slot);
            ability.setCooldown(message.cooldown);
        });
    }
}
