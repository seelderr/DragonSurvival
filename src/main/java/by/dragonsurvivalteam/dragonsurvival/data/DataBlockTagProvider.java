package by.dragonsurvivalteam.dragonsurvival.data;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonDoor;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.SmallDragonDoor;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class DataBlockTagProvider extends BlockTagsProvider{
	public DataBlockTagProvider(DataGenerator pGenerator, String modId, @Nullable ExistingFileHelper existingFileHelper){
		super(pGenerator, modId, existingFileHelper);
	}

	@Override
	protected void addTags(){
		this.tag(mod("wooden_dragon_doors")).add(DSBlocks.DS_BLOCKS.values().stream().filter(s -> s instanceof DragonDoor || s instanceof SmallDragonDoor).filter(s -> s.defaultBlockState().getMaterial() == Material.WOOD).toList().toArray(new Block[0]));

		this.tag(BlockTags.MINEABLE_WITH_AXE).add(DSBlocks.DS_BLOCKS.values().stream().filter(s -> s.defaultBlockState().getMaterial() == Material.WOOD).toList().toArray(new Block[0]));
		this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(DSBlocks.DS_BLOCKS.values().stream()
			                                              .filter(s -> s.defaultBlockState().getMaterial() == Material.STONE
			                                                           || s.defaultBlockState().getMaterial() == Material.METAL
			                                                           || s.defaultBlockState().getMaterial() == Material.HEAVY_METAL).toList().toArray(new Block[0]));


		this.tag(BlockTags.NEEDS_STONE_TOOL).add(DSBlocks.DS_BLOCKS.values().stream()
			                                              .filter(s -> s.defaultBlockState().getMaterial() == Material.METAL).toList().toArray(new Block[0]));

		this.tag(BlockTags.NEEDS_IRON_TOOL).add(DSBlocks.DS_BLOCKS.values().stream()
			                                        .filter(s -> s.defaultBlockState().getMaterial() == Material.HEAVY_METAL).toList().toArray(new Block[0]));
	}

	@Override
	public String getName(){
		return "Dragon Survival Block tags";
	}

	private static TagKey<Block> mod(String name) {
		return BlockTags.create(new ResourceLocation(DragonSurvivalMod.MODID, name));
	}
	private static TagKey<Block> forge(String name) {
		return BlockTags.create(new ResourceLocation("forge", name));
	}
}