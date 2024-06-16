package by.dragonsurvivalteam.dragonsurvival.registry.datagen;

import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonAltarBlock;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonDoor;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonPressurePlates;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.TreasureBlock;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class DataBlockStateProvider extends BlockStateProvider {
	public DataBlockStateProvider(final PackOutput output, final String modId, final ExistingFileHelper existingFileHelper) {
		super(output, modId, existingFileHelper);
	}

	// FIXME: This almost certainly does not work as intended. Will probably need a refactor.
	@Override
	protected void registerStatesAndModels() {
		DSBlocks.DS_BLOCKS.getEntries().forEach((holder) -> {
			if (holder.get() instanceof DragonDoor) {
				// TODO
			} else if (holder.get() instanceof DragonPressurePlates plate) {
				String name = holder.getId().getPath();
				ResourceLocation texture = modLoc("block/" + name);
				ModelFile pressurePlate = models().pressurePlate(name, texture);
				ModelFile pressurePlateDown = models().pressurePlateDown(name + "_down", texture);
				horizontalFacingPressurePlate(plate, pressurePlateDown, pressurePlate);
			} else if (holder.get() instanceof DragonAltarBlock) {
				String name = holder.getId().getPath();
				BlockModelBuilder builder = models().withExistingParent(name, "orientable")
						.texture("up", modLoc("block/" + name + "_top"))
						.texture("down", modLoc("block/" + name + "_top"))
						.texture("east", modLoc("block/" + name + "_east"))
						.texture("west", modLoc("block/" + name + "_west"))
						.texture("north", modLoc("block/" + name + "_north"))
						.texture("south", modLoc("block/" + name + "_south"))
						.texture("particle", modLoc("block/" + name + "_top"));

				getVariantBuilder(holder.get())
						.forAllStates(state ->
								ConfiguredModel.builder()
										.modelFile(builder)
										.rotationY((int) state.getValue(DragonAltarBlock.FACING).toYRot())
										.build()
						);
			} else if (holder.get() instanceof TreasureBlock treasureBlock) {
				getVariantBuilder(treasureBlock)
						.forAllStatesExcept(state -> {
							int layers = state.getValue(TreasureBlock.LAYERS);
							String name = holder.getId().getPath();
							BlockModelBuilder builder = layers != 8 ? models()
									.withExistingParent(name + layers * 2, "block/snow_height" + layers * 2)
									.texture("particle", modLoc("block/" + name))
									.texture("texture", modLoc("block/" + name))
									: /* 8 layers */ models().cubeAll(name, modLoc("block/" + name));
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