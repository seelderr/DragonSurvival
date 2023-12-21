package by.dragonsurvivalteam.dragonsurvival.data;

import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonAltarBlock;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonDoor;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonPressurePlates;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.TreasureBlock;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class DataBlockStateProvider extends BlockStateProvider {
	public DataBlockStateProvider(final PackOutput output, final String modId, final ExistingFileHelper existingFileHelper) {
		super(output, modId, existingFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		DSBlocks.DS_BLOCKS.forEach((key, block) -> {
			if (block instanceof DragonDoor) {
				// TODO
			} else if (block instanceof DragonPressurePlates plate) {
				ResourceLocation texture = modLoc("block/" + key);
				ModelFile pressurePlate = models().pressurePlate(key, texture);
				ModelFile pressurePlateDown = models().pressurePlateDown(key + "_down", texture);
				horizontalFacingPressurePlate(plate, pressurePlateDown, pressurePlate);
			} else if (block instanceof DragonAltarBlock) {
				BlockModelBuilder builder = models().withExistingParent(key, "orientable")
						.texture("up", modLoc("block/" + key + "_top"))
						.texture("down", modLoc("block/" + key + "_top"))
						.texture("east", modLoc("block/" + key + "_east"))
						.texture("west", modLoc("block/" + key + "_west"))
						.texture("north", modLoc("block/" + key + "_north"))
						.texture("south", modLoc("block/" + key + "_south"))
						.texture("particle", modLoc("block/" + key + "_top"));

				getVariantBuilder(block)
						.forAllStates(state ->
								ConfiguredModel.builder()
										.modelFile(builder)
										.rotationY((int) state.getValue(DragonAltarBlock.FACING).toYRot())
										.build()
						);
			} else if (block instanceof TreasureBlock treasureBlock) {
				getVariantBuilder(treasureBlock)
						.forAllStatesExcept(state -> {
							int layers = state.getValue(TreasureBlock.LAYERS);
							BlockModelBuilder builder = layers != 8 ? models()
									.withExistingParent(key + layers * 2, "block/snow_height" + layers * 2)
									.texture("particle", modLoc("block/" + key))
									.texture("texture", modLoc("block/" + key))
									: /* 8 layers */ models().cubeAll(key, modLoc("block/" + key));
							return ConfiguredModel.builder().modelFile(builder).build();
						}, TreasureBlock.WATERLOGGED);
			}
		});
	}

	public void horizontalFacingPressurePlate(final DragonPressurePlates plate, final ModelFile poweredModel, final ModelFile unpoweredModel) {
		getVariantBuilder(plate)
				.forAllStatesExcept(state -> ConfiguredModel.builder()
								.modelFile(state.getValue(DragonPressurePlates.POWERED) ? poweredModel : unpoweredModel)
								.rotationY((((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180)) % 360)
								.build()
						, DragonPressurePlates.WATERLOGGED);
	}

	@Override
	public @NotNull String getName() {
		return "Dragon Survival Block states";
	}
}