package by.jackraidenph.dragonsurvival.client.render.entity.creatures;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.common.entity.monsters.MagicalPredatorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MagicalPredatorRenderer <MagicalPredatorModel extends EntityModel<MagicalPredatorEntity>> extends MobRenderer<MagicalPredatorEntity, MagicalPredatorModel> {

    public static List<ResourceLocation> MAGICAL_BEAST_TEXTURES = new ArrayList<>(Arrays.asList(
            new ResourceLocation(DragonSurvivalMod.MODID, "textures/magical_beast/magical_predator_dark.png"),
            new ResourceLocation(DragonSurvivalMod.MODID, "textures/magical_beast/magical_predator_dark_broken.png"),
            new ResourceLocation(DragonSurvivalMod.MODID, "textures/magical_beast/magical_predator_grass.png"),
            new ResourceLocation(DragonSurvivalMod.MODID, "textures/magical_beast/magical_predator_gray.png"),
            new ResourceLocation(DragonSurvivalMod.MODID, "textures/magical_beast/magical_predator_green.png"),
            new ResourceLocation(DragonSurvivalMod.MODID, "textures/magical_beast/magical_predator_jungle.png"),
            new ResourceLocation(DragonSurvivalMod.MODID, "textures/magical_beast/magical_predator_jungle_flowers.png"),
            new ResourceLocation(DragonSurvivalMod.MODID, "textures/magical_beast/magical_predator_light.png"),
            new ResourceLocation(DragonSurvivalMod.MODID, "textures/magical_beast/magical_predator_sand.png"),
            new ResourceLocation(DragonSurvivalMod.MODID, "textures/magical_beast/magical_predator_zombie.png")
    ));

    //TODO Do this
    public MagicalPredatorRenderer(EntityRendererProvider.Context p_i50961_1_) {
        super(p_i50961_1_, null, 0.66F);//new MagicalPredatorModel(RenderType::entityTranslucent)
    }

    @Override
    protected void scale(MagicalPredatorEntity entitylivingbaseIn, PoseStack pStack, float partialTickTime) {
        this.shadowRadius = entitylivingbaseIn.size / entitylivingbaseIn.getBbHeight() / 1.44F;
        float scale = entitylivingbaseIn.size / entitylivingbaseIn.getBbHeight();
        pStack.scale(scale, scale, scale);
    }


    @Override
    public ResourceLocation getTextureLocation(MagicalPredatorEntity entity) {
        return MAGICAL_BEAST_TEXTURES.get(entity.type);
    }
}
