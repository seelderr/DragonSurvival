package by.dragonsurvivalteam.dragonsurvival.registry.datagen;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DataItemModelProvider extends ItemModelProvider {
	public DataItemModelProvider(final PackOutput output, final String modId, final ExistingFileHelper existingFileHelper) {
		super(output, modId, existingFileHelper);
	}


	private static final List<String> blockItemsThatShouldBeBasicInstead = List.of(
			"door",
			"source",
			"helmet"
	);

	private static final List<String> blockItemsThatAreManuallyAuthored = List.of(
			"beacon"
	);

	private static final List<String> itemsThatAreManuallyAuthored = List.of(
			"good_dragon_key",
			"evil_dragon_key",
			"hunter_dragon_key"
	);

	@Override
	protected void registerModels(){
		DSItems.DS_ITEMS.getEntries().forEach((holder) -> {
			if(holder.get() instanceof BlockItem blockItem && blockItemsThatShouldBeBasicInstead.stream().noneMatch(blockItem.toString()::contains)) {
				if(blockItemsThatAreManuallyAuthored.stream().anyMatch(blockItem.toString()::contains)) {
					return;
				}

				if(blockItem.toString().contains("skeleton")) {
					// Parse the string up to "_skin"
					String[] split = holder.getId().getPath().split("_skin");

					// The last character has the number for the skin to select, so parse it
					String skin = split[1].substring(split[1].length() - 1);

					getBuilder(blockItem.toString())
							.parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(DragonSurvivalMod.MODID, BLOCK_FOLDER + "/" + split[0])))
							.texture("skeleton_texture", ResourceLocation.fromNamespaceAndPath(DragonSurvivalMod.MODID, BLOCK_FOLDER + "/" + "skeleton_dragon_" + skin));
				} else if (blockItem.toString().contains("vault")){
					getBuilder(blockItem.toString()).parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(DragonSurvivalMod.MODID, BLOCK_FOLDER + "/" + holder.getId().getPath()) + "_inactive"));
				} else {
					getBuilder(blockItem.toString()).parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(DragonSurvivalMod.MODID, BLOCK_FOLDER + "/" + holder.getId().getPath())));
				}
			} else {
				if(itemsThatAreManuallyAuthored.stream().anyMatch(holder.getId().getPath()::contains)) {
					return;
				}

				basicItem(holder.get());
			}
		});
	}

	@Override
	public @NotNull String getName() {
		return "Dragon Survival Item models";
	}
}