package by.jackraidenph.dragonsurvival.containers;

import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.gui.magic.Slots.ClawToolSlot;
import by.jackraidenph.dragonsurvival.registration.Containers;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.IIntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DragonContainer extends RecipeBookContainer<CraftingInventory> {
    public final CraftingInventory craftMatrix;
    public final CraftResultInventory craftResult = new CraftResultInventory();
    
    public List<Slot> craftingSlots = new ArrayList<>();
    public List<Slot> inventorySlots = new ArrayList<>();
    
    public final boolean isLocalWorld;
    public final PlayerEntity player;
    public PlayerInventory playerInventory;
    
    private int craftingSlot;
    
    private static final EquipmentSlotType[] VALID_EQUIPMENT_SLOTS = new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};
    private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{PlayerContainer.EMPTY_ARMOR_SLOT_BOOTS, PlayerContainer.EMPTY_ARMOR_SLOT_LEGGINGS, PlayerContainer.EMPTY_ARMOR_SLOT_CHESTPLATE, PlayerContainer.EMPTY_ARMOR_SLOT_HELMET};
    
    public int menuStatus = 0;
    protected final IIntArray dataStatus = new IIntArray() {
        public int get(int p_221476_1_) {
            switch(p_221476_1_) {
                case 0:
                    return DragonContainer.this.menuStatus;
                    
                default:
                    return 0;
            }
        }
        
        public void set(int p_221477_1_, int p_221477_2_) {
            switch(p_221477_1_) {
                case 0:
                    DragonContainer.this.menuStatus = p_221477_2_;
                    break;
            }
            
        }
        
        public int getCount() {
            return 1;
        }
    };
    
    public DragonContainer(int id, PlayerInventory playerInventory, boolean localWorld) {
        super(Containers.dragonContainer, id);
        this.isLocalWorld = localWorld;
        this.player = playerInventory.player;
        this.playerInventory = playerInventory;
        craftMatrix = new CraftingInventory(this, 3, 3);
        
        slots.clear();
        craftingSlots.clear();
        inventorySlots.clear();
        clearCraftingContent();
    
        this.addDataSlots(dataStatus);
        
        
        for (int k = 0; k < 4; ++k) {
            final EquipmentSlotType equipmentslottype = VALID_EQUIPMENT_SLOTS[k];
            this.addSlot(new Slot(playerInventory, 39 - k, 8, 8 + k * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.canEquip(equipmentslottype, player);
                }
            
                @Override
                public boolean mayPickup(PlayerEntity playerIn) {
                    ItemStack itemstack = this.getItem();
                    return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.mayPickup(playerIn);
                }
            
                @Override
                @OnlyIn( Dist.CLIENT)
                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(PlayerContainer.BLOCK_ATLAS, ARMOR_SLOT_TEXTURES[equipmentslottype.getIndex()]);
                }
            });
        
            broadcastChanges();
        }
        
        //main inventory
        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                Slot s = new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18);
                this.addSlot(s);
                inventorySlots.add(s);
            }
        }
        //hotbar
        for (int i1 = 0; i1 < 9; ++i1) {
            Slot s = new Slot(playerInventory, i1, 8 + i1 * 18, 142);
            this.addSlot(s);
            inventorySlots.add(s);
        }
    
        DragonStateProvider.getCap(player).ifPresent((cap) -> {
            for (int i = 0; i < 4; ++i) {
                ClawToolSlot s = new ClawToolSlot(this, cap.clawsInventory, DragonStateHandler.CLAW_TOOL_TYPES[i], i, -50, 35 + (i * 18));
                this.addSlot(s);
                inventorySlots.add(s);
            }
        });
    
        //Offhand
        this.addSlot(new Slot(playerInventory, 40, 26, 62));
    
        
        this.addSlot(new CraftingResultSlot(playerInventory.player, this.craftMatrix, this.craftResult, 0, 178, 33));
        craftingSlot = slots.size();
        
        int slotIndex = 0;
        for (int i = 0; i < craftMatrix.getWidth(); ++i) {
            for (int j = 0; j < craftMatrix.getHeight(); ++j) {
                Slot s = new Slot(this.craftMatrix, slotIndex++, 111 + j * 18, 15 + i * 18);
                this.addSlot(s);
                craftingSlots.add(s);
            }
        }
        
        update();
    }
    
    public void update(){
        DragonStateProvider.getCap(player).ifPresent((cap) -> {
            menuStatus = cap.clawsMenuOpen ? 1 : 0;
        });
        
        broadcastChanges();
    }
    
    
    @Override
    public void fillCraftSlotsStackedContents(RecipeItemHelper p_201771_1_)
    {
        craftMatrix.fillStackedContents(p_201771_1_);
    }
    
    @Override
    public void clearCraftingContent()
    {
        craftMatrix.clearContent();
        craftResult.clearContent();
    }
    
    @Override
    public boolean recipeMatches(IRecipe<? super CraftingInventory> p_201769_1_)
    {
        return p_201769_1_.matches(this.craftMatrix, this.player.level);
    }
    
    @Override
    public int getResultSlotIndex()
    {
        return 45;
    }
    
    @Override
    public int getGridWidth()
    {
        return craftMatrix.getWidth();
    }
    
    @Override
    public int getGridHeight()
    {
        return craftMatrix.getHeight();
    }
    
    @Override
    public int getSize() {
        return 10;
    }
    

    @Override
    public boolean canTakeItemForPickAll(@Nonnull ItemStack stack, Slot slotIn) {
        return slotIn.container != this.craftResult && super.canTakeItemForPickAll(stack, slotIn);
    }
    
    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            EquipmentSlotType equipmentslottype = MobEntity.getEquipmentSlotForItem(itemstack);
            
            if (index == craftingSlot) {
                if (!this.moveItemStackTo(itemstack1, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (equipmentslottype.getType() == EquipmentSlotType.Group.ARMOR && !this.slots.get(3 - equipmentslottype.getIndex()).hasItem()) {
                int i = 3 - equipmentslottype.getIndex();
                if (!this.moveItemStackTo(itemstack1, i, i + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 4) {
                if (!this.moveItemStackTo(itemstack1, 4, 40, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 9) {
                if (!this.moveItemStackTo(itemstack1, 31, 40, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (equipmentslottype == EquipmentSlotType.OFFHAND && !this.slots.get(44).hasItem()) {
                if (!this.moveItemStackTo(itemstack1, 44, 46, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 4 && index < 30) {
                if (!this.moveItemStackTo(itemstack1, 31, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 31 && index < 39) {
                if (!this.moveItemStackTo(itemstack1, 4, 31, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 9, 45, false)) {
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
        
            ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);
            if (index == 0) {
                playerIn.drop(itemstack2, false);
            }
        }
    
        return itemstack;
    }
    

    /**
     * Callback for when the crafting matrix is changed.
     */
    @Override
    public void slotsChanged(IInventory inventory) {
        if (!player.level.isClientSide) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) player;
            ItemStack itemstack = ItemStack.EMPTY;
            Optional<ICraftingRecipe> optional = player.level.getServer().getRecipeManager().getRecipeFor(IRecipeType.CRAFTING, craftMatrix, player.level);
            if (optional.isPresent()) {
                ICraftingRecipe icraftingrecipe = optional.get();
                if (craftResult.setRecipeUsed(player.level, serverplayerentity, icraftingrecipe)) {
                    itemstack = icraftingrecipe.assemble(craftMatrix);
                }
            }
    
            craftResult.setItem(45, itemstack);
            serverplayerentity.connection.send(new SSetSlotPacket(containerId, 45, itemstack));
        }
    }

    /**
     * Called when the container is closed.
     */
    @Override
    public void removed(PlayerEntity playerEntity) {
        super.removed(playerEntity);
        this.craftResult.clearContent();
        if (!playerEntity.level.isClientSide) {
            this.clearContainer(playerEntity, playerEntity.level, this.craftMatrix);
        }
    }
    

    @Override
    public List<RecipeBookCategories> getRecipeBookCategories() {
        return Lists.newArrayList(RecipeBookCategories.CRAFTING_SEARCH, RecipeBookCategories.CRAFTING_EQUIPMENT, RecipeBookCategories.CRAFTING_BUILDING_BLOCKS, RecipeBookCategories.CRAFTING_MISC, RecipeBookCategories.CRAFTING_REDSTONE);
    }
    
    @Override
    public RecipeBookCategory getRecipeBookType()
    {
        return RecipeBookCategory.CRAFTING;
    }
    
    @Override
    public boolean stillValid(PlayerEntity p_75145_1_)
    {
        return true;
    }
}
