package by.jackraidenph.dragonsurvival.network.magic;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
		
		for(int i = 0; i < 4; i++){
			ItemStack stack = message.inv.getItem(i);
			
			if(!stack.isEmpty()){
				nbt.put(Integer.toString(i), stack.save(new CompoundNBT()));
			}
		}
		
		buffer.writeNbt(nbt);
	}
	
	@Override
	public SyncDragonClawsMenu decode(PacketBuffer buffer) {
		int playerId = buffer.readInt();
		boolean state = buffer.readBoolean();
		CompoundNBT tag = buffer.readNbt();
		
		Inventory inventory = new Inventory(4);
		
		for(int i = 0; i < 4; i++){
			if(tag.contains(Integer.toString(i))){
				ItemStack stack = ItemStack.of(tag.getCompound(Integer.toString(i)));
				
				if(!stack.isEmpty()){
					inventory.setItem(i, stack);
				}
			}
		}
		
		return new SyncDragonClawsMenu(playerId, state, inventory);
	}
	
	@Override
	public void handle(SyncDragonClawsMenu message, Supplier<NetworkEvent.Context> supplier) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> run(message, supplier));
	}
	
	@OnlyIn(Dist.CLIENT)
	public void run(SyncDragonClawsMenu message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if (thisPlayer != null) {
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if (entity instanceof PlayerEntity) {
					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						dragonStateHandler.clawsMenuOpen = message.state;
						dragonStateHandler.clawsInventory = new Inventory(4);
						
						for(int i = 0; i < 4; i++){
							dragonStateHandler.clawsInventory.setItem(i, message.inv.getItem(i));
						}
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}