package by.dragonsurvivalteam.dragonsurvival.registry;

import static by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks.DS_BLOCKS;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import java.util.List;
import java.util.stream.Stream;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class DSCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> DS_CREATIVE_MODE_TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB,
            DragonSurvivalMod.MODID
    );

    static CreativeModeTab.DisplayItemsGenerator displayItemsGenerator = (parameters, output) -> {
        Stream.of(DS_BLOCKS).forEach(
                holder -> holder.getEntries().forEach(
                        entry -> output.accept(entry.get().asItem())
                )
        );
    };

    public static Holder<CreativeModeTab> DS_TAB = DS_CREATIVE_MODE_TABS.register("dragon_survival", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(DSItems.ELDER_DRAGON_BONE))
            .title(Component.translatable("itemGroup.dragon.survival.blocks"))
            .displayItems(displayItemsGenerator)
            .build()
    );
}
