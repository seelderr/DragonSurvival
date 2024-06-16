package by.dragonsurvivalteam.dragonsurvival.client.render.blocks;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.HelmetTileEntity;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class HelmetEntityRenderer implements BlockEntityRenderer<HelmetTileEntity>{
	private static final Map<Block, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), resourceLocationHashMap -> {
		resourceLocationHashMap.put(DSBlocks.HELMET_BLOCK_1.get(), ResourceLocation.fromNamespaceAndPath(MODID, "textures/block/broken_knight_helmet_0.png"));
		resourceLocationHashMap.put(DSBlocks.HELMET_BLOCK_2.get(), ResourceLocation.fromNamespaceAndPath(MODID, "textures/block/broken_knight_helmet_1.png"));
		resourceLocationHashMap.put(DSBlocks.HELMET_BLOCK_3.get(), ResourceLocation.fromNamespaceAndPath(MODID, "textures/block/broken_knight_helmet_2.png"));
	});
	static SkullModel humanoidHeadModel = new SkullModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_HEAD));

	public HelmetEntityRenderer(BlockEntityRendererProvider.Context pContext){}

	@Override
	public void render(HelmetTileEntity helmetEntity, float p_225616_2_, PoseStack PoseStack, MultiBufferSource renderTypeBuffer, int p_225616_5_, int p_225616_6_){
		BlockState blockstate = helmetEntity.getBlockState();
		float f1 = 22.5F * blockstate.getValue(SkullBlock.ROTATION);
		renderHelmet(null, f1, blockstate.getBlock(), 0, PoseStack, renderTypeBuffer, p_225616_5_);
	}

	public static void renderHelmet(
		@Nullable Direction direction, float p_228879_1_, Block helmetBlock, float p_228879_4_, PoseStack PoseStack, MultiBufferSource renderTypeBuffer, int p_228879_7_){
		PoseStack.pushPose();
		if(direction == null){
			PoseStack.translate(0.5D, 0.0D, 0.5D);
		}else{
			PoseStack.translate(0.5F - (float)direction.getStepX() * 0.25F, 0.25D, 0.5F - (float)direction.getStepZ() * 0.25F);
		}

		PoseStack.scale(-1.0F, -1.0F, 1.0F);
		VertexConsumer ivertexbuilder = renderTypeBuffer.getBuffer(getRenderType(helmetBlock));
		humanoidHeadModel.setupAnim(p_228879_4_, p_228879_1_, 0.0F);
		humanoidHeadModel.renderToBuffer(PoseStack, ivertexbuilder, p_228879_7_, OverlayTexture.NO_OVERLAY);
		PoseStack.popPose();
	}

	private static RenderType getRenderType(Block block){
		ResourceLocation resourcelocation = TEXTURE_BY_TYPE.get(block);
		return RenderType.entityCutoutNoCullZOffset(resourcelocation);
	}
}