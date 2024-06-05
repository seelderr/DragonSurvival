package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.stream.Stream;

import static by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks.DS_BLOCKS;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class DSCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> DS_CREATIVE_MODE_TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB,
            DragonSurvivalMod.MODID
    );

    @SuppressWarnings("unused")
    public static Registry<CreativeModeTab> DS_TAB = DS_CREATIVE_MODE_TABS.register("dragon_survival", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(DSItems.ELDER_DRAGON_BONE))
            .title(Component.translatable("itemGroup.dragon.survival.blocks"))
            .displayItems((parameters, output) -> {
                List<ItemLike> list = Stream.of(DS_BLOCKS).forEach(
                        holder -> holder.getEntries().forEach(
                                entry -> output.accept(entry.get().asItem())
                        )
                );
            }.build())
    );
}
