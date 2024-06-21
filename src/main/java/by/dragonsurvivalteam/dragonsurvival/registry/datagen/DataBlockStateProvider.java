package by.dragonsurvivalteam.dragonsurvival.registry.datagen;

import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonAltarBlock;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonDoor;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonPressurePlates;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.TreasureBlock;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import static net.neoforged.neoforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

public class DataBlockStateProvider extends BlockStateProvider {
	public DataBlockStateProvider(final PackOutput output, final String modId, final ExistingFileHelper existingFileHelper) {
		super(output, modId, existingFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		DSBlocks.DS_BLOCKS.getEntries().forEach((holder) -> {
			if (holder.get() instanceof DragonDoor) {
				getVariantBuilder(holder.get())
						.forAllStates(state -> {
							boolean partTop = state.getValue(DragonDoor.PART) == DragonDoor.Part.TOP;
							boolean partMiddle = state.getValue(DragonDoor.PART) == DragonDoor.Part.MIDDLE;
							boolean hingeRight = state.getValue(DragonDoor.HINGE) == DoorHingeSide.RIGHT;
							boolean open = state.getValue(DragonDoor.OPEN);
							String name = holder.getId().getPath();
							String suffix = (partTop ? "_top" : partMiddle ? "_middle" : "_bottom") + (hingeRight ? "_hinge" : "") + (open ? "_open" : "");
							String modelName = BLOCK_FOLDER + "/" + name + suffix;
							ResourceLocation bottom = modLoc(BLOCK_FOLDER + "/" + name + "_bottom");
							ResourceLocation center = modLoc(BLOCK_FOLDER + "/" + name + "_center");
							ResourceLocation top = modLoc(BLOCK_FOLDER + "/" + name + "_top");
							ResourceLocation selected = partTop ? top : partMiddle ? center : bottom;
							ModelFile door;
							if(hingeRight) {
								if(partTop) {
									if(open) {
										door = models().doorTopRightOpen(modelName, selected, selected).renderType("cutout");
									} else {
										door = models().doorTopRight(modelName, selected, selected).renderType("cutout");
									}
								} else {
									if(open) {
										door = models().doorBottomRightOpen(modelName, selected, selected).renderType("cutout");
									} else {
										door = models().doorBottomRight(modelName, selected, selected).renderType("cutout");
									}
								}
							} else {
								if(partTop) {
									if(open) {
										door = models().doorTopLeftOpen(modelName, selected, selected).renderType("cutout");
									} else {
										door = models().doorTopLeft(modelName, selected, selected).renderType("cutout");
									}
								} else {
									if(open) {
										door = models().doorBottomLeftOpen(modelName, selected, selected).renderType("cutout");
									} else {
										door = models().doorBottomLeft(modelName, selected, selected).renderType("cutout");
									}
								}
							}
							int yRot = (int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 90;
							yRot = open && hingeRight ? yRot - 90 : yRot;
							yRot = open && !hingeRight ? yRot + 90 : yRot;
							yRot = yRot < 0 ? 360 + yRot : yRot;
							yRot = yRot % 360;
							return ConfiguredModel.builder().modelFile(door).rotationY(yRot).build();
						});
			} else if (holder.get() instanceof DragonPressurePlates plate) {
				String name = holder.getId().getPath();
				ResourceLocation texture = modLoc("block/" + name);
				ModelFile pressurePlate = models().pressurePlate(name, texture);
				ModelFile pressurePlateDown = models().pressurePlateDown(name + "_down", texture);
				horizontalFacingPressurePlate(plate, pressurePlateDown, pressurePlate);
			} else if (holder.get() instanceof DragonAltarBlock) {
				String name = holder.getId().getPath();
				BlockModelBuilder builder = models().withExistingParent(name, "orientable")
						.texture("up", modLoc(BLOCK_FOLDER + "/" + name + "_top"))
						.texture("down", modLoc(BLOCK_FOLDER + "/" + name + "_top"))
						.texture("east", modLoc(BLOCK_FOLDER + "/" + name + "_east"))
						.texture("west", modLoc(BLOCK_FOLDER + "/" + name + "_west"))
						.texture("north", modLoc(BLOCK_FOLDER + "/" + name + "_north"))
						.texture("south", modLoc(BLOCK_FOLDER + "/" + name + "_south"))
						.texture("particle", modLoc(BLOCK_FOLDER + "/" + name + "_top"));

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
									.withExistingParent(name + layers * 2, BLOCK_FOLDER + "/" + "snow_height" + layers * 2)
									.texture("particle", modLoc(BLOCK_FOLDER + "/" + name))
									.texture("texture", modLoc(BLOCK_FOLDER + "/" + name))
									: /* 8 layers */ models().cubeAll(name, modLoc(BLOCK_FOLDER + "/" + name));
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