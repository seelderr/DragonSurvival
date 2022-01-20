package by.jackraidenph.dragonsurvival.client.render.blocks;

public class PredatorStarTESR {
//
//        extends TileEntityRenderer<PredatorStarTileEntity> {
//
//    public static final RenderMaterial CAGE_TEXTURE = new RenderMaterial(PlayerContainer.BLOCK_ATLAS, new ResourceLocation(DragonSurvivalMod.MODID, "te/star/cage"));
//    public static final RenderMaterial WIND_TEXTURE = new RenderMaterial(PlayerContainer.BLOCK_ATLAS, new ResourceLocation(DragonSurvivalMod.MODID, "te/star/wind"));
//    public static final RenderMaterial VERTICAL_WIND_TEXTURE = new RenderMaterial(PlayerContainer.BLOCK_ATLAS, new ResourceLocation(DragonSurvivalMod.MODID, "te/star/wind_vertical"));
//    public static final RenderMaterial OPEN_EYE_TEXTURE = new RenderMaterial(PlayerContainer.BLOCK_ATLAS, new ResourceLocation(DragonSurvivalMod.MODID, "te/star/open_eye"));
//
//    /*private static final ShaderCallback CALLBACK = shader -> {
//        int width = GlStateManager.getUniformLocation(shader, "width");
//        ShaderHelper.FLOAT_BUF.position(0);
//        ShaderHelper.FLOAT_BUF.put(0, 1920);
//        RenderSystem.glUniform1(width, ShaderHelper.FLOAT_BUF);
//
//        int height = GlStateManager.getUniformLocation(shader, "height");
//        ShaderHelper.FLOAT_BUF.position(0);
//        ShaderHelper.FLOAT_BUF.put(0, 1080);
//        RenderSystem.glUniform1(height, ShaderHelper.FLOAT_BUF);
//
//        int image = GlStateManager.getUniformLocation(shader, "image");
//        ShaderHelper.FLOAT_BUF.position(0);
//        ShaderHelper.FLOAT_BUF.put(0, 0);
//        RenderSystem.glUniform1(image, ShaderHelper.FLOAT_BUF);
//
//        GL13.glActiveTexture(GL13.GL_TEXTURE3);
//        GL11.glBindTexture(GL13.GL_TEXTURE_2D, Minecraft.getInstance().getFramebuffer().framebufferTexture);
//
//        int imageBack = GlStateManager.getUniformLocation(shader, "imageBack");
//        ShaderHelper.FLOAT_BUF.position(0);
//        ShaderHelper.FLOAT_BUF.put(0, 3);
//        RenderSystem.glUniform1(imageBack, ShaderHelper.FLOAT_BUF);
//
//        GL13.glActiveTexture(GL13.GL_TEXTURE0);
//    };*/
//    private final ModelPart field_228872_h_ = new ModelPart(16, 16, 0, 0);
//    private final ModelPart field_228873_i_;
//    private final ModelPart field_228874_j_;
//    private final ModelPart field_228875_k_;
//
//    public PredatorStarTESR(TileEntityRendererDispatcher p_i226009_1_) {
//        super(p_i226009_1_);
//
//        this.field_228872_h_.addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F, 0.01F);
//        this.field_228873_i_ = new ModelPart(64, 32, 0, 0);
//        this.field_228873_i_.addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F);
//        this.field_228874_j_ = new ModelPart(32, 16, 0, 0);
//        this.field_228874_j_.addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F);
//        this.field_228875_k_ = new ModelPart(32, 16, 0, 0);
//        this.field_228875_k_.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
//    }
//
//    private static RenderType makeRenderType(ResourceLocation texture) {
//        RenderType normal = RenderType.entityTranslucent(texture);
//        return new ShaderWrappedRenderLayer(ShaderHelper.BotaniaShader.COLOR_CYCLE, null, normal);
//    }
//
//    @Override
//    public void render(PredatorStarTileEntity tileEntityIn, float partialTicks, PoseStack  pStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
//        float f = (float) tileEntityIn.getTicksExisted() + partialTicks;
//        float f1 = tileEntityIn.getActiveRotation(partialTicks) * (180F / (float) Math.PI);
//        float f2 = Mth.sin(f * 0.1F) / 2.0F + 0.5F;
//        f2 = f2 * f2 + f2;
//        pStack.pushPose();
//
//        pStack.translate(0.5D, (double) (0.3F + f2 * 0.2F), 0.5D);
//        Vector3f vector3f = new Vector3f(0.5F, 1.0F, 0.5F);
//        vector3f.normalize();
//        pStack.mulPose(new Quaternion(vector3f, f1, true));
//        this.field_228875_k_.render(pStack, CAGE_TEXTURE.buffer(bufferIn, RenderType::entityTranslucent), combinedLightIn, combinedOverlayIn);
//
//        pStack.popPose();
//        /*int i = tileEntityIn.getTicksExisted() / 66 % 3;
//
//        pStack.push();
//        pStack.translate(0.5D, 0.5D, 0.5D);
//        if (i == 1) {
//            pStack.rotate(Vector3f.XP.rotationDegrees(90.0F));
//        } else if (i == 2) {
//            pStack.rotate(Vector3f.ZP.rotationDegrees(90.0F));
//        }
//
//        VertexConsumer ivertexbuilder = (i == 1 ? VERTICAL_WIND_TEXTURE : WIND_TEXTURE).getBuffer(bufferIn, RenderType::getEntityTranslucent);
//        this.field_228873_i_.render(pStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);
//        pStack.pop();
//        pStack.push();
//        pStack.translate(0.5D, 0.5D, 0.5D);
//        pStack.scale(0.875F, 0.875F, 0.875F);
//        pStack.rotate(Vector3f.XP.rotationDegrees(180.0F));
//        pStack.rotate(Vector3f.ZP.rotationDegrees(180.0F));
//        this.field_228873_i_.render(pStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);
//        pStack.pop();*/
//
//        Camera activerenderinfo = this.renderer.camera;
//        
//        RenderSystem.enableBlend();
//        pStack.pushPose();
//
//        pStack.translate(0.5D, 0.3F + f2 * 0.2F, 0.5D);
//        pStack.scale(0.5F, 0.5F, 0.5F);
//        float f3 = -activerenderinfo.getYRot();
//        pStack.mulPose(Vector3f.YP.rotationDegrees(f3));
//        pStack.mulPose(Vector3f.XP.rotationDegrees(activerenderinfo.getXRot()));
//        pStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
//        pStack.scale(1.3333334F, 1.3333334F, 1.3333334F);
//        this.field_228872_h_.render(pStack, OPEN_EYE_TEXTURE.buffer(bufferIn, RenderType::entityTranslucent), combinedLightIn, combinedOverlayIn);
//
//        pStack.popPose();
//        
//    }
//    */
}
