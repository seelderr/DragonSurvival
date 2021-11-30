package by.jackraidenph.dragonsurvival.network.magic;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncDragonClawsMenu implements IMessage<SyncDragonClawsMenu>
{
	public int playerId;
	public boolean state;
	public Inventory inv;
	
	public SyncDragonClawsMenu() {}
	
	public SyncDragonClawsMenu(int playerId, boolean state, Inventory inv) {
		this.playerId = playerId;
		this.state = state;
		this.inv = inv;
	}
	
	@Override
	public void encode(SyncDragonClawsMenu message, PacketBuffer buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.state);
		
		CompoundNBT nbt = new CompoundNBT();
		nbt.put("list", message.inv.createTag());
		
		buffer.writeNbt(nbt);
	}
	
	@Override
	public SyncDragonClawsMenu decode(PacketBuffer buffer) {
		int playerId = buffer.readInt();
		boolean state = buffer.readBoolean();
		CompoundNBT tag = buffer.readNbt();
		ListNBT list = tag.getList("list", 10);
		Inventory inventory = new Inventory(4);
		inventory.fromTag(list);
		return new SyncDragonClawsMenu(playerId, state, inventory);
	}
	
	@Override
	public void handle(SyncDragonClawsMenu message, Supplier<NetworkEvent.Context> supplier) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> {
			NetworkEvent.Context context = supplier.get();
			context.enqueueWork(() -> {
				PlayerEntity thisPlayer = Minecraft.getInstance().player;
				if (thisPlayer != null) {
					World world = thisPlayer.level;
					Entity entity = world.getEntity(message.playerId);
					if (entity instanceof PlayerEntity) {
						DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
							dragonStateHandler.clawsMenuOpen = message.state;
							dragonStateHandler.clawsInventory.fromTag(message.inv.createTag());
						});
					}
				}
				context.setPacketHandled(true);
			});
		});
	}
}