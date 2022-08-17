package by.dragonsurvivalteam.dragonsurvival.data;

import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DataItemModelProvider extends ItemModelProvider{
	public DataItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper){
		super(generator, modid, existingFileHelper);
	}

	@Override
	public String getName(){
		return "Dragon Survival Item models";
	}

	@Override
	protected void registerModels(){
		DSItems.DS_ITEMS.forEach((key, value) -> {
			getBuilder(key)
				.parent(new ModelFile.UncheckedModelFile("item/generated"))
				.texture("layer0", modLoc("item/" + key));
		});
	}
}