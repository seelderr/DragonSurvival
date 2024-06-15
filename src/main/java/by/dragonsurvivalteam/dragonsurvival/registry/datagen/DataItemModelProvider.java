package by.dragonsurvivalteam.dragonsurvival.registry.datagen;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonAltarBlock;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonPressurePlates;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.TreasureBlock;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class DataItemModelProvider extends ItemModelProvider {
	public DataItemModelProvider(final PackOutput output, final String modId, final ExistingFileHelper existingFileHelper) {
		super(output, modId, existingFileHelper);
	}

	@Override
	protected void registerModels(){
		DSItems.DS_ITEMS.getEntries().forEach((key) -> basicItem(key.get().asItem()));

		// TODO: Test this
		DSBlocks.DS_BLOCKS.getEntries().forEach((key) -> {
			if (key.get() instanceof DragonPressurePlates || key.get() instanceof DragonAltarBlock) {
				ResourceLocation resource = ResourceLocation.fromNamespaceAndPath(MODID, "block/");
				withExistingParent(key.toString(), resource);
			} else if (key.get() instanceof TreasureBlock) {
				ResourceLocation resource = ResourceLocation.fromNamespaceAndPath(MODID, "block/");
				withExistingParent(key.toString(), resource);
			}
		});
	}

	@Override
	public @NotNull String getName() {
		return "Dragon Survival Item models";
	}
}