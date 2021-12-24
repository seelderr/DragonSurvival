package by.jackraidenph.dragonsurvival.network.nest;

import by.jackraidenph.dragonsurvival.common.blocks.DSBlocks;
import by.jackraidenph.dragonsurvival.network.IMessage;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class GiveNest implements IMessage<GiveNest>
{
    public DragonType dragonType;

    public GiveNest(DragonType dragonType) {
        this.dragonType = dragonType;
    }

    public GiveNest() {
    }
    
    @Override
    public void encode(GiveNest message, PacketBuffer buffer)
    {
        buffer.writeEnum(message.dragonType);
    }
    
    @Override
    public GiveNest decode(PacketBuffer buffer)
    {
        return new GiveNest(buffer.readEnum(DragonType.class));
    }
    
    @Override
    public void handle(GiveNest message, Supplier<Context> supplier)
    {
        ServerPlayerEntity playerEntity = supplier.get().getSender();
        Block item;
        switch (message.dragonType) {
            case CAVE:
                item = DSBlocks.smallCaveNest;
                break;
            case FOREST:
                item = DSBlocks.smallForestNest;
                break;
            case SEA:
                item = DSBlocks.smallSeaNest;
                break;
            default:
                item = null;
        }
        ItemStack itemStack = new ItemStack(item);
        if (playerEntity.getOffhandItem().isEmpty()) {
            playerEntity.setItemInHand(Hand.OFF_HAND, itemStack);
        } else {
            ItemStack stack = playerEntity.getOffhandItem().copy();
            playerEntity.setItemInHand(Hand.OFF_HAND, itemStack);
            if (!playerEntity.inventory.add(stack)) {
                playerEntity.drop(stack, false, false);
            }
        }
    }
}
