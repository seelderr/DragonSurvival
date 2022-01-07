package by.jackraidenph.dragonsurvival.server.containers;

import by.jackraidenph.dragonsurvival.server.tileentity.SourceOfMagicTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

public class SourceOfMagicContainer extends Container {
    public SourceOfMagicTileEntity nestEntity;

    public SourceOfMagicContainer(int windowId, PlayerInventory inv, PacketBuffer data) {
        super(DSContainers.nestContainer, windowId);
        nestEntity = (SourceOfMagicTileEntity) inv.player.level.getBlockEntity(data.readBlockPos());
        int index = 0;
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(inv, index++, 8 + 18 * i, 142));
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(inv, index++, 8 + 18 * j, 84 + 18 * i));
            }
        }

        addSlot(new SlotItemHandler(new InvWrapper(nestEntity), 0, 80, 62) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack stack) {
                return SourceOfMagicTileEntity.consumables.containsKey(stack.getItem());
            }
        });
    }
    
    public ItemStack quickMoveStack(PlayerEntity p_82846_1_, int p_82846_2_) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(p_82846_2_);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (this.moveItemStackTo(itemstack1, 36, 36, false)) {
                return ItemStack.EMPTY;
            }else if (p_82846_2_ >= 9 && p_82846_2_ < 28) {
                if (!this.moveItemStackTo(itemstack1, 0, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (p_82846_2_ < 37) {
                if (!this.moveItemStackTo(itemstack1, 9, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 37, false)) {
                return ItemStack.EMPTY;
            }
            
            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            
            slot.onTake(p_82846_1_, itemstack1);
        }
        
        return itemstack;
    }
    
    @Override
    public boolean stillValid(PlayerEntity p_75145_1_)
    {
        return true;
    }
}
