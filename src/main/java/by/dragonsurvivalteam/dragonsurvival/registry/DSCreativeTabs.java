package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import static by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks.*;

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

    static CreativeModeTab.DisplayItemsGenerator displayItemsGenerator = (parameters, output) -> {
        Stream.of(DSItems.DS_ITEMS).forEach(
                holder -> holder.getEntries().forEach(
                        entry -> output.accept(entry.get().asItem())
                )
        );
    };

    public static Holder<CreativeModeTab> DS_TAB = DS_CREATIVE_MODE_TABS.register("dragon_survival", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(DSItems.ELDER_DRAGON_BONE))
            .title(Component.translatable("itemGroup.dragon.survival.blocks"))
            .displayItems(displayBlockItemsGenerator)
            .displayItems(displayItemsGenerator)
            .build()
    );
}
