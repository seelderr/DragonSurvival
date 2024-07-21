package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.items.armor.PermanentEnchantmentItem;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.GrindstoneEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@SuppressWarnings("unused")
@EventBusSubscriber
public class CraftingHandler {
    @SubscribeEvent
    public static void returnBeacon(PlayerEvent.ItemCraftedEvent craftedEvent){
        Container inventory = craftedEvent.getInventory();
        ItemStack result = craftedEvent.getCrafting();
        int rem = ContainerHelper.clearOrCountMatchingItems(inventory, item -> item.getItem() == DSItems.PASSIVE_FIRE_BEACON
                || item.getItem() == DSItems.PASSIVE_MAGIC_BEACON
                || item.getItem() == DSItems.PASSIVE_PEACE_BEACON, 1, true);
        if(rem == 0 && result.getItem() == DSBlocks.DRAGON_BEACON.get().asItem()){
            craftedEvent.getEntity().addItem(new ItemStack(Items.BEACON));
        }
    }

    @SubscribeEvent
    public static void returnNetherStarHeart(PlayerEvent.ItemCraftedEvent craftedEvent){
        Container inventory = craftedEvent.getInventory();
        ItemStack result = craftedEvent.getCrafting();
        int rem = ContainerHelper.clearOrCountMatchingItems(inventory, item -> item.getItem() == DSItems.STAR_HEART, 1, true);
        if(rem == 0 && result.getItem() == DSItems.STAR_HEART){
            craftedEvent.getEntity().addItem(new ItemStack(Items.NETHER_STAR));
        }
    }

    @SubscribeEvent
    public static void returnNetherStarBone(PlayerEvent.ItemCraftedEvent craftedEvent){
        Container inventory = craftedEvent.getInventory();
        ItemStack result = craftedEvent.getCrafting();
        int rem = ContainerHelper.clearOrCountMatchingItems(inventory, item -> item.getItem() == DSItems.STAR_BONE, 1, true);
        if(rem == 0 && result.getItem() == DSItems.STAR_BONE){
            craftedEvent.getEntity().addItem(new ItemStack(Items.NETHER_STAR));
        }
    }

    @SubscribeEvent
    public static void showGrindstoneResult(GrindstoneEvent.OnPlaceItem grindstoneEvent) {
        ItemStack itemStack = grindstoneEvent.getTopItem().copy();
        if (itemStack.getItem() instanceof PermanentEnchantmentItem alignedItem) {
            if (!grindstoneEvent.getBottomItem().getItem().isValidRepairItem(itemStack, grindstoneEvent.getBottomItem()) && getExperienceFromAlignedDragonArmor(itemStack, alignedItem.getDefaultEnchantments()) <= 0) {
                grindstoneEvent.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void takeFromGrindstone(GrindstoneEvent.OnTakeItem grindstoneEvent) {
        ItemStack itemStack = grindstoneEvent.getNewTopItem();
        if (itemStack.getItem() instanceof PermanentEnchantmentItem item) {
            grindstoneEvent.setXp(getExperienceFromAlignedDragonArmor(itemStack, item.getDefaultEnchantments()));
            EnchantmentHelper.setEnchantments(itemStack, item.getDefaultEnchantments());
            grindstoneEvent.setNewTopItem(itemStack);
        }
    }

    // Reimplementing a function that only exists in an anonymous class to change exp logic
    private static int getExperienceFromAlignedDragonArmor(ItemStack pStack, ItemEnchantments baseEnchantments) {
        int l = 0;
        ItemEnchantments itemenchantments = EnchantmentHelper.getEnchantmentsForCrafting(pStack);

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemenchantments.entrySet()) {
            Holder<Enchantment> holder = entry.getKey();
            int i1 = entry.getIntValue();
            if (!holder.is(EnchantmentTags.CURSE) && !baseEnchantments.keySet().contains(holder)) {
                l += holder.value().getMinCost(i1);
            }
        }

        return l;
    }
}
