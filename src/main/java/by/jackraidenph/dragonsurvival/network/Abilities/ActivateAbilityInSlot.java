package by.jackraidenph.dragonsurvival.network.Abilities;

import by.jackraidenph.dragonsurvival.abilities.common.DragonAbility;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ActivateAbilityInSlot implements IMessage<ActivateAbilityInSlot>
{

    private int slot;
    private byte glfwMode;

    public ActivateAbilityInSlot(int slot, byte glfwMode) {
        this.slot = slot;
        this.glfwMode = glfwMode;
    }

    public ActivateAbilityInSlot() {
    }
    
    @Override
    public void encode(ActivateAbilityInSlot message, PacketBuffer buffer) {
        buffer.writeInt(message.slot);
        buffer.writeByte(message.glfwMode);
    }

    @Override
    public ActivateAbilityInSlot decode(PacketBuffer buffer) {
        int slot = buffer.readInt();
        byte glfwMode = buffer.readByte();
        return new ActivateAbilityInSlot(slot, glfwMode);
    }

    @Override
    public void handle(ActivateAbilityInSlot message, Supplier<NetworkEvent.Context> supplier) {
        ServerPlayerEntity playerEntity = supplier.get().getSender();

        if(playerEntity == null)
            return;

        DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
            DragonAbility ability = dragonStateHandler.getAbilityFromSlot(message.slot);
            if(ability.getLevel() > 0) {
                ability.onKeyPressed(playerEntity);
            }
        });
    }
}
