package by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class ClawInventory extends SubCap {
    public enum Slot {
        SWORD,
        PICKAXE,
        AXE,
        SHOVEL;

        /**
         * Equivalent to the container size
         */
        public static int size() {
            return values().length;
        }
    }

    private final SimpleContainer clawsInventory = new SimpleContainer(4);

    public boolean shouldRenderClaws = true;

    private boolean isMenuOpen;

    public ClawInventory(final DragonStateHandler handler) {
        super(handler);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        tag.putBoolean("clawsMenu", isMenuOpen);
        for (Slot slot : Slot.values()) {
            if (clawsInventory.getItem(slot.ordinal()).isEmpty()) continue;
            tag.put(slot.name(), clawsInventory.getItem(slot.ordinal()).save(provider));
        }
        tag.putBoolean("renderClaws", shouldRenderClaws);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        setMenuOpen(tag.getBoolean("clawsMenu"));
        shouldRenderClaws = tag.getBoolean("renderClaws");

        for (Slot slot : Slot.values()) {
            CompoundTag slotTag = tag.getCompound(slot.name());
            if (slotTag.isEmpty()) continue;
            Optional<ItemStack> stack = ItemStack.parse(provider, slotTag);
            if (stack.isEmpty()) continue;
            clawsInventory.setItem(slot.ordinal(), stack.get());
        }
    }

    public void set(final Slot slot, final ItemStack stack) {
        clawsInventory.setItem(slot.ordinal(), stack);
    }

    public ItemStack get(final Slot slot) {
        return clawsInventory.getItem(slot.ordinal());
    }

    public ItemStack getSword() {
        return clawsInventory.getItem(Slot.SWORD.ordinal());
    }

    public ItemStack getPickaxe() {
        return clawsInventory.getItem(Slot.PICKAXE.ordinal());
    }

    public ItemStack getAxe() {
        return clawsInventory.getItem(Slot.AXE.ordinal());
    }

    public ItemStack getShovel() {
        return clawsInventory.getItem(Slot.SHOVEL.ordinal());
    }

    public void setMenuOpen(boolean isMenuOpen) {
        this.isMenuOpen = isMenuOpen;
    }

    public SimpleContainer getClawsInventory() {
        return clawsInventory;
    }

    public boolean isMenuOpen() {
        return isMenuOpen;
    }
}