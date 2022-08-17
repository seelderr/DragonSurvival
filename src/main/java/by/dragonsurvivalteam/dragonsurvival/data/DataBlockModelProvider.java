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
	}
}