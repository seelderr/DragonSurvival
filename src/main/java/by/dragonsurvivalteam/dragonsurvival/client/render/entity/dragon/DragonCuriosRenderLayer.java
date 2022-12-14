package by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.ArrayList;

public class DragonCuriosRenderLayer extends GeoLayerRenderer<DragonEntity> {

    private final GeoEntityRenderer<DragonEntity> renderer;

    public DragonCuriosRenderLayer(GeoEntityRenderer<DragonEntity> entityRendererIn) {
        super(entityRendererIn);
        this.renderer = entityRendererIn;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, DragonEntity entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        Player player = entityLivingBaseIn.getPlayer();

        if (player.isSpectator()) return;

        IBone neck = ClientDragonRender.dragonArmorModel.getAnimationProcessor().getBone("Neck");

        if(neck != null)
            neck.setHidden(false);

        ArrayList<ResourceLocation> curioTextures = getCurioTextures(player);

        if (!curioTextures.isEmpty()) {
            ((DragonRenderer) renderer).isRenderLayers = true;
            GeoModel model = ClientDragonRender.dragonModel.getModel(ClientDragonRender.dragonModel.getModelLocation(null));
            for (ResourceLocation tex : curioTextures) {
                renderCurioPiece(model, matrixStackIn, bufferIn, packedLightIn, entityLivingBaseIn, partialTicks, tex);
            }
            ((DragonRenderer) renderer).isRenderLayers = false;
        }
    }

    private void renderCurioPiece(GeoModel model, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, DragonEntity entitylivingbaseIn, float partialTicks, ResourceLocation curioTexture) {
        ClientDragonRender.dragonModel.setCurrentTexture(curioTexture);
        ClientDragonRender.dragonArmor.copyPosition(entitylivingbaseIn);
        RenderType type = renderer.getRenderType(entitylivingbaseIn, partialTicks, matrixStackIn, bufferIn, null, packedLightIn, curioTexture);
        VertexConsumer vertexConsumer = bufferIn.getBuffer(type);
        renderer.render(model, entitylivingbaseIn,
                partialTicks,
                type,
                matrixStackIn,
                bufferIn,
                vertexConsumer,
                packedLightIn,
                OverlayTexture.NO_OVERLAY,
                1F, 1F, 1F, 1F);
    }

    private ArrayList<ResourceLocation> getCurioTextures(Player player) {
        ArrayList<ResourceLocation> resources = new ArrayList<>();
        // mostly taken from https://github.com/TheIllusiveC4/Curios/blob/1.18.x/src/main/java/top/theillusivec4/curios/client/render/CuriosLayer.java
        CuriosApi.getCuriosHelper().getCuriosHandler(player).ifPresent(handler -> handler.getCurios().forEach(
                (id, stacksHandler) -> {
                    IDynamicStackHandler stackHandler = stacksHandler.getStacks();
                    IDynamicStackHandler cosmeticStacksHandler = stacksHandler.getCosmeticStacks();

                    for (int i = 0; i < stackHandler.getSlots(); i++) {
                        ItemStack stack = cosmeticStacksHandler.getStackInSlot(i);
                        if (stack.isEmpty()) {
                            stack = stackHandler.getStackInSlot(i);
                        }
                        NonNullList<Boolean> renderStates = stacksHandler.getRenders();
                        boolean renderable = renderStates.size() > i && renderStates.get(i);
                        if (renderable) {
                            String resId = DragonArmorRenderLayer.itemToResLoc(stack.getItem());
                            if (resId != null) {
                                resId = "textures/armor/" + resId;
                                if (Minecraft.getInstance().getResourceManager().hasResource(
                                        new ResourceLocation(DragonSurvivalMod.MODID, resId))) {
                                    resources.add(new ResourceLocation(DragonSurvivalMod.MODID, resId));
                                }
                            }
                        }
                    }
                }
        ));
        return resources;
    }
}