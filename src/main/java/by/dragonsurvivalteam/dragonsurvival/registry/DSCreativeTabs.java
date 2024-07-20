package by.dragonsurvivalteam.dragonsurvival.registry;

import static by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks.*;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import by.dragonsurvivalteam.dragonsurvival.common.items.armor.PermanentEnchantmentItem;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DSCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> DS_CREATIVE_MODE_TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB,
            DragonSurvivalMod.MODID
    );

    static CreativeModeTab.DisplayItemsGenerator displayBlockItemsGenerator = (parameters, output) -> {
        Stream.of(DS_BLOCKS).forEach(
                holder -> holder.getEntries().forEach(
                        entry -> output.accept(entry.get().asItem())
                )
        );
    };

    static List<Holder<Item>> hiddenCreativeModeItems = Arrays.asList(
            DSItems.HUNTING_NET,
            DSItems.LIGHTNING_TEXTURE_ITEM,
            DSItems.PASSIVE_MAGIC_BEACON,
            DSItems.PASSIVE_PEACE_BEACON,
            DSItems.PASSIVE_FIRE_BEACON,
            DSItems.INACTIVE_MAGIC_DRAGON_BEACON,
            DSItems.INACTIVE_PEACE_DRAGON_BEACON,
            DSItems.INACTIVE_FIRE_DRAGON_BEACON
    );

    static CreativeModeTab.DisplayItemsGenerator displayItemsGenerator = (parameters, output) -> {
        Stream.of(DSItems.DS_ITEMS).forEach(
                holder -> holder.getEntries().forEach(
                        entry -> {
                            if(entry.get() instanceof PermanentEnchantmentItem item) {
                                generateGearWithEnchants(output, (Item) item, item.getDefaultEnchantments());
                            } else if(!hiddenCreativeModeItems.contains(entry)) {
                                output.accept(entry.get().asItem());
                            }
                        }
                )
        );
    };

    private static void generateGearWithEnchants(CreativeModeTab.Output output, Item item, ItemEnchantments itemEnchantments) {
        ItemStack stack = new ItemStack(item);

        for(Holder<Enchantment> enchantment : itemEnchantments.keySet()) {
            stack.enchant(enchantment, 1);
        }
        output.accept(stack);
    }

    public static Holder<CreativeModeTab> DS_TAB = DS_CREATIVE_MODE_TABS.register("dragon_survival", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(DSItems.ELDER_DRAGON_BONE))
            .title(Component.translatable("itemGroup.dragon.survival.blocks"))
            .displayItems(displayBlockItemsGenerator)
            .displayItems(displayItemsGenerator)
            .build()
    );
}
