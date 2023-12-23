package by.dragonsurvivalteam.dragonsurvival.data;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonAltarBlock;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonPressurePlates;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.TreasureBlock;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class DataItemModelProvider extends ItemModelProvider{
	public DataItemModelProvider(final PackOutput output, final String modId, final ExistingFileHelper existingFileHelper) {
		super(output, modId, existingFileHelper);
	}

	@Override
	protected void registerModels(){
		DSItems.DS_ITEMS.forEach((key, item) -> basicItem(item));

		DSBlocks.DS_BLOCKS.forEach((key, block) -> {
			if (block instanceof DragonPressurePlates || block instanceof DragonAltarBlock) {
				ResourceLocation resource = new ResourceLocation(DragonSurvivalMod.MODID, "block/" + key);
				withExistingParent(key, resource);
			} else if (block instanceof TreasureBlock) {
				ResourceLocation resource = new ResourceLocation(DragonSurvivalMod.MODID, "block/" + key + "2");
				withExistingParent(key, resource);
			}
		});
	}

	@Override
	public @NotNull String getName() {
		return "Dragon Survival Item models";
	}
}