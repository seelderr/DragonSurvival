package by.jackraidenph.dragonsurvival.network.nest;

import by.jackraidenph.dragonsurvival.network.IMessage;
import by.jackraidenph.dragonsurvival.server.tileentity.NestTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SynchronizeNest implements IMessage<SynchronizeNest>
{
    public BlockPos pos;
    public int health;
    public int cooldown;

    public SynchronizeNest(BlockPos nestPos, int nestHealth, int cooldown) {
        pos = nestPos;
        health = nestHealth;
        this.cooldown = cooldown;
    }

    public SynchronizeNest() {

    }

    @Override
    public void encode(SynchronizeNest message, PacketBuffer buffer) {
        buffer.writeBlockPos(message.pos);
        buffer.writeInt(message.health);
        buffer.writeInt(message.cooldown);
    }

    @Override
    public SynchronizeNest decode(PacketBuffer buffer) {
        return new SynchronizeNest(buffer.readBlockPos(), buffer.readInt(), buffer.readInt());
    }

    @Override
    public void handle(SynchronizeNest message, Supplier<NetworkEvent.Context> supplier) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));
    }
    
    @OnlyIn(Dist.CLIENT)
    public void runClient(SynchronizeNest message, Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            PlayerEntity player = Minecraft.getInstance().player;
            ClientWorld world = Minecraft.getInstance().level;
            TileEntity entity = world.getBlockEntity(message.pos);
            if (entity instanceof NestTileEntity) {
                NestTileEntity nestEntity = (NestTileEntity) entity;
                nestEntity.energy = message.health;
                nestEntity.damageCooldown = message.cooldown;
                nestEntity.setChanged();
                if (nestEntity.energy <= 0) {
                    world.playSound(player, message.pos, SoundEvents.METAL_BREAK, SoundCategory.BLOCKS, 1, 1);
                } else {
                    world.playSound(player, message.pos, SoundEvents.SHIELD_BLOCK, SoundCategory.BLOCKS, 1, 1);
                }
                supplier.get().setPacketHandled(true);
            }
        });
    }
}
