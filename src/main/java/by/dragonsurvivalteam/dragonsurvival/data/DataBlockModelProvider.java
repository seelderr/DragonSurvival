package by.dragonsurvivalteam.dragonsurvival.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DataBlockModelProvider extends BlockModelProvider{
	public DataBlockModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper){
		super(generator, modid, existingFileHelper);
	}

	@Override
	protected void registerModels(){
		/*
		cubeAll("example_block", modLoc("block/example_block"));
		
		cube("sided_example_block",
		     modLoc("block/example_block_down"),
		     modLoc("block/example_block_up"),
		     modLoc("block/example_block_north"),
		     modLoc("block/example_block_south"),
		     modLoc("block/example_block_east"),
		     modLoc("block/example_block_west"));
		 */
	}
}