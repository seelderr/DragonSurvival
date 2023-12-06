package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DSCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DragonSurvivalMod.MODID);

    @SuppressWarnings("unused")
    public static RegistryObject<CreativeModeTab> DS_TAB = CREATIVE_MODE_TABS.register("dragon_survival", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(DSItems.elderDragonBone))
            .title(Component.translatable("itemGroup.dragon.survival.blocks"))
            .displayItems((parameters, output) -> {
                List<ItemLike> list = Arrays.asList(DSBlocks.dragon_altar_stone, DSBlocks.dragon_altar_sandstone, DSBlocks.dragon_altar_red_sandstone, DSBlocks.dragon_altar_purpur_block, DSBlocks.dragon_altar_oak_log, DSBlocks.dragon_altar_nether_bricks, DSBlocks.dragon_altar_mossy_cobblestone, DSBlocks.dragon_altar_blackstone, DSBlocks.dragon_altar_birch_log, DSItems.elderDragonDust, DSItems.elderDragonBone, DSItems.dragonHeartShard, DSItems.weakDragonHeart, DSItems.elderDragonHeart, DSItems.starBone, DSItems.starHeart, DSItems.wingGrantItem, DSItems.spinGrantItem, DSItems.seaDragonTreat, DSItems.forestDragonTreat, DSItems.caveDragonTreat, DSItems.hotDragonRod, DSItems.explosiveCopper, DSItems.doubleQuartz, DSItems.quartzExplosiveCopper, DSItems.charredMeat, DSItems.charredVegetable, DSItems.charredMushroom, DSItems.charredSeafood, DSItems.chargedCoal, DSItems.chargedSoup, DSItems.meatWildBerries, DSItems.smellyMeatPorridge, DSItems.sweetSourRabbit, DSItems.meatChorusMix, DSItems.diamondChorus, DSItems.luminousOintment, DSItems.frozenRawFish, DSItems.seasonedFish, DSItems.goldenCoralPufferfish, DSItems.goldenTurtleEgg, DSBlocks.dragonBeacon, DSBlocks.peaceDragonBeacon, DSBlocks.magicDragonBeacon, DSBlocks.fireDragonBeacon, DSBlocks.forestSourceOfMagic, DSBlocks.caveSourceOfMagic, DSBlocks.seaSourceOfMagic, DSBlocks.dragonMemoryBlock, DSBlocks.treasureDebris, DSBlocks.treasureDiamond, DSBlocks.treasureEmerald, DSBlocks.treasureCopper, DSBlocks.treasureGold, DSBlocks.treasureIron, DSBlocks.helmet2, DSBlocks.helmet1, DSBlocks.helmet3, DSItems.princeSummon, DSItems.princessSummon, DSBlocks.caveDoor, DSBlocks.forestDoor, DSBlocks.seaDoor, DSBlocks.spruceDoor, DSBlocks.legacyDoor, DSBlocks.oakDoor, DSBlocks.acaciaDoor, DSBlocks.birchDoor, DSBlocks.jungleDoor, DSBlocks.darkOakDoor, DSBlocks.crimsonDoor, DSBlocks.warpedDoor, DSBlocks.ironDoor, DSBlocks.murdererDoor, DSBlocks.sleeperDoor, DSBlocks.stoneDoor, DSBlocks.caveSmallDoor, DSBlocks.forestSmallDoor, DSBlocks.seaSmallDoor, DSBlocks.spruceSmallDoor, DSBlocks.oakSmallDoor, DSBlocks.acaciaSmallDoor, DSBlocks.birchSmallDoor, DSBlocks.jungleSmallDoor, DSBlocks.darkOakSmallDoor, DSBlocks.crimsonSmallDoor, DSBlocks.warpedSmallDoor, DSBlocks.ironSmallDoor, DSBlocks.murdererSmallDoor, DSBlocks.sleeperSmallDoor, DSBlocks.stoneSmallDoor.asItem());
                list.forEach(output::accept);
            })
            .build());

    @SubscribeEvent
    public static void buildCreativeModeTabs(final BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            DSEntities.SPAWN_EGGS.forEach(event::accept);
        }
    }
}
