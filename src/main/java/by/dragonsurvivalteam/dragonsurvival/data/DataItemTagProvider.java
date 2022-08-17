package by.dragonsurvivalteam.dragonsurvival.data;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonAltarBlock;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonDoor;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.SmallDragonDoor;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class DataItemTagProvider extends ItemTagsProvider{
	public DataItemTagProvider(DataGenerator pGenerator, BlockTagsProvider pBlockTagsProvider, String modId, @Nullable ExistingFileHelper existingFileHelper){
		super(pGenerator, pBlockTagsProvider, modId, existingFileHelper);
	}

	@Override
	protected void addTags(){
		this.tag(forge("raw_fishes")).add(Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH);
		this.tag(forge("raw_meats")).add(Items.BEEF, Items.CHICKEN, Items.MUTTON, Items.PORKCHOP, Items.RABBIT);

		this.tag(mod("charred_food")).add(DSItems.chargedCoal, DSItems.chargedSoup, DSItems.charredMeat, DSItems.charredMushroom, DSItems.charredSeafood, DSItems.charredVegetable);
		this.tag(mod("copper")).addTag(forge("ingots/copper")).addOptional(new ResourceLocation("cavesandcliffs:raw_copper"));

		this.tag(mod("dragon_altars")).add(DSBlocks.DS_BLOCK_ITEMS.values().stream().filter(s -> s.getBlock() instanceof DragonAltarBlock).map(Item::asItem).toList().toArray(new Item[0]));
		this.tag(mod("wooden_dragon_doors")).add(DSBlocks.DS_BLOCK_ITEMS.values().stream().filter(s -> s.getBlock() instanceof DragonDoor || s.getBlock() instanceof SmallDragonDoor).filter(s -> s.getBlock().defaultBlockState().getMaterial() == Material.WOOD).map(Item::asItem).toList().toArray(new Item[0]));
	}


	@Override
	public String getName(){
		return "Dragon Survival Item tags";
	}

	private static TagKey<Item> mod(String name) {
		return ItemTags.create(new ResourceLocation(DragonSurvivalMod.MODID, name));
	}
	private static TagKey<Item> forge(String name) {
		return ItemTags.create(new ResourceLocation("forge", name));
	}
}