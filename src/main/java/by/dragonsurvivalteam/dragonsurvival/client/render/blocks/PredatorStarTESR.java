package by.dragonsurvivalteam.dragonsurvival.client.render.blocks;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.shader.ShaderHelper;
import by.dragonsurvivalteam.dragonsurvival.client.shader.ShaderWrappedRenderLayer;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.PredatorStarTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class PredatorStarTESR extends TileEntityRenderer<PredatorStarTileEntity>{

	public static final RenderMaterial CAGE_TEXTURE = new RenderMaterial(PlayerContainer.BLOCK_ATLAS, new ResourceLocation(DragonSurvivalMod.MODID, "te/star/cage"));
	public static final RenderMaterial WIND_TEXTURE = new RenderMaterial(PlayerContainer.BLOCK_ATLAS, new ResourceLocation(DragonSurvivalMod.MODID, "te/star/wind"));
	public static final RenderMaterial VERTICAL_WIND_TEXTURE = new RenderMaterial(PlayerContainer.BLOCK_ATLAS, new ResourceLocation(DragonSurvivalMod.MODID, "te/star/wind_vertical"));
	public static final RenderMaterial OPEN_EYE_TEXTURE = new RenderMaterial(PlayerContainer.BLOCK_ATLAS, new ResourceLocation(DragonSurvivalMod.MODID, "te/star/open_eye"));

	private final ModelRenderer field_228872_h_ = new ModelRenderer(16, 16, 0, 0);
	private final ModelRenderer field_228873_i_;
	private final ModelRenderer field_228874_j_;
	private final ModelRenderer field_228875_k_;

	public PredatorStarTESR(TileEntityRendererDispatcher p_i226009_1_){
		super(p_i226009_1_);

		this.field_228872_h_.addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F, 0.01F);
		this.field_228873_i_ = new ModelRenderer(64, 32, 0, 0);
		this.field_228873_i_.addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F);
		this.field_228874_j_ = new ModelRenderer(32, 16, 0, 0);
		this.field_228874_j_.addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F);
		this.field_228875_k_ = new ModelRenderer(32, 16, 0, 0);
		this.field_228875_k_.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
	}

	private static RenderType makeRenderType(ResourceLocation texture){
		RenderType normal = RenderType.entityTranslucent(texture);
		return new ShaderWrappedRenderLayer(ShaderHelper.BotaniaShader.COLOR_CYCLE, null, normal);
	}

	@Override
	public void render(PredatorStarTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn){
		float f = (float)tileEntityIn.getTicksExisted() + partialTicks;
		float f1 = tileEntityIn.getActiveRotation(partialTicks) * (180F / (float)Math.PI);
		float f2 = MathHelper.sin(f * 0.1F) / 2.0F + 0.5F;
		f2 = f2 * f2 + f2;
		matrixStackIn.pushPose();

		matrixStackIn.translate(0.5D, 0.3F + f2 * 0.2F, 0.5D);
		Vector3f vector3f = new Vector3f(0.5F, 1.0F, 0.5F);
		vector3f.normalize();
		matrixStackIn.mulPose(new Quaternion(vector3f, f1, true));
		this.field_228875_k_.render(matrixStackIn, CAGE_TEXTURE.buffer(bufferIn, RenderType::entityTranslucent), combinedLightIn, combinedOverlayIn);
		matrixStackIn.popPose();


		ActiveRenderInfo activerenderinfo = this.renderer.camera;
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		matrixStackIn.pushPose();

		matrixStackIn.translate(0.5D, 0.3F + f2 * 0.2F, 0.5D);
		matrixStackIn.scale(0.5F, 0.5F, 0.5F);
		float f3 = -activerenderinfo.getYRot();
		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f3));
		matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(activerenderinfo.getXRot()));
		matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
		matrixStackIn.scale(1.3333334F, 1.3333334F, 1.3333334F);
		this.field_228872_h_.render(matrixStackIn, OPEN_EYE_TEXTURE.buffer(bufferIn, RenderType::entityTranslucent), combinedLightIn, combinedOverlayIn);

		matrixStackIn.popPose();
		RenderSystem.popMatrix();
	}
}