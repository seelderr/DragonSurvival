package by.dragonsurvivalteam.dragonsurvival.data;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;

import java.util.Optional;

public class DataSpriteSourceProvider extends SpriteSourceProvider {
    public DataSpriteSourceProvider(final PackOutput output, final ExistingFileHelper fileHelper, final String modId) {
        super(output, fileHelper, modId);
    }

    @Override
    protected void addSources() {
        atlas(SpriteSourceProvider.BLOCKS_ATLAS)
                .addSource(new SingleFile(new ResourceLocation(DragonSurvivalMod.MODID, "te/star/cage"), Optional.empty()))
                .addSource(new SingleFile(new ResourceLocation(DragonSurvivalMod.MODID, "te/star/wind"), Optional.empty()))
                .addSource(new SingleFile(new ResourceLocation(DragonSurvivalMod.MODID, "te/star/open_eye"), Optional.empty()))
                .addSource(new SingleFile(new ResourceLocation(DragonSurvivalMod.MODID, "te/star/wind_vertical"), Optional.empty()))
                .addSource(new SingleFile(new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_axe"), Optional.empty()))
                .addSource(new SingleFile(new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_pickaxe"), Optional.empty()))
                .addSource(new SingleFile(new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_shovel"), Optional.empty()))
                .addSource(new SingleFile(new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_sword"), Optional.empty()));
    }
}
