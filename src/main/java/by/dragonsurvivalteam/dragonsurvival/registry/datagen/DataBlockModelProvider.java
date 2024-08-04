package by.dragonsurvivalteam.dragonsurvival.registry.datagen;

import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonAltarBlock;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class DataBlockModelProvider extends BlockModelProvider {
    public DataBlockModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        DSBlocks.DS_BLOCKS.getEntries().forEach((holder) -> {
            if (holder.get() instanceof DragonAltarBlock) {
                withExistingParent(holder.getId().getPath(), BLOCK_FOLDER + "/orientable")
                        .texture("down", ResourceLocation.fromNamespaceAndPath(modid, BLOCK_FOLDER + "/" + holder.getId().getPath() + "_top"))
                        .texture("east", ResourceLocation.fromNamespaceAndPath(modid, BLOCK_FOLDER + "/" + holder.getId().getPath() + "_east"))
                        .texture("north", ResourceLocation.fromNamespaceAndPath(modid, BLOCK_FOLDER + "/" + holder.getId().getPath() + "_north"))
                        .texture("particle", ResourceLocation.fromNamespaceAndPath(modid, BLOCK_FOLDER + "/" + holder.getId().getPath() + "_top"))
                        .texture("south", ResourceLocation.fromNamespaceAndPath(modid, BLOCK_FOLDER + "/" + holder.getId().getPath() + "_south"))
                        .texture("up", ResourceLocation.fromNamespaceAndPath(modid, BLOCK_FOLDER + "/" + holder.getId().getPath() + "_top"))
                        .texture("west", ResourceLocation.fromNamespaceAndPath(modid, BLOCK_FOLDER + "/" + holder.getId().getPath() + "_west"));
            }
        });
    }
}
