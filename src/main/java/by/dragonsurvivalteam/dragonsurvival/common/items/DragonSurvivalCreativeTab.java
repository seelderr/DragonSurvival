package by.dragonsurvivalteam.dragonsurvival.common.items;

import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import com.google.common.collect.Ordering;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DragonSurvivalCreativeTab extends CreativeModeTab{
	public DragonSurvivalCreativeTab(String label){
		super(label);
	}

	@Override
	public ItemStack makeIcon(){
		return new ItemStack(DSItems.elderDragonBone);
	}

	@Override
	public void fillItemList(NonNullList<ItemStack> items){
		super.fillItemList(items);
		List<ItemLike> list = Arrays.asList(  DSBlocks.dragon_altar_stone, DSBlocks.dragon_altar_sandstone, DSBlocks.dragon_altar_red_sandstone, DSBlocks.dragon_altar_purpur_block, DSBlocks.dragon_altar_oak_log, DSBlocks.dragon_altar_nether_bricks, DSBlocks.dragon_altar_mossy_cobblestone, DSBlocks.dragon_altar_blackstone, DSBlocks.dragon_altar_birch_log, DSItems.elderDragonDust, DSItems.elderDragonBone, DSItems.dragonHeartShard, DSItems.weakDragonHeart, DSItems.elderDragonHeart, DSItems.starBone, DSItems.starHeart, DSItems.wingGrantItem, DSItems.spinGrantItem, DSItems.seaDragonTreat, DSItems.forestDragonTreat, DSItems.caveDragonTreat, DSItems.hotDragonRod, DSItems.explosiveCopper, DSItems.doubleQuartz, DSItems.quartzExplosiveCopper, DSItems.charredMeat, DSItems.charredVegetable, DSItems.charredMushroom, DSItems.charredSeafood, DSItems.chargedCoal, DSItems.chargedSoup, DSItems.meatWildBerries, DSItems.smellyMeatPorridge, DSItems.sweetSourRabbit, DSItems.meatChorusMix, DSItems.diamondChorus, DSItems.luminousOintment, DSItems.frozenRawFish, DSItems.seasonedFish, DSItems.goldenCoralPufferfish, DSItems.goldenTurtleEgg, DSBlocks.dragonBeacon, DSBlocks.peaceDragonBeacon, DSBlocks.magicDragonBeacon, DSBlocks.fireDragonBeacon, DSBlocks.forestSourceOfMagic, DSBlocks.caveSourceOfMagic, DSBlocks.seaSourceOfMagic, DSBlocks.PREDATOR_STAR_BLOCK, DSBlocks.dragonMemoryBlock, DSBlocks.treasureDebris, DSBlocks.treasureDiamond, DSBlocks.treasureEmerald, DSBlocks.treasureCopper, DSBlocks.treasureGold, DSBlocks.treasureIron, DSBlocks.helmet2, DSBlocks.helmet1, DSBlocks.helmet3, DSItems.princeSummon, DSItems.princessSummon, DSBlocks.caveDoor, DSBlocks.forestDoor, DSBlocks.seaDoor, DSBlocks.spruceDoor, DSBlocks.legacyDoor, DSBlocks.oakDoor, DSBlocks.acaciaDoor, DSBlocks.birchDoor, DSBlocks.jungleDoor, DSBlocks.darkOakDoor, DSBlocks.crimsonDoor, DSBlocks.warpedDoor, DSBlocks.ironDoor, DSBlocks.murdererDoor, DSBlocks.sleeperDoor, DSBlocks.stoneDoor, DSBlocks.caveSmallDoor, DSBlocks.forestSmallDoor, DSBlocks.seaSmallDoor, DSBlocks.spruceSmallDoor, DSBlocks.oakSmallDoor, DSBlocks.acaciaSmallDoor, DSBlocks.birchSmallDoor, DSBlocks.jungleSmallDoor, DSBlocks.darkOakSmallDoor, DSBlocks.crimsonSmallDoor, DSBlocks.warpedSmallDoor, DSBlocks.ironSmallDoor, DSBlocks.murdererSmallDoor, DSBlocks.sleeperSmallDoor, DSBlocks.stoneSmallDoor.asItem());
		List<Item> list1 = new ArrayList<>();
		list.forEach(c -> list1.add(c.asItem()));
		Comparator<ItemStack> c = Ordering.explicit(list1).onResultOf(ItemStack::getItem);
		items.sort((c1, c2) -> list1.contains(c1.getItem()) && !list1.contains(c2.getItem()) ? -1 : !list1.contains(c1.getItem()) && list1.contains(c2.getItem()) ? 1 : list1.contains(c1.getItem()) && list1.contains(c2.getItem()) ? c.compare(c1, c2) : 0);
	}
}