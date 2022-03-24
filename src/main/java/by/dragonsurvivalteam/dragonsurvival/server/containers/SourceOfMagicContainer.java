package by.dragonsurvivalteam.dragonsurvival.server.containers;

import by.dragonsurvivalteam.dragonsurvival.server.tileentity.SourceOfMagicTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

public class SourceOfMagicContainer extends Container{
	public SourceOfMagicTileEntity nestEntity;

	public SourceOfMagicContainer(int windowId, PlayerInventory inv, PacketBuffer data){
		super(DSContainers.nestContainer, windowId);
		nestEntity = (SourceOfMagicTileEntity)inv.player.level.getBlockEntity(data.readBlockPos());
		int index = 0;
		for(int i = 0; i < 9; i++){
			addSlot(new Slot(inv, index++, 8 + 18 * i, 142));
		}
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 9; j++){
				addSlot(new Slot(inv, index++, 8 + 18 * j, 84 + 18 * i));
			}
		}

		addSlot(new SlotItemHandler(new InvWrapper(nestEntity), 0, 80, 62){
			@Override
			public boolean mayPlace(
				@Nonnull
					ItemStack stack){
				return SourceOfMagicTileEntity.consumables.containsKey(stack.getItem());
			}
		});
	}

	public ItemStack quickMoveStack(PlayerEntity pPlayer, int pIndex){
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(pIndex);
		if(slot != null && slot.hasItem()){
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if(pIndex == 0){
				if(!this.moveItemStackTo(itemstack1, 1, 37, true)){
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(itemstack1, itemstack);
			}else if(this.moveItemStackTo(itemstack1, 0, 1, false)){
				return ItemStack.EMPTY;
			}else if(pIndex >= 1 && pIndex < 28){
				if(!this.moveItemStackTo(itemstack1, 28, 37, false)){
					return ItemStack.EMPTY;
				}
			}else if(pIndex >= 28 && pIndex < 37){
				if(!this.moveItemStackTo(itemstack1, 1, 28, false)){
					return ItemStack.EMPTY;
				}
			}else if(!this.moveItemStackTo(itemstack1, 1, 37, false)){
				return ItemStack.EMPTY;
			}

			if(itemstack1.isEmpty()){
				slot.set(ItemStack.EMPTY);
			}else{
				slot.setChanged();
			}

			if(itemstack1.getCount() == itemstack.getCount()){
				return ItemStack.EMPTY;
			}

			slot.onTake(pPlayer, itemstack1);
		}

		return itemstack;
	}

	@Override
	public boolean stillValid(PlayerEntity p_75145_1_){
		return true;
	}
}