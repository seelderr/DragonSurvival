package by.jackraidenph.dragonsurvival.network.container;

import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.IMessage;
import by.jackraidenph.dragonsurvival.server.containers.DragonContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenDragonInventory implements IMessage<OpenDragonInventory>
{
    @Override
    public void encode(OpenDragonInventory message, FriendlyByteBuf buffer) {}

    @Override
    public OpenDragonInventory decode(FriendlyByteBuf buffer) {
        return new OpenDragonInventory();
    }

    @Override
    public void handle(OpenDragonInventory message, Supplier<NetworkEvent.Context> supplier) {
        ServerPlayer serverPlayer = supplier.get().getSender();
        if(DragonStateProvider.isDragon(serverPlayer)) {
            
            if(serverPlayer.containerMenu != null){
                serverPlayer.containerMenu.removed(serverPlayer);
            }
            
            serverPlayer.openMenu(new SimpleMenuProvider((val1, inv, player) -> new DragonContainer(val1, inv, false), new TextComponent("")));
        }
    }
}
